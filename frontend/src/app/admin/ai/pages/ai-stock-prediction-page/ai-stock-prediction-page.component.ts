import { Component, OnInit } from '@angular/core'; // Outils Angular de base
import { AiFacadeService } from '../../ai-facade.service'; // Service façade qui centralise et met en cache l'analyse IA
import { StockPrediction, StockPredictionItem } from 'src/app/models/stock-prediction.model'; // Modèles métier du bloc stock prediction
import { ErrorResponse } from 'src/app/models/error-response.model'; // Contrat d'erreur backend

@Component({
  selector: 'app-ai-stock-prediction-page', // Balise HTML du composant
  templateUrl: './ai-stock-prediction-page.component.html', // Template HTML associé
  styleUrls: ['./ai-stock-prediction-page.component.css'] // Feuille de style CSS associée
})
export class AiStockPredictionPageComponent implements OnInit {

  stockPrediction: StockPrediction | null = null; // Bloc stock_prediction extrait de l'analyse complète
  isLoading: boolean = true; // Indique si les données sont en cours de chargement
  errorMessage: string = ''; // Message d'erreur visible dans l'interface

  constructor(private aiFacadeService: AiFacadeService) {} // Injection du service façade IA

  ngOnInit(): void { // Méthode appelée automatiquement au chargement du composant
    this.loadStockPrediction(); // Chargement initial du module stock prediction
  }

  loadStockPrediction(forceRefresh: boolean = false): void { // Charge le bloc stock prediction avec option de refresh complet
    this.isLoading = true; // Active l'état de chargement
    this.errorMessage = ''; // Réinitialise le message d'erreur

    if (forceRefresh) { // Si l'utilisateur demande un vrai rafraîchissement
      this.aiFacadeService.clearCache(); // Vide le cache de l'analyse complète
    }

    this.aiFacadeService.getFullAnalysis().subscribe({
      next: (analysis) => { // Cas où les données reviennent correctement
        if (analysis && analysis.stock_prediction) { // Vérifie que le bloc stock_prediction existe
          this.stockPrediction = analysis.stock_prediction; // Stocke les données de prévision de stock
        } else { // Cas inattendu où le bloc est absent
          this.stockPrediction = null; // Aucune donnée exploitable
          this.errorMessage = 'AI stock prediction data is unavailable.'; // Message fallback
        }

        this.isLoading = false; // Fin du chargement
      },
      error: (err) => { // Cas d'erreur backend ou réseau
        this.stockPrediction = null; // Nettoie les données affichées
        this.errorMessage = this.extractErrorMessage(err.error); // Transforme l'erreur en message lisible
        this.isLoading = false; // Fin du chargement
      }
    });
  }

  retry(): void { // Relance complète de l'appel en cas d'erreur
    this.loadStockPrediction(true); // Recharge les données en vidant le cache
  }

  refreshStockPrediction(): void { // Action utilisateur de rafraîchissement manuel
    this.loadStockPrediction(true); // Recharge les données en vidant le cache
  }

  get coverageTargetDays(): number { // Retourne le nombre de jours de couverture visé
    return this.stockPrediction?.coverage_target_days ?? 0; // Valeur sécurisée
  }

  get anomaliesCount(): number { // Retourne le nombre total d'anomalies liées
    return this.stockPrediction?.anomalies_count ?? 0; // Valeur sécurisée
  }

  get highPriorityAnomalies(): number { // Retourne le nombre d'anomalies prioritaires
    return this.stockPrediction?.high_priority_anomalies ?? 0; // Valeur sécurisée
  }

  get businessComment(): string { // Retourne le commentaire métier global
    return this.stockPrediction?.business_comment ?? ''; // Valeur sécurisée
  }

  get recommendations(): string[] { // Retourne la liste des recommandations globales
    return this.stockPrediction?.recommendations ?? []; // Valeur sécurisée
  }

  get items(): StockPredictionItem[] { // Retourne la liste détaillée des produits
    return this.stockPrediction?.stock_prediction ?? []; // Valeur sécurisée
  }

  get criticalCount(): number { // Retourne le nombre de produits à risque critique
    return this.items.filter(item => item.risk_level === 'critical').length; // Compte les éléments critiques
  }

  get highCount(): number { // Retourne le nombre de produits à risque élevé
    return this.items.filter(item => item.risk_level === 'high').length; // Compte les éléments high
  }

  get upwardTrendCount(): number { // Retourne le nombre de produits avec demande haussière
    return this.items.filter(item => item.sales_trend === 'upward').length; // Compte les tendances upward
  }

  hasItems(): boolean { // Indique si la liste détaillée contient des produits
    return this.items.length > 0; // Vrai si la liste contient au moins un élément
  }

  hasCriticalRisk(): boolean { // Indique si la page doit être visuellement mise en alerte
    return this.criticalCount > 0 || this.highCount > 0; // Vrai si des risques forts existent
  }

  getRiskLevelLabel(riskLevel: string | undefined): string { // Convertit le niveau de risque technique en libellé lisible
    if (riskLevel === 'critical') { // Cas critique
      return 'Critical';
    }

    if (riskLevel === 'high') { // Cas élevé
      return 'High';
    }

    if (riskLevel === 'medium') { // Cas moyen
      return 'Medium';
    }

    if (riskLevel === 'low') { // Cas faible
      return 'Low';
    }

    if (riskLevel === 'unknown') { // Cas inconnu
      return 'Unknown';
    }

    return 'Unknown'; // Fallback
  }

  getRiskLevelClass(riskLevel: string | undefined): string { // Retourne la classe CSS du niveau de risque
    if (riskLevel === 'critical') { // Cas critique
      return 'risk-critical';
    }

    if (riskLevel === 'high') { // Cas élevé
      return 'risk-high';
    }

    if (riskLevel === 'medium') { // Cas moyen
      return 'risk-medium';
    }

    if (riskLevel === 'low') { // Cas faible
      return 'risk-low';
    }

    if (riskLevel === 'unknown') { // Cas inconnu
      return 'risk-unknown';
    }

    return 'risk-default'; // Fallback
  }

  getTrendLabel(trend: string | undefined): string { // Convertit la tendance technique en libellé lisible
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

  getRelatedSeverityLabel(severity: string | undefined): string { // Convertit la sévérité liée à une anomalie en libellé lisible
    if (severity === 'critical') { // Cas critique
      return 'Critical';
    }

    if (severity === 'high') { // Cas élevé
      return 'High';
    }

    if (severity === 'medium') { // Cas moyen
      return 'Medium';
    }

    if (severity === 'low') { // Cas faible
      return 'Low';
    }

    return 'N/A'; // Fallback si aucune sévérité n'est définie
  }

  getRelatedSeverityClass(severity: string | undefined): string { // Retourne la classe CSS de la sévérité liée
    if (severity === 'critical') { // Cas critique
      return 'severity-critical';
    }

    if (severity === 'high') { // Cas élevé
      return 'severity-high';
    }

    if (severity === 'medium') { // Cas moyen
      return 'severity-medium';
    }

    if (severity === 'low') { // Cas faible
      return 'severity-low';
    }

    return 'severity-default'; // Fallback
  }

  formatDaysBeforeStockout(value: number | null | undefined): string { // Formate le nombre de jours avant rupture
    if (value === null || value === undefined) { // Cas où l'estimation n'est pas disponible
      return 'N/A'; // Fallback lisible
    }

    return `${value.toFixed(1)} days`; // Affiche le nombre avec une décimale
  }

  trackByProduct(index: number, item: StockPredictionItem): string { // Optimise le rendu Angular de la liste
    return `${item.product}-${index}`; // Clé stable basée sur le produit
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