import { Component } from '@angular/core'; // Décorateur Angular
import { FormBuilder, FormGroup, Validators } from '@angular/forms'; // Construction du formulaire réactif
import { Router } from '@angular/router'; // Navigation après login réussi
import { AuthService } from '../auth/auth.service'; // Service auth centralisé
import { ErrorResponse } from '../models/error-response.model'; // Type d'erreur backend

@Component({
  selector: 'app-login', // Sélecteur HTML
  templateUrl: './login.component.html', // Template HTML
  styleUrls: ['./login.component.css'] // Styles CSS
})
export class LoginComponent {

  loginForm: FormGroup; // Objet du formulaire
  isLoading: boolean = false; // Indique si la connexion est en cours
  errorMessage: string = ''; // Message d’erreur à afficher dans la page

  constructor(
    private fb: FormBuilder, // Injection du builder de formulaire
    private authService: AuthService, // Injection du service d'auth
    private router: Router // Injection du routeur
  ) {
    this.loginForm = this.fb.group({ // Création du formulaire
      email: ['', [Validators.required, Validators.email]], // Valeur initiale du champ email
      password: ['', [Validators.required]] // Valeur initiale du champ password
    });
  }

  onSubmit(): void { // Méthode appelée au submit
    if (this.loginForm.invalid) { // Si le formulaire est invalide
      this.errorMessage = 'Please fill in both fields.'; // Message utilisateur
      return; // On stoppe l’exécution
    }

    this.isLoading = true; // On active l’état chargement
    this.errorMessage = ''; // On efface l’ancien message

    const formData = this.loginForm.value as { email: string; password: string }; // On lit les données du formulaire

    this.authService.login(formData).subscribe({ // Appel du backend via le service
      next: (response) => { // Réponse réussie
        if (response.success && response.data) { // Si le backend confirme le succès
          this.authService.saveTokens( // On stocke les tokens
            response.data.accessToken, // Access token
            response.data.refreshToken // Refresh token
          );
          
          //Si Admin
          if (this.authService.isAdmin()){
            this.router.navigateByUrl('/dashboard'); // Redirection vers le dashboard
          } else{
            this.router.navigateByUrl('/sales'); // User -> Sales
          }
          
        } else { // Si la réponse n’est pas exploitable
          this.errorMessage = 'Login failed.'; // Message fallback
        }

        this.isLoading = false; // On coupe le chargement
      },
      error: (err) => { // Réponse en erreur
        this.errorMessage = this.extractErrorMessage(err.error); // On lit le message backend
        this.isLoading = false; // On coupe le chargement
      }
    });
  }

  onReset(): void { // Réinitialisation du formulaire
    this.loginForm.reset(); // Vide tous les champs
    this.errorMessage = ''; // Efface le message d'erreur
  }

  private extractErrorMessage(errorBody: unknown): string { // Lecture robuste du message backend
    const backendError = errorBody as ErrorResponse | null; // On tolère l’objet d’erreur

    if (typeof backendError === 'object' && backendError !== null) { // Si c’est un objet
      if ('message' in backendError && typeof backendError.message === 'string') { // Cas message
        return backendError.message; // On renvoie message
      }

      if ('error' in backendError && typeof backendError.error === 'string') { // Cas error
        return backendError.error; // On renvoie error
      }
    }

    if (typeof errorBody === 'string') { // Si le backend envoie une string
      return errorBody; // On la renvoie
    }

    return 'Login failed.'; // Fallback
  }
}