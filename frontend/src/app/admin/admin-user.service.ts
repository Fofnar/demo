import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse } from '../models/api-response.model';
import { PagedResponse } from '../models/paged-response.model';
import { UserDTO } from '../models/user-dto.model';
import { UpdateRoleRequest } from '../models/update-role-request.model';
import { Role } from '../models/role.model';

@Injectable({
  providedIn: 'root' // Rend le service disponible dans toute l'application
})
export class AdminUserService {

  private readonly apiUrl = 'http://localhost:8080/api/admin/users'; // URL de base du contrôleur admin users

  constructor(private http: HttpClient) {} // Injection du client HTTP Angular

  getUsers(
    page: number,
    size: number,
    role?: Role | '',
    search?: string
  ): Observable<ApiResponse<PagedResponse<UserDTO>>> { // Charge une page d'utilisateurs avec filtres optionnels
    let params = new HttpParams() // Initialise les paramètres HTTP
      .set('page', page)
      .set('size', size)
      .set('sort', 'id,desc');

    if (role && role.trim() !== '') { // Ajoute le filtre rôle seulement s'il existe
      params = params.set('role', role);
    }

    if (search && search.trim() !== '') { // Ajoute le filtre de recherche seulement s'il existe
      params = params.set('search', search.trim());
    }

    return this.http.get<ApiResponse<PagedResponse<UserDTO>>>(this.apiUrl, { params }); // Appel GET paginé
  }

  deleteUser(id: number): Observable<ApiResponse<void>> { // Supprime un utilisateur par identifiant
    return this.http.delete<ApiResponse<void>>(`${this.apiUrl}/${id}`); // Appel DELETE
  }

  updateUserRole(id: number, payload: UpdateRoleRequest): Observable<ApiResponse<UserDTO>> { // Modifie le rôle d'un utilisateur
    return this.http.put<ApiResponse<UserDTO>>(`${this.apiUrl}/${id}/role`, payload); // Appel PUT
  }
}