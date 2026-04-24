import { Role } from "./role.model"; // Enum des rôles utilisables dans le frontend

export interface UpdateRoleRequest { // Payload envoyé au backend pour modifier le rôle
  role: Role; // Nouveau rôle à appliquer
}