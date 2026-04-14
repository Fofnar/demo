import pandas as pd
import numpy as np

# Import optionnel de scikit-learn
try:
    from sklearn.linear_model import LinearRegression
    from sklearn.metrics import mean_absolute_error
    SKLEARN_AVAILABLE = True
except Exception:
    LinearRegression = None
    mean_absolute_error = None
    SKLEARN_AVAILABLE = False

# Import optionnel du moteur d'anomalies
try:
    from .anomaly_detection import detect_sales_anomalies
except Exception:
    try:
        from analysis.anomaly_detection import detect_sales_anomalies
    except Exception:
        detect_sales_anomalies = None


def _build_time_features(daily_revenue: pd.Series) -> pd.DataFrame:
    """
    Construit un jeu de variables temporelles à partir d'une série de revenus journaliers.
    """
    df = daily_revenue.reset_index()
    df.columns = ["date", "revenue"]

    # Variables calendaires de base
    df["day_of_week"] = df["date"].dt.dayofweek
    df["day_of_month"] = df["date"].dt.day
    df["month"] = df["date"].dt.month

    # Indicateur de week-end
    df["is_weekend"] = df["day_of_week"].isin([5, 6]).astype(int)

    # Variables retardées
    df["lag_1"] = df["revenue"].shift(1)
    df["lag_2"] = df["revenue"].shift(2)
    df["lag_3"] = df["revenue"].shift(3)

    # Statistiques glissantes
    df["rolling_mean_3"] = df["revenue"].shift(1).rolling(window=3).mean()
    df["rolling_std_3"] = df["revenue"].shift(1).rolling(window=3).std()

    # Évolution récente
    df["recent_diff"] = df["rolling_mean_3"] - df["lag_3"]

    return df


def _detect_trend(daily_revenue: pd.Series) -> str:
    """
    Détecte la tendance globale avec une régression linéaire simple.
    Retourne : upward / downward / stable
    """
    if daily_revenue.empty or len(daily_revenue) < 2:
        return "stable"

    x = np.arange(len(daily_revenue))
    y = daily_revenue.values.astype(float)

    slope = np.polyfit(x, y, 1)[0]

    if slope > 0:
        return "upward"
    if slope < 0:
        return "downward"
    return "stable"


def _build_next_row_features(daily_revenue: pd.Series) -> dict:
    """
    Construit les variables du prochain jour à prédire.
    """
    last_date = daily_revenue.index[-1]
    last_values = daily_revenue.values.astype(float)

    lag_1 = float(last_values[-1]) if len(last_values) >= 1 else 0.0
    lag_2 = float(last_values[-2]) if len(last_values) >= 2 else lag_1
    lag_3 = float(last_values[-3]) if len(last_values) >= 3 else lag_2

    last_3 = last_values[-3:] if len(last_values) >= 3 else last_values
    rolling_mean_3 = float(np.mean(last_3)) if len(last_3) > 0 else 0.0
    rolling_std_3 = float(np.std(last_3, ddof=1)) if len(last_3) > 1 else 0.0
    recent_diff = rolling_mean_3 - lag_3

    next_date = last_date + pd.Timedelta(days=1)

    return {
        "date": next_date,
        "day_of_week": next_date.dayofweek,
        "day_of_month": next_date.day,
        "month": next_date.month,
        "is_weekend": int(next_date.dayofweek in [5, 6]),
        "lag_1": lag_1,
        "lag_2": lag_2,
        "lag_3": lag_3,
        "rolling_mean_3": rolling_mean_3,
        "rolling_std_3": rolling_std_3,
        "recent_diff": recent_diff,
    }


