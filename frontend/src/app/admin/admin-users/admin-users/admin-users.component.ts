import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { AdminUserService } from '../../admin-user.service';
import { ErrorResponse } from 'src/app/models/error-response.model';
import { PagedResponse } from 'src/app/models/paged-response.model';
import { UserDTO } from 'src/app/models/user-dto.model';
import { Role } from 'src/app/models/role.model';

@Component({
  selector: 'app-admin-users',
  templateUrl: './admin-users.component.html',
  styleUrls: ['./admin-users.component.css']
})
export class AdminUsersComponent implements OnInit {

  filterForm: FormGroup; // Formulaire des filtres utilisateur
  users: UserDTO[] = []; // Liste des utilisateurs affichés
  isLoadingList: boolean = true; // Indique si la liste est en cours de chargement
  deletingId: number | null = null; // Identifiant de l'utilisateur en cours de suppression
  updatingRoleId: number | null = null; // Identifiant de l'utilisateur en cours de mise à jour de rôle
  errorMessage: string = ''; // Message d'erreur visible
  successMessage: string = ''; // Message de succès visible

  currentPage: number = 0; // Page courante
  pageSize: number = 10; // Taille de page
  totalPages: number = 0; // Nombre total de pages
  totalElements: number = 0; // Nombre total d'éléments

  readonly availableRoles: Role[] = ['ADMIN', 'USER']; // Rôles autorisés dans l'interface

  constructor(
    private fb: FormBuilder, // Injection du constructeur de formulaire
    private adminUserService: AdminUserService // Injection du service métier admin users
  ) {
    this.filterForm = this.fb.group({ // Initialisation du formulaire de filtres
      role: [''], // Filtre rôle
      search: [''] // Recherche globale
    });
  }

  ngOnInit(): void { // Méthode appelée au chargement du composant
    this.loadUsers(0); // Charge la première page
  }

  loadUsers(page: number): void { // Charge les utilisateurs selon la page courante et les filtres saisis
    this.isLoadingList = true; // Active le chargement
    this.errorMessage = ''; // Réinitialise le message d'erreur

    const role = this.filterForm.get('role')?.value as Role | ''; // Lit le filtre rôle
    const search = this.filterForm.get('search')?.value as string; // Lit le filtre de recherche

    this.adminUserService.getUsers(page, this.pageSize, role, search).subscribe({
      next: (response) => { // Cas où la réponse est correcte
        if (response.success && response.data) { // Vérifie que la réponse contient bien des données
          const paged: PagedResponse<UserDTO> = response.data; // Typage de la réponse paginée

          this.users = paged.content; // Utilisateurs de la page courante
          this.currentPage = paged.page; // Page courante
          this.pageSize = paged.size; // Taille de page renvoyée par l'API
          this.totalPages = paged.totalPages; // Nombre total de pages
          this.totalElements = paged.totalElements; // Nombre total d'éléments
        } else { // Cas de réponse inexploitable
          this.users = []; // Vide la liste
          this.errorMessage = 'No users available.'; // Message fallback
        }

        this.isLoadingList = false; // Désactive le chargement
      },
      error: (err) => { // Cas d'erreur backend ou réseau
        this.users = []; // Vide la liste
        this.errorMessage = this.extractErrorMessage(err.error); // Rend l'erreur lisible
        this.isLoadingList = false; // Désactive le chargement
      }
    });
  }

  applyFilters(): void { // Applique les filtres saisis
    this.successMessage = ''; // Réinitialise le message de succès
    this.loadUsers(0); // Recharge la liste à partir de la première page
  }

  resetFilters(): void { // Réinitialise les filtres utilisateur
    this.filterForm.reset({
      role: '', // Vide le rôle
      search: '' // Vide la recherche
    });

    this.errorMessage = ''; // Réinitialise les erreurs
    this.successMessage = ''; // Réinitialise les succès
    this.loadUsers(0); // Recharge sans filtre
  }

