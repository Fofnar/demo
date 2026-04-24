import { Component, OnInit } from '@angular/core'; // Décorateurs Angular de base
import { ErrorResponse } from 'src/app/models/error-response.model'; // Contrat d'erreur backend
import { AIResponse } from 'src/app/models/ai-response.model'; // Contrat global de l'analyse IA
import { AiFacadeService } from '../ai-facade.service'; // Service façade avec mise en cache

// Représente une carte de navigation de module IA dans la vue overview
interface AiModuleCard {
  title: string; // Titre affiché sur la carte
  description: string; // Description métier du module
  route: string; // Route de destination du module
  badge?: string; // Badge facultatif affiché en haut à droite
  alert?: boolean; // Indique si la carte doit être mise en avant visuellement
}

@Component({
  selector: 'app-admin-ai', // Balise HTML du composant
  templateUrl: './admin-ai-page.component.html', // Template HTML associé
  styleUrls: ['./admin-ai-page.component.css'] // Feuille de style associée
})
export class AdminAiPageComponent implements OnInit {

  analysis: AIResponse | null = null; // Données complètes renvoyées par le moteur IA
  isLoading: boolean = true; // Indique si la page est en chargement
  errorMessage: string = ''; // Message d'erreur affiché à l'écran

  constructor(private aiFacadeService: AiFacadeService) {} // Injection du service façade IA

  ngOnInit(): void { // Déclenché automatiquement au chargement du composant
    this.loadAnalysis(); // Chargement initial de l'overview IA
  }

  loadAnalysis(forceRefresh: boolean = false): void { // Charge l'analyse complète avec option de refresh
    this.isLoading = true; // Active l'état de chargement
    this.errorMessage = ''; // Réinitialise le message d'erreur

    if (forceRefresh) { // Si un rafraîchissement complet est demandé
      this.aiFacadeService.clearCache(); // Vide le cache local de l'analyse IA
    }

    this.aiFacadeService.getFullAnalysis().subscribe({
      next: (data) => { // Réception correcte des données
        if (data) { // Vérifie qu'une analyse exploitable est présente
          this.analysis = data; // Stocke l'analyse complète
        } else { // Cas inattendu où la réponse est vide
          this.analysis = null; // Aucun contenu exploitable
          this.errorMessage = 'AI analysis is unavailable.'; // Message fallback
        }

        this.isLoading = false; // Fin du chargement
      },
      error: (err) => { // Erreur backend ou réseau
        this.analysis = null; // Nettoie l'analyse affichée
        this.errorMessage = this.extractErrorMessage(err.error); // Transforme l'erreur en message lisible
        this.isLoading = false; // Fin du chargement
      }
    });
  }

  retry(): void { // Relance complète de l'appel en cas d'erreur
    this.loadAnalysis(true); // Recharge l'analyse en vidant le cache
  }

  refreshAnalysis(): void { // Action manuelle de rafraîchissement
    this.loadAnalysis(true); // Recharge l'analyse en vidant le cache
  }

  get anomalyCount(): number { // Retourne le nombre total d'anomalies détectées
    return this.analysis?.anomalies?.anomalies?.length ?? 0; // Valeur par défaut à zéro
  }

  get criticalSeverityCount(): number { // Retourne le nombre d'anomalies critiques
    return this.analysis?.anomalies?.anomalies?.filter(anomaly => anomaly.severity === 'critical').length ?? 0;
  }

  get highSeverityCount(): number { // Retourne le nombre d'anomalies de sévérité haute
    return this.analysis?.anomalies?.anomalies?.filter(anomaly => anomaly.severity === 'high').length ?? 0;
  }

  get mediumSeverityCount(): number { // Retourne le nombre d'anomalies de sévérité moyenne
    return this.analysis?.anomalies?.anomalies?.filter(anomaly => anomaly.severity === 'medium').length ?? 0;
  }  

