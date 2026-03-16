from analysis.sales_analysis import analyze_sales
from analysis.anomaly_detection import detect_sales_anomalies
from analysis.prediction import predict_sales
from analysis.recommendation import generate_recommendations
from analysis.business_health import business_health_score
from analysis.inventory_analysis import analyze_inventory
from analysis.stock_prediction import predict_stockout, stock_recommendations



def run_ai_analysis(data):

    if not data:
        return {"error": "no sales data"}

    # Analyse des ventes
    sales_analysis = analyze_sales(data)

    # Détection d'anomalies
    anomalies = detect_sales_anomalies(data)

    # Prédiction des ventes
    prediction = predict_sales(data)

    # Recommandations
    recommendations = generate_recommendations(data)

    # Score
    health = business_health_score(data)

    #Analyse des stocks
    inventory = analyze_inventory(data)

    #Prédire rupture
    stock_pred = predict_stockout(data)

    #Recommander réapprovisionnement
    stock_rec = stock_recommendations(stock_pred)

    return {
        "sales_analysis": sales_analysis,
        
        "anomalies": anomalies,

        "prediction": prediction,

        "recommendations": recommendations,

        "health_score": health,

        "inventory": inventory,

        "stock_prediction": stock_pred,

        "stock_recommendations": stock_rec
    }