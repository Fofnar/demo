import { Anomalies } from './anomalies.model'; // Contexte d'anomalies réutilisé
import { Forecast } from './prediction.model'; // Forecast réutilisable

// Représente les recommandations détaillées par thème
export interface RecommendationDetails { // Bloc recommendations.recommendations
  product_insights: string[]; // Recommandations liées aux produits
  trend_insights: string[]; // Recommandations liées à la tendance
  anomaly_insights: string[]; // Recommandations liées aux anomalies
}

// Représente le bloc principal recommendations
export interface Recommendations { // Bloc de recommandations globales
  trend: string; // Tendance globale
  trend_slope: number; // Pente de la tendance
  trend_change_rate: number; // Taux de variation
  top_selling_product: string; // Produit le plus vendu
  top_revenue_product: string; // Produit le plus rentable
  recommendations: RecommendationDetails; // Recommandations détaillées
  executive_summary: string; // Résumé exécutif
  anomalies_count: number; // Nombre total d'anomalies
  high_priority_anomalies: number; // Nombre d'anomalies prioritaires
  forecast: Forecast; // Bloc forecast associé
  anomalies_report: Anomalies; // Bloc anomalies associé
}