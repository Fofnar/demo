// Représente une prévision de stock pour un produit
export interface StockPredictionItem { // Bloc stock_prediction[]
  product: string; // Nom du produit
  current_stock: number; // Stock actuel
  avg_daily_sales: number; // Moyenne globale des ventes journalières
  recent_avg_daily_sales: number; // Moyenne récente des ventes journalières
  sales_trend: string; // Tendance des ventes : upward, downward, stable
  estimated_days_before_stockout: number; // Jours estimés avant rupture
  risk_level: string; // Niveau de risque : critical, medium, healthy...
  recommended_restock_quantity: number; // Quantité recommandée à réapprovisionner
  recommendation: string; // Message métier associé
  has_related_anomaly: boolean; // Indique un lien avec une anomalie
  related_anomaly_count: number; // Nombre d'anomalies liées
  related_anomaly_severity: string; // Sévérité maximale des anomalies liées
}

// Représente le bloc stock_prediction complet
export interface StockPrediction { // Bloc stock_prediction
  coverage_target_days: number; // Nombre de jours de couverture visé
  stock_prediction: StockPredictionItem[]; // Liste des prévisions par produit
  business_comment: string; // Commentaire métier global
  recommendations: string[]; // Recommandations globales
  anomalies_count: number; // Nombre total d'anomalies
  high_priority_anomalies: number; // Nombre d'anomalies prioritaires
  anomalies_report: import('./anomalies.model').Anomalies; // Contexte des anomalies
}