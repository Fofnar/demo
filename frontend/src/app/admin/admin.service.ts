import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { environment } from 'src/environments/environment';
import { ApiResponse } from '../models/api-response.model';
import { AdminStatsDTO } from '../models/admin-stats.model';

@Injectable({
  providedIn: 'root'
})
export class AdminService {

  // Point d'entrée du contrôleur admin.
  // L'URL de base est externalisée pour supporter le local et la production.
  private readonly apiUrl = `${environment.apiBaseUrl}/admin`;

  constructor(private http: HttpClient) {}

  getStats(): Observable<ApiResponse<AdminStatsDTO>> {
    return this.http.get<ApiResponse<AdminStatsDTO>>(`${this.apiUrl}/stats`);
  }
}