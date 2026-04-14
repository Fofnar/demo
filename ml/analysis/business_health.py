import pandas as pd
import numpy as np

# Import du détecteur d'anomalies métier
try:
    from .anomaly_detection import detect_sales_anomalies
except Exception:
    try:
        from analysis.anomaly_detection import detect_sales_anomalies
    except Exception:
        detect_sales_anomalies = None


def _safe_ratio(numerator: float, denominator: float) -> float:
    """
    Calcule un ratio en évitant toute division par zéro.
    """
    if denominator == 0:
        return 0.0
    return float(numerator / denominator)


def _compute_trend_score(daily_revenue: pd.Series) -> tuple[int, str]:
    """
    Calcule un score de tendance à partir d'une régression linéaire simple.

    Retourne :
    - un score sur 30
    - un commentaire synthétique sur la tendance
    """
    if daily_revenue.empty or len(daily_revenue) < 2:
        return 10, "Not enough data to evaluate trend reliably."

    x = np.arange(len(daily_revenue))
    y = daily_revenue.values.astype(float)

    # La pente résume la direction générale de la série
    slope = np.polyfit(x, y, 1)[0]

    if slope > 0:
        return 30, f"Positive trend detected (slope={slope:.2f})."
    if slope < 0:
        return 8, f"Negative trend detected (slope={slope:.2f})."
    return 15, "Stable trend detected."


def _compute_stability_score(daily_revenue: pd.Series) -> tuple[int, str]:
    """
    Calcule un score de stabilité basé sur le coefficient de variation.

    Retourne :
    - un score sur 25
    - un commentaire synthétique sur la volatilité
    """
    if daily_revenue.empty:
        return 0, "No revenue data."

    mean = float(daily_revenue.mean())
    std = float(daily_revenue.std()) if len(daily_revenue) > 1 else 0.0

    if mean == 0:
        return 5, "Revenue too low to evaluate stability."

    cv = abs(std / mean)

    if cv < 0.15:
        return 25, f"Very stable revenue profile (CV={cv:.2f})."
    if cv < 0.30:
        return 18, f"Moderately stable revenue profile (CV={cv:.2f})."
    if cv < 0.50:
        return 10, f"Volatile revenue profile (CV={cv:.2f})."
    return 4, f"Highly volatile revenue profile (CV={cv:.2f})."


def _compute_growth_score(daily_revenue: pd.Series) -> tuple[int, str]:
    """
    Calcule un score de croissance à partir de l'évolution entre le premier et le dernier jour.

    Retourne :
    - un score sur 20
    - un commentaire synthétique sur la croissance
    """
    if daily_revenue.empty or len(daily_revenue) < 2:
        return 5, "Not enough data to evaluate growth."

    first = float(daily_revenue.iloc[0])
    last = float(daily_revenue.iloc[-1])

    growth_rate = _safe_ratio(last - first, first)

    if growth_rate > 0.25:
        return 20, f"Excellent growth detected (+{growth_rate:.1%})."
    if growth_rate > 0.10:
        return 15, f"Good growth detected (+{growth_rate:.1%})."
    if growth_rate >= 0:
        return 10, f"Slow growth or flat performance ({growth_rate:.1%})."
    if growth_rate >= -0.15:
        return 5, f"Minor decline detected ({growth_rate:.1%})."
    return 1, f"Strong decline detected ({growth_rate:.1%})."


def _compute_diversification_score(df: pd.DataFrame) -> tuple[int, str]:
    """
    Mesure la dépendance au produit le plus contributeur.

    Retourne :
    - un score sur 15
    - un commentaire synthétique sur la concentration du chiffre d'affaires
    """
    if df.empty:
        return 0, "No product data available."

    product_revenue = df.groupby("product")["revenue"].sum().sort_values(ascending=False)
    total_revenue = float(product_revenue.sum())

    if total_revenue == 0:
        return 5, "No revenue generated."

    top_share = float(product_revenue.iloc[0] / total_revenue)

    if top_share < 0.35:
        return 15, f"Healthy diversification (top product share={top_share:.1%})."
    if top_share < 0.50:
        return 11, f"Moderate dependence on the top product (share={top_share:.1%})."
    if top_share < 0.70:
        return 6, f"High dependence on one product (share={top_share:.1%})."
    return 2, f"Very high concentration risk (share={top_share:.1%})."


