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


def _compute_sales_trend(product_daily: pd.Series) -> str:
    """
    Détecte la tendance de demande d'un produit à partir de ses ventes journalières.

    Retourne :
    - upward
    - downward
    - stable
    """
    if product_daily.empty or len(product_daily) < 2:
        return "stable"

    x = np.arange(len(product_daily))
    y = product_daily.values.astype(float)

    # La pente de la régression résume la direction globale de la demande
    slope = np.polyfit(x, y, 1)[0]

    if slope > 0.05:
        return "upward"
    if slope < -0.05:
        return "downward"
    return "stable"


def _risk_level(days_left: float | None, current_stock: int, trend: str) -> str:
    """
    Classe le niveau de risque stock en combinant couverture et tendance.
    """
    if current_stock <= 0:
        return "critical"

    if days_left is None:
        return "unknown"

    if days_left < 3:
        return "critical"
    if days_left < 7:
        return "high"
    if days_left < 14:
        return "medium"

    # Une demande orientée à la hausse conserve une vigilance renforcée
    if trend == "upward" and days_left < 21:
        return "medium"

    return "low"


def _build_stock_recommendation(
    product: str,
    current_stock: int,
    recent_avg_sales: float,
    days_left: float | None,
    trend: str,
    risk: str,
    target_days: int,
) -> str:
    """
    Génère une recommandation business claire et actionnable.
    """
    if current_stock <= 0:
        return f"{product}: no stock left. Restock immediately to avoid lost sales."

    if days_left is None:
        return (
            f"{product}: demand signal is too weak or inconsistent to estimate stockout reliably. "
            f"Monitor sales closely."
        )

    if risk == "critical":
        return (
            f"{product}: critical stock risk. At the current pace ({recent_avg_sales:.2f} units/day), "
            f"stockout is expected in about {days_left:.1f} days. Restock immediately."
        )

    if risk == "high":
        return (
            f"{product}: high stock risk. Estimated coverage is only {days_left:.1f} days. "
            f"Prepare a restock order and review demand acceleration."
        )

    if risk == "medium":
        return (
            f"{product}: moderate stock risk. Coverage is around {days_left:.1f} days. "
            f"Monitor sales and plan replenishment before the situation becomes urgent."
        )

    if trend == "upward":
        return (
            f"{product}: stock looks healthy today, but demand is rising. "
            f"Keep at least {target_days} days of coverage and consider increasing safety stock."
        )

    return (
        f"{product}: stock level is healthy. "
        f"Current coverage is around {days_left:.1f} days, which is acceptable for now."
    )


def _build_business_comment(
    top_risk: str,
    critical_count: int,
    high_count: int,
    upward_count: int,
) -> str:
    """
    Génère un résumé business global à partir des risques produits.
    """
    if critical_count > 0:
        return (
            f"Business stock health is critical. {critical_count} product(s) are at immediate risk of stockout. "
            f"Priority should be given to replenishment, demand monitoring, and supply coordination."
        )

    if high_count > 0:
        return (
            f"Business stock health is under pressure. {high_count} product(s) have a high stock risk. "
            f"Replenishment planning should be accelerated."
        )

    if upward_count > 0:
        return (
            f"Business stock health is acceptable, but demand is rising on {upward_count} product(s). "
            f"Safety stock should be reviewed to avoid future shortages."
        )

    if top_risk == "unknown":
        return (
            "Business stock health cannot be fully assessed for some products because demand signals are too weak. "
            "Additional sales history is needed."
        )

    return (
        "Business stock health is stable. Coverage remains acceptable across the tracked products, "
        "with no immediate replenishment pressure."
    )


