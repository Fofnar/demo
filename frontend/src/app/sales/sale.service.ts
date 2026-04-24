import { Injectable } from '@angular/core'; // Rend le service injectable dans toute l'application
import { HttpClient, HttpParams } from '@angular/common/http'; // Permet les requêtes HTTP et la construction des paramètres d'URL
import { Observable } from 'rxjs'; // Représente les flux asynchrones Angular
import { ApiResponse } from '../models/api-response.model'; // Type standard des réponses backend
import { Sale } from '../models/sale.model'; // Modèle d'une vente
import { SaleRequest } from '../models/sale-request.model'; // Modèle de création d'une vente
import { PagedResponse } from '../models/paged-response.model'; // Modèle de réponse paginée
import { SaleSearchFilters } from '../models/sale-search-filters.model'; // Modèle des filtres admin
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root' // Service disponible globalement
})
export class SaleService {

  // L'URL est externalisée pour éviter les valeurs localhost en production.
  private readonly apiUrl = `${environment.apiBaseUrl}/sales`;

  constructor(private http: HttpClient) {} // Injection du client HTTP

  createSale(payload: SaleRequest): Observable<ApiResponse<Sale>> { // Crée une nouvelle vente
    return this.http.post<ApiResponse<Sale>>(this.apiUrl, payload); // POST /api/sales
  }

  getMySalesPaged(page: number = 0, size: number = 5): Observable<ApiResponse<PagedResponse<Sale>>> { // Récupère les ventes personnelles paginées
    const params = new HttpParams() // Prépare les query params
      .set('page', String(page)) // Numéro de page
      .set('size', String(size)) // Taille de page
      .set('sort', 'saleDate,desc'); // Tri par date décroissante

    return this.http.get<ApiResponse<PagedResponse<Sale>>>(`${this.apiUrl}/my-sales/paged`, { params }); // GET /api/sales/my-sales/paged
  }

  searchSales(filters: SaleSearchFilters): Observable<ApiResponse<PagedResponse<Sale>>> { // Recherche avancée admin
    let params = new HttpParams() // Démarre la construction des paramètres
      .set('page', String(filters.page ?? 0)) // Page par défaut
      .set('size', String(filters.size ?? 10)) // Taille par défaut
      .set('sort', filters.sort ?? 'saleDate,desc'); // Tri par défaut

    if (filters.product?.trim()) { // Si un produit est fourni
      params = params.set('product', filters.product.trim()); // Ajoute le filtre produit
    }

    if (filters.keyword?.trim()) { // Si un mot-clé est fourni
      params = params.set('keyword', filters.keyword.trim()); // Ajoute le mot-clé
    }

    if (filters.stockLessThan !== undefined && filters.stockLessThan !== null) { // Si le stock inférieur est fourni
      params = params.set('stockLessThan', String(filters.stockLessThan)); // Ajoute le filtre
    }

    if (filters.minStock !== undefined && filters.minStock !== null) { // Si le stock minimum est fourni
      params = params.set('minStock', String(filters.minStock)); // Ajoute le filtre
    }

    if (filters.maxStock !== undefined && filters.maxStock !== null) { // Si le stock maximum est fourni
      params = params.set('maxStock', String(filters.maxStock)); // Ajoute le filtre
    }

    if (filters.minPrice !== undefined && filters.minPrice !== null) { // Si le prix minimum est fourni
      params = params.set('minPrice', String(filters.minPrice)); // Ajoute le filtre
    }

    if (filters.maxPrice !== undefined && filters.maxPrice !== null) { // Si le prix maximum est fourni
      params = params.set('maxPrice', String(filters.maxPrice)); // Ajoute le filtre
    }

    if (filters.startDate?.trim()) { // Si une date de début est fournie
      params = params.set('startDate', this.toIsoLocalDateTime(filters.startDate.trim())); // Ajoute la date de début
    }

    if (filters.endDate?.trim()) { // Si une date de fin est fournie
      params = params.set('endDate', this.toIsoLocalDateTime(filters.endDate.trim())); // Ajoute la date de fin
    }

    return this.http.get<ApiResponse<PagedResponse<Sale>>>(`${this.apiUrl}/search`, { params }); // GET /api/sales/search
  }

  deleteSale(id: number): Observable<ApiResponse<void>> { // Supprime une vente par identifiant
    return this.http.delete<ApiResponse<void>>(`${this.apiUrl}/${id}`); // DELETE /api/sales/{id}
  }

  private toIsoLocalDateTime(value: string): string { //convertir en LocalDateTime
    return value.length === 16 ? `${value}:00` : value; //si c'est un format sans seconde, on rajoute la seconde
  }
}