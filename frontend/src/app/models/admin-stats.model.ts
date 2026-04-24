// Interface qui décrit la forme des statistiques renvoyées par le backend
export interface AdminStatsDTO {
  // Nombre total d'utilisateurs dans l'application
  totalUsers: number;

  // Nombre total de ventes enregistrées
  totalSales: number;

  // Nombre de ventes réalisées aujourd'hui
  salesToday: number;

  // Nombre total de produits différents
  totalProducts: number;

  // Nombre de produits avec un stock faible
  lowStockProducts: number;
}