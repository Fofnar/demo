import { inject } from '@angular/core';
import { CanActivateFn, Router, UrlTree } from '@angular/router';
import { AuthService } from './auth.service';

export const guestGuard: CanActivateFn = (): boolean | UrlTree => {
  const authService = inject(AuthService);
  const router = inject(Router);

  // Si une session existe encore, on empêche l'accès à login/register
  if (authService.hasSession()) {
    if (authService.isAdmin()) {
      return router.createUrlTree(['/dashboard']);
    }

    return router.createUrlTree(['/sales']);
  }

  // Sinon accès autorisé aux pages publiques
  return true;
};