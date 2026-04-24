import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, throwError } from 'rxjs';
import { map } from 'rxjs/operators';
import { ApiResponse } from '../models/api-response.model'; 
import { AuthResponse } from '../models/auth-response.model'; 
import { RegisterRequest } from '../models/register-request.model';
import { UserDTO } from '../models/user-dto.model';
import { environment } from 'src/environments/environment';

// Représente le payload décodé du JWT
interface JwtPayload {
  role?: string; // Rôle stocké dans le token, par exemple ADMIN
  exp?: number; // Date d'expiration du token
  [key: string]: unknown; // Autorise d'autres champs éventuels dans le token
}

@Injectable({
  providedIn: 'root' // Le service est disponible partout sans le re-déclarer dans un module
})
export class AuthService {

  // Point d'entrée du module d'authentification.
  // L'URL est externalisée pour éviter les valeurs localhost en production.
  private readonly apiUrl = `${environment.apiBaseUrl}/auth`;

  // Injection du client HTTP
  constructor(private http : HttpClient) {} 

  //Appel l'API /login du backend
  login(payload: {email:string; password: string}): Observable<ApiResponse<AuthResponse>>{
    return this.http.post<ApiResponse<AuthResponse>>(`${this.apiUrl}/login`, payload);
  }

  //Appel register
  register(payload: RegisterRequest): Observable<ApiResponse<UserDTO>>{
    return this.http.post<ApiResponse<UserDTO>>(`${this.apiUrl}/register`, payload);
  }

  //Récupère l'accesstoken stocké
  getAccessToken(): string | null {
    return localStorage.getItem('token');
  }


  //Récupère le refresh token stocké
  getRefreshToken(): string | null {
    return localStorage.getItem('refreshToken');
  }

  //Verifie si le token est valide
  isLoggedIn(): boolean{
    const token = this.getAccessToken();
    if (!token) return false;

    const payload = this.decodeTokenPayload(token);
    if (!payload || !payload.exp) return false; // Pas connecté

    const now = Math.floor(Date.now()/1000); // en secondes

    return payload.exp > now; // Vrai si le token n'est pas expiré
  }

  //Verifie si une session existe
  hasSession(): boolean{
    const accessToken = this.getAccessToken();
    const refreshToken = this.getRefreshToken();

    const now = Math.floor(Date.now()/1000); // Temps actuel

    // Vérifie access token
    if(accessToken) {
      const payloadAccess = this.decodeTokenPayload(accessToken)

      if (payloadAccess?.exp && payloadAccess.exp > now){
        return true; //session active
      }
    }

    //Verifie refresh token
    if (refreshToken){
      const payloadRefresh = this.decodeTokenPayload(refreshToken);

      if(payloadRefresh?.exp && payloadRefresh.exp > now){
        return true; // session récupérable
      }
    }

    return false; // plus rien de valide
  }

  //Recupère le rôle
  getUserRole(): string | null{
    const token= this.getAccessToken();
    if (!token){
      return null; // pas de rôle
    }

    const payload = this.decodeTokenPayload(token);
    
    //Si decodage echoue
    if (!payload || typeof payload.role !== 'string'){
      return null;
    }

    if (typeof payload.role === 'string'){
      return payload.role;
    }

    return null; //Sinon on ne troupe pas de rôle exploitable

  }

  //Verifie si le rôle est ADMIN
  isAdmin(): boolean{
    return this.getUserRole() === 'ADMIN';
  }

  //Decode le token JWT
  private decodeTokenPayload(token: string): JwtPayload | null{
    try{
      const base64Url = token.split('.')[1]; // On récupère la partie payload du JWT
      const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/'); // Conversion en base64 standard
      const paddedBase64 = base64.padEnd(base64.length + ((4 - base64.length % 4) % 4), '='); // On ajoute le padding manquant
      const jsonPayload = atob(paddedBase64); // On décode la chaîne base64
      return JSON.parse(jsonPayload) as JwtPayload; // On transforme le JSON en objet
    }catch{
      return null; //Si le token est cassé ou illisible
    }
  }

  //Sauvegarde les tokens
  saveTokens(accessToken: string, refreshToken: string): void {
    localStorage.setItem('token', accessToken);
    localStorage.setItem('refreshToken', refreshToken);
  }

  //Supprime les tokens
  logout(): void{
    localStorage.removeItem('token');
    localStorage.removeItem('refreshToken');
  }

  //Demande un nouvelle access token au backen
  refreshAccessToken(): Observable<string>{
    const refreshToken = this.getRefreshToken();

    //si on a pas de refresh token, on arrête tout
    if(!refreshToken){
      return throwError(()=> new Error('No refresh Token found'));
    }

    return this.http.post<ApiResponse<AuthResponse>>(
      `${this.apiUrl}/refresh`, {refreshToken}
    ).pipe(
      map((response) =>{
        //On récupère le nouveau token
        const newAccessToken = response.data.accessToken;

        //On récupère le refresh token mis à jour
        const newRefreshToken = response.data.refreshToken;

        //On remplace les anciens tokens
        this.saveTokens(newAccessToken, newRefreshToken);

        //On renvoie juste l'access token à l'interceptor
        return newAccessToken;
      })
    );
  }
 
}
