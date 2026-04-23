export interface RegisterRequest { // Décrit les données envoyées au endpoint /register
  email: string; // Email de l'utilisateur
  firstName: string; // Prénom
  lastName: string; // Nom
  age: number; // Âge
  password: string; // Mot de passe
  phone: string; // Numéro de téléphone
}