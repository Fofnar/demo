import { Injectable } from '@angular/core'; // Permet de rendre le service injectable partout
import { HttpClient } from '@angular/common/http'; // Sert à appeler les APIs HTTP
import { Observable } from 'rxjs'; // Permet de travailler avec les réponses asynchrones
import { ApiResponse } from '../models/api-response.model'; // Modèle générique de réponse API
import { AdminStatsDTO } from '../models/admin-stats.model'; // Modèle des statistiques admin


@Injectable({
  providedIn: 'root'
})
export class AdminService {

  // URL de base du contrôleur admin
  private readonly apiUrl = 'http://localhost:8080/api/admin';

  // Injection de HttpClient pour appeler le backend
  constructor(private http: HttpClient) {}

  // Méthode qui récupère les statistiques admin
  getStats(): Observable<ApiResponse<AdminStatsDTO>>{
    return this.http.get<ApiResponse<AdminStatsDTO>>(`${this.apiUrl}/stats`);
  }
}
