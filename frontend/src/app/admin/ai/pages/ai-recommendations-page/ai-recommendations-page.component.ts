import { Component, OnInit } from '@angular/core'; // Outils Angular de base
import { AiFacadeService } from '../../ai-facade.service'; // Service façade qui centralise et met en cache l'analyse IA
import { Recommendations } from 'src/app/models/recommendations.model'; // Modèle métier du bloc recommendations
import { ForecastPoint } from 'src/app/models/prediction.model'; // Modèle d'un point de forecast
import { ErrorResponse } from 'src/app/models/error-response.model'; // Contrat d'erreur backend

@Component({
  selector: 'app-ai-recommendations-page', // Balise HTML du composant
  templateUrl: './ai-recommendations-page.component.html', // Template HTML associé
  styleUrls: ['./ai-recommendations-page.component.css'] // Feuille de style CSS associée
})
export class AiRecommendationsPageComponent implements OnInit {

  recommendationsData: Recommendations | null = null; // Bloc recommendations extrait de l'analyse complète
  isLoading: boolean = true; // Indique si les données sont en cours de chargement
  errorMessage: string = ''; // Message d'erreur visible dans l'interface

  constructor(private aiFacadeService: AiFacadeService) {} // Injection du service façade IA

  ngOnInit(): void { // Méthode appelée automatiquement au chargement du composant
    this.loadRecommendations(); // Chargement initial du module recommendations
  }

  loadRecommendations(forceRefresh: boolean = false): void { // Charge le bloc recommendations avec option de refresh complet
    this.isLoading = true; // Active l'état de chargement
    this.errorMessage = ''; // Réinitialise le message d'erreur

    if (forceRefresh) { // Si l'utilisateur demande un vrai rafraîchissement
      this.aiFacadeService.clearCache(); // Vide le cache de l'analyse complète
    }

    this.aiFacadeService.getFullAnalysis().subscribe({
      next: (analysis) => { // Cas où les données reviennent correctement
        if (analysis && analysis.recommendations) { // Vérifie que le bloc recommendations existe
          this.recommendationsData = analysis.recommendations; // Stocke les données de recommandations globales
        } else { // Cas inattendu où le bloc est absent
          this.recommendationsData = null; // Aucune donnée exploitable
          this.errorMessage = 'AI recommendations data is unavailable.'; // Message fallback
        }

        this.isLoading = false; // Fin du chargement
      },
      error: (err) => { // Cas d'erreur backend ou réseau
        this.recommendationsData = null; // Nettoie les données affichées
        this.errorMessage = this.extractErrorMessage(err.error); // Transforme l'erreur en message lisible
        this.isLoading = false; // Fin du chargement
      }
    });
  }

  retry(): void { // Relance complète de l'appel en cas d'erreur
    this.loadRecommendations(true); // Recharge les données en vidant le cache
  }

  refreshRecommendations(): void { // Action utilisateur de rafraîchissement manuel
    this.loadRecommendations(true); // Recharge les données en vidant le cache
  }

  get trend(): string { // Retourne la tendance globale
    return this.recommendationsData?.trend ?? 'unknown'; // Valeur sécurisée
  }

  get trendSlope(): number { // Retourne la pente de tendance
    return this.recommendationsData?.trend_slope ?? 0; // Valeur sécurisée
  }

  get trendChangeRate(): number { // Retourne le taux de variation global
    return this.recommendationsData?.trend_change_rate ?? 0; // Valeur sécurisée
  }

  get topSellingProduct(): string { // Retourne le produit le plus vendu
    return this.recommendationsData?.top_selling_product ?? 'N/A'; // Valeur sécurisée
  }

  get topRevenueProduct(): string { // Retourne le produit le plus rentable
    return this.recommendationsData?.top_revenue_product ?? 'N/A'; // Valeur sécurisée
  }

  get executiveSummary(): string { // Retourne le résumé exécutif
    return this.recommendationsData?.executive_summary ?? ''; // Valeur sécurisée
  }

  get anomaliesCount(): number { // Retourne le nombre total d'anomalies
    return this.recommendationsData?.anomalies_count ?? 0; // Valeur sécurisée
  }

  get highPriorityAnomalies(): number { // Retourne le nombre d'anomalies prioritaires
    return this.recommendationsData?.high_priority_anomalies ?? 0; // Valeur sécurisée
  }

  get productInsights(): string[] { // Retourne les recommandations liées aux produits
    return this.recommendationsData?.recommendations?.product_insights ?? []; // Valeur sécurisée
  }

  get trendInsights(): string[] { // Retourne les recommandations liées à la tendance
    return this.recommendationsData?.recommendations?.trend_insights ?? []; // Valeur sécurisée
  }

  get anomalyInsights(): string[] { // Retourne les recommandations liées aux anomalies
    return this.recommendationsData?.recommendations?.anomaly_insights ?? []; // Valeur sécurisée
  }

  get forecastMethod(): string { // Retourne la méthode de forecast utilisée
    return this.recommendationsData?.forecast?.method ?? 'N/A'; // Valeur sécurisée
  }

  get forecastAvailable(): boolean { // Indique si un forecast est disponible
    return this.recommendationsData?.forecast?.available ?? false; // Valeur sécurisée
  }

  get forecastPoints(): ForecastPoint[] { // Retourne la liste des points forecast
    return this.recommendationsData?.forecast?.forecast ?? []; // Valeur sécurisée
  }

  hasForecast(): boolean { // Indique si des points forecast existent
    return this.forecastPoints.length > 0; // Vrai si au moins un point forecast existe
  }

  hasCriticalAttention(): boolean { // Indique si la page doit être visuellement mise en alerte
    return this.highPriorityAnomalies > 0 || this.trend === 'downward'; // Vrai si anomalies prioritaires ou tendance baissière
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

  formatPercentage(value: number | undefined | null): string { // Formate un ratio décimal en pourcentage lisible
    if (value === null || value === undefined) { // Si la valeur est absente
      return 'N/A'; // Valeur fallback
    }

    return `${(value * 100).toFixed(1)}%`; // Conversion en pourcentage avec une décimale
  }

  trackByForecastDate(index: number, point: ForecastPoint): string { // Optimise le rendu Angular de la liste forecast
    return `${point.date}-${index}`; // Clé stable basée sur la date
  }

  trackByInsight(index: number, insight: string): string { // Optimise le rendu Angular des listes d'insights
    return `${insight}-${index}`; // Clé stable combinée
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