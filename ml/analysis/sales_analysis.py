import pandas as pd
import numpy as np


def _safe_ratio(numerator: float, denominator: float) -> float:
    """
    Évite les divisions par zéro.
    """
    if denominator == 0:
        return 0.0
    return float(numerator / denominator)


def _detect_sales_trend(daily_revenue: pd.Series) -> tuple[str, float]:
    """
    Détecte une tendance simple à partir d'une régression linéaire.
    Retourne :
    - trend: upward / downward / stable
    - slope: pente de la tendance
    """
    if daily_revenue.empty or len(daily_revenue) < 2:
        return "stable", 0.0

    x = np.arange(len(daily_revenue))
    y = daily_revenue.values.astype(float)

    slope = float(np.polyfit(x, y, 1)[0])

    if slope > 0.05:
        return "upward", slope
    if slope < -0.05:
        return "downward", slope
    return "stable", slope


def _build_sales_comment(
    trend: str,
    total_revenue: float,
    top_revenue_product: str,
    top_selling_product: str,
) -> str:
    """
    Produit un résumé business lisible.
    """
    if trend == "upward":
        return (
            f"Revenue is trending upward. {top_revenue_product} is driving the strongest value, "
            f"while {top_selling_product} leads in quantity. The business is showing growth momentum."
        )

    if trend == "downward":
        return (
            f"Revenue is trending downward. {top_revenue_product} still contributes the most value, "
            f"but overall performance is weakening. Immediate attention is recommended."
        )

    return (
        f"Revenue is relatively stable at {total_revenue:.2f}. "
        f"{top_revenue_product} remains the strongest value driver and {top_selling_product} leads in volume."
    )


def analyze_sales(data):
    """
    Analyse commerciale globale des ventes.
    Retourne un bloc enrichi, utile pour le frontend et pour le contrat Spring Boot.
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

    df["price"] = pd.to_numeric(df["price"], errors="coerce").fillna(0)
    df["quantity"] = pd.to_numeric(df["quantity"], errors="coerce").fillna(0)

    df["revenue"] = df["price"] * df["quantity"]

    # Totaux globaux
    total_revenue = float(df["revenue"].sum())
    total_quantity_sold = int(df["quantity"].sum())
    average_order_value = float(total_revenue / len(df)) if len(df) > 0 else 0.0
    unique_products = int(df["product"].nunique())
    unique_days = int(df["date"].dt.date.nunique())

    # Top produit en quantité
    quantity_by_product = df.groupby("product")["quantity"].sum().sort_values(ascending=False)
    top_selling_product = quantity_by_product.idxmax()
    top_selling_quantity = float(quantity_by_product.max())

    # Top produit en revenu
    revenue_by_product = df.groupby("product")["revenue"].sum().sort_values(ascending=False)
    top_revenue_product = revenue_by_product.idxmax()
    top_revenue_value = float(revenue_by_product.max())

    # Revenu par jour
    daily_revenue = df.groupby("date")["revenue"].sum().sort_index()
    trend, trend_slope = _detect_sales_trend(daily_revenue)

    revenue_per_day = []
    for date, revenue in daily_revenue.items():
        revenue_per_day.append(
            {
                "date": str(date.date()),
                "revenue": round(float(revenue), 2),
            }
        )

    sales_comment = _build_sales_comment(
        trend=trend,
        total_revenue=total_revenue,
        top_revenue_product=top_revenue_product,
        top_selling_product=top_selling_product,
    )

    recommendations = []

    recommendations.append(
        f"{top_selling_product}: top selling product by volume with {top_selling_quantity:.0f} units sold. "
        "Highlight it in marketing and keep it visible in the catalog."
    )

    if top_revenue_product != top_selling_product:
        recommendations.append(
            f"{top_revenue_product}: highest revenue generator with {top_revenue_value:.2f} in sales. "
            "Use it as a flagship product in commercial strategy."
        )

    if trend == "upward":
        recommendations.append(
            "Sales are improving. Increase stock monitoring, reinforce acquisition, and prepare a scale-up plan."
        )
    elif trend == "downward":
        recommendations.append(
            "Sales are declining. Review pricing, traffic quality, and conversion bottlenecks."
        )
    else:
        recommendations.append(
            "Sales are stable. Focus on product optimization, upsell, and cross-sell opportunities."
        )

    return {
        "total_revenue": round(total_revenue, 2),
        "total_quantity_sold": total_quantity_sold,
        "average_order_value": round(average_order_value, 2),
        "unique_products": unique_products,
        "unique_days": unique_days,
        "top_selling_product": top_selling_product,
        "top_selling_quantity": round(top_selling_quantity, 2),
        "top_revenue_product": top_revenue_product,
        "top_revenue_value": round(top_revenue_value, 2),
        "trend": trend,
        "trend_slope": round(trend_slope, 4),
        "revenue_per_day": revenue_per_day,
        "sales_comment": sales_comment,
        "recommendations": recommendations,
    }


if __name__ == "__main__":
    data = [
        {"date": "2025-01-01", "product": "Laptop", "price": 1000, "quantity": 1, "stock": 100},
        {"date": "2025-01-02", "product": "Mouse", "price": 20, "quantity": 10, "stock": 5},
        {"date": "2025-01-03", "product": "Laptop", "price": 1000, "quantity": 2, "stock": 50},
        {"date": "2025-01-04", "product": "Keyboard", "price": 50, "quantity": 5, "stock": 9},
        {"date": "2025-01-05", "product": "Laptop", "price": 1000, "quantity": 1, "stock": 70},
    ]

    print(analyze_sales(data))