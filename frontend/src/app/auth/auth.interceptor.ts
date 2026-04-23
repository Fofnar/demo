import { inject, Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpEvent,
  HttpErrorResponse,
  HttpInterceptorFn,
  HttpHandlerFn
} from '@angular/common/http';
import { Router } from '@angular/router';
import { catchError, Observable, switchMap, throwError } from 'rxjs';
import { ErrorResponse } from '../models/error-response.model';
import { AuthService } from './auth.service';

//Guard pour les routes Auth
export const authInterceptor: HttpInterceptorFn=(
  request: HttpRequest<any>,
  next: HttpHandlerFn
): Observable<HttpEvent<any>> => {

  const router = inject(Router);
  const authService = inject(AuthService);

  const token = authService.getAccessToken();

  let authReq = request;

  if(token){
    authReq = request.clone({ // On clone la requête, car HttpRequest est immuable
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
  }

  //On en
  return next(authReq).pipe(
    catchError((err: HttpErrorResponse) =>{
      const isAuthEndpoint = 
        request.url.includes('/api/auth/login') ||
        request.url.includes('/api/auth/register') ||
        request.url.includes('/api/auth/refresh');

      // Si le token est expiré ou invalide sur une route protégée
      //On tente un refresh token
      if (err.status === 401 && !isAuthEndpoint){
        return authService.refreshAccessToken().pipe(
          switchMap((newToken: string) =>{
            const retryReq = request.clone({
              setHeaders:{
                Authorization: `Bearer ${newToken}`
              } 
            });

            return next(retryReq);
          }),
          //Si le refresh echoue -> login
          catchError((refreshErr: HttpErrorResponse) =>{
            authService.logout();
            router.navigateByUrl('/login', {replaceUrl: true});

            return throwError(() => refreshErr);
          })
        );
      }

      if (request.url.includes('/api/auth/refresh')) { // Si le refresh lui-même échoue
        authService.logout(); // On supprime les tokens
        router.navigateByUrl('/login', { replaceUrl: true }); // On redirige vers login
        return throwError(() => err); // On relance l'erreur
      }

      if (isAuthEndpoint) { // Pour login/register, on laisse le composant afficher l'erreur
        return throwError(() => err); // On ne bloque pas l’erreur ici
      }

      alert(extractErrorMessage(err.error)); // Pour les autres pages, on affiche le message global
      return throwError(() => err); // On relance l'erreur

    })
  );
};

function extractErrorMessage(errorBody: unknown): string{

  // On tolère plusieurs formes d'erreur
  const backendError = errorBody as ErrorResponse | string | null;

  // on élimine null d'abord
  if (!backendError) {
    return 'Unexpected error';
  }

  // Si c'est un objet
  if (typeof backendError === 'object' && backendError !== null){

    // Cas field message
    if('message' in backendError && typeof backendError.message === 'string'){
      return backendError.message;
    }

    //Cas field error
    if ('error' in backendError && typeof backendError.error === 'string'){
      return backendError.error;
    }
  }

  // Si le backend envoie une string brute
  if (typeof backendError === 'string'){
    return backendError;
  }

  return 'Unexpected error'; //Fallback générique

}