def _train_and_predict(daily_revenue: pd.Series) -> dict:
    """
    Entraîne un modèle simple si possible et prédit le prochain jour.
    La qualité est évaluée sur un split temporel, puis le modèle final est ré-entraîné sur toutes les données.
    """
    features_df = _build_time_features(daily_revenue)

    feature_columns = [
        "day_of_week",
        "day_of_month",
        "month",
        "is_weekend",
        "lag_1",
        "lag_2",
        "lag_3",
        "rolling_mean_3",
        "rolling_std_3",
        "recent_diff",
    ]

    # Suppression des lignes incomplètes dues aux décalages temporels
    model_df = features_df.dropna().copy()

    # Retour de secours si les données sont insuffisantes ou si sklearn est absent
    if len(model_df) < 5 or not SKLEARN_AVAILABLE:
        return {
            "available": False,
            "method": "fallback",
            "prediction": None,
            "mae": None,
            "model": None,
            "feature_columns": feature_columns,
        }

    X = model_df[feature_columns].copy()
    y = model_df["revenue"].copy()

    # Split temporel pour éviter une évaluation biaisée
    split = max(int(len(X) * 0.8), 1)
    if split >= len(X):
        split = len(X) - 1

    if split < 1 or len(X) - split < 1:
        return {
            "available": False,
            "method": "fallback",
            "prediction": None,
            "mae": None,
            "model": None,
            "feature_columns": feature_columns,
        }

    X_train = X.iloc[:split]
    y_train = y.iloc[:split]
    X_test = X.iloc[split:]
    y_test = y.iloc[split:]

    # Modèle d'évaluation
    eval_model = LinearRegression()
    eval_model.fit(X_train, y_train)

    test_pred = eval_model.predict(X_test)
    mae = float(mean_absolute_error(y_test, test_pred)) if mean_absolute_error else None

    # Modèle final ré-entraîné sur toutes les données
    final_model = LinearRegression()
    final_model.fit(X, y)

    # Construction de la ligne future
    next_features = _build_next_row_features(daily_revenue)
    X_next = pd.DataFrame([next_features])[feature_columns]

    next_prediction = max(float(final_model.predict(X_next)[0]), 0.0)

    return {
        "available": True,
        "method": "linear_regression",
        "prediction": next_prediction,
        "mae": mae,
        "model": final_model,
        "feature_columns": feature_columns,
    }


def _recursive_forecast_3_days(daily_revenue: pd.Series) -> list[dict]:
    """
    Prédit les 3 prochains jours de façon récursive.
    """
    history = daily_revenue.copy().astype(float)
    forecast = []

    if len(history) < 2:
        return forecast

    # Entraînement du modèle de base
    base_result = _train_and_predict(history)

    if not base_result["available"]:
        # Retour de secours basé sur la moyenne des 3 derniers jours
        tail_mean = float(history.tail(3).mean())
        future_dates = pd.date_range(
            start=history.index[-1] + pd.Timedelta(days=1),
            periods=3,
            freq="D",
        )

        for date in future_dates:
            forecast.append(
                {
                    "date": date.strftime("%Y-%m-%d"),
                    "yhat": max(tail_mean, 0.0),
                    "method": "moving_average_fallback",
                }
            )
        return forecast

    model = base_result["model"]
    feature_columns = base_result["feature_columns"]

    for _ in range(3):
        next_features = _build_next_row_features(history)
        X_next = pd.DataFrame([next_features])[feature_columns]

        yhat = float(model.predict(X_next)[0])
        yhat = max(yhat, 0.0)

        forecast.append(
            {
                "date": next_features["date"].strftime("%Y-%m-%d"),
                "yhat": yhat,
                "method": "linear_regression",
            }
        )

        # Ajout de la prédiction dans l'historique pour la suite
        history.loc[next_features["date"]] = yhat
        history = history.sort_index()

    return forecast


def _build_business_comment(
    trend: str,
    predicted_next_day_revenue: float,
    daily_revenue_mean: float,
    anomalies_count: int,
    high_priority_count: int,
) -> str:
    """
    Produit un commentaire business lisible pour le frontend.
    """
    if high_priority_count > 0:
        return (
            f"Revenue signals are mixed. The model forecasts {predicted_next_day_revenue:.2f} "
            f"for the next day, and {high_priority_count} high-priority anomaly/anomalies were detected "
            f"out of {anomalies_count} total anomaly/anomalies. "
            "Investigate pricing, stock availability, and traffic quality immediately."
        )

    if trend == "upward" and predicted_next_day_revenue > daily_revenue_mean:
        return (
            f"Revenue momentum is positive. The model forecasts {predicted_next_day_revenue:.2f} "
            f"for the next day, above the current average of {daily_revenue_mean:.2f}. "
            "The business is likely entering a growth phase."
        )

    if trend == "downward" and predicted_next_day_revenue < daily_revenue_mean:
        return (
            f"Revenue momentum is weakening. The model forecasts {predicted_next_day_revenue:.2f} "
            f"for the next day, below the current average of {daily_revenue_mean:.2f}. "
            "The business should investigate demand, pricing, and stock issues."
        )

    return (
        f"Revenue is relatively stable. The model forecasts {predicted_next_day_revenue:.2f} "
        f"for the next day, compared with an average of {daily_revenue_mean:.2f}. "
        "The business should optimize conversion and strengthen strong products."
    )


def _summarize_anomalies(anomalies: list[dict]) -> tuple[int, int]:
    """
    Compte le nombre total d'anomalies et le nombre d'anomalies prioritaires.
    """
    if not anomalies:
        return 0, 0

    high_priority = 0
    for anomaly in anomalies:
        severity = anomaly.get("severity", "low")
        if severity in {"high", "critical"}:
            high_priority += 1

    return len(anomalies), high_priority


