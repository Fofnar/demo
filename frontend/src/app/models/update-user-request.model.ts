export interface UpdateUserRequest { // Décrit le body envoyé au endpoint PUT /api/users/me
  email: string; // Email du compte
  firstName: string; // Prénom
  lastName: string; // Nom
  age: number; // Âge
  phone: string; // Numéro de téléphone
  password?: string; // Nouveau mot de passe optionnel
}