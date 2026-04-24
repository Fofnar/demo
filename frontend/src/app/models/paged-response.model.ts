export interface PagedResponse<T> { // Décrit une réponse paginée générique
  content: T[]; // Liste des éléments de la page courante
  page: number; // Numéro de page courant
  size: number; // Taille de page
  totalElements: number; // Nombre total d'éléments
  totalPages: number; // Nombre total de pages
}