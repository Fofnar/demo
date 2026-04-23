import { Injectable } from '@angular/core'; // Rend le service injectable dans l'application
import { HttpClient } from '@angular/common/http'; // Permet d'effectuer des requêtes HTTP
import { Observable } from 'rxjs'; // Représente une réponse asynchrone
import { ApiResponse } from '../../models/api-response.model'; // Contrat standard de réponse API
import { AIResponse } from '../../models/ai-response.model'; // Contrat global de l'analyse IA

@Injectable({
  providedIn: 'root' // Service disponible partout sans déclaration supplémentaire
})
export class AiService {

  private readonly apiUrl = 'http://localhost:8080/api/ai'; // URL de base du contrôleur IA

  constructor(private http: HttpClient) {} // Injection du client HTTP Angular

  getAnalysis(): Observable<ApiResponse<AIResponse>> { // Appel de l'API principale IA
    return this.http.get<ApiResponse<AIResponse>>(`${this.apiUrl}/analysis`); // GET /api/ai/analysis
  }
}