import { Component, OnInit } from '@angular/core'; 
import { FormBuilder, FormGroup, Validators } from '@angular/forms'; 
import { SaleService } from './sale.service'; 
import { ErrorResponse } from '../models/error-response.model'; 
import { Sale } from '../models/sale.model'; 
import { SaleRequest } from '../models/sale-request.model'; 
import { PagedResponse } from '../models/paged-response.model'; 

@Component({
  selector: 'app-sales', // Sélecteur HTML du composant
  templateUrl: './sales.component.html', // Template HTML
  styleUrls: ['./sales.component.css'] // Styles CSS
})
export class SalesComponent implements OnInit {

  salesForm: FormGroup; // Formulaire de création de vente
  sales: Sale[] = []; // Liste des ventes affichées
  isLoadingList: boolean = true; // État de chargement de la liste
  isSubmitting: boolean = false; // État de chargement lors de la création
  errorMessage: string = ''; // Message d'erreur affiché à l'écran
  successMessage: string = ''; // Message de succès affiché à l'écran

  currentPage: number = 0; // Page courante
  pageSize: number = 5; // Taille de page affichée
  totalPages: number = 0; // Nombre total de pages
  totalElements: number = 0; // Nombre total d'éléments

  constructor(private fb: FormBuilder, private saleService: SaleService) { // Injection du builder de formulaire et du service sales
    this.salesForm = this.fb.group({ // Création du formulaire
      date: [this.getLocalDateTimeValue(), [Validators.required]], // Date de vente initialisée à maintenant
      product: ['', [Validators.required, Validators.minLength(2)]], // Produit requis
      price: [null, [Validators.required, Validators.min(0.01)]], // Prix requis
      quantity: [1, [Validators.required, Validators.min(1)]], // Quantité minimale 1
      stock: [1, [Validators.required, Validators.min(0)]], // Stock minimal 0
    });
  }

  ngOnInit(): void { // Méthode exécutée au chargement du composant
    this.loadMySales(0); // Charge la première page des ventes
  }

  loadMySales(page: number): void { // Charge une page des ventes du user connecté
    this.isLoadingList = true; // Active le chargement de la liste
    this.errorMessage = ''; // Vide l'ancien message d'erreur

    this.saleService.getMySalesPaged(page, this.pageSize).subscribe({ // Appel du backend avec pagination
      next: (response) => { // Si la réponse arrive correctement
        if (response.success && response.data) { // Si le backend renvoie des données valides
          const paged: PagedResponse<Sale> = response.data; // Typage de la réponse paginée

          this.sales = paged.content; // Ventes de la page courante
          this.currentPage = paged.page; // Page courante
          this.pageSize = paged.size; // Taille de page
          this.totalPages = paged.totalPages; // Nombre total de pages
          this.totalElements = paged.totalElements; // Nombre total d'éléments
        } else { // Si la réponse n'est pas exploitable
          this.sales = []; // Liste vide
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

  onSubmit(): void { // Envoi du formulaire de création
    if (this.salesForm.invalid) { // Si le formulaire est invalide
      this.errorMessage = 'Please fill in all required fields correctly.'; // Message utilisateur
      return; // Arrêt de l'exécution
    }

    this.isSubmitting = true; // Active le chargement de création
    this.errorMessage = ''; // Vide l'erreur
    this.successMessage = ''; // Vide le succès

    const raw = this.salesForm.getRawValue(); // Récupère les valeurs du formulaire

    const payload: SaleRequest = { // Construction du body envoyé au backend
      date: this.toIsoLocalDateTime(raw.date), // Date
      product: raw.product, // Produit
      price: Number(raw.price), // Prix converti en nombre
      quantity: Number(raw.quantity), // Quantité convertie en nombre
      stock: Number(raw.stock) // Stock converti en nombre
    };

    this.saleService.createSale(payload).subscribe({ // Appel POST /api/sales
      next: (response) => { // Si la création réussit
        if (response.success) { // Si le backend confirme
          this.successMessage = 'Sale created successfully.'; // Message de succès
          this.salesForm.reset({ // Réinitialisation du formulaire
            date: this.getLocalDateTimeValue(), // Nouvelle date par défaut
            product: '', // Produit vide
            price: null, // Prix vide
            quantity: 1, // Quantité par défaut
            stock: 1 // Stock par défaut
          });

          this.loadMySales(0); // Recharge la première page pour afficher la nouvelle vente
        } else { // Si la réponse est inattendue
          this.errorMessage = 'Sale creation failed.'; // Message fallback
        }

        this.isSubmitting = false; // Désactive le chargement de création
      },
      error: (err) => { // Si le backend renvoie une erreur
        this.errorMessage = this.extractErrorMessage(err.error); // Lit le message backend
        this.isSubmitting = false; // Désactive le chargement de création
      }
    });
  }

  goToPreviousPage(): void { // Revient à la page précédente
    if (this.currentPage > 0) { // Vérifie qu'une page précédente existe
      this.loadMySales(this.currentPage - 1); // Charge la page précédente
    }
  }

  private toIsoLocalDateTime(value: string): string { //convertir en LocalDateTime
    return value.length === 16 ? `${value}:00` : value; //si c'est un format sans seconde, on rajoute la seconde
  }

  goToNextPage(): void { // Va à la page suivante
    if (this.currentPage + 1 < this.totalPages) { // Vérifie qu'une page suivante existe
      this.loadMySales(this.currentPage + 1); // Charge la page suivante
    }
  }

  onReset(): void { // Réinitialise seulement le formulaire
    this.salesForm.reset({ // Remet les valeurs par défaut
      date: this.getLocalDateTimeValue(), // Date actuelle
      product: '', // Produit vide
      price: null, // Prix vide
      quantity: 1, // Quantité par défaut
      stock: 1 // Stock par défaut
    });

    this.errorMessage = ''; // Vide l'erreur
    this.successMessage = ''; // Vide le succès
  }

  trackBySaleId(index: number, sale: Sale): number { // Optimise le rendu de la liste
    return sale.id; // Utilise l'identifiant comme clé
  }

  private getLocalDateTimeValue(): string { // Construit une valeur compatible avec datetime-local
    const now = new Date(); // Date actuelle
    const offset = now.getTimezoneOffset(); // Décalage timezone en minutes
    const localDate = new Date(now.getTime() - offset * 60000); // Ajustement à l'heure locale
    return localDate.toISOString().slice(0, 16); // Format yyyy-MM-ddTHH:mm
  }

  private extractErrorMessage(errorBody: unknown): string { // Transforme l'erreur backend en message lisible
    const backendError = errorBody as ErrorResponse | string | null; // Tolère objet ou string

    if (!backendError) { // Si vide
      return 'Unexpected error'; // Message générique
    }

    if (typeof backendError === 'object') { // Si c'est un objet
      if ('message' in backendError && typeof backendError.message === 'string') { // Cas champ message
        return backendError.message; // Message backend
      }

      if ('error' in backendError && typeof backendError.error === 'string') { // Cas champ error
        return backendError.error; // Message backend alternatif
      }
    }

    if (typeof backendError === 'string') { // Si le backend envoie une string brute
      return backendError; // Retour direct
    }

    return 'Unexpected error'; // Fallback
  }
}