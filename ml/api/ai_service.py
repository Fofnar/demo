import traceback

# Imports principaux utilisés lorsque le service est exécuté comme package Python.
try:
    from ..analysis.sales_analysis import analyze_sales
    from ..analysis.anomaly_detection import detect_sales_anomalies
    from ..analysis.prediction import predict_sales
    from ..analysis.recommendation import generate_recommendations
    from ..analysis.business_health import business_health_score
    from ..analysis.inventory_analysis import analyze_inventory
    from ..analysis.stock_prediction import predict_stockout, stock_recommendations
except Exception:
    # Fallback conservé pour les exécutions locales directes hors contexte package.
    from analysis.sales_analysis import analyze_sales
    from analysis.anomaly_detection import detect_sales_anomalies
    from analysis.prediction import predict_sales
    from analysis.recommendation import generate_recommendations
    from analysis.business_health import business_health_score
    from analysis.inventory_analysis import analyze_inventory
    from analysis.stock_prediction import predict_stockout, stock_recommendations


def _safe_call(function, data, default_error_label: str):
    """
    Exécute une fonction d'analyse sans interrompre l'ensemble du pipeline IA.

    Chaque bloc d'analyse reste isolé : une erreur sur une analyse spécifique
    ne bloque pas les autres résultats retournés au backend.
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
    Orchestre l'ensemble des analyses IA appliquées aux données de vente.

    Le service retourne une réponse JSON unique afin de conserver un contrat
    stable entre FastAPI, Spring Boot et le frontend Angular.
    """
    if not data:
        return {"error": "no sales data"}

    sales_analysis = _safe_call(analyze_sales, data, "sales_analysis_failed")
    anomalies = _safe_call(detect_sales_anomalies, data, "anomaly_detection_failed")
    prediction = _safe_call(predict_sales, data, "prediction_failed")
    recommendations = _safe_call(generate_recommendations, data, "recommendation_failed")
    health = _safe_call(business_health_score, data, "business_health_failed")
    inventory = _safe_call(analyze_inventory, data, "inventory_analysis_failed")
    stock_pred = _safe_call(predict_stockout, data, "stock_prediction_failed")

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