import pandas as pd

def  analyze_sales(data):
    
    if not data:
        return {"error": "no sales data"}
    
    #Convertir les données en DataFrame.
    df = pd.DataFrame(data)

    df["date"] = pd.to_datetime(df["date"]) # les dates arrivent souvent comme string

    #Créer la colonne revenue:
    df["revenue"] = df["price"] * df["quantity"]

    #Le revenu total
    total_revenue = df["revenue"].sum()

    #Quantité totale de vente
    total_quantity_sold = df["quantity"].sum()

    #Produit le plus vendu
    top_selling_product =(
        df.groupby("product")["quantity"] #groupby("product") → groupe les lignes
        .sum() #total des quantités par produit
        .idxmax() # produit avec maximum 
    )

    #Revenu par jour
    revenue_per_day = df.groupby("date")["revenue"].sum()
    
    #JSON
    daily_revenue = []
    for date, revenue in revenue_per_day.items():
        daily_revenue.append({
            "date": str(date.date()),
            "revenue" : float(revenue)
        }
        )

    return {
        "total_revenue": float(total_revenue),
        "total_quantity_sold": int(total_quantity_sold),
        "top_selling_product": top_selling_product,
        "revenue_per_day": daily_revenue
    }

if __name__ == "__main__":

    data = [
     {"date":"2025-01-01","product":"Laptop","price":1000,"quantity":1,"stock":100},
     {"date":"2025-01-02","product":"Mouse","price":20,"quantity":10,"stock":5},
     {"date":"2025-01-03","product":"Laptop","price":1000,"quantity":2,"stock":50},
     {"date":"2025-01-04","product":"Keyboard","price":50,"quantity":5,"stock":9},
     {"date":"2025-01-05","product":"Laptop","price":1000,"quantity":1,"stock":70}
    ]

    print(analyze_sales(data))