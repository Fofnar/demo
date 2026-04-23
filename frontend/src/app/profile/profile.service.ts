import { Injectable } from '@angular/core'; 
import { HttpClient } from '@angular/common/http'; 
import { Observable } from 'rxjs'; 
import { ApiResponse } from '../models/api-response.model'; 
import { UserDTO } from '../models/user-dto.model'; 
import { UpdateUserRequest } from '../models/update-user-request.model'; 

@Injectable({
  providedIn: 'root' // Le service est disponible partout
})
export class ProfileService {

  private readonly apiUrl = 'http://localhost:8080/api/users'; // URL de base du contrôleur user

  constructor(private http: HttpClient) {} // Injection du client HTTP

  getCurrentUser(): Observable<ApiResponse<UserDTO>> { // Récupère le profil du user connecté
    return this.http.get<ApiResponse<UserDTO>>(`${this.apiUrl}/me`); // GET /api/users/me
  }

  updateCurrentUser(payload: UpdateUserRequest): Observable<ApiResponse<UserDTO>> { // Met à jour le profil
    return this.http.put<ApiResponse<UserDTO>>(`${this.apiUrl}/me`, payload); // PUT /api/users/me
  }
}