import { Component, OnInit } from '@angular/core'; // Outils Angular de base
import { AiFacadeService } from '../../ai-facade.service'; // Service façade qui centralise et met en cache l'analyse IA
import { Anomalies } from 'src/app/models/anomalies.model'; // Modèle métier du bloc anomalies
import { ErrorResponse } from 'src/app/models/error-response.model'; // Contrat d'erreur backend

@Component({
  selector: 'app-ai-anomalies-page', // Balise HTML du composant
  templateUrl: './ai-anomalies-page.component.html', // Template HTML associé
  styleUrls: ['./ai-anomalies-page.component.css'] // Feuille de style CSS associée
})
export class AiAnomaliesPageComponent implements OnInit {

  anomalies: Anomalies | null = null; // Bloc anomalies extrait de l'analyse complète
  isLoading: boolean = true; // Indique si les données sont en cours de chargement
  errorMessage: string = ''; // Message d'erreur visible dans l'interface

  constructor(private aiFacadeService: AiFacadeService) {} // Injection du service façade IA

  ngOnInit(): void { // Méthode appelée automatiquement au chargement du composant
    this.loadAnomalies(); // Chargement initial du module anomalies
  }

  loadAnomalies(forceRefresh: boolean = false): void { // Charge les anomalies avec option de refresh complet
    this.isLoading = true; // Active l'état de chargement
    this.errorMessage = ''; // Réinitialise le message d'erreur

    if (forceRefresh) { // Si l'utilisateur demande un vrai rafraîchissement
      this.aiFacadeService.clearCache(); // Vide le cache de l'analyse complète
    }

    this.aiFacadeService.getFullAnalysis().subscribe({
      next: (analysis) => { // Cas où les données reviennent correctement
        if (analysis && analysis.anomalies) { // Vérifie que le bloc anomalies existe bien
          this.anomalies = analysis.anomalies; // Stocke les anomalies
        } else { // Cas inattendu où le bloc est absent
          this.anomalies = null; // Aucune donnée exploitable
          this.errorMessage = 'AI anomalies data is unavailable.'; // Message fallback
        }

        this.isLoading = false; // Fin du chargement
      },
      error: (err) => { // Cas d'erreur backend ou réseau
        this.anomalies = null; // Nettoie les données affichées
        this.errorMessage = this.extractErrorMessage(err.error); // Transforme l'erreur en message lisible
        this.isLoading = false; // Fin du chargement
      }
    });
  }

  retry(): void { // Relance complète de l'appel en cas d'erreur
    this.loadAnomalies(true); // Recharge les anomalies en vidant le cache
  }

  refreshAnomalies(): void { // Action utilisateur de rafraîchissement manuel
    this.loadAnomalies(true); // Recharge les anomalies en vidant le cache
  }

  get anomalyCount(): number { // Retourne le nombre total d'anomalies détectées
    return this.anomalies?.anomalies?.length ?? 0; // Valeur sécurisée par défaut
  }

  get criticalSeverityCount(): number { // Retourne le nombre d'anomalies critiques
    return this.anomalies?.anomalies?.filter(anomaly => anomaly.severity === 'critical').length ?? 0;
  }


  get highSeverityCount(): number { // Retourne le nombre d'anomalies de sévérité haute
    return this.anomalies?.anomalies?.filter(anomaly => anomaly.severity === 'high').length ?? 0;
  }

  get mediumSeverityCount(): number { // Retourne le nombre d'anomalies de sévérité moyenne
    return this.anomalies?.anomalies?.filter(anomaly => anomaly.severity === 'medium').length ?? 0;
  }

  get lowSeverityCount(): number { // Retourne le nombre d'anomalies faibles
    return this.anomalies?.anomalies?.filter(anomaly => anomaly.severity === 'low').length ?? 0;
  }

  get mlStatusLabel(): string { // Retourne un libellé lisible pour le statut ML
    return this.anomalies?.ml_enabled ? 'Enabled' : 'Disabled';
  }

 hasCriticalAttention(): boolean { // Indique si la page doit visuellement alerter l'utilisateur
  return this.criticalSeverityCount > 0; // Vrai si au moins une anomalie critique existe
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