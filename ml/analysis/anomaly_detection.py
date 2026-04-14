import pandas as pd
import numpy as np

# Import optionnel de scikit-learn
try:
    from sklearn.ensemble import IsolationForest
    SKLEARN_AVAILABLE = True
except Exception:
    IsolationForest = None
    SKLEARN_AVAILABLE = False


def _build_severity(deviation_ratio: float, stock: int | None = None) -> str:
    """
    Transforme un écart en niveau de gravité lisible business.
    """
    # Si l'écart est très élevé → critique
    if deviation_ratio >= 0.75:
        return "critical"
    # Si l'écart est élevé
    if deviation_ratio >= 0.35:
        return "high"
    # Si l'écart est modéré
    if deviation_ratio >= 0.15:
        return "medium"
    # Sinon faible
    return "low"


def _build_reason_and_recommendation(
    direction: str,
    product: str,
    stock: int | None,
    share: float,
) -> tuple[str, str]:
    """
    Génère une explication business et une action recommandée.
    Les messages sont en anglais et adaptés dynamiquement au contexte.
    """
    # CAS 1 : ventes anormalement basses
    if direction == "low":
        if stock is not None and stock <= 10:
            reason = (
                f"Daily revenue is abnormally low for {product}. "
                f"Current stock is only {stock} units, which may indicate a partial stockout, "
                f"weak product visibility, or reduced conversion."
            )
            recommendation = (
                f"Check product availability, pricing, and marketing visibility for {product}. "
                f"If this product is strategic, increase safety stock."
            )
        else:
            reason = (
                f"Daily revenue is abnormally low for {product} despite available stock. "
                f"This may reflect weak demand, pricing issues, or insufficient traffic."
            )
            recommendation = (
                f"Analyze pricing, competitors, and marketing performance for {product}. "
                f"Consider a targeted promotion if the product is important."
            )

    # CAS 2 : ventes anormalement élevées
    else:
        if share >= 0.5:
            reason = (
                f"The revenue spike is mainly driven by {product}, which represents {share:.1%} of the day's revenue. "
                f"This may reflect a successful promotion, a large order, or exceptional demand."
            )
            recommendation = (
                f"Secure inventory for {product}, monitor profit margins, and leverage this product in future campaigns."
            )
        else:
            reason = (
                f"Daily revenue is abnormally high, and {product} is among the products contributing to the spike. "
                f"The increase may come from a general uplift in demand or multiple strong product performances."
            )
            recommendation = (
                f"Identify the cause of the spike, verify profitability, and prepare higher stock if the trend continues."
            )

    return reason, recommendation


def _detect_ml_daily_anomalies(daily_revenue: pd.Series) -> dict[pd.Timestamp, dict]:
    """
    Détecte des anomalies journalières avec Isolation Forest.
    Retourne un dictionnaire indexé par date.
    """
    if not SKLEARN_AVAILABLE:
        return {}

    if len(daily_revenue) < 8:
        # Pas assez de jours pour que le modèle soit vraiment utile
        return {}

    values = daily_revenue.values.astype(float).reshape(-1, 1)

    # On garde une contamination modérée pour éviter de sur-détecter
    contamination = min(0.2, max(0.05, 2.0 / len(daily_revenue)))

    model = IsolationForest(
        n_estimators=200,
        contamination=contamination,
        random_state=42,
    )

    labels = model.fit_predict(values)          # -1 = anomalie, 1 = normal
    scores = model.decision_function(values)    # score de normalité

    anomalies = {}
    for i, date in enumerate(daily_revenue.index):
        if labels[i] == -1:
            anomalies[pd.Timestamp(date)] = {
                "ml_score": float(scores[i]),
                "ml_label": -1,
            }

    return anomalies


