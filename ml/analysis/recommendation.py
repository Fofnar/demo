import pandas as pd
import numpy as np

# Import du moteur d'anomalies, utilisé comme source unique de vérité
try:
    from .anomaly_detection import detect_sales_anomalies
except ImportError:
    from analysis.anomaly_detection import detect_sales_anomalies

# Import optionnel de Prophet pour le forecast
try:
    from prophet import Prophet
    PROPHET_AVAILABLE = True
except Exception:
    Prophet = None
    PROPHET_AVAILABLE = False


def _detect_trend(daily_revenue: pd.Series) -> tuple[str, float, float]:
    """
    Détecte la tendance globale des revenus.

    Retourne :
    - trend : upward / downward / stable
    - slope : pente de la régression linéaire
    - change_rate : variation relative entre le début et la fin
    """
    if daily_revenue.empty:
        return "stable", 0.0, 0.0

    values = daily_revenue.values.astype(float)

    if len(values) == 1:
        return "stable", 0.0, 0.0

    x = np.arange(len(values))
    slope, _ = np.polyfit(x, values, 1)

    first = float(values[0])
    last = float(values[-1])

    if first == 0:
        change_rate = 1.0 if last > 0 else 0.0
    else:
        change_rate = (last - first) / first

    if slope > 0:
        return "upward", float(slope), float(change_rate)
    if slope < 0:
        return "downward", float(slope), float(change_rate)
    return "stable", float(slope), float(change_rate)


def _build_trend_recommendation(trend: str, change_rate: float) -> str:
    """
    Génère une recommandation selon la tendance globale.
    """
    if trend == "upward":
        if change_rate > 0.25:
            return f"Strong growth (+{change_rate:.1%}). Scale inventory and marketing."
        return f"Growth observed (+{change_rate:.1%}). Maintain momentum."

    if trend == "downward":
        if change_rate < -0.25:
            return f"Sharp decline ({change_rate:.1%}). Urgent action required."
        return f"Decline detected ({change_rate:.1%}). Investigate demand and pricing."

    return "Stable performance. Focus on optimization and growth levers."


def _build_product_recommendation(product, quantity, revenue, stock):
    """
    Génère une recommandation ciblée pour un produit.
    """
    if stock is not None and stock <= 10:
        return f"{product}: high demand but low stock ({stock}). Restock urgently."

    if quantity >= 5:
        return f"{product}: strong volume ({quantity:.0f} units). Promote more."

    if revenue >= 1000:
        return f"{product}: strong revenue driver ({revenue:.2f}). Optimize margins."

    return f"{product}: moderate performance. Optimize pricing and visibility."


def _build_anomaly_recommendation(anomaly: dict) -> str:
    """
    Transforme une anomalie déjà enrichie en recommandation business lisible.
    """
    return (
        f"[{anomaly.get('severity', 'low').upper()}] "
        f"{anomaly.get('product', 'unknown')} on {anomaly.get('date', '?')} → "
        f"{anomaly.get('reason', 'No reason')} | "
        f"Action: {anomaly.get('recommendation', 'Check manually')}"
    )


def _build_forecast(daily_revenue: pd.Series, periods: int = 7) -> dict:
    """
    Produit un forecast sur plusieurs jours.

    Prophet est utilisé en priorité lorsque disponible.
    Un fallback linéaire est utilisé sinon.
    """
    if daily_revenue.empty:
        return {"available": False, "method": "none", "forecast": []}

    df = daily_revenue.reset_index()
    df.columns = ["ds", "y"]

    # Forecast avec Prophet
    if PROPHET_AVAILABLE and len(df) >= 6:
        try:
            model = Prophet(daily_seasonality=True)
            model.fit(df)

            future = model.make_future_dataframe(periods=periods)
            forecast = model.predict(future).tail(periods)

            return {
                "available": True,
                "method": "prophet",
                "forecast": [
                    {
                        "date": row["ds"].strftime("%Y-%m-%d"),
                        "yhat": float(row["yhat"]),
                    }
                    for _, row in forecast.iterrows()
                ],
            }
        except Exception:
            pass

    # Fallback linéaire
    values = daily_revenue.values.astype(float)

    if len(values) < 2:
        return {"available": False, "method": "insufficient_data", "forecast": []}

    x = np.arange(len(values))
    slope, intercept = np.polyfit(x, values, 1)

    future_dates = pd.date_range(
        start=daily_revenue.index[-1] + pd.Timedelta(days=1),
        periods=periods,
    )

    forecast = []
    for i, date in enumerate(future_dates, start=len(values)):
        yhat = max(slope * i + intercept, 0.0)
        forecast.append(
            {
                "date": date.strftime("%Y-%m-%d"),
                "yhat": float(yhat),
            }
        )

    return {"available": True, "method": "linear", "forecast": forecast}