def _compute_anomaly_penalty(anomalies: list[dict]) -> tuple[int, str, int]:
    """
    Transforme les anomalies détectées en pénalité de score.

    Retourne :
    - la pénalité à retirer
    - un commentaire synthétique
    - le nombre d'anomalies prioritaires
    """
    if not anomalies:
        return 0, "No anomaly detected.", 0

    high_priority = 0
    penalty = 0

    for anomaly in anomalies:
        severity = anomaly.get("severity", "low")

        if severity == "critical":
            penalty += 12
            high_priority += 1
        elif severity == "high":
            penalty += 8
            high_priority += 1
        elif severity == "medium":
            penalty += 4
        else:
            penalty += 2

    penalty = min(penalty, 25)

    if high_priority > 0:
        comment = f"{high_priority} high-priority anomaly/anomalies detected."
    else:
        comment = f"{len(anomalies)} anomaly/anomalies detected."

    return penalty, comment, high_priority


def _build_health_label(score: int) -> str:
    """
    Convertit un score numérique en label business lisible.
    """
    if score >= 85:
        return "excellent"
    if score >= 70:
        return "good"
    if score >= 50:
        return "average"
    if score >= 30:
        return "weak"
    return "critical"


def _build_business_comment(
    score: int,
    label: str,
    trend_comment: str,
    stability_comment: str,
    anomaly_comment: str,
) -> str:
    """
    Génère un résumé business global à partir du score et des sous-composants.
    """
    if label == "excellent":
        return (
            f"Business health is excellent ({score}/100). "
            f"{trend_comment} {stability_comment} {anomaly_comment} "
            "The business looks ready to scale."
        )

    if label == "good":
        return (
            f"Business health is good ({score}/100). "
            f"{trend_comment} {stability_comment} {anomaly_comment} "
            "The business is healthy but should keep monitoring stock and conversion."
        )

    if label == "average":
        return (
            f"Business health is average ({score}/100). "
            f"{trend_comment} {stability_comment} {anomaly_comment} "
            "There are solid signals, but some areas need improvement."
        )

    if label == "weak":
        return (
            f"Business health is weak ({score}/100). "
            f"{trend_comment} {stability_comment} {anomaly_comment} "
            "The business needs corrective action on demand, pricing, or stock management."
        )

    return (
        f"Business health is critical ({score}/100). "
        f"{trend_comment} {stability_comment} {anomaly_comment} "
        "Immediate corrective action is required."
    )


def _build_recommendations(
    final_score: int,
    diversification_score: int,
    high_priority_count: int,
    anomalies_count: int,
    health_status: str,
) -> list[str]:
    """
    Construit une liste de recommandations business classées par priorité.
    """
    recommendations = []

    if final_score < 30:
        recommendations.append(
            "Urgent: review pricing, stock availability, and marketing conversion immediately."
        )
    elif final_score < 50:
        recommendations.append(
            "Focus on improving stability, reducing volatility, and strengthening weak products."
        )
    elif final_score < 70:
        recommendations.append(
            "Optimize the best products, reduce concentration risk, and monitor anomaly signals."
        )
    else:
        recommendations.append(
            "Maintain current performance and prepare scaling actions around the strongest products."
        )

    if high_priority_count > 0:
        recommendations.append(
            "High-priority anomalies were detected. Run a fast operational review on stock, traffic, and pricing."
        )

    if diversification_score < 10:
        recommendations.append(
            "Reduce dependency on a single product by improving the performance of secondary products."
        )

    if anomalies_count == 0 and health_status in {"good", "excellent"}:
        recommendations.append(
            "The business profile looks healthy. Focus on growth levers such as upsell, cross-sell, and retention."
        )

    return recommendations