def detect_sales_anomalies(data):
    """
    Détecte les anomalies de ventes et enrichit la réponse avec :
    - produit concerné
    - date
    - valeur observée
    - gravité
    - raison métier
    - recommandation ciblée
    - source de détection (statistique / ML / les deux)
    """
    if not data:
        return {"error": "no sales data"}

    df = pd.DataFrame(data)

    required_columns = {"date", "product", "price", "quantity", "stock"}
    missing = required_columns - set(df.columns)
    if missing:
        return {"error": f"missing columns: {sorted(missing)}"}

    df["date"] = pd.to_datetime(df["date"], errors="coerce")
    df = df.dropna(subset=["date", "product", "price", "quantity"])

    if df.empty:
        return {"error": "no valid sales data"}

    df["revenue"] = df["price"] * df["quantity"]

    # Revenus totaux par jour
    daily_revenue = df.groupby("date")["revenue"].sum().sort_index()

    if len(daily_revenue) == 0:
        return {"error": "no daily revenue available"}

    # Médiane utile pour classer les anomalies ML-only en low/high
    median_daily_revenue = float(daily_revenue.median())

    # ===============================
    # Détection statistique des bornes
    # ===============================
    if len(daily_revenue) >= 4:
        q1 = daily_revenue.quantile(0.25)
        q3 = daily_revenue.quantile(0.75)
        iqr = q3 - q1
        lower_bound = float(q1 - 1.5 * iqr)
        upper_bound = float(q3 + 1.5 * iqr)
    else:
        mean = daily_revenue.mean()
        std = daily_revenue.std()

        if pd.isna(std) or std == 0:
            lower_bound = float(mean)
            upper_bound = float(mean)
        else:
            lower_bound = float(mean - 2 * std)
            upper_bound = float(mean + 2 * std)

    # ===============================
    # Détection ML avec Isolation Forest
    # ===============================
    ml_anomalies = _detect_ml_daily_anomalies(daily_revenue)

    anomalies = []

    # CA par produit et par jour
    product_daily = (
        df.groupby(["date", "product"], as_index=False)
        .agg(
            revenue=("revenue", "sum"),
            stock=("stock", "last"),
            quantity=("quantity", "sum"),
        )
    )

    for date, day_revenue in daily_revenue.items():
        is_low = day_revenue < lower_bound
        is_high = day_revenue > upper_bound
        is_ml_anomaly = pd.Timestamp(date) in ml_anomalies

        # Si aucune anomalie statistique et pas d'anomalie ML → on passe
        if not is_low and not is_high and not is_ml_anomaly:
            continue

        day_products = product_daily[product_daily["date"] == date].copy()
        if day_products.empty:
            continue

        day_products["share_of_day"] = (
            day_products["revenue"] / float(day_revenue)
            if day_revenue != 0 else 0.0
        )
        day_products = day_products.sort_values(by="revenue", ascending=False)
        top_products = day_products.head(3)

        # Détermination de la direction si l'anomalie vient du ML
        if is_low:
            direction = "low"
            reference_value = lower_bound
            deviation_ratio = abs(day_revenue - lower_bound) / max(abs(lower_bound), 1.0)
        elif is_high:
            direction = "high"
            reference_value = upper_bound
            deviation_ratio = abs(day_revenue - upper_bound) / max(abs(upper_bound), 1.0)
        else:
            direction = "low" if day_revenue < median_daily_revenue else "high"
            reference_value = median_daily_revenue
            deviation_ratio = abs(day_revenue - median_daily_revenue) / max(abs(median_daily_revenue), 1.0)

        anomaly_sources = []
        if is_low or is_high:
            anomaly_sources.append("statistical")
        if is_ml_anomaly:
            anomaly_sources.append("ml")

        ml_info = ml_anomalies.get(pd.Timestamp(date), {})
        ml_score = ml_info.get("ml_score")

        for _, row in top_products.iterrows():
            product = str(row["product"])
            product_revenue = float(row["revenue"])
            stock = None if pd.isna(row["stock"]) else int(row["stock"])
            share = float(row["share_of_day"])

            severity = _build_severity(deviation_ratio, stock)

            # Si le ML a détecté quelque chose, on ne descend pas trop bas en gravité
            if "ml" in anomaly_sources and severity == "low":
                severity = "medium"

            reason, recommendation = _build_reason_and_recommendation(
                direction=direction,
                product=product,
                stock=stock,
                share=share,
            )

            anomalies.append(
                {
                    "date": str(date.date()),
                    "product": product,
                    "value": product_revenue,
                    "daily_revenue": float(day_revenue),
                    "lower_bound": float(lower_bound),
                    "upper_bound": float(upper_bound),
                    "reference_value": float(reference_value),
                    "severity": severity,
                    "reason": reason,
                    "recommendation": recommendation,
                    "share_of_day_revenue": round(share, 4),
                    "deviation_ratio": round(float(deviation_ratio), 4),
                    "sources": anomaly_sources,
                    "ml_score": None if ml_score is None else round(float(ml_score), 6),
                }
            )

    return {
        "lower_bound": float(lower_bound),
        "upper_bound": float(upper_bound),
        "anomalies": anomalies,
        "ml_enabled": SKLEARN_AVAILABLE,
    }


if __name__ == "__main__":
    # Jeu de données exemple avec anomalies
    data = [
        # Jours normaux
        {"date": "2025-01-01", "product": "Laptop", "price": 1000, "quantity": 1, "stock": 100},
        {"date": "2025-01-01", "product": "Mouse", "price": 20, "quantity": 5, "stock": 50},
        {"date": "2025-01-02", "product": "Laptop", "price": 1000, "quantity": 1, "stock": 95},
        {"date": "2025-01-02", "product": "Keyboard", "price": 50, "quantity": 4, "stock": 30},
        {"date": "2025-01-03", "product": "Laptop", "price": 1000, "quantity": 1, "stock": 90},
        {"date": "2025-01-03", "product": "Mouse", "price": 20, "quantity": 6, "stock": 45},

        # Anomalie basse
        {"date": "2025-01-04", "product": "Mouse", "price": 20, "quantity": 1, "stock": 5},
        {"date": "2025-01-04", "product": "Keyboard", "price": 50, "quantity": 1, "stock": 8},

        # Anomalie haute
        {"date": "2025-01-05", "product": "Laptop", "price": 1000, "quantity": 8, "stock": 70},
        {"date": "2025-01-05", "product": "Mouse", "price": 20, "quantity": 20, "stock": 40},
        {"date": "2025-01-05", "product": "Keyboard", "price": 50, "quantity": 10, "stock": 20},

        # Jour normal
        {"date": "2025-01-06", "product": "Laptop", "price": 1000, "quantity": 1, "stock": 85},
        {"date": "2025-01-06", "product": "Mouse", "price": 20, "quantity": 4, "stock": 38},
    ]

    print(detect_sales_anomalies(data))