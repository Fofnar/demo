// Représente le détail des sous-scores de santé business
export interface HealthBreakdown { // Bloc health_breakdown
  trend_score: number; // Score lié à la tendance
  stability_score: number; // Score lié à la stabilité
  growth_score: number; // Score lié à la croissance
  diversification_score: number; // Score lié à la diversification
  anomaly_penalty: number; // Pénalité liée aux anomalies
}

// Représente le détail textuel des sous-scores business
export interface HealthDetails { // Bloc health_details
  trend_comment: string; // Commentaire sur la tendance
  stability_comment: string; // Commentaire sur la stabilité
  growth_comment: string; // Commentaire sur la croissance
  diversification_comment: string; // Commentaire sur la diversification
  anomaly_comment: string; // Commentaire sur les anomalies
}

// Représente le bloc principal health_score
export interface BusinessHealth { // Santé globale du business
  health_score: number; // Score global sur 100
  health_status: string; // Statut lisible : weak, good, etc.
  health_breakdown: HealthBreakdown; // Détail des sous-scores
  health_details: HealthDetails; // Détail textuel des sous-scores
  business_comment: string; // Commentaire métier global
  recommendations: string[]; // Recommandations générales
  anomalies_count: number; // Nombre total d'anomalies
  high_priority_anomalies: number; // Nombre d'anomalies prioritaires
}