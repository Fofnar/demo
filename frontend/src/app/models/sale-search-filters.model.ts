export interface SaleSearchFilters { // Décrit les critères de recherche avancée des ventes
  product?: string; // Filtre par nom de produit
  stockLessThan?: number; // Filtre stock inférieur à une valeur donnée
  minStock?: number; // Stock minimum
  maxStock?: number; // Stock maximum
  minPrice?: number; // Prix minimum
  maxPrice?: number; // Prix maximum
  startDate?: string; // Date de début au format ISO
  endDate?: string; // Date de fin au format ISO
  keyword?: string; // Mot-clé global
  page?: number; // Numéro de page
  size?: number; // Taille de page
  sort?: string; // Tri appliqué
}