def business_health_score(data):
    """
    Calcule un score global de santé business à partir des ventes.

    Retourne un objet riche, prêt pour le frontend et pour le contrat Spring Boot.
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

    daily_revenue = df.groupby("date")["revenue"].sum().sort_index()

    if daily_revenue.empty:
        return {"error": "no daily revenue available"}

    # Sous-scores principaux
    trend_score, trend_comment = _compute_trend_score(daily_revenue)
    stability_score, stability_comment = _compute_stability_score(daily_revenue)
    growth_score, growth_comment = _compute_growth_score(daily_revenue)
    diversification_score, diversification_comment = _compute_diversification_score(df)

    # Détection d'anomalies métier
    anomalies_report = {"anomalies": []}
    if detect_sales_anomalies is not None:
        try:
            anomalies_report = detect_sales_anomalies(data)
        except Exception:
            anomalies_report = {"anomalies": []}

    anomalies = anomalies_report.get("anomalies", [])

    # Pénalité liée aux anomalies
    anomaly_penalty, anomaly_comment, high_priority_count = _compute_anomaly_penalty(anomalies)

    # Score brut puis score final borné
    raw_score = trend_score + stability_score + growth_score + diversification_score
    final_score = raw_score - anomaly_penalty
    final_score = max(0, min(100, int(round(final_score))))

    health_label = _build_health_label(final_score)

    business_comment = _build_business_comment(
        score=final_score,
        label=health_label,
        trend_comment=trend_comment,
        stability_comment=stability_comment,
        anomaly_comment=anomaly_comment,
    )

    recommendations = _build_recommendations(
        final_score=final_score,
        diversification_score=diversification_score,
        high_priority_count=high_priority_count,
        anomalies_count=len(anomalies),
        health_status=health_label,
    )

    return {
        "health_score": final_score,
        "health_status": health_label,
        "health_breakdown": {
            "trend_score": trend_score,
            "stability_score": stability_score,
            "growth_score": growth_score,
            "diversification_score": diversification_score,
            "anomaly_penalty": anomaly_penalty,
        },
        "health_details": {
            "trend_comment": trend_comment,
            "stability_comment": stability_comment,
            "growth_comment": growth_comment,
            "diversification_comment": diversification_comment,
            "anomaly_comment": anomaly_comment,
        },
        "business_comment": business_comment,
        "recommendations": recommendations,
        "anomalies_count": len(anomalies),
        "high_priority_anomalies": high_priority_count,
    }


if __name__ == "__main__":
    # Jeu de données volontairement contrasté pour pousser l'évaluation du score
    data = [
        # Phase normale
        {"date": "2025-01-01", "product": "Laptop", "price": 1000, "quantity": 1, "stock": 100},
        {"date": "2025-01-01", "product": "Mouse", "price": 20, "quantity": 5, "stock": 50},

        {"date": "2025-01-02", "product": "Laptop", "price": 1000, "quantity": 1, "stock": 98},
        {"date": "2025-01-02", "product": "Keyboard", "price": 50, "quantity": 4, "stock": 40},

        {"date": "2025-01-03", "product": "Laptop", "price": 1000, "quantity": 1, "stock": 96},
        {"date": "2025-01-03", "product": "Mouse", "price": 20, "quantity": 6, "stock": 48},

        {"date": "2025-01-04", "product": "Laptop", "price": 1000, "quantity": 1, "stock": 95},
        {"date": "2025-01-04", "product": "Keyboard", "price": 50, "quantity": 5, "stock": 38},

        {"date": "2025-01-05", "product": "Laptop", "price": 1000, "quantity": 1, "stock": 94},
        {"date": "2025-01-05", "product": "Mouse", "price": 20, "quantity": 5, "stock": 45},

        {"date": "2025-01-06", "product": "Laptop", "price": 1000, "quantity": 2, "stock": 90},
        {"date": "2025-01-06", "product": "Keyboard", "price": 50, "quantity": 4, "stock": 36},

        {"date": "2025-01-07", "product": "Laptop", "price": 1000, "quantity": 1, "stock": 88},
        {"date": "2025-01-07", "product": "Mouse", "price": 20, "quantity": 6, "stock": 44},

        {"date": "2025-01-08", "product": "Laptop", "price": 1000, "quantity": 1, "stock": 87},
        {"date": "2025-01-08", "product": "Keyboard", "price": 50, "quantity": 5, "stock": 35},

        # Anomalie basse
        {"date": "2025-01-09", "product": "Mouse", "price": 20, "quantity": 1, "stock": 5},
        {"date": "2025-01-09", "product": "Keyboard", "price": 50, "quantity": 1, "stock": 8},

        # Retour à la normale
        {"date": "2025-01-10", "product": "Laptop", "price": 1000, "quantity": 1, "stock": 85},
        {"date": "2025-01-10", "product": "Mouse", "price": 20, "quantity": 4, "stock": 42},

        # Anomalie haute
        {"date": "2025-01-11", "product": "Laptop", "price": 1000, "quantity": 9, "stock": 70},
        {"date": "2025-01-11", "product": "Mouse", "price": 20, "quantity": 20, "stock": 40},
        {"date": "2025-01-11", "product": "Keyboard", "price": 50, "quantity": 15, "stock": 25},

        # Retour à la normale
        {"date": "2025-01-12", "product": "Laptop", "price": 1000, "quantity": 1, "stock": 84},
        {"date": "2025-01-12", "product": "Mouse", "price": 20, "quantity": 5, "stock": 41},
    ]

    print(business_health_score(data))