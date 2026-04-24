import { Anomalies } from './anomalies.model'; // Bloc anomalies
import { SalesAnalysis } from './sales-analysis.model'; // Bloc analyse des ventes
import { BusinessHealth } from './business-health.model'; // Bloc santé business
import { Prediction } from './prediction.model'; // Bloc prédiction
import { Recommendations } from './recommendations.model'; // Bloc recommandations
import { Inventory } from './inventory.model'; // Bloc inventaire
import { StockPrediction } from './stock-prediction.model'; // Bloc prévision du stock
import { StockRecommendations } from './stock-recommendations.model'; // Bloc recommandations stock

// Contrat racine renvoyé par /api/ai/analysis
export interface AIResponse { // Représente tout le JSON IA
  sales_analysis: SalesAnalysis; // Bloc principal des ventes
  anomalies: Anomalies; // Bloc de détection d'anomalies
  prediction: Prediction; // Bloc de prévision
  recommendations: Recommendations; // Bloc de recommandations
  health_score: BusinessHealth; // Bloc de santé business
  inventory: Inventory; // Bloc inventaire
  stock_prediction: StockPrediction; // Bloc de prévision du stock
  stock_recommendations: StockRecommendations; // Bloc de recommandations stock
}