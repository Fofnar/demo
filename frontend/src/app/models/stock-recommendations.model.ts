import { Anomalies } from './anomalies.model'; // Contexte d'anomalies réutilisé

// Représente une recommandation de stock pour un produit
export interface StockRecommendationItem { // Bloc stock_recommendations[]
  product: string; // Nom du produit
  risk_level: string; // Niveau de risque
  message: string; // Message métier principal
  recommended_restock_quantity: number; // Quantité recommandée à réapprovisionner
  estimated_days_before_stockout: number; // Jours estimés avant rupture
  has_related_anomaly: boolean; // Indique un lien avec une anomalie
  related_anomaly_count: number; // Nombre d'anomalies liées
  related_anomaly_severity: string; // Sévérité maximale des anomalies liées
}

// Représente le bloc stock_recommendations complet
export interface StockRecommendations { // Bloc stock_recommendations
  critical_alerts: number; // Nombre d'alertes critiques
  high_alerts: number; // Nombre d'alertes élevées
  executive_note: string; // Note exécutive synthétique
  business_comment: string; // Commentaire métier global
  recommendations: string[]; // Recommandations globales
  anomalies_count: number; // Nombre total d'anomalies
  high_priority_anomalies: number; // Nombre d'anomalies prioritaires
  anomalies_report: Anomalies; // Contexte des anomalies
  stock_recommendations: StockRecommendationItem[]; // Détails par produit
}