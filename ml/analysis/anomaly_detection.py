import pandas as pd 

def detect_sales_anomalies(data):

    #vérifier données
    if not data:
        return {"error": "no sales data"}

    #Convertir les données en DataFrame.
    df = pd.DataFrame(data) 

    #Convertir date
    df["date"] = pd.to_datetime(df["date"]) # les dates arrivent souvent comme string

    df["revenue"] = df["price"] * df["quantity"]

    #Revenu par jour
    daily_revenue = df.groupby("date")["revenue"].sum()

    #Calcul du premier quartile
    Q1 = daily_revenue.quantile(0.25) #Valeur en desous de laquelle se situe 25% des données

    #Calcul du troisième quartile
    Q3 = daily_revenue.quantile(0.75)

    #Ecart Interquartile
    IQR = Q3 - Q1

    #Bornes
    lower = float(Q1 - (1.5 * IQR))
    upper = float(Q3 + (1.5 * IQR))

    anomalies = daily_revenue[
        (daily_revenue < lower) | (daily_revenue > upper)
    ]

    result = []

    for date, revenue in anomalies.items():
        result.append({
            "date": str(date.date()),
            "revenue" : revenue
        }
        )
    return {
        "lower_bound": lower,
        "upper_bound": upper,
        "anomalies": result
    }

if __name__ == "__main__":

    data = [
     {"date":"2025-01-01","product":"Laptop","price":1000,"quantity":1,"stock":100},
     {"date":"2025-01-02","product":"Mouse","price":20,"quantity":10,"stock":5},
     {"date":"2025-01-03","product":"Laptop","price":1000,"quantity":2,"stock":50},
     {"date":"2025-01-04","product":"Keyboard","price":50,"quantity":5,"stock":9},
     {"date":"2025-01-05","product":"Laptop","price":1000,"quantity":1,"stock":70}
    ]
    
    print(detect_sales_anomalies(data))