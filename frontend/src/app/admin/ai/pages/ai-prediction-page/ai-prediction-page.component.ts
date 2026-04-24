import { Component, OnInit } from '@angular/core'; // Outils Angular de base
import { AiFacadeService } from '../../ai-facade.service'; // Service façade qui centralise et met en cache l'analyse IA
import { Prediction, ForecastDay } from 'src/app/models/prediction.model'; // Modèles métier du bloc prediction
import { ErrorResponse } from 'src/app/models/error-response.model'; // Contrat d'erreur backend

@Component({
  selector: 'app-ai-prediction-page', // Balise HTML du composant
  templateUrl: './ai-prediction-page.component.html', // Template HTML associé
  styleUrls: ['./ai-prediction-page.component.css'] // Feuille de style CSS associée
})
export class AiPredictionPageComponent implements OnInit {

  prediction: Prediction | null = null; // Bloc prediction extrait de l'analyse complète
  isLoading: boolean = true; // Indique si les données sont en cours de chargement
  errorMessage: string = ''; // Message d'erreur visible dans l'interface

  constructor(private aiFacadeService: AiFacadeService) {} // Injection du service façade IA

  ngOnInit(): void { // Méthode appelée automatiquement au chargement du composant
    this.loadPrediction(); // Chargement initial du module prediction
  }

  loadPrediction(forceRefresh: boolean = false): void { // Charge le bloc prediction avec option de refresh complet
    this.isLoading = true; // Active l'état de chargement
    this.errorMessage = ''; // Réinitialise le message d'erreur

    if (forceRefresh) { // Si l'utilisateur demande un vrai rafraîchissement
      this.aiFacadeService.clearCache(); // Vide le cache de l'analyse complète
    }

    this.aiFacadeService.getFullAnalysis().subscribe({
      next: (analysis) => { // Cas où les données reviennent correctement
        if (analysis && analysis.prediction) { // Vérifie que le bloc prediction existe
          this.prediction = analysis.prediction; // Stocke les données de prévision
        } else { // Cas inattendu où le bloc est absent
          this.prediction = null; // Aucune donnée exploitable
          this.errorMessage = 'AI prediction data is unavailable.'; // Message fallback
        }

        this.isLoading = false; // Fin du chargement
      },
      error: (err) => { // Cas d'erreur backend ou réseau
        this.prediction = null; // Nettoie les données affichées
        this.errorMessage = this.extractErrorMessage(err.error); // Transforme l'erreur en message lisible
        this.isLoading = false; // Fin du chargement
      }
    });
  }

  retry(): void { // Relance complète de l'appel en cas d'erreur
    this.loadPrediction(true); // Recharge les données en vidant le cache
  }

  refreshPrediction(): void { // Action utilisateur de rafraîchissement manuel
    this.loadPrediction(true); // Recharge les données en vidant le cache
  }

  get predictedNextDayRevenue(): number { // Retourne le revenu prévu pour le prochain jour
    return this.prediction?.predicted_next_day_revenue ?? 0; // Valeur sécurisée
  }

  get next3DaysPrediction(): number { // Retourne la prévision moyenne / synthèse sur 3 jours
    return this.prediction?.next_3_days_prediction ?? 0; // Valeur sécurisée
  }

  get trendNext(): string { // Retourne la tendance attendue pour le prochain jour
    return this.prediction?.trend_next ?? 'unknown'; // Valeur sécurisée
  }

  get trendNext3Days(): string { // Retourne la tendance attendue pour les 3 prochains jours
    return this.prediction?.trend_next_3_days ?? 'unknown'; // Valeur sécurisée
  }

  get predictionMethod(): string { // Retourne la méthode de prévision utilisée
    return this.prediction?.prediction_method ?? 'N/A'; // Valeur sécurisée
  }

  get modelQualityMae(): number { // Retourne la qualité du modèle sous forme de MAE
    return this.prediction?.model_quality_mae ?? 0; // Valeur sécurisée
  }

  get anomaliesCount(): number { // Retourne le nombre total d'anomalies liées à la prévision
    return this.prediction?.anomalies_count ?? 0; // Valeur sécurisée
  }

  get highPriorityAnomalies(): number { // Retourne le nombre d'anomalies prioritaires
    return this.prediction?.high_priority_anomalies ?? 0; // Valeur sécurisée
  }

  get forecastDays(): ForecastDay[] { // Retourne les prévisions détaillées sur 3 jours
    return this.prediction?.forecast_next_3_days ?? []; // Valeur sécurisée
  }

  hasForecast(): boolean { // Indique si la liste de prévisions détaillées existe
    return this.forecastDays.length > 0; // Vrai si des jours sont disponibles
  }

  hasPredictionRisk(): boolean { // Indique si la prévision doit être visuellement signalée comme sensible
    return this.highPriorityAnomalies > 0 || this.trendNext === 'downward' || this.trendNext3Days === 'downward';
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

    return 'Unknown'; // Valeur fallback
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

    return 'trend-default'; // Valeur fallback
  }

  trackByForecastDate(index: number, day: ForecastDay): string { // Optimise le rendu Angular des prévisions par jour
    return `${day.date}-${index}`; // Clé stable basée sur la date
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