  goToPreviousPage(): void { // Va à la page précédente
    if (this.currentPage > 0) { // Vérifie qu'une page précédente existe
      this.loadUsers(this.currentPage - 1); // Charge la page précédente
    }
  }

  goToNextPage(): void { // Va à la page suivante
    if (this.currentPage + 1 < this.totalPages) { // Vérifie qu'une page suivante existe
      this.loadUsers(this.currentPage + 1); // Charge la page suivante
    }
  }

  updateRole(user: UserDTO, newRoleValue: string): void { // Met à jour le rôle d'un utilisateur
    const newRole = newRoleValue as Role; // Convertit la valeur HTML en type Role

    if (user.role === newRole) { // Évite un appel inutile si le rôle n'a pas changé
      return; // Stoppe l'exécution
    }

    this.updatingRoleId = user.id; // Mémorise l'utilisateur en cours de mise à jour
    this.errorMessage = ''; // Réinitialise l'erreur
    this.successMessage = ''; // Réinitialise le succès

    this.adminUserService.updateUserRole(user.id, { role: newRole }).subscribe({
      next: (response) => { // Cas succès
        if (response.success && response.data) { // Vérifie la présence des données mises à jour
          this.successMessage = 'User role updated successfully.'; // Message de succès

          this.users = this.users.map(existingUser => // Met à jour localement la liste affichée
            existingUser.id === user.id ? response.data : existingUser
          );
        } else { // Cas inattendu
          this.errorMessage = 'Role update failed.'; // Message fallback
        }

        this.updatingRoleId = null; // Libère l'état de mise à jour
      },
      error: (err) => { // Cas erreur
        this.errorMessage = this.extractErrorMessage(err.error); // Rend l'erreur lisible
        this.updatingRoleId = null; // Libère l'état de mise à jour
      }
    });
  }

  deleteUser(user: UserDTO): void { // Supprime un utilisateur
    const confirmed = confirm(`Delete user ${user.email}?`); // Demande de confirmation
    if (!confirmed) { // Si l'action est annulée
      return; // Stoppe l'exécution
    }

    this.deletingId = user.id; // Mémorise l'utilisateur en cours de suppression
    this.errorMessage = ''; // Réinitialise l'erreur
    this.successMessage = ''; // Réinitialise le succès

    this.adminUserService.deleteUser(user.id).subscribe({
      next: () => { // Cas succès
        this.successMessage = 'User deleted successfully.'; // Message de succès

        const nextPage = this.users.length === 1 && this.currentPage > 0
          ? this.currentPage - 1 // Revient à la page précédente si la page devient vide
          : this.currentPage; // Sinon recharge la page courante

        this.deletingId = null; // Libère l'état de suppression
        this.loadUsers(nextPage); // Recharge la liste
      },
      error: (err) => { // Cas erreur
        this.errorMessage = this.extractErrorMessage(err.error); // Rend l'erreur lisible
        this.deletingId = null; // Libère l'état de suppression
      }
    });
  }

  trackByUserId(index: number, user: UserDTO): number { // Optimise le rendu Angular de la liste
    return user.id; // Utilise l'identifiant comme clé stable
  }

  private extractErrorMessage(errorBody: unknown): string { // Transforme une erreur backend en message lisible
    const backendError = errorBody as ErrorResponse | string | null; // Tolère objet, string ou null

    if (!backendError) { // Cas vide
      return 'Unexpected error'; // Fallback générique
    }

    if (typeof backendError === 'object') { // Cas objet JSON
      if ('message' in backendError && typeof backendError.message === 'string') { // Champ message
        return backendError.message; // Retourne le message backend
      }

      if ('error' in backendError && typeof backendError.error === 'string') { // Champ error
        return backendError.error; // Retourne le message alternatif
      }
    }

    if (typeof backendError === 'string') { // Cas chaîne brute
      return backendError; // Retour direct
    }

    return 'Unexpected error'; // Fallback final
  }
}