def predict_sales(data):
    """
    Prévoit les revenus à court terme à partir des ventes.
    Retourne une structure enrichie, compatible avec un contrat JSON propre.
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

    df["revenue"] = df["price"] * df["quantity"]

    # Revenu journalier
    daily_revenue = df.groupby("date")["revenue"].sum().sort_index()

    if len(daily_revenue) == 0:
        return {"error": "no daily revenue available"}

    # Tendance globale
    trend = _detect_trend(daily_revenue)

    # Prévision du prochain jour
    ml_result = _train_and_predict(daily_revenue)

    if ml_result["available"]:
        predicted_next_day_revenue = float(ml_result["prediction"])
        prediction_method = ml_result["method"]
        mae = ml_result["mae"]
    else:
        # Retour de secours si les données sont insuffisantes ou si sklearn est absent
        predicted_next_day_revenue = max(float(daily_revenue.tail(3).mean()), 0.0)
        prediction_method = "moving_average_fallback"
        mae = None

    # Prévision sur 3 jours
    next_3_days_forecast = _recursive_forecast_3_days(daily_revenue)
    next_3_days_prediction = (
        float(np.mean([item["yhat"] for item in next_3_days_forecast]))
        if next_3_days_forecast
        else predicted_next_day_revenue
    )

    # Tendance des 3 prochains jours
    if len(next_3_days_forecast) >= 2:
        first_forecast = next_3_days_forecast[0]["yhat"]
        last_forecast = next_3_days_forecast[-1]["yhat"]

        if last_forecast > first_forecast:
            trend_next_3_days = "upward"
        elif last_forecast < first_forecast:
            trend_next_3_days = "downward"
        else:
            trend_next_3_days = "stable"
    else:
        trend_next_3_days = "stable"

    # Récupération des anomalies déjà enrichies par le moteur dédié
    anomalies_report = {"anomalies": []}
    if detect_sales_anomalies is not None:
        try:
            anomalies_report = detect_sales_anomalies(data)
        except Exception:
            anomalies_report = {"anomalies": []}

    anomalies = anomalies_report.get("anomalies", [])
    anomalies_count, high_priority_count = _summarize_anomalies(anomalies)

    # Commentaire business final
    business_comment = _build_business_comment(
        trend=trend,
        predicted_next_day_revenue=predicted_next_day_revenue,
        daily_revenue_mean=float(daily_revenue.mean()),
        anomalies_count=anomalies_count,
        high_priority_count=high_priority_count,
    )

    return {
        "predicted_next_day_revenue": round(float(predicted_next_day_revenue), 2),
        "trend_next": trend,
        "next_3_days_prediction": round(float(next_3_days_prediction), 2),
        "trend_next_3_days": trend_next_3_days,
        "prediction_method": prediction_method,
        "model_quality_mae": None if mae is None else round(float(mae), 4),
        "business_comment": business_comment,
        "anomalies_count": anomalies_count,
        "high_priority_anomalies": high_priority_count,
        "anomalies_report": anomalies_report,
        "forecast_next_3_days": [
            {
                "date": item["date"],
                "predicted_revenue": round(float(item["yhat"]), 2),
                "method": item["method"],
            }
            for item in next_3_days_forecast
        ],
    }


if __name__ == "__main__":
    # Jeu de données exemple avec une anomalie basse et une anomalie haute
    data = [
        {"date": "2025-01-01", "product": "Laptop", "price": 1000, "quantity": 1, "stock": 100},
        {"date": "2025-01-01", "product": "Mouse", "price": 20, "quantity": 5, "stock": 50},
        {"date": "2025-01-02", "product": "Laptop", "price": 1000, "quantity": 1, "stock": 95},
        {"date": "2025-01-02", "product": "Keyboard", "price": 50, "quantity": 4, "stock": 30},
        {"date": "2025-01-03", "product": "Laptop", "price": 1000, "quantity": 1, "stock": 90},
        {"date": "2025-01-03", "product": "Mouse", "price": 20, "quantity": 6, "stock": 45},
        {"date": "2025-01-04", "product": "Mouse", "price": 20, "quantity": 1, "stock": 5},
        {"date": "2025-01-04", "product": "Keyboard", "price": 50, "quantity": 1, "stock": 8},
        {"date": "2025-01-05", "product": "Laptop", "price": 1000, "quantity": 8, "stock": 70},
        {"date": "2025-01-05", "product": "Mouse", "price": 20, "quantity": 20, "stock": 40},
        {"date": "2025-01-05", "product": "Keyboard", "price": 50, "quantity": 10, "stock": 20},
        {"date": "2025-01-06", "product": "Laptop", "price": 1000, "quantity": 1, "stock": 85},
        {"date": "2025-01-06", "product": "Mouse", "price": 20, "quantity": 4, "stock": 38},
    ]

    print(predict_sales(data))