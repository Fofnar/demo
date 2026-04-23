export interface SaleRequest { // Décrit le body envoyé au endpoint POST /api/sales
  date: string; // Date de vente au format ISO
  product: string; // Nom du produit
  price: number; // Prix
  quantity: number; // Quantité
  stock: number; // Stock restant
}