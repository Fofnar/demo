import pandas as pd

def predict_sales(data):

    #vérifier données
    if not data:
        return {"error": "no sales data"}
    
    #DataFrame
    df = pd.DataFrame(data)

    #Convertir date
    df["date"]= pd.to_datetime(df["date"])

    #Créer revenue
    df["revenue"] = df["price"] * df["quantity"]

    #Revenu par jour
    daily_revenue = df.groupby("date")["revenue"].sum()

    #moving average (3 jours)
    moving_avg = daily_revenue.rolling(window=3).mean() #calcule la moyennz glissante

    #prediction
    prediction = float(moving_avg.iloc[-1]) #prend la dernière valeur

    #S'il n'y a pas assez de données, on utilise la moyenne simple
    if pd.isna(prediction): prediction = float(daily_revenue.mean())

    #détecter tendance
    first = daily_revenue.iloc[0]
    last = daily_revenue.iloc[-1]

    if first < last: trend = "upward"
    elif first >last: trend = "downward"
    else: trend = "stable"

    #prediction des 3 prochains jours
    next_3_days = float(daily_revenue.tail(3).mean())

    #tendance des 3 jours
    last3 = daily_revenue.tail(3).mean() #les 6 derniers jours
    prev3 = daily_revenue.tail(6).head(3).mean() #les 3 premiers parmi les 6 derniers jours

    if last3 > prev3:
        trend_3days = "upward"
    elif last3 < prev3:
        trend_3days = "downward"
    else:
        trend_3days = "stable"

    return{
        "predicted_next_day_revenue": prediction,
        "trend_next": trend,
        "next_3_days_prediction": next_3_days,
        "trend_next_3_days": trend_3days
    }

if __name__ == "__main__":

    data = [
     {"date":"2025-01-01","product":"Laptop","price":1000,"quantity":1,"stock":100},
     {"date":"2025-01-02","product":"Mouse","price":20,"quantity":10,"stock":5},
     {"date":"2025-01-03","product":"Laptop","price":1000,"quantity":2,"stock":50},
     {"date":"2025-01-04","product":"Keyboard","price":50,"quantity":5,"stock":9},
     {"date":"2025-01-05","product":"Laptop","price":1000,"quantity":1,"stock":70}
    ]

    print(predict_sales(data))