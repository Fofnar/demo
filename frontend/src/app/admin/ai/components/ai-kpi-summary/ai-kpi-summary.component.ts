import { Component, Input } from '@angular/core'; // Décorateur du composant et prise en charge des entrées
import { AIResponse } from 'src/app/models/ai-response.model';  // Contrat global renvoyé par /api/ai/analysis

@Component({
  selector: 'app-ai-kpi-summary', // Balise HTML du composant
  templateUrl: './ai-kpi-summary.component.html', // Template HTML associé
  styleUrls: ['./ai-kpi-summary.component.css'] // Styles CSS associés
})
export class AiKpiSummaryComponent {

  @Input() analysis: AIResponse | null = null; // Analyse IA complète reçue depuis le parent

  get totalRevenue(): number { // Récupère le chiffre d'affaires total
    return this.analysis?.sales_analysis?.total_revenue ?? 0; // Valeur sécurisée par défaut
  }

  get totalQuantitySold(): number { // Récupère la quantité totale vendue
    return this.analysis?.sales_analysis?.total_quantity_sold ?? 0; // Valeur sécurisée par défaut
  }

  get averageOrderValue(): number { // Récupère le panier moyen
    return this.analysis?.sales_analysis?.average_order_value ?? 0; // Valeur sécurisée par défaut
  }

  get uniqueProducts(): number { // Récupère le nombre de produits distincts
    return this.analysis?.sales_analysis?.unique_products ?? 0; // Valeur sécurisée par défaut
  }

  get healthScore(): number { // Récupère le score de santé business
    return this.analysis?.health_score?.health_score ?? 0; // Valeur sécurisée par défaut
  }

  get healthStatus(): string { // Récupère le statut lisible de santé business
    return this.analysis?.health_score?.health_status ?? 'unknown'; // Valeur sécurisée par défaut
  }

  get topSellingProduct(): string { // Récupère le produit le plus vendu
    return this.analysis?.sales_analysis?.top_selling_product ?? '-'; // Valeur sécurisée par défaut
  }

  get topRevenueProduct(): string { // Récupère le produit générant le plus de revenu
    return this.analysis?.sales_analysis?.top_revenue_product ?? '-'; // Valeur sécurisée par défaut
  }

  isHealthWeak(): boolean { // Détermine si le score de santé est faible
    return this.healthStatus === 'weak' || this.healthStatus === 'critical'; // Règle d'alerte visuelle
  }
}