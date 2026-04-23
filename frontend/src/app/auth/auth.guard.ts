import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { catchError, map, of } from 'rxjs';
import { AuthService } from './auth.service';

export const authGuard: CanActivateFn = () => {

  // On injecte le service d'auth et le router
  const authService = inject(AuthService);
  const router = inject(Router);

  // Si on a déjà un access token, on autorise l'accès
  if (authService.isLoggedIn()) {
    return true;
  }

  // Si même le refresh token a disparu (pas de session), on redirige vers login
  if (!authService.hasSession()) {
    return router.createUrlTree(['/login']);
  }

  // Si refresh token existe, on tente de régénérer un access token
  return authService.refreshAccessToken().pipe(
    map(() => true),
    catchError(() => {
      authService.logout();
      return of(router.createUrlTree(['/login']));
    })
  );
};