// Représente une anomalie détaillée
export interface Anomaly { // Une ligne d'anomalie détectée
  date: string; // Date de l'anomalie
  product: string; // Produit concerné
  value: number; // Valeur observée
  daily_revenue: number; // Revenu total du jour
  lower_bound: number; // Borne basse
  upper_bound: number; // Borne haute
  reference_value: number; // Valeur de référence
  severity: string; // Gravité : low, medium, high, critical 
  reason: string; // Explication métier
  recommendation: string; // Recommandation associée
  share_of_day_revenue: number; // Part du produit dans le revenu du jour
  deviation_ratio: number; // Ratio d'écart
  sources: string[]; // Sources de détection
  ml_score: number; // Score ML
}

// Représente le bloc "anomalies"
export interface Anomalies { // Ensemble des anomalies détectées
  lower_bound: number; // Borne basse globale
  upper_bound: number; // Borne haute globale
  anomalies: Anomaly[]; // Liste détaillée des anomalies
  ml_enabled: boolean; // Indique si le ML est actif
}