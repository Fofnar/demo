import { Component, OnInit } from '@angular/core'; // Outils Angular de base
import { AiFacadeService } from '../../ai-facade.service'; // Service façade qui centralise et met en cache l'analyse IA
import { BusinessHealth } from 'src/app/models/business-health.model'; // Modèle métier du bloc health_score
import { ErrorResponse } from 'src/app/models/error-response.model'; // Contrat d'erreur backend

@Component({
  selector: 'app-ai-health-page', // Balise HTML du composant
  templateUrl: './ai-health-page.component.html', // Template HTML associé
  styleUrls: ['./ai-health-page.component.css'] // Feuille de style CSS associée
})
export class AiHealthPageComponent implements OnInit {

  health: BusinessHealth | null = null; // Bloc santé business extrait de l'analyse complète
  isLoading: boolean = true; // Indique si les données sont en cours de chargement
  errorMessage: string = ''; // Message d'erreur visible dans l'interface

  constructor(private aiFacadeService: AiFacadeService) {} // Injection du service façade IA

  ngOnInit(): void { // Méthode appelée automatiquement au chargement du composant
    this.loadHealth(); // Chargement initial du module health
  }

  loadHealth(forceRefresh: boolean = false): void { // Charge le bloc de santé business avec option de refresh complet
    this.isLoading = true; // Active l'état de chargement
    this.errorMessage = ''; // Réinitialise le message d'erreur

    if (forceRefresh) { // Si l'utilisateur demande un vrai rafraîchissement
      this.aiFacadeService.clearCache(); // Vide le cache de l'analyse complète
    }

    this.aiFacadeService.getFullAnalysis().subscribe({
      next: (analysis) => { // Cas où les données reviennent correctement
        if (analysis && analysis.health_score) { // Vérifie que le bloc health_score existe
          this.health = analysis.health_score; // Stocke les données de santé business
        } else { // Cas inattendu où le bloc est absent
          this.health = null; // Aucune donnée exploitable
          this.errorMessage = 'AI health data is unavailable.'; // Message fallback
        }

        this.isLoading = false; // Fin du chargement
      },
      error: (err) => { // Cas d'erreur backend ou réseau
        this.health = null; // Nettoie les données affichées
        this.errorMessage = this.extractErrorMessage(err.error); // Transforme l'erreur en message lisible
        this.isLoading = false; // Fin du chargement
      }
    });
  }

  retry(): void { // Relance complète de l'appel en cas d'erreur
    this.loadHealth(true); // Recharge les données en vidant le cache
  }

  refreshHealth(): void { // Action utilisateur de rafraîchissement manuel
    this.loadHealth(true); // Recharge les données en vidant le cache
  }

  get healthScore(): number { // Retourne le score global de santé business
    return this.health?.health_score ?? 0; // Valeur sécurisée par défaut
  }

  get healthStatus(): string { // Retourne le statut lisible de santé business
    return this.health?.health_status ?? 'unknown'; // Valeur sécurisée par défaut
  }

  get anomaliesCount(): number { // Retourne le nombre total d'anomalies liées au score
    return this.health?.anomalies_count ?? 0; // Valeur sécurisée
  }

  get highPriorityAnomalies(): number { // Retourne le nombre d'anomalies prioritaires
    return this.health?.high_priority_anomalies ?? 0; // Valeur sécurisée
  }

  get anomalyPenalty(): number { // Retourne la pénalité liée aux anomalies
    return this.health?.health_breakdown?.anomaly_penalty ?? 0; // Valeur sécurisée
  }

  get trendScore(): number { // Retourne le sous-score lié à la tendance
    return this.health?.health_breakdown?.trend_score ?? 0; // Valeur sécurisée
  }

  get stabilityScore(): number { // Retourne le sous-score lié à la stabilité
    return this.health?.health_breakdown?.stability_score ?? 0; // Valeur sécurisée
  }

  get growthScore(): number { // Retourne le sous-score lié à la croissance
    return this.health?.health_breakdown?.growth_score ?? 0; // Valeur sécurisée
  }

  get diversificationScore(): number { // Retourne le sous-score lié à la diversification
    return this.health?.health_breakdown?.diversification_score ?? 0; // Valeur sécurisée
  }

  get statusLabel(): string { // Retourne un libellé propre pour l'affichage du statut
    if (this.healthStatus === 'excellent') { // Cas excellent
      return 'Excellent';
    }

    if (this.healthStatus === 'good') { // Cas bon
      return 'Good';
    }

    if (this.healthStatus === 'average') { // Cas moyen
      return 'Average';
    }

    if (this.healthStatus === 'weak') { // Cas faible
      return 'Weak';
    }

    if (this.healthStatus === 'critical') { // Cas critique
      return 'Critical';
    }

    return 'Unknown'; // Valeur fallback
  }

  get statusClass(): string { // Retourne la classe CSS à appliquer selon le statut
    if (this.healthStatus === 'excellent') { // Cas excellent
      return 'status-excellent';
    }

    if (this.healthStatus === 'good') { // Cas bon
      return 'status-good';
    }

    if (this.healthStatus === 'average') { // Cas moyen
      return 'status-average';
    }

    if (this.healthStatus === 'weak') { // Cas faible
      return 'status-weak';
    }

    if (this.healthStatus === 'critical') { // Cas critique
      return 'status-critical';
    }

    return 'status-default'; // Valeur fallback
  }

  isHealthFragile(): boolean { // Indique si la santé business doit être visuellement mise en alerte
    return this.healthStatus === 'weak' || this.healthStatus === 'critical'; // Signale les états préoccupants
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