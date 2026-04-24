import { Component, OnInit } from '@angular/core'; // Crée le composant et active le cycle de vie OnInit
import { FormBuilder, FormGroup } from '@angular/forms'; // Crée et manipule le formulaire réactif
import { SaleService } from '../../sales/sale.service'; // Service des ventes
import { ErrorResponse } from '../../models/error-response.model'; // Modèle d'erreur backend
import { Sale } from '../../models/sale.model'; // Modèle d'une vente
import { PagedResponse } from '../../models/paged-response.model'; // Modèle de réponse paginée
import { SaleSearchFilters } from '../../models/sale-search-filters.model'; // Modèle des filtres admin

@Component({
  selector: 'app-admin-sales', // Balise HTML du composant
  templateUrl: './admin-sales.component.html', // Template HTML
  styleUrls: ['./admin-sales.component.css'] // Styles CSS
})
export class AdminSalesComponent implements OnInit {

  filterForm: FormGroup; // Formulaire des filtres avancés
  sales: Sale[] = []; // Liste des ventes affichées
  isLoadingList: boolean = true; // Indique si la liste est en cours de chargement
  deletingId: number | null = null; // Identifiant de la vente en cours de suppression
  errorMessage: string = ''; // Message d'erreur visible dans l'interface
  successMessage: string = ''; // Message de succès visible dans l'interface

  currentPage: number = 0; // Page courante
  pageSize: number = 10; // Taille de page
  totalPages: number = 0; // Nombre total de pages
  totalElements: number = 0; // Nombre total d'éléments

  constructor(
    private fb: FormBuilder, // Injection du constructeur de formulaire
    private saleService: SaleService // Injection du service des ventes
  ) {
    this.filterForm = this.fb.group({ // Initialisation du formulaire de filtres
      product: [''], // Filtre produit
      stockLessThan: [null], // Filtre stock inférieur
      minStock: [null], // Stock minimum
      maxStock: [null], // Stock maximum
      minPrice: [null], // Prix minimum
      maxPrice: [null], // Prix maximum
      startDate: [''], // Date de début
      endDate: [''], // Date de fin
      keyword: [''] // Mot-clé
    });
  }

  ngOnInit(): void { // Appelé au chargement du composant
    this.loadSales(0); // Charge la première page
  }

  loadSales(page: number): void { // Charge les ventes selon la page courante et les filtres actuels
    this.isLoadingList = true; // Active le chargement
    this.errorMessage = ''; // Vide l'ancien message d'erreur

    const filters = this.buildFilters(page); // Construit l'objet de filtres à envoyer au backend

    this.saleService.searchSales(filters).subscribe({ // Appel de la recherche avancée
      next: (response) => { // Si la réponse arrive correctement
        if (response.success && response.data) { // Si le backend renvoie des données valides
          const paged: PagedResponse<Sale> = response.data; // Typage de la réponse paginée

          this.sales = paged.content; // Liste des ventes affichées
          this.currentPage = paged.page; // Page courante
          this.pageSize = paged.size; // Taille de page
          this.totalPages = paged.totalPages; // Nombre total de pages
          this.totalElements = paged.totalElements; // Nombre total d'éléments
        } else { // Si la réponse ne contient pas de données exploitables
          this.sales = []; // Vide la liste
          this.errorMessage = 'No sales available.'; // Message fallback
        }

        this.isLoadingList = false; // Désactive le chargement
      },
      error: (err) => { // Si le backend renvoie une erreur
        this.errorMessage = this.extractErrorMessage(err.error); // Lit le message backend
        this.isLoadingList = false; // Désactive le chargement
      }
    });
  }

  applyFilters(): void { // Applique les filtres saisis
    if (!this.validateRangePairs()) { // Vérifie la cohérence des paires min/max
      return; // Arrêt si les filtres sont incohérents
    }

    this.loadSales(0); // Relance la recherche à la première page
  }

  resetFilters(): void { // Réinitialise les filtres
    this.filterForm.reset({ // Remet les valeurs par défaut
      product: '', // Produit vide
      stockLessThan: null, // Stock inférieur vide
      minStock: null, // Stock minimum vide
      maxStock: null, // Stock maximum vide
      minPrice: null, // Prix minimum vide
      maxPrice: null, // Prix maximum vide
      startDate: '', // Date de début vide
      endDate: '', // Date de fin vide
      keyword: '' // Mot-clé vide
    });

    this.errorMessage = ''; // Vide l'erreur
    this.successMessage = ''; // Vide le succès
    this.loadSales(0); // Recharge la liste sans filtre
  }

  goToPreviousPage(): void { // Charge la page précédente
    if (this.currentPage > 0) { // Vérifie si une page précédente existe
      this.loadSales(this.currentPage - 1); // Charge la page précédente
    }
  }

  goToNextPage(): void { // Charge la page suivante
    if (this.currentPage + 1 < this.totalPages) { // Vérifie si une page suivante existe
      this.loadSales(this.currentPage + 1); // Charge la page suivante
    }
  }

