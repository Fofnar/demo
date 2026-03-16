import pandas as pd

def business_health_score(data):

    if not data:
        return {"error": "no sales data"}

    df = pd.DataFrame(data)

    df["date"] = pd.to_datetime(df["date"])
    df["revenue"] = df["price"] * df["quantity"]

    daily_revenue = df.groupby("date")["revenue"].sum()

    score = 0

    # Score de tendance (40 pts)
    first = daily_revenue.iloc[0]
    last = daily_revenue.iloc[-1]

    if last > first:
        score += 40
    elif last == first:
        score += 20
    else:
        score += 5

    # Score de stabilité (30 pts)
    volatility = daily_revenue.std()#calcule l’écart-type, une mesure de dispersion / volatilité.

    if volatility < daily_revenue.mean() * 0.2:
        score += 30
    elif volatility < daily_revenue.mean() * 0.5:
        score += 15
    else:
        score += 5

    # Score de croissance (30 pts)
    growth_rate = (last - first) / first

    if growth_rate > 0.2:
        score += 30
    elif growth_rate > 0.05:
        score += 15
    else:
        score += 5

    return {
        "health_score": score
    }

if __name__ == "__main__":

    data = [
     {"date":"2025-01-01","product":"Laptop","price":1000,"quantity":1,"stock":100},
     {"date":"2025-01-02","product":"Mouse","price":20,"quantity":10,"stock":5},
     {"date":"2025-01-03","product":"Laptop","price":1000,"quantity":2,"stock":50},
     {"date":"2025-01-04","product":"Keyboard","price":50,"quantity":5,"stock":9},
     {"date":"2025-01-05","product":"Laptop","price":1000,"quantity":1,"stock":70}
    ]

    print(business_health_score(data))