def generate_recommendations(data):
    """
    Génère des recommandations business à partir des ventes.

    Retourne :
    - tendances
    - produits clés
    - anomalies transformées en actions
    - résumé exécutif
    - forecast
    """
    if not data:
        return {"error": "no sales data"}

    df = pd.DataFrame(data)

    required = {"date", "product", "price", "quantity", "stock"}
    if missing := (required - set(df.columns)):
        return {"error": f"missing columns: {sorted(missing)}"}

    df["date"] = pd.to_datetime(df["date"], errors="coerce")
    df = df.dropna(subset=["date", "product", "price", "quantity"])

    if df.empty:
        return {"error": "no valid sales data"}

    df["revenue"] = df["price"] * df["quantity"]

    # Produits les plus visibles dans les ventes
    product_quantity = df.groupby("product")["quantity"].sum()
    product_revenue = df.groupby("product")["revenue"].sum()

    top_selling = product_quantity.idxmax()
    top_revenue = product_revenue.idxmax()

    # Tendance globale des revenus
    daily_revenue = df.groupby("date")["revenue"].sum().sort_index()
    trend, slope, change_rate = _detect_trend(daily_revenue)

    # Forecast
    forecast = _build_forecast(daily_revenue)

    # Anomalies détectées par le moteur dédié
    anomalies_report = detect_sales_anomalies(data)
    anomalies = anomalies_report.get("anomalies", [])

    high_priority = sum(
        1 for a in anomalies if a.get("severity") in {"high", "critical"}
    )

    # Structure des recommandations
    recos = {
        "product_insights": [],
        "trend_insights": [],
        "anomaly_insights": [],
    }

    # Recommandation sur le produit le plus vendu
    top_product_stock_series = (
        df[df["product"] == top_selling]
        .sort_values("date")["stock"]
    )
    top_product_stock = (
        top_product_stock_series.iloc[-1]
        if not top_product_stock_series.empty
        else None
    )
    top_product_stock = None if pd.isna(top_product_stock) else int(top_product_stock)

    recos["product_insights"].append(
        _build_product_recommendation(
            top_selling,
            float(product_quantity[top_selling]),
            float(product_revenue[top_selling]),
            top_product_stock,
        )
    )

    # Recommandation sur le produit le plus rentable
    if top_revenue != top_selling:
        recos["product_insights"].append(
            f"{top_revenue}: highest revenue generator with {top_revenue_value(product_revenue, top_revenue):.2f} in sales. "
            "Prioritize it in commercial strategy, featured placement, and margin monitoring."
        )

    # Recommandation liée à la tendance globale
    recos["trend_insights"].append(
        _build_trend_recommendation(trend, change_rate)
    )

    # Recommandations liées aux anomalies
    if anomalies:
        for anomaly in anomalies:
            recos["anomaly_insights"].append(
                _build_anomaly_recommendation(anomaly)
            )

        if high_priority > 0:
            recos["anomaly_insights"].append(
                f"{high_priority} high-priority anomaly/anomalies detected. "
                "Schedule a rapid business review for pricing, stock, and traffic issues."
            )
    else:
        recos["anomaly_insights"].append(
            "No major anomaly detected. The business flow looks relatively healthy and stable."
        )

    # Résumé exécutif
    summary = (
        f"Trend: {trend}. "
        f"{top_revenue} drives most revenue. "
        f"{len(anomalies)} anomalies detected, including {high_priority} high-priority cases. "
    )

    if forecast["available"] and forecast["forecast"]:
        avg = np.mean([f["yhat"] for f in forecast["forecast"]])
        summary += f"Forecast avg: {avg:.2f}. "

    summary += "Focus on stock, pricing, and growth optimization."

    return {
        "trend": trend,
        "trend_slope": slope,
        "trend_change_rate": change_rate,
        "top_selling_product": top_selling,
        "top_revenue_product": top_revenue,
        "recommendations": recos,
        "executive_summary": summary,
        "anomalies_count": len(anomalies),
        "high_priority_anomalies": high_priority,
        "forecast": forecast,
        "anomalies_report": anomalies_report,
    }


def top_revenue_value(product_revenue: pd.Series, product_name: str) -> float:
    """
    Retourne le revenu total d'un produit donné.
    """
    return float(product_revenue[product_name])


if __name__ == "__main__":
    # Jeu de données exemple avec une anomalie basse et une anomalie haute
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

    print(generate_recommendations(data))