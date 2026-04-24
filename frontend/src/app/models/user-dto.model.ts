export interface UserDTO { // Décrit l'utilisateur renvoyé par le backend
  id: number; // Identifiant utilisateur
  email: string; // Email
  lastName: string; // Nom
  firstName: string; // Prénom
  age: number; // Âge
  phone: string; // Téléphone
  role: string; // Rôle utilisateur
}