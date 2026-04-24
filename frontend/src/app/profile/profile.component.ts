import { Component, OnInit } from '@angular/core'; 
import { FormBuilder, FormGroup, Validators } from '@angular/forms'; 
import { Router } from '@angular/router'; 
import { ProfileService } from './profile.service'; 
import { ErrorResponse } from '../models/error-response.model'; 
import { UpdateUserRequest } from '../models/update-user-request.model'; 
import { UserDTO } from '../models/user-dto.model'; 
import { AuthService } from '../auth/auth.service'; 

@Component({
  selector: 'app-profile', // Balise HTML du composant
  templateUrl: './profile.component.html', // Template HTML
  styleUrls: ['./profile.component.css'] // Styles CSS
})
export class ProfileComponent implements OnInit {

  profileForm: FormGroup; // Formulaire du profil
  isLoading: boolean = true; // État de chargement
  errorMessage: string = ''; // Message d'erreur
  successMessage: string = ''; // Message de succès
  currentUser: UserDTO | null = null; // Données du profil chargées
  originalEmail: string = ''; // Email initial pour détecter un changement

  constructor(
    private fb: FormBuilder, // Construction du formulaire
    private profileService: ProfileService, // Appels API profil
    private authService: AuthService, // Déconnexion si besoin
    private router: Router // Navigation
  ) {
    this.profileForm = this.fb.group({ // Création du formulaire
      email: ['', [Validators.required, Validators.email]], // Email obligatoire et valide
      firstName: ['', [Validators.required, Validators.minLength(2)]], // Prénom obligatoire
      lastName: ['', [Validators.required, Validators.minLength(2)]], // Nom obligatoire
      age: ['', [Validators.required, Validators.min(0)]], // Âge obligatoire
      phone: ['', [Validators.required, Validators.minLength(6)]], // Téléphone obligatoire
      password: ['', [Validators.minLength(8)]] // Nouveau mot de passe optionnel
    });
  }

  ngOnInit(): void { // Appelé au chargement du composant
    this.loadProfile(); // On charge le profil depuis le backend
  }

  loadProfile(): void { // Charge les infos utilisateur
    this.isLoading = true; // Active le chargement
    this.errorMessage = ''; // Vide les anciens messages

    this.profileService.getCurrentUser().subscribe({ // Appel GET /api/users/me
      next: (response) => { // Si la réponse est OK
        if (response.success && response.data) { // Si le backend renvoie des données
          this.currentUser = response.data; // On stocke le user
          this.originalEmail = response.data.email; // On garde l'email initial

          this.profileForm.patchValue({ // On remplit le formulaire avec les données reçues
            email: response.data.email, // Email
            firstName: response.data.firstName, // Prénom
            lastName: response.data.lastName, // Nom
            age: response.data.age, // Âge
            phone: response.data.phone, // Téléphone
            password: '' // On laisse le mot de passe vide
          });
        } else { // Si réponse inattendue
          this.errorMessage = 'Unable to load profile.'; // Message fallback
        }

        this.isLoading = false; // On coupe le chargement
      },
      error: (err) => { // Si le backend renvoie une erreur
        this.errorMessage = this.extractErrorMessage(err.error); // On lit le message backend
        this.isLoading = false; // On coupe le chargement
      }
    });
  }

  onSubmit(): void { // Envoi du formulaire
    if (this.profileForm.invalid) { // Si le formulaire est invalide
      this.errorMessage = 'Please fill in all required fields correctly.'; // Message utilisateur
      return; // On arrête
    }

    this.isLoading = true; // On active le chargement
    this.errorMessage = ''; // On vide l'erreur
    this.successMessage = ''; // On vide le succès

    const raw = this.profileForm.getRawValue(); // Récupère toutes les valeurs du formulaire

    const payload: UpdateUserRequest = { // Construction du body envoyé au backend
      email: raw.email, // Email
      firstName: raw.firstName, // Prénom
      lastName: raw.lastName, // Nom
      age: Number(raw.age), // Conversion en nombre
      phone: raw.phone, // Téléphone
      ...(raw.password && raw.password.trim() !== '' ? { password: raw.password } : {}) // Ajoute password seulement si rempli
    };

    this.profileService.updateCurrentUser(payload).subscribe({ // Appel PUT /api/users/me
      next: (response) => { // Si la mise à jour réussit
        if (response.success && response.data) { // Si le backend confirme
          this.currentUser = response.data; // Mise à jour locale
          this.successMessage = 'Profile updated successfully.'; // Message succès

          const emailChanged = payload.email !== this.originalEmail; // Vérifie si l'email a changé
          const passwordChanged = !!payload.password; // Vérifie si un nouveau mot de passe a été saisi

          this.isLoading = false; // On coupe le chargement

          if (emailChanged || passwordChanged) { // Si identifiants modifiés
            setTimeout(() => { // Petite pause pour lire le message
              this.authService.logout(); // On supprime les tokens
              this.router.navigateByUrl('/login', { replaceUrl: true }); // Retour login
            }, 1200);
          } else { // Si seule la partie profil a changé
            this.originalEmail = response.data.email; // On met à jour l'email initial
          }
        } else { // Si réponse inattendue
          this.errorMessage = 'Profile update failed.'; // Message fallback
          this.isLoading = false; // On coupe le chargement
        }
      },
      error: (err) => { // Si le backend renvoie une erreur
        this.errorMessage = this.extractErrorMessage(err.error); // On affiche le message backend
        this.isLoading = false; // On coupe le chargement
      }
    });
  }

  onReset(): void { // Réinitialise le formulaire
    if (this.currentUser) { // Si on a déjà chargé un profil
      this.profileForm.patchValue({ // On remet les valeurs d'origine
        email: this.currentUser.email, // Email
        firstName: this.currentUser.firstName, // Prénom
        lastName: this.currentUser.lastName, // Nom
        age: this.currentUser.age, // Âge
        phone: this.currentUser.phone, // Téléphone
        password: '' // Mot de passe vide
      });
    }

    this.errorMessage = ''; // Vide l'erreur
    this.successMessage = ''; // Vide le succès
  }

  private extractErrorMessage(errorBody: unknown): string { // Transforme l'erreur backend en message lisible
    const backendError = errorBody as ErrorResponse | null; // Tolère l'objet d'erreur

    if (typeof backendError === 'object' && backendError !== null) { // Si c'est un objet
      if ('message' in backendError && typeof backendError.message === 'string') { // Cas message
        return backendError.message; // Renvoie le message
      }

      if ('error' in backendError && typeof backendError.error === 'string') { // Cas error
        return backendError.error; // Renvoie l'erreur
      }
    }

    if (typeof errorBody === 'string') { // Si backend envoie une string
      return errorBody; // On la renvoie
    }

    return 'Profile update failed.'; // Fallback
  }
}