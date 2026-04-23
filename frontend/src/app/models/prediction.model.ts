import { Anomalies } from './anomalies.model'; // Contexte d'anomalies réutilisé

// Représente une prévision journalière
export interface ForecastDay { // Bloc forecast_next_3_days
  date: string; // Date au format yyyy-MM-dd
  predicted_revenue: number; // Revenu prédit
  method: string; // Méthode utilisée
}

// Représente une prévision de la courbe de forecast
export interface ForecastPoint { // Bloc forecast
  date: string; // Date de la projection
  yhat: number; // Valeur prédite
}

// Représente le bloc complet forecast
export interface Forecast { // Bloc forecast associé aux recommandations
  available: boolean; // Indique si le forecast est disponible
  method: string; // Méthode de prévision
  forecast: ForecastPoint[]; // Liste des points prévus
}

// Représente le bloc principal prediction
export interface Prediction { // Bloc de prédiction des revenus
  predicted_next_day_revenue: number; // Revenu prédit pour le prochain jour
  trend_next: string; // Tendance du prochain jour
  next_3_days_prediction: number; // Prévision moyenne sur 3 jours
  trend_next_3_days: string; // Tendance sur 3 jours
  prediction_method: string; // Méthode utilisée
  model_quality_mae: number; // Qualité du modèle
  business_comment: string; // Commentaire métier
  anomalies_count: number; // Nombre total d'anomalies
  high_priority_anomalies: number; // Nombre d'anomalies prioritaires
  anomalies_report: Anomalies; // Bloc anomalies réutilisé comme contexte
  forecast_next_3_days: ForecastDay[]; // Prévisions détaillées sur 3 jours
}