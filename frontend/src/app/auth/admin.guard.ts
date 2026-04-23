import { inject } from '@angular/core';
import { CanActivateFn, Router, UrlTree } from '@angular/router';
import { AuthService } from './auth.service';

// Guard fonctionnel pour protéger les routes ADMIN
export const adminGuard: CanActivateFn = (): boolean | UrlTree => {

  const authService = inject(AuthService);
  const router = inject(Router);

  const tokenExists = authService.isLoggedIn();

  // Si l'utilisateur n'est pas connecté -> login
  if (!tokenExists) {
    return router.createUrlTree(['/login']);
  }
  
  const role = authService.getUserRole();
  if (role === 'ADMIN') {
    return true; // Accès autorisé
  }

  return router.createUrlTree(['/forbidden']); // Sinon on va vers la page 403
};