def _build_overall_recommendations(
    stock_prediction: list[dict],
    anomalies_count: int,
    high_priority_count: int,
) -> list[str]:
    """
    Génère une liste de recommandations globales à partir des prédictions de stock.
    """
    recommendations = []

    critical_items = [item for item in stock_prediction if item.get("risk_level") == "critical"]
    high_items = [item for item in stock_prediction if item.get("risk_level") == "high"]
    upward_items = [item for item in stock_prediction if item.get("sales_trend") == "upward"]

    if critical_items:
        recommendations.append(
            "Immediate action required: replenish the critical products first to prevent lost sales."
        )
    elif high_items:
        recommendations.append(
            "High-priority replenishment planning is needed for products close to stockout."
        )
    elif upward_items:
        recommendations.append(
            "Demand is increasing on several products. Review safety stock and restocking cadence."
        )
    else:
        recommendations.append(
            "Stock levels look controlled. Continue monitoring demand and coverage regularly."
        )

    if high_priority_count > 0:
        recommendations.append(
            "High-priority anomalies were detected. Review pricing, traffic, and inventory consistency."
        )

    if anomalies_count > 0:
        recommendations.append(
            "Anomalies and stock risks are correlated on several products. Operational review is recommended."
        )

    if len(stock_prediction) > 0:
        low_diversification = [
            item for item in stock_prediction if item.get("risk_level") in {"critical", "high"}
        ]
        if len(low_diversification) >= max(1, len(stock_prediction) // 2):
            recommendations.append(
                "Many products are under stock pressure at the same time. Supplier coordination should be prioritized."
            )

    return recommendations


def _summarize_anomalies_by_product(anomalies: list[dict]) -> dict:
    """
    Agrège les anomalies par produit pour enrichir la lecture business.
    """
    summary = {}

    severity_order = {
        "low": 1,
        "medium": 2,
        "high": 3,
        "critical": 4,
    }

    for anomaly in anomalies:
        product = anomaly.get("product")
        if not product:
            continue

        severity = anomaly.get("severity", "low")

        if product not in summary:
            summary[product] = {
                "count": 0,
                "highest_severity": severity,
            }

        summary[product]["count"] += 1

        current_rank = severity_order.get(summary[product]["highest_severity"], 1)
        new_rank = severity_order.get(severity, 1)

        if new_rank > current_rank:
            summary[product]["highest_severity"] = severity

    return summary


def predict_stockout(data):
    """
    Prévoit la rupture de stock par produit à partir des ventes.
    Retourne une structure enrichie, exploitable côté frontend et côté Spring.
    """
    if not data:
        return {"error": "no sales data"}

    df = pd.DataFrame(data)

    required_columns = {"date", "product", "price", "quantity", "stock"}
    missing = required_columns - set(df.columns)
    if missing:
        return {"error": f"missing columns: {sorted(missing)}"}

    df["date"] = pd.to_datetime(df["date"], errors="coerce")
    df = df.dropna(subset=["date", "product", "quantity", "stock"])

    if df.empty:
        return {"error": "no valid sales data"}

    df["quantity"] = pd.to_numeric(df["quantity"], errors="coerce").fillna(0)
    df["stock"] = pd.to_numeric(df["stock"], errors="coerce").fillna(0)
    df = df.dropna(subset=["quantity", "stock"])

    # Agrégation journalière par produit
    product_daily = (
        df.groupby(["product", "date"], as_index=False)
        .agg(
            daily_quantity=("quantity", "sum"),
            daily_stock=("stock", "last"),
        )
        .sort_values(["product", "date"])
    )

    # Préparation des anomalies métier
    anomalies_report = {"anomalies": []}
    if detect_sales_anomalies is not None:
        try:
            anomalies_report = detect_sales_anomalies(data)
        except Exception:
            anomalies_report = {"anomalies": []}

    anomalies = anomalies_report.get("anomalies", [])
    anomaly_products = {a.get("product") for a in anomalies if a.get("product")}
    anomaly_by_product = _summarize_anomalies_by_product(anomalies)

    result = []
    target_days = 14

    products = product_daily["product"].unique()

    for product in products:
        product_df = product_daily[product_daily["product"] == product].copy()

        if product_df.empty:
            continue

        # Dernier stock observé
        current_stock = int(product_df["daily_stock"].iloc[-1])

        # Moyenne globale des ventes journalières
        avg_sales = float(product_df["daily_quantity"].mean())

        # Moyenne récente lissée sur les 3 derniers jours disponibles
        recent_window = product_df["daily_quantity"].tail(3)
        recent_avg_sales = float(recent_window.mean()) if not recent_window.empty else avg_sales

        # Tendance de la demande
        trend = _compute_sales_trend(product_df["daily_quantity"])

        # Estimation des jours avant rupture
        if recent_avg_sales <= 0:
            days_left = None
        else:
            days_left = float(current_stock / recent_avg_sales)

        risk = _risk_level(days_left, current_stock, trend)

        # Quantité recommandée pour couvrir 14 jours
        recommended_restock_quantity = int(
            max(0, np.ceil(recent_avg_sales * target_days - current_stock))
        )

        recommendation = _build_stock_recommendation(
            product=product,
            current_stock=current_stock,
            recent_avg_sales=recent_avg_sales,
            days_left=days_left,
            trend=trend,
            risk=risk,
            target_days=target_days,
        )

        result.append(
            {
                "product": product,
                "current_stock": current_stock,
                "avg_daily_sales": round(avg_sales, 2),
                "recent_avg_daily_sales": round(recent_avg_sales, 2),
                "sales_trend": trend,
                "estimated_days_before_stockout": None if days_left is None else round(days_left, 2),
                "risk_level": risk,
                "recommended_restock_quantity": recommended_restock_quantity,
                "recommendation": recommendation,
                "has_related_anomaly": product in anomaly_products,
                "related_anomaly_count": anomaly_by_product.get(product, {}).get("count", 0),
                "related_anomaly_severity": anomaly_by_product.get(product, {}).get("highest_severity", None),
            }
        )

    # Tri par priorité business
    risk_order = {"critical": 0, "high": 1, "medium": 2, "low": 3, "unknown": 4}
    result = sorted(
        result,
        key=lambda x: (
            risk_order.get(x["risk_level"], 99),
            x["estimated_days_before_stockout"] is None,
            x["estimated_days_before_stockout"] or 9999,
        ),
    )

    critical_count = sum(1 for item in result if item["risk_level"] == "critical")
    high_count = sum(1 for item in result if item["risk_level"] == "high")
    upward_count = sum(1 for item in result if item["sales_trend"] == "upward")
    top_risk = result[0]["risk_level"] if result else "unknown"

    business_comment = _build_business_comment(
        top_risk=top_risk,
        critical_count=critical_count,
        high_count=high_count,
        upward_count=upward_count,
    )

    recommendations = _build_overall_recommendations(
        stock_prediction=result,
        anomalies_count=len(anomalies),
        high_priority_count=sum(
            1 for a in anomalies if a.get("severity") in {"high", "critical"}
        ),
    )

    return {
        "coverage_target_days": target_days,
        "stock_prediction": result,
        "business_comment": business_comment,
        "recommendations": recommendations,
        "anomalies_count": len(anomalies),
        "high_priority_anomalies": sum(
            1 for a in anomalies if a.get("severity") in {"high", "critical"}
        ),
        "anomalies_report": anomalies_report,
    }


def stock_recommendations(stock_predictions):
    """
    Transforme les prédictions de stock en recommandations business.
    """
    if not stock_predictions or "stock_prediction" not in stock_predictions:
        return {"error": "invalid stock predictions"}

    recommendations = []
    critical_count = 0
    high_count = 0

    for item in stock_predictions["stock_prediction"]:
        product = item.get("product", "unknown product")
        risk_level = item.get("risk_level", "unknown")
        recommendation_text = item.get("recommendation", "")

        if risk_level == "critical":
            critical_count += 1
        elif risk_level == "high":
            high_count += 1

        recommendations.append(
            {
                "product": product,
                "risk_level": risk_level,
                "message": recommendation_text,
                "recommended_restock_quantity": item.get("recommended_restock_quantity", 0),
                "estimated_days_before_stockout": item.get("estimated_days_before_stockout"),
                "has_related_anomaly": item.get("has_related_anomaly", False),
                "related_anomaly_count": item.get("related_anomaly_count", 0),
                "related_anomaly_severity": item.get("related_anomaly_severity", None),
            }
        )

    executive_note = (
        "Critical stock alerts require immediate action."
        if critical_count > 0
        else "High stock alerts should be planned quickly."
        if high_count > 0
        else "Stock levels are under control, but continue monitoring demand."
    )

    return {
        "critical_alerts": critical_count,
        "high_alerts": high_count,
        "executive_note": executive_note,
        "business_comment": stock_predictions.get("business_comment", ""),
        "recommendations": stock_predictions.get("recommendations", []),
        "anomalies_count": stock_predictions.get("anomalies_count", 0),
        "high_priority_anomalies": stock_predictions.get("high_priority_anomalies", 0),
        "anomalies_report": stock_predictions.get("anomalies_report", {}),
        "stock_recommendations": recommendations,
    }


if __name__ == "__main__":
    # Jeu de données de test réaliste avec plusieurs profils de demande
    data = [
        # Phase stable
        {"date": "2025-01-01", "product": "Laptop", "price": 1000, "quantity": 3, "stock": 120},
        {"date": "2025-01-01", "product": "Mouse", "price": 20, "quantity": 15, "stock": 80},
        {"date": "2025-01-01", "product": "Keyboard", "price": 50, "quantity": 8, "stock": 60},

        {"date": "2025-01-02", "product": "Laptop", "price": 1000, "quantity": 4, "stock": 116},
        {"date": "2025-01-02", "product": "Mouse", "price": 20, "quantity": 14, "stock": 76},
        {"date": "2025-01-02", "product": "Keyboard", "price": 50, "quantity": 7, "stock": 56},

        {"date": "2025-01-03", "product": "Laptop", "price": 1000, "quantity": 4, "stock": 112},
        {"date": "2025-01-03", "product": "Mouse", "price": 20, "quantity": 16, "stock": 72},
        {"date": "2025-01-03", "product": "Keyboard", "price": 50, "quantity": 8, "stock": 52},

        {"date": "2025-01-04", "product": "Laptop", "price": 1000, "quantity": 5, "stock": 107},
        {"date": "2025-01-04", "product": "Mouse", "price": 20, "quantity": 15, "stock": 68},
        {"date": "2025-01-04", "product": "Keyboard", "price": 50, "quantity": 7, "stock": 48},

        {"date": "2025-01-05", "product": "Laptop", "price": 1000, "quantity": 4, "stock": 103},
        {"date": "2025-01-05", "product": "Mouse", "price": 20, "quantity": 15, "stock": 64},
        {"date": "2025-01-05", "product": "Keyboard", "price": 50, "quantity": 8, "stock": 44},

        # Accélération de la demande
        {"date": "2025-01-06", "product": "Laptop", "price": 1000, "quantity": 9, "stock": 94},
        {"date": "2025-01-06", "product": "Mouse", "price": 20, "quantity": 16, "stock": 60},
        {"date": "2025-01-06", "product": "Keyboard", "price": 50, "quantity": 8, "stock": 40},

        {"date": "2025-01-07", "product": "Laptop", "price": 1000, "quantity": 10, "stock": 84},
        {"date": "2025-01-07", "product": "Mouse", "price": 20, "quantity": 14, "stock": 56},
        {"date": "2025-01-07", "product": "Keyboard", "price": 50, "quantity": 7, "stock": 36},

        {"date": "2025-01-08", "product": "Laptop", "price": 1000, "quantity": 11, "stock": 73},
        {"date": "2025-01-08", "product": "Mouse", "price": 20, "quantity": 15, "stock": 52},
        {"date": "2025-01-08", "product": "Keyboard", "price": 50, "quantity": 8, "stock": 32},

        # Anomalie basse
        {"date": "2025-01-09", "product": "Laptop", "price": 1000, "quantity": 5, "stock": 68},
        {"date": "2025-01-09", "product": "Mouse", "price": 20, "quantity": 1, "stock": 6},
        {"date": "2025-01-09", "product": "Keyboard", "price": 50, "quantity": 1, "stock": 7},

        {"date": "2025-01-10", "product": "Laptop", "price": 1000, "quantity": 6, "stock": 62},
        {"date": "2025-01-10", "product": "Mouse", "price": 20, "quantity": 2, "stock": 5},
        {"date": "2025-01-10", "product": "Keyboard", "price": 50, "quantity": 1, "stock": 6},

        # Anomalie haute
        {"date": "2025-01-11", "product": "Laptop", "price": 1000, "quantity": 18, "stock": 44},
        {"date": "2025-01-11", "product": "Mouse", "price": 20, "quantity": 22, "stock": 30},
        {"date": "2025-01-11", "product": "Keyboard", "price": 50, "quantity": 14, "stock": 18},

        {"date": "2025-01-12", "product": "Laptop", "price": 1000, "quantity": 2, "stock": 42},
        {"date": "2025-01-12", "product": "Mouse", "price": 20, "quantity": 15, "stock": 25},
        {"date": "2025-01-12", "product": "Keyboard", "price": 50, "quantity": 7, "stock": 12},

        # Retour à un rythme plus normal
        {"date": "2025-01-13", "product": "Laptop", "price": 1000, "quantity": 4, "stock": 38},
        {"date": "2025-01-13", "product": "Mouse", "price": 20, "quantity": 14, "stock": 21},
        {"date": "2025-01-13", "product": "Keyboard", "price": 50, "quantity": 8, "stock": 8},

        {"date": "2025-01-14", "product": "Laptop", "price": 1000, "quantity": 5, "stock": 33},
        {"date": "2025-01-14", "product": "Mouse", "price": 20, "quantity": 13, "stock": 18},
        {"date": "2025-01-14", "product": "Keyboard", "price": 50, "quantity": 7, "stock": 1},
    ]

    stock_prediction = predict_stockout(data)

    print(stock_prediction)
    print(stock_recommendations(stock_prediction))