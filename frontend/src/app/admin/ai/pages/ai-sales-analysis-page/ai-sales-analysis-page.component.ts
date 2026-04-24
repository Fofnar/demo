import { Component, OnInit } from '@angular/core'; 
import { AiFacadeService } from '../../ai-facade.service'; // Service façade qui centralise et met en cache l'analyse IA
import { SalesAnalysis } from 'src/app/models/sales-analysis.model'; // Modèle métier du bloc sales_analysis
import { ErrorResponse } from 'src/app/models/error-response.model'; // Contrat d'erreur backend

@Component({
  selector: 'app-ai-sales-analysis-page', // Balise HTML du composant
  templateUrl: './ai-sales-analysis-page.component.html', // Template HTML associé
  styleUrls: ['./ai-sales-analysis-page.component.css'] // Feuille de style CSS associée
})
export class AiSalesAnalysisPageComponent implements OnInit {

  salesAnalysis: SalesAnalysis | null = null; // Bloc sales_analysis extrait de l'analyse complète
  isLoading: boolean = true; // Indique si les données sont en cours de chargement
  errorMessage: string = ''; // Message d'erreur visible dans l'interface

  constructor(private aiFacadeService: AiFacadeService) {} // Injection du service façade IA

  ngOnInit(): void { // Méthode appelée automatiquement au chargement du composant
    this.loadSalesAnalysis(); // Chargement initial du module sales analysis
  }

  loadSalesAnalysis(forceRefresh: boolean = false): void { // Charge le bloc sales_analysis avec option de refresh complet
    this.isLoading = true; // Active l'état de chargement
    this.errorMessage = ''; // Réinitialise le message d'erreur

    if (forceRefresh) { // Si l'utilisateur demande un vrai rafraîchissement
      this.aiFacadeService.clearCache(); // Vide le cache de l'analyse complète
    }

    this.aiFacadeService.getFullAnalysis().subscribe({
      next: (analysis) => { // Cas où les données reviennent correctement
        if (analysis && analysis.sales_analysis) { // Vérifie que le bloc sales_analysis existe
          this.salesAnalysis = analysis.sales_analysis; // Stocke les données d'analyse commerciale
        } else { // Cas inattendu où le bloc est absent
          this.salesAnalysis = null; // Aucune donnée exploitable
          this.errorMessage = 'AI sales analysis data is unavailable.'; // Message fallback
        }

        this.isLoading = false; // Fin du chargement
      },
      error: (err) => { // Cas d'erreur backend ou réseau
        this.salesAnalysis = null; // Nettoie les données affichées
        this.errorMessage = this.extractErrorMessage(err.error); // Transforme l'erreur en message lisible
        this.isLoading = false; // Fin du chargement
      }
    });
  }

  retry(): void { // Relance complète de l'appel en cas d'erreur
    this.loadSalesAnalysis(true); // Recharge les données en vidant le cache
  }

  refreshSalesAnalysis(): void { // Action utilisateur de rafraîchissement manuel
    this.loadSalesAnalysis(true); // Recharge les données en vidant le cache
  }

  get totalRevenue(): number { // Retourne le chiffre d'affaires total
    return this.salesAnalysis?.total_revenue ?? 0; // Valeur sécurisée
  }

  get totalQuantitySold(): number { // Retourne la quantité totale vendue
    return this.salesAnalysis?.total_quantity_sold ?? 0; // Valeur sécurisée
  }

  get averageOrderValue(): number { // Retourne le panier moyen
    return this.salesAnalysis?.average_order_value ?? 0; // Valeur sécurisée
  }

  get uniqueProducts(): number { // Retourne le nombre de produits distincts
    return this.salesAnalysis?.unique_products ?? 0; // Valeur sécurisée
  }

  get uniqueDays(): number { // Retourne le nombre de jours analysés
    return this.salesAnalysis?.unique_days ?? 0; // Valeur sécurisée
  }

  get topSellingProduct(): string { // Retourne le produit le plus vendu
    return this.salesAnalysis?.top_selling_product ?? 'N/A'; // Valeur sécurisée
  }

  get topSellingQuantity(): number { // Retourne la quantité du top produit volume
    return this.salesAnalysis?.top_selling_quantity ?? 0; // Valeur sécurisée
  }

  get topRevenueProduct(): string { // Retourne le produit qui génère le plus de revenu
    return this.salesAnalysis?.top_revenue_product ?? 'N/A'; // Valeur sécurisée
  }

  get topRevenueValue(): number { // Retourne le revenu généré par le top produit revenu
    return this.salesAnalysis?.top_revenue_value ?? 0; // Valeur sécurisée
  }

  get trend(): string { // Retourne la tendance commerciale globale
    return this.salesAnalysis?.trend ?? 'unknown'; // Valeur sécurisée
  }

  get trendSlope(): number { // Retourne la pente de la tendance
    return this.salesAnalysis?.trend_slope ?? 0; // Valeur sécurisée
  }

  get salesComment(): string { // Retourne le commentaire métier global
    return this.salesAnalysis?.sales_comment ?? ''; // Valeur sécurisée
  }

  get recommendations(): string[] { // Retourne les recommandations simples du moteur
    return this.salesAnalysis?.recommendations ?? []; // Valeur sécurisée
  }

  hasRecommendations(): boolean { // Indique s'il existe au moins une recommandation
    return this.recommendations.length > 0; // Vrai si la liste contient au moins un élément
  }

  hasRevenueData(): boolean { // Indique si la série revenue_per_day contient des points
    return Array.isArray(this.salesAnalysis?.revenue_per_day) && this.salesAnalysis!.revenue_per_day.length > 0; // Vérifie la présence de données
  }

  hasCriticalAttention(): boolean { // Indique si la page doit être visuellement mise en alerte
    return this.trend === 'downward'; // Ici, une tendance baissière est le principal signal d'alerte
  }

  getTrendLabel(trend: string | undefined): string { // Convertit une tendance technique en libellé lisible
    if (trend === 'upward') { // Cas haussier
      return 'Upward';
    }

    if (trend === 'downward') { // Cas baissier
      return 'Downward';
    }

    if (trend === 'stable') { // Cas stable
      return 'Stable';
    }

    return 'Unknown'; // Fallback
  }

  getTrendClass(trend: string | undefined): string { // Retourne la classe CSS de tendance
    if (trend === 'upward') { // Cas haussier
      return 'trend-upward';
    }

    if (trend === 'downward') { // Cas baissier
      return 'trend-downward';
    }

    if (trend === 'stable') { // Cas stable
      return 'trend-stable';
    }

    return 'trend-default'; // Fallback
  }

  trackByRecommendation(index: number, recommendation: string): string { // Optimise le rendu Angular des recommandations
    return `${recommendation}-${index}`; // Clé stable combinée
  }

  private extractErrorMessage(errorBody: unknown): string { // Transforme un corps d'erreur backend en message lisible
    const backendError = errorBody as ErrorResponse | string | null; // Tolère plusieurs formats

    if (!backendError) { // Cas vide ou null
      return 'Unexpected error'; // Fallback générique
    }

    if (typeof backendError === 'object') { // Cas objet JSON
      if ('message' in backendError && typeof backendError.message === 'string') { // Champ message
        return backendError.message; // Retourne le message métier
      }

      if ('error' in backendError && typeof backendError.error === 'string') { // Champ error
        return backendError.error; // Retourne le message technique
      }
    }

    if (typeof backendError === 'string') { // Cas chaîne brute
      return backendError; // Retour direct
    }

    return 'Unexpected error'; // Fallback final
  }
}