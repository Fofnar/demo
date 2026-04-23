import { Component } from '@angular/core';
import { Router } from '@angular/router'; 
import { AuthService } from './auth/auth.service'; 

@Component({
  selector: 'app-root', // Racine Angular
  templateUrl: './app.component.html', // Template global
  styleUrls: ['./app.component.css'] // CSS global du shell
})
export class AppComponent {

  constructor(
    private authService: AuthService, // Injection du service auth
    private router: Router // Injection du routeur
  ) {}

  isLoggedIn(): boolean { // Vérifie si un utilisateur est connecté
    return this.authService.isLoggedIn(); // Retour du service
  }

  // Vérifie si l'utilisateur est connecté ou a encore une session
  hasSession(): boolean{
    return this.authService.hasSession();
  }

  isAdmin(): boolean { // Vérifie si l'utilisateur est admin
    return this.authService.isAdmin(); // Retour du service
  }

  logout(): void { // Déconnexion globale
    this.authService.logout(); // Suppression des tokens
    this.router.navigateByUrl('/login', { replaceUrl: true }); // Retour au login
  }
}