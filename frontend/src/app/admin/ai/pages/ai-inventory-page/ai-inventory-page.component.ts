import { Component, OnInit } from '@angular/core'; // Outils Angular de base
import { AiFacadeService } from '../../ai-facade.service'; // Service façade qui centralise et met en cache l'analyse IA
import { Inventory } from 'src/app/models/inventory.model'; // Modèle métier du bloc inventory
import { InventoryAlert } from 'src/app/models/inventory.model'; // Modèle métier d'une alerte stock
import { ErrorResponse } from 'src/app/models/error-response.model'; // Contrat d'erreur backend

@Component({
  selector: 'app-ai-inventory-page', // Balise HTML du composant
  templateUrl: './ai-inventory-page.component.html', // Template HTML associé
  styleUrls: ['./ai-inventory-page.component.css'] // Feuille de style CSS associée
})
export class AiInventoryPageComponent implements OnInit {

  inventory: Inventory | null = null; // Bloc inventory extrait de l'analyse complète
  isLoading: boolean = true; // Indique si les données sont en cours de chargement
  errorMessage: string = ''; // Message d'erreur visible dans l'interface

  constructor(private aiFacadeService: AiFacadeService) {} // Injection du service façade IA

  ngOnInit(): void { // Méthode appelée automatiquement au chargement du composant
    this.loadInventory(); // Chargement initial du module inventory
  }

  loadInventory(forceRefresh: boolean = false): void { // Charge le bloc inventaire avec option de refresh complet
    this.isLoading = true; // Active l'état de chargement
    this.errorMessage = ''; // Réinitialise le message d'erreur

    if (forceRefresh) { // Si l'utilisateur demande un vrai rafraîchissement
      this.aiFacadeService.clearCache(); // Vide le cache de l'analyse complète
    }

    this.aiFacadeService.getFullAnalysis().subscribe({
      next: (analysis) => { // Cas où les données reviennent correctement
        if (analysis && analysis.inventory) { // Vérifie que le bloc inventory existe
          this.inventory = analysis.inventory; // Stocke les données d'inventaire
        } else { // Cas inattendu où le bloc est absent
          this.inventory = null; // Aucune donnée exploitable
          this.errorMessage = 'AI inventory data is unavailable.'; // Message fallback
        }

        this.isLoading = false; // Fin du chargement
      },
      error: (err) => { // Cas d'erreur backend ou réseau
        this.inventory = null; // Nettoie les données affichées
        this.errorMessage = this.extractErrorMessage(err.error); // Transforme l'erreur en message lisible
        this.isLoading = false; // Fin du chargement
      }
    });
  }

  retry(): void { // Relance complète de l'appel en cas d'erreur
    this.loadInventory(true); // Recharge les données en vidant le cache
  }

  refreshInventory(): void { // Action utilisateur de rafraîchissement manuel
    this.loadInventory(true); // Recharge les données en vidant le cache
  }

  get totalProducts(): number { // Retourne le nombre total de produits analysés
    return this.inventory?.total_products ?? 0; // Valeur sécurisée par défaut
  }

  get lowStockCount(): number { // Retourne le nombre de produits en stock faible
    return this.inventory?.low_stock_count ?? 0; // Valeur sécurisée par défaut
  }

  get criticalStockCount(): number { // Retourne le nombre de produits en stock critique
    return this.inventory?.critical_stock_count ?? 0; // Valeur sécurisée par défaut
  }

  get outOfStockCount(): number { // Retourne le nombre de produits en rupture
    return this.inventory?.out_of_stock_count ?? 0; // Valeur sécurisée par défaut
  }

  get alerts(): InventoryAlert[] { // Retourne la liste des alertes stock
    return this.inventory?.low_stock_alerts ?? []; // Valeur sécurisée par défaut
  }

  hasCriticalStockPressure(): boolean { // Indique si le stock présente une urgence visuelle
    return this.criticalStockCount > 0 || this.outOfStockCount > 0; // Vrai si situation critique ou rupture
  }

  hasAlerts(): boolean { // Indique s'il existe au moins une alerte produit
    return this.alerts.length > 0; // Vrai si la liste contient des alertes
  }

  getStockLevelLabel(stockLevel: string | undefined): string { // Convertit le niveau de stock technique en libellé lisible
    if (stockLevel === 'critical') { // Cas critique
      return 'Critical';
    }

    if (stockLevel === 'medium') { // Cas stock moyen
      return 'Medium';
    }

    if (stockLevel === 'low') { // Cas stock faible
      return 'Low';
    }

    if (stockLevel === 'healthy') { // Cas stock sain
      return 'Healthy';
    }

    if (stockLevel === 'out_of_stock') { // Cas rupture
      return 'Out of stock';
    }

    return 'Unknown'; // Valeur fallback
  }

  getStockLevelClass(stockLevel: string | undefined): string { // Retourne la classe CSS du niveau de stock
    if (stockLevel === 'critical') { // Cas critique
      return 'level-critical';
    }

    if (stockLevel === 'medium') { // Cas moyen
      return 'level-medium';
    }

    if (stockLevel === 'low') { // Cas faible
      return 'level-low';
    }

    if (stockLevel === 'healthy') { // Cas sain
      return 'level-healthy';
    }

    if (stockLevel === 'out_of_stock') { // Cas rupture 
      
      return 'level-out';
    }

    return 'level-default'; // Valeur fallback
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

    return 'severity-default'; // Valeur fallback
  }

  trackByProduct(index: number, alert: InventoryAlert): string { // Optimise le rendu Angular de la liste des alertes
    return `${alert.product}-${index}`; // Clé stable basée sur le produit
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