import { Component } from '@angular/core'; 
import { FormBuilder, FormGroup, Validators } from '@angular/forms'; 
import { Router } from '@angular/router'
import { AuthService } from '../auth/auth.service'; 
import { ErrorResponse } from '../models/error-response.model'; 
import { RegisterRequest } from '../models/register-request.model';

@Component({
  selector: 'app-register', // Balise HTML du composant
  templateUrl: './register.component.html', // Template HTML
  styleUrls: ['./register.component.css'] // Styles CSS
})
export class RegisterComponent {

  registerForm: FormGroup; // Formulaire d'inscription
  isLoading: boolean = false; // Indique si l'appel API est en cours
  successMessage: string = ''; // Message de succès après inscription
  errorMessage: string = ''; // Message d'erreur à afficher

  constructor(
    private fb: FormBuilder, // Permet de créer le formulaire
    private authService: AuthService, // Appels backend auth
    private router: Router // Navigation
  ) {
    this.registerForm = this.fb.group({ // Création du formulaire
      email: ['', [Validators.required, Validators.email]], // Email requis et valide
      firstName: ['', [Validators.required, Validators.minLength(2)]], // Prénom requis
      lastName: ['', [Validators.required, Validators.minLength(2)]], // Nom requis
      age: ['', [Validators.required, Validators.min(1)]], // Âge requis
      phone: ['', [Validators.required, Validators.minLength(8)]], // Téléphone requis
      password: ['', [Validators.required, Validators.minLength(8)]], // Mot de passe requis
      confirmPassword: ['', [Validators.required]] // Confirmation mot de passe
    });
  }

  onSubmit(): void { // Appel au submit
    if (this.registerForm.invalid) { // Si le formulaire est invalide
      this.errorMessage = 'Please fill in all required fields correctly.'; // Message utilisateur
      return; // On arrête
    }

    const password = this.registerForm.value.password; // Mot de passe saisi
    const confirmPassword = this.registerForm.value.confirmPassword; // Confirmation saisie

    if (password !== confirmPassword) { // Vérifie la correspondance des mots de passe
      this.errorMessage = 'Passwords do not match.'; // Message d'erreur
      return; // On arrête
    }

    this.isLoading = true; // On active le loading
    this.errorMessage = ''; // On vide les anciens messages
    this.successMessage = ''; // On vide l'ancien message de succès

    const payload: RegisterRequest = { // Construction du payload backend
      email: this.registerForm.value.email, // Email
      firstName: this.registerForm.value.firstName, // Prénom
      lastName: this.registerForm.value.lastName, // Nom
      age: Number(this.registerForm.value.age), // Conversion en nombre
      password: this.registerForm.value.password, // Mot de passe
      phone: this.registerForm.value.phone // Téléphone
    };

    this.authService.register(payload).subscribe({ // Appel POST /register
      next: (response) => { // Si l'inscription a réussi
        if (response.success) { // Si le backend confirme le succès
          this.successMessage = 'Account created successfully. Redirecting to login...'; // Message succès
          this.registerForm.reset(); // Vide le formulaire
          this.isLoading = false; // Coupe le loading

          setTimeout(() => { // Petite pause pour laisser voir le succès
            this.router.navigateByUrl('/login'); // Redirection vers login
          }, 1500);
        } else { // Si response.success est false
          this.errorMessage = 'Registration failed.'; // Message fallback
          this.isLoading = false; // Coupe le loading
        }
      },
      error: (err) => { // Si le backend renvoie une erreur
        this.errorMessage = this.extractErrorMessage(err.error); // Lit le message backend
        this.isLoading = false; // Coupe le loading
      }
    });
  }

  onReset(): void { // Réinitialisation
    this.registerForm.reset(); // Vide le formulaire
    this.errorMessage = ''; // Vide erreur
    this.successMessage = ''; // Vide succès
  }

  private extractErrorMessage(errorBody: unknown): string { // Extraction sûre du message backend
    const backendError = errorBody as ErrorResponse | null; // Tolère l'objet d'erreur

    if (typeof backendError === 'object' && backendError !== null) { // Si c'est un objet
      if ('message' in backendError && typeof backendError.message === 'string') { // Cas message
        return backendError.message; // Renvoie message
      }

      if ('error' in backendError && typeof backendError.error === 'string') { // Cas error
        return backendError.error; // Renvoie error
      }
    }

    if (typeof errorBody === 'string') { // Si le backend renvoie une string
      return errorBody; // Renvoie la string
    }

    return 'Registration failed.'; // Fallback
  }
}