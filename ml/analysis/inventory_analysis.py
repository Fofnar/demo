import pandas as pd

def analyze_inventory(data):
    if not data:
        return {"error": "no sales data"}
    
    #Data frame
    df = pd.DataFrame(data)

    #Conversion en date
    df["date"] = pd.to_datetime(df["date"])

    #Stock à l'instant T par produit
    stock_by_product = df.groupby("product")["stock"].last()

    #Si stock < 10
    low_stock_products = stock_by_product[stock_by_product < 10]

    result = []

    for product, stock in low_stock_products.items():
        result.append({
            "product": product,
            "stock": int(stock),
            "warning": "Low stock"
        })

    return {
        "low_stock_alerts": result
    }

# ============================= TEST ============================

if __name__ == "__main__":

    data = [
     {"date":"2025-01-01","product":"Laptop","price":1000,"quantity":1,"stock":100},
     {"date":"2025-01-02","product":"Mouse","price":20,"quantity":10,"stock":5},
     {"date":"2025-01-03","product":"Laptop","price":1000,"quantity":2,"stock":50},
     {"date":"2025-01-04","product":"Keyboard","price":50,"quantity":5,"stock":9},
     {"date":"2025-01-05","product":"Laptop","price":1000,"quantity":1,"stock":70}
    ]

    print(analyze_inventory(data))