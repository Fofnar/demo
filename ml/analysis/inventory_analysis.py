import pandas as pd

# Import du moteur d'anomalies métier, utilisé pour enrichir le contexte produit
try:
    from .anomaly_detection import detect_sales_anomalies
except Exception:
    try:
        from analysis.anomaly_detection import detect_sales_anomalies
    except Exception:
        detect_sales_anomalies = None


def _stock_level(stock: int) -> str:
    """
    Classe le niveau de stock selon le volume restant.
    """
    if stock <= 0:
        return "out_of_stock"
    if stock <= 3:
        return "critical"
    if stock <= 10:
        return "low"
    if stock <= 20:
        return "medium"
    return "healthy"


def _build_inventory_recommendation(product: str, stock: int, stock_level: str) -> str:
    """
    Génère une recommandation métier pour un produit donné.
    """
    if stock_level == "out_of_stock":
        return f"{product}: out of stock. Restock immediately to avoid lost sales."
    if stock_level == "critical":
        return f"{product}: critical stock level ({stock}). Replenish urgently."
    if stock_level == "low":
        return f"{product}: low stock level ({stock}). Plan restocking soon."
    if stock_level == "medium":
        return f"{product}: moderate stock level ({stock}). Monitor demand and plan replenishment."
    return f"{product}: healthy stock level ({stock}). Keep monitoring regularly."


def _build_inventory_comment(low_count: int, critical_count: int, out_count: int) -> str:
    """
    Génère un résumé business de l'état du stock.
    """
    if out_count > 0:
        return (
            f"Inventory is critical. {out_count} product(s) are already out of stock and "
            f"{critical_count} more are at critical level."
        )

    if critical_count > 0:
        return (
            f"Inventory is under pressure. {critical_count} product(s) are at critical stock level "
            f"and {low_count} more are low."
        )

    if low_count > 0:
        return (
            f"Inventory is acceptable but needs attention. {low_count} product(s) are low in stock."
        )

    return "Inventory levels look healthy across tracked products."


def _build_business_comment(total_products: int, low_count: int, critical_count: int, out_count: int) -> str:
    """
    Génère un commentaire global lisible sur l'état du stock.
    """
    if out_count > 0:
        return (
            f"Stock health is critical across {total_products} product(s). "
            f"Immediate replenishment is required for out-of-stock items."
        )

    if critical_count > 0:
        return (
            f"Stock health is under pressure across {total_products} product(s). "
            f"Critical items should be prioritized for replenishment."
        )

    if low_count > 0:
        return (
            f"Stock health is acceptable across {total_products} product(s), "
            f"but some items are approaching reorder thresholds."
        )

    return f"Stock health is stable across {total_products} product(s)."


def _build_overall_recommendations(
    low_count: int,
    critical_count: int,
    out_count: int,
    any_upstream_anomaly: bool,
) -> list[str]:
    """
    Génère des recommandations globales à partir de l'état du stock.
    """
    recommendations = []

    if out_count > 0:
        recommendations.append("Immediate action required: restock out-of-stock products first.")
    elif critical_count > 0:
        recommendations.append("Critical inventory pressure detected. Prioritize replenishment planning.")
    elif low_count > 0:
        recommendations.append("Some products are low in stock. Review reorder points and lead times.")
    else:
        recommendations.append("Inventory is healthy. Keep current replenishment rhythm and monitor demand.")

    if any_upstream_anomaly:
        recommendations.append(
            "Related anomalies were detected upstream. Review pricing, demand shifts, and product visibility."
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


def analyze_inventory(data):
    """
    Analyse de stock enrichie.
    Retourne une vue business utile pour le frontend et pour le backend Spring.
    """
    if not data:
        return {"error": "no sales data"}

    df = pd.DataFrame(data)

    required_columns = {"date", "product", "price", "quantity", "stock"}
    missing = required_columns - set(df.columns)
    if missing:
        return {"error": f"missing columns: {sorted(missing)}"}

    df["date"] = pd.to_datetime(df["date"], errors="coerce")
    df = df.dropna(subset=["date", "product", "stock"])

    if df.empty:
        return {"error": "no valid sales data"}

    df["stock"] = pd.to_numeric(df["stock"], errors="coerce").fillna(0)
    df = df.sort_values("date")

    # Dernier stock connu par produit
    stock_by_product = df.groupby("product")["stock"].last().sort_values()

    # Anomalies métier éventuelles, utilisées uniquement pour enrichir le contexte
    anomalies = []
    if detect_sales_anomalies is not None:
        try:
            anomalies_report = detect_sales_anomalies(data)
            anomalies = anomalies_report.get("anomalies", [])
        except Exception:
            anomalies = []

    anomaly_products = {a.get("product") for a in anomalies if a.get("product")}
    anomaly_by_product = _summarize_anomalies_by_product(anomalies)

    result = []
    low_count = 0
    critical_count = 0
    out_count = 0

    for product, stock in stock_by_product.items():
        stock_int = int(stock)
        level = _stock_level(stock_int)

        if level == "low":
            low_count += 1
        elif level == "critical":
            critical_count += 1
        elif level == "out_of_stock":
            out_count += 1

        result.append(
            {
                "product": product,
                "stock": stock_int,
                "stock_level": level,
                "warning": "Low stock" if level in {"low", "critical", "out_of_stock"} else "OK",
                "recommendation": _build_inventory_recommendation(product, stock_int, level),
                "has_related_anomaly": product in anomaly_products,
                "related_anomaly_count": anomaly_by_product.get(product, {}).get("count", 0),
                "related_anomaly_severity": anomaly_by_product.get(product, {}).get("highest_severity", None),
            }
        )

    # Trier les alertes les plus urgentes en premier
    level_order = {
        "out_of_stock": 0,
        "critical": 1,
        "low": 2,
        "medium": 3,
        "healthy": 4,
    }
    result = sorted(
        result,
        key=lambda x: level_order.get(x["stock_level"], 99)
    )

    inventory_comment = _build_inventory_comment(low_count, critical_count, out_count)
    business_comment = _build_business_comment(
        total_products=len(result),
        low_count=low_count,
        critical_count=critical_count,
        out_count=out_count,
    )

    recommendations = _build_overall_recommendations(
        low_count=low_count,
        critical_count=critical_count,
        out_count=out_count,
        any_upstream_anomaly=len(anomalies) > 0,
    )

    return {
        "total_products": len(result),
        "low_stock_count": low_count,
        "critical_stock_count": critical_count,
        "out_of_stock_count": out_count,
        "inventory_comment": inventory_comment,
        "business_comment": business_comment,
        "low_stock_alerts": result,
        "recommendations": recommendations,
    }


if __name__ == "__main__":
    # Jeu de données de test simple avec plusieurs niveaux de stock
    data = [
        {"date": "2025-01-01", "product": "Laptop", "price": 1000, "quantity": 1, "stock": 100},
        {"date": "2025-01-02", "product": "Mouse", "price": 20, "quantity": 10, "stock": 5},
        {"date": "2025-01-03", "product": "Laptop", "price": 1000, "quantity": 2, "stock": 50},
        {"date": "2025-01-04", "product": "Keyboard", "price": 50, "quantity": 5, "stock": 9},
        {"date": "2025-01-05", "product": "Laptop", "price": 1000, "quantity": 1, "stock": 70},
    ]

    print(analyze_inventory(data))