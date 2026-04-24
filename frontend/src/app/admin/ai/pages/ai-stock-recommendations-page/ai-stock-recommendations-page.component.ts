import { Component, OnInit } from '@angular/core'; // Outils Angular de base
import { AiFacadeService } from '../../ai-facade.service'; // Service façade qui centralise et met en cache l'analyse IA
import { StockRecommendations, StockRecommendationItem } from 'src/app/models/stock-recommendations.model'; // Modèles métier du bloc stock recommendations
import { ErrorResponse } from 'src/app/models/error-response.model'; // Contrat d'erreur backend

@Component({
  selector: 'app-ai-stock-recommendations-page', // Balise HTML du composant
  templateUrl: './ai-stock-recommendations-page.component.html', // Template HTML associé
  styleUrls: ['./ai-stock-recommendations-page.component.css'] // Feuille de style CSS associée
})
export class AiStockRecommendationsPageComponent implements OnInit {

  stockRecommendations: StockRecommendations | null = null; // Bloc stock_recommendations extrait de l'analyse complète
  isLoading: boolean = true; // Indique si les données sont en cours de chargement
  errorMessage: string = ''; // Message d'erreur visible dans l'interface

  constructor(private aiFacadeService: AiFacadeService) {} // Injection du service façade IA

  ngOnInit(): void { // Méthode appelée automatiquement au chargement du composant
    this.loadStockRecommendations(); // Chargement initial du module stock recommendations
  }

  loadStockRecommendations(forceRefresh: boolean = false): void { // Charge le bloc stock recommendations avec option de refresh complet
    this.isLoading = true; // Active l'état de chargement
    this.errorMessage = ''; // Réinitialise le message d'erreur

    if (forceRefresh) { // Si l'utilisateur demande un vrai rafraîchissement
      this.aiFacadeService.clearCache(); // Vide le cache de l'analyse complète
    }

    this.aiFacadeService.getFullAnalysis().subscribe({
      next: (analysis) => { // Cas où les données reviennent correctement
        if (analysis && analysis.stock_recommendations) { // Vérifie que le bloc stock_recommendations existe
          this.stockRecommendations = analysis.stock_recommendations; // Stocke les données de recommandations stock
        } else { // Cas inattendu où le bloc est absent
          this.stockRecommendations = null; // Aucune donnée exploitable
          this.errorMessage = 'AI stock recommendations data is unavailable.'; // Message fallback
        }

        this.isLoading = false; // Fin du chargement
      },
      error: (err) => { // Cas d'erreur backend ou réseau
        this.stockRecommendations = null; // Nettoie les données affichées
        this.errorMessage = this.extractErrorMessage(err.error); // Transforme l'erreur en message lisible
        this.isLoading = false; // Fin du chargement
      }
    });
  }

  retry(): void { // Relance complète de l'appel en cas d'erreur
    this.loadStockRecommendations(true); // Recharge les données en vidant le cache
  }

  refreshStockRecommendations(): void { // Action utilisateur de rafraîchissement manuel
    this.loadStockRecommendations(true); // Recharge les données en vidant le cache
  }

  get criticalAlerts(): number { // Retourne le nombre d'alertes critiques
    return this.stockRecommendations?.critical_alerts ?? 0; // Valeur sécurisée
  }

  get highAlerts(): number { // Retourne le nombre d'alertes élevées
    return this.stockRecommendations?.high_alerts ?? 0; // Valeur sécurisée
  }

  get anomaliesCount(): number { // Retourne le nombre total d'anomalies liées
    return this.stockRecommendations?.anomalies_count ?? 0; // Valeur sécurisée
  }

  get highPriorityAnomalies(): number { // Retourne le nombre d'anomalies prioritaires
    return this.stockRecommendations?.high_priority_anomalies ?? 0; // Valeur sécurisée
  }

  get executiveNote(): string { // Retourne la note exécutive synthétique
    return this.stockRecommendations?.executive_note ?? ''; // Valeur sécurisée
  }

