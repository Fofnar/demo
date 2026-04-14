import traceback

# Imports des modules d'analyse
try:
    from ..analysis.sales_analysis import analyze_sales
    from ..analysis.anomaly_detection import detect_sales_anomalies
    from ..analysis.prediction import predict_sales
    from ..analysis.recommendation import generate_recommendations
    from ..analysis.business_health import business_health_score
    from ..analysis.inventory_analysis import analyze_inventory
    from ..analysis.stock_prediction import predict_stockout, stock_recommendations
except Exception:
    # Fallback pour exécution directe hors package
    from analysis.sales_analysis import analyze_sales
    from analysis.anomaly_detection import detect_sales_anomalies
    from analysis.prediction import predict_sales
    from analysis.recommendation import generate_recommendations
    from analysis.business_health import business_health_score
    from analysis.inventory_analysis import analyze_inventory
    from analysis.stock_prediction import predict_stockout, stock_recommendations


def _safe_call(function, data, default_error_label: str):
    """
    Exécute une fonction d'analyse sans interrompre le pipeline global.
    """
    try:
        return function(data)
    except Exception as exc:
        return {
            "error": f"{default_error_label}: {str(exc)}",
            "trace": traceback.format_exc(),
        }


def run_ai_analysis(data):
    """
    Ordonne l'ensemble des analyses IA sur les ventes.

    Retourne un bloc JSON unique, prêt pour FastAPI et pour le contrat Spring Boot.
    """
    if not data:
        return {"error": "no sales data"}

    # Analyse des ventes
    sales_analysis = _safe_call(analyze_sales, data, "sales_analysis_failed")

    # Détection d'anomalies
    anomalies = _safe_call(detect_sales_anomalies, data, "anomaly_detection_failed")

    # Prédiction des ventes
    prediction = _safe_call(predict_sales, data, "prediction_failed")

    # Recommandations business
    recommendations = _safe_call(generate_recommendations, data, "recommendation_failed")

    # Score de santé business
    health = _safe_call(business_health_score, data, "business_health_failed")

    # Analyse des stocks
    inventory = _safe_call(analyze_inventory, data, "inventory_analysis_failed")

    # Prédiction de rupture de stock
    stock_pred = _safe_call(predict_stockout, data, "stock_prediction_failed")

    # Recommandations de réapprovisionnement
    stock_rec = (
        _safe_call(stock_recommendations, stock_pred, "stock_recommendation_failed")
        if isinstance(stock_pred, dict) and "stock_prediction" in stock_pred
        else {"error": "invalid stock prediction output"}
    )

    return {
        "sales_analysis": sales_analysis,
        "anomalies": anomalies,
        "prediction": prediction,
        "recommendations": recommendations,
        "health_score": health,
        "inventory": inventory,
        "stock_prediction": stock_pred,
        "stock_recommendations": stock_rec,
    }