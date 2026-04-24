import { Component, Input } from '@angular/core'; // Importe Component pour créer le composant et Input pour recevoir les données du parent
import { Anomalies, Anomaly } from 'src/app/models/anomalies.model'; // Importe les modèles métier des anomalies

@Component({
  selector: 'app-ai-anomalies-panel', // Nom de la balise HTML du composant
  templateUrl: './ai-anomalies-panel.component.html', // Fichier HTML associé
  styleUrls: ['./ai-anomalies-panel.component.css'] // Fichier CSS associé
})
export class AiAnomaliesPanelComponent {

  @Input() anomalies?: Anomalies; // Bloc anomalies reçu depuis le parent
  @Input() overviewMode: boolean = false; // Active le mode aperçu pour la page overview
  @Input() maxItems: number | null = null; // Limite facultative du nombre d'anomalies affichées
  @Input() showFooterAction: boolean = false; // Affiche un bouton vers la page détaillée si nécessaire

  get anomalyCount(): number { // Retourne le nombre total d'anomalies
    return this.anomalies?.anomalies?.length ?? 0; // Valeur par défaut à zéro
  }

  get criticalSeverityCount(): number { // Retourne le nombre d'anomalies de sévérité critique
    return this.anomalies?.anomalies?.filter(anomaly => anomaly.severity === 'critical').length ?? 0;
  }

  get highSeverityCount(): number { // Retourne le nombre d'anomalies de sévérité haute
    return this.anomalies?.anomalies?.filter(anomaly => anomaly.severity === 'high').length ?? 0;
  }

  get mediumSeverityCount(): number { // Retourne le nombre d'anomalies de sévérité moyenne
    return this.anomalies?.anomalies?.filter(anomaly => anomaly.severity === 'medium').length ?? 0;
  }

  get lowSeverityCount(): number { // Retourne le nombre d'anomalies de sévérité faible
    return this.anomalies?.anomalies?.filter(anomaly => anomaly.severity === 'low').length ?? 0;
  }

  get displayedAnomalies(): Anomaly[] { // Retourne la liste finale à afficher selon le mode actif
    const items = this.anomalies?.anomalies ?? []; // Liste brute sécurisée

    if (this.maxItems === null || this.maxItems === undefined) { // Si aucune limite n'est fournie
      return items; // Retourne toute la liste
    }

    return items.slice(0, this.maxItems); // Retourne seulement la portion souhaitée
  }

  get remainingCount(): number { // Retourne le nombre d'anomalies non affichées en mode aperçu
    return Math.max(this.anomalyCount - this.displayedAnomalies.length, 0); // Différence sécurisée
  }

  hasAnomalies(): boolean { // Indique si la liste contient au moins une anomalie
    return this.anomalyCount > 0; // Vrai si le nombre total est supérieur à zéro
  }

  getSeverityLabel(severity: string | undefined): string { // Convertit une sévérité technique en libellé lisible
    if (severity === 'critical') { // Cas anomalie critique
      return 'Critical'; // Libellé critique
    }

    if (severity === 'high') { // Cas anomalie élevée
      return 'High'; // Libellé élevé
    }

    if (severity === 'medium') { // Cas anomalie moyenne
      return 'Medium'; // Libellé moyen
    }

    if (severity === 'low') { // Cas anomalie faible
      return 'Low'; // Libellé faible
    }

    return 'Unknown'; // Valeur fallback
  }

  getSeverityClass(severity: string | undefined): string { // Retourne la classe CSS adaptée à la sévérité
    if (severity === 'critical') { // Cas sévérité critique
      return 'severity-critical'; // Classe CSS critique
    }

    if (severity === 'high') { // Cas sévérité haute
      return 'severity-high'; // Classe CSS haute criticité
    }

    if (severity === 'medium') { // Cas sévérité moyenne
      return 'severity-medium'; // Classe CSS moyenne criticité
    }

    if (severity === 'low') { // Cas sévérité faible
      return 'severity-low'; // Classe CSS faible criticité
    }

    return 'severity-default'; // Classe CSS par défaut
  }

  formatPercentage(value: number | undefined | null): string { // Formate une valeur décimale en pourcentage lisible
    if (value === null || value === undefined) { // Si la valeur est absente
      return 'N/A'; // Retourne un fallback
    }

    return `${(value * 100).toFixed(1)}%`; // Convertit en pourcentage avec une décimale
  }

  trackByAnomaly(index: number, anomaly: Anomaly): string { // Optimise le rendu Angular de la liste
    return `${anomaly.date}-${anomaly.product}-${index}`; // Clé stable
  }
}