  get businessComment(): string { // Retourne le commentaire métier global
    return this.stockRecommendations?.business_comment ?? ''; // Valeur sécurisée
  }

  get recommendations(): string[] { // Retourne la liste des recommandations globales
    return this.stockRecommendations?.recommendations ?? []; // Valeur sécurisée
  }

  get items(): StockRecommendationItem[] { // Retourne la liste détaillée des recommandations par produit
    return this.stockRecommendations?.stock_recommendations ?? []; // Valeur sécurisée
  }

  hasItems(): boolean { // Indique si la liste détaillée contient des produits
    return this.items.length > 0; // Vrai si la liste contient au moins un élément
  }

  hasCriticalPriority(): boolean { // Indique si la page doit être visuellement mise en alerte
    return this.criticalAlerts > 0 || this.highAlerts > 0; // Vrai si des alertes fortes existent
  }

  getRiskLevelLabel(riskLevel: string | undefined): string { // Convertit le niveau de risque technique en libellé lisible
    if (riskLevel === 'critical') {
      return 'Critical';
    }

    if (riskLevel === 'high') {
      return 'High';
    }

    if (riskLevel === 'medium') {
      return 'Medium';
    }

    if (riskLevel === 'low') {
      return 'Low';
    }

    if (riskLevel === 'unknown') {
      return 'Unknown';
    }

    return 'Unknown'; // Fallback
  }

  getRiskLevelClass(riskLevel: string | undefined): string { // Retourne la classe CSS du niveau de risque
    if (riskLevel === 'critical') {
      return 'risk-critical';
    }

    if (riskLevel === 'high') {
      return 'risk-high';
    }

    if (riskLevel === 'medium') {
      return 'risk-medium';
    }

    if (riskLevel === 'low') {
      return 'risk-low';
    }

    if (riskLevel === 'unknown') {
      return 'risk-unknown';
    }

    return 'risk-default'; // Fallback
  }

  getRelatedSeverityLabel(severity: string | undefined): string { // Convertit la sévérité liée à une anomalie en libellé lisible
    if (severity === 'critical') {
      return 'Critical';
    }

    if (severity === 'high') {
      return 'High';
    }

    if (severity === 'medium') {
      return 'Medium';
    }

    if (severity === 'low') {
      return 'Low';
    }

    return 'N/A'; // Fallback si aucune sévérité n'est définie
  }

  getRelatedSeverityClass(severity: string | undefined): string { // Retourne la classe CSS de la sévérité liée
    if (severity === 'critical') {
      return 'severity-critical';
    }

    if (severity === 'high') {
      return 'severity-high';
    }

    if (severity === 'medium') {
      return 'severity-medium';
    }

    if (severity === 'low') {
      return 'severity-low';
    }

    return 'severity-default'; // Fallback
  }

  formatDaysBeforeStockout(value: number | null | undefined): string { // Formate le nombre de jours avant rupture
    if (value === null || value === undefined) {
      return 'N/A'; // Fallback lisible
    }

    return `${value.toFixed(1)} days`; // Affiche le nombre avec une décimale
  }

  trackByProduct(index: number, item: StockRecommendationItem): string { // Optimise le rendu Angular de la liste
    return `${item.product}-${index}`; // Clé stable basée sur le produit
  }

  private extractErrorMessage(errorBody: unknown): string { // Transforme un corps d'erreur backend en message lisible
    const backendError = errorBody as ErrorResponse | string | null; // Tolère plusieurs formats

    if (!backendError) {
      return 'Unexpected error'; // Fallback générique
    }

    if (typeof backendError === 'object') {
      if ('message' in backendError && typeof backendError.message === 'string') {
        return backendError.message; // Retourne le message métier
      }

      if ('error' in backendError && typeof backendError.error === 'string') {
        return backendError.error; // Retourne le message technique
      }
    }

    if (typeof backendError === 'string') {
      return backendError; // Retour direct
    }

    return 'Unexpected error'; // Fallback final
  }
}