  deleteSale(id: number): void { // Supprime une vente
    const confirmed = confirm('Delete this sale?'); // Demande confirmation
    if (!confirmed) { // Si l'action est annulée
      return; // Arrêt
    }

    this.deletingId = id; // Marque l'élément en cours de suppression
    this.errorMessage = ''; // Vide l'erreur
    this.successMessage = ''; // Vide le succès

    this.saleService.deleteSale(id).subscribe({ // Appel DELETE /api/sales/{id}
      next: () => { // Si la suppression réussit
        this.successMessage = 'Sale deleted successfully.'; // Message succès

        const nextPage = this.sales.length === 1 && this.currentPage > 0
          ? this.currentPage - 1 // Revient à la page précédente si la page courante devient vide
          : this.currentPage; // Sinon recharge la page courante

        this.deletingId = null; // Réinitialise l'état de suppression
        this.loadSales(nextPage); // Recharge la liste
      },
      error: (err) => { // Si la suppression échoue
        this.deletingId = null; // Réinitialise l'état de suppression
        this.errorMessage = this.extractErrorMessage(err.error); // Lit le message backend
      }
    });
  }

  trackBySaleId(index: number, sale: Sale): number { // Optimise l'affichage de la liste
    return sale.id; // Utilise l'identifiant technique comme clé
  }

  private buildFilters(page: number): SaleSearchFilters { // Construit l'objet de filtres à envoyer au backend
    const raw = this.filterForm.getRawValue(); // Lit toutes les valeurs du formulaire

    return {
      product: this.trimOrUndefined(raw.product), // Filtre produit
      stockLessThan: this.toNumberOrUndefined(raw.stockLessThan), // Filtre stock inférieur
      minStock: this.toNumberOrUndefined(raw.minStock), // Stock minimum
      maxStock: this.toNumberOrUndefined(raw.maxStock), // Stock maximum
      minPrice: this.toNumberOrUndefined(raw.minPrice), // Prix minimum
      maxPrice: this.toNumberOrUndefined(raw.maxPrice), // Prix maximum
      startDate: this.trimOrUndefined(raw.startDate), // Date de début
      endDate: this.trimOrUndefined(raw.endDate), // Date de fin
      keyword: this.trimOrUndefined(raw.keyword), // Mot-clé
      page, // Numéro de page
      size: this.pageSize, // Taille de page
      sort: 'saleDate,desc' // Tri par date décroissante
    };
  }

  private validateRangePairs(): boolean { // Vérifie que les filtres de plage sont cohérents
    const raw = this.filterForm.getRawValue(); // Lit les valeurs du formulaire

    const pricePairInvalid = this.hasOnlyOneBound(raw.minPrice, raw.maxPrice); // Vérifie la plage de prix
    const stockPairInvalid = this.hasOnlyOneBound(raw.minStock, raw.maxStock); // Vérifie la plage de stock
    const datePairInvalid = this.hasOnlyOneBound(raw.startDate, raw.endDate); // Vérifie la plage de dates

    if (pricePairInvalid || stockPairInvalid || datePairInvalid) { // Si une paire est incomplète
      this.errorMessage = 'Fill both boundaries for a range filter or leave both empty.'; // Message explicite
      return false; // Validation refusée
    }

    return true; // Validation acceptée
  }

  private hasOnlyOneBound(min: unknown, max: unknown): boolean { // Vérifie qu'une seule borne est fournie
    const hasMin = min !== null && min !== undefined && `${min}`.trim() !== ''; // Présence de la borne min
    const hasMax = max !== null && max !== undefined && `${max}`.trim() !== ''; // Présence de la borne max

    return (hasMin && !hasMax) || (!hasMin && hasMax); // Vrai si une seule borne est présente
  }

  private toNumberOrUndefined(value: unknown): number | undefined { // Convertit une valeur en nombre ou renvoie undefined
    if (value === null || value === undefined || `${value}`.trim() === '') { // Si vide
      return undefined; // Aucun filtre
    }

    const parsed = Number(value); // Conversion numérique
    return Number.isNaN(parsed) ? undefined : parsed; // Renvoie undefined si la conversion échoue
  }

  private trimOrUndefined(value: unknown): string | undefined { // Convertit une chaîne en valeur nettoyée ou undefined
    if (value === null || value === undefined) { // Si vide
      return undefined; // Aucun filtre
    }

    const trimmed = String(value).trim(); // Nettoie la chaîne
    return trimmed === '' ? undefined : trimmed; // Renvoie undefined si vide après nettoyage
  }

  private extractErrorMessage(errorBody: unknown): string { // Transforme l'erreur backend en message lisible
    const backendError = errorBody as ErrorResponse | string | null; // Tolère objet ou string

    if (!backendError) { // Si aucune donnée exploitable
      return 'Unexpected error'; // Message générique
    }

    if (typeof backendError === 'object') { // Si l'erreur est un objet
      if ('message' in backendError && typeof backendError.message === 'string') { // Cas champ message
        return backendError.message; // Message backend
      }

      if ('error' in backendError && typeof backendError.error === 'string') { // Cas champ error
        return backendError.error; // Message backend alternatif
      }
    }

    if (typeof backendError === 'string') { // Si le backend renvoie une chaîne brute
      return backendError; // Retour direct
    }

    return 'Unexpected error'; // Fallback
  }
}