// Représente une ligne de chiffre d'affaires par jour
export interface RevenuePerDay { // Utilisé pour le graphique des revenus
  date: string; // Date au format ISO simple yyyy-MM-dd
  revenue: number; // Revenu total de la journée
}

// Représente le bloc "sales_analysis"
export interface SalesAnalysis { // Analyse globale des ventes
  total_revenue: number; // Chiffre d'affaires total
  total_quantity_sold: number; // Quantité totale vendue
  average_order_value: number; // Valeur moyenne d'une commande
  unique_products: number; // Nombre de produits distincts
  unique_days: number; // Nombre de jours analysés
  top_selling_product: string; // Produit le plus vendu en volume
  top_selling_quantity: number; // Quantité vendue du produit leader
  top_revenue_product: string; // Produit générant le plus de revenus
  top_revenue_value: number; // Revenu généré par ce produit
  trend: string; // Tendance globale : upward, downward ou stable
  trend_slope: number; // Pente de tendance
  revenue_per_day: RevenuePerDay[]; // Série quotidienne pour graphique
  sales_comment: string; // Résumé métier généré par l'IA
  recommendations: string[]; // Recommandations simples
}