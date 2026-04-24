import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { environment } from 'src/environments/environment';
import { ApiResponse } from '../../models/api-response.model';
import { AIResponse } from '../../models/ai-response.model';

@Injectable({
  providedIn: 'root'
})
export class AiService {

  // Point d'entrée du contrôleur IA exposé par le backend Spring Boot.
  // Le service FastAPI reste appelé uniquement par le backend.
  private readonly apiUrl = `${environment.apiBaseUrl}/ai`;

  constructor(private http: HttpClient) {}

  getAnalysis(): Observable<ApiResponse<AIResponse>> {
    return this.http.get<ApiResponse<AIResponse>>(`${this.apiUrl}/analysis`);
  }
}