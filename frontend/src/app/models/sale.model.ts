export interface Sale { // Décrit une vente renvoyée par le backend
  id: number; // Identifiant technique de la vente
  product: string; // Nom du produit vendu
  quantity: number; // Quantité vendue
  price: number; // Prix unitaire
  saleDate: string; // Date de la vente au format ISO envoyé par le backend
  stock: number; // Stock restant après la vente
}