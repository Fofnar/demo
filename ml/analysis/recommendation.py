import pandas as pd

def generate_recommendations(data):
    
    if not data:
        return {"error": "no sales data"}
    
    ##Convertir les données en DataFrame.
    df = pd.DataFrame(data)

    #Convertir date
    df["date"] = pd.to_datetime(df["date"])

    #Créer colonne revenue
    df["revenue"] = df["price"] * df["quantity"]

    #Produit le plus vendu
    top_selling_product = (
        df.groupby("product")["quantity"]
        .sum()
        .idxmax()
        )
    #Revenu par jour
    daily_revenue = df.groupby("date")["revenue"].sum()

    #détecter tendance
    first = daily_revenue.iloc[0]
    last = daily_revenue.iloc[-1]

    if first < last: trend = "upward"
    elif first >last: trend = "downward"
    else: trend = "stable"

    recommendations = []

    recommendations.append(
    f"{top_selling_product} is the top selling product. Highlight it in marketing."
    )

    if trend == "upward":
        recommendations.append("Sales increasing. Increase stock.")

    elif trend == "downward":
        recommendations.append("Sales decreasing. Launch promotion.")

    else:
        recommendations.append("Sales stable. Monitor performance.")

    return {
        "trend": trend,
        "recommendations": recommendations
    }

if __name__ == "__main__":

    data = [
     {"date":"2025-01-01","product":"Laptop","price":1000,"quantity":1,"stock":100},
     {"date":"2025-01-02","product":"Mouse","price":20,"quantity":10,"stock":5},
     {"date":"2025-01-03","product":"Laptop","price":1000,"quantity":2,"stock":50},
     {"date":"2025-01-04","product":"Keyboard","price":50,"quantity":5,"stock":9},
     {"date":"2025-01-05","product":"Laptop","price":1000,"quantity":1,"stock":70}
    ]

    print(generate_recommendations(data))