  get healthScore(): number { // Extrait le score global de santé business
    return this.analysis?.health_score?.health_score ?? 0; // Valeur par défaut à zéro
  }

  get healthStatus(): string { // Extrait le statut lisible de santé business
    return this.analysis?.health_score?.health_status ?? 'unknown'; // Valeur par défaut
  }

  get lowStockCount(): number { // Extrait le nombre de produits en stock faible
    return this.analysis?.inventory?.low_stock_count ?? 0; // Valeur par défaut à zéro
  }

  get criticalStockCount(): number { // Extrait le nombre de produits en stock critique
    return this.analysis?.inventory?.critical_stock_count ?? 0; // Valeur par défaut à zéro
  }

  get predictionMethod(): string { // Extrait le nom de la méthode de prédiction
    return this.analysis?.prediction?.prediction_method ?? 'N/A'; // Valeur fallback
  }

  get moduleCards(): AiModuleCard[] { // Construit dynamiquement les cartes de navigation des modules
    return [
      {
        title: 'Sales analysis',
        description: 'Revenue trend, top products, daily performance and sales insights.',
        route: '/admin/ai/sales-analysis',
        badge: `${this.analysis?.sales_analysis?.unique_days ?? 0} days`
      },
      {
        title: 'Anomalies',
        description: 'Review unusual revenue signals, severity levels and risk explanations.',
        route: '/admin/ai/anomalies',
        badge: `${this.anomalyCount} total / ${this.highSeverityCount} high`,
        alert: this.highSeverityCount > 0
      },
      {
        title: 'Health',
        description: 'Inspect business health score, breakdown, penalties and executive comments.',
        route: '/admin/ai/health',
        badge: `${this.healthScore} / 100`
      },
      {
        title: 'Inventory',
        description: 'Monitor low stock, critical products, alerts and replenishment priorities.',
        route: '/admin/ai/inventory',
        badge: `${this.lowStockCount} low / ${this.criticalStockCount} critical`,
        alert: this.criticalStockCount > 0
      },
      {
        title: 'Prediction',
        description: 'Analyze next-day revenue, 3-day trend and model quality indicators.',
        route: '/admin/ai/prediction',
        badge: this.predictionMethod
      },
      {
        title: 'Stock prediction',
        description: 'Estimate stockout risk, coverage horizon and suggested restock quantities.',
        route: '/admin/ai/stock-prediction'
      },
      {
        title: 'Stock recommendations',
        description: 'Review operational restock actions prioritized by urgency and business impact.',
        route: '/admin/ai/stock-recommendations'
      },
      {
        title: 'Recommendations',
        description: 'Read executive recommendations, trend insights and anomaly-based actions.',
        route: '/admin/ai/recommendations'
      }
    ];
  }

  hasCriticalAnomalies(): boolean { // Indique si des anomalies de sévérité haute sont présentes
    return this.criticalSeverityCount > 0; // Vrai si au moins une anomalie critique existe
  }

  trackByModuleRoute(index: number, module: AiModuleCard): string { // Optimise le rendu Angular des cartes modules
    return module.route; // Utilise la route comme identifiant stable
  }

  private extractErrorMessage(errorBody: unknown): string { // Traduit un corps d'erreur backend en message exploitable
    const backendError = errorBody as ErrorResponse | string | null; // Tolère objet, string ou null

    if (!backendError) { // Cas vide ou non défini
      return 'Unexpected error'; // Message générique
    }

    if (typeof backendError === 'object') { // Cas objet JSON
      if ('error' in backendError && typeof backendError.error === 'string') { // Champ error
        return backendError.error; // Retourne le message technique backend
      }

      if ('message' in backendError && typeof backendError.message === 'string') { // Champ message
        return backendError.message; // Retourne le message métier backend
      }
    }

    if (typeof backendError === 'string') { // Cas string brute
      return backendError; // Retour direct
    }

    return 'Unexpected error'; // Fallback final
  }
}