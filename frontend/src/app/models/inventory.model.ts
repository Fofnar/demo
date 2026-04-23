// Représente une alerte de stock pour un produit
export interface InventoryAlert { // Bloc low_stock_alerts
  product: string; // Nom du produit concerné
  stock: number; // Stock restant
  stock_level: string; // Niveau de stock : critical, low, healthy...
  warning: string; // Message d'alerte court
  recommendation: string; // Recommandation métier
  has_related_anomaly: boolean; // Indique un lien avec une anomalie
  related_anomaly_count: number; // Nombre d'anomalies liées
  related_anomaly_severity: string; // Sévérité maximale des anomalies liées
}

// Représente le bloc inventory complet
export interface Inventory { // Bloc inventory
  total_products: number; // Nombre total de produits analysés
  low_stock_count: number; // Nombre de produits en stock faible
  critical_stock_count: number; // Nombre de produits en stock critique
  out_of_stock_count: number; // Nombre de produits en rupture
  inventory_comment: string; // Commentaire métier sur l'inventaire
  business_comment: string; // Commentaire global sur la santé du stock
  low_stock_alerts: InventoryAlert[]; // Liste des alertes de stock
  recommendations: string[]; // Recommandations globales
}