import { Component, Input, OnChanges, SimpleChanges } from '@angular/core'; // Outils Angular pour composant et détection des changements
import { ChartConfiguration, ChartOptions } from 'chart.js'; // Types Chart.js
import { RevenuePerDay } from 'src/app/models/sales-analysis.model'; // Modèle des revenus par jour

@Component({
  selector: 'app-ai-revenue-chart', // Balise HTML du composant
  templateUrl: './ai-revenue-chart.component.html', // Template HTML
  styleUrls: ['./ai-revenue-chart.component.css'] // Feuille de style CSS
})
export class AiRevenueChartComponent implements OnChanges {

  @Input() revenueData: RevenuePerDay[] = []; // Liste des revenus journaliers reçue du parent
  @Input() trend: string = ''; // Tendance globale reçue du parent
  @Input() topRevenueProduct: string = ''; // Produit top revenu
  @Input() totalRevenue: number = 0; // Chiffre d'affaires total

  lineChartData: ChartConfiguration<'line'>['data'] = { // Données du graphique
    labels: [], // Axe horizontal
    datasets: [
      {
        data: [], // Valeurs du chiffre d'affaires
        label: 'Revenue', // Libellé du dataset
        fill: true, // Active le remplissage sous la courbe
        tension: 0.35, // Lissage de la courbe
        borderWidth: 3, // Épaisseur du trait
        pointRadius: 4, // Taille des points
        pointHoverRadius: 6 // Taille au survol
      }
    ]
  };

  lineChartOptions: ChartOptions<'line'> = { // Options générales du graphique
    responsive: true, // Le graphique s’adapte au conteneur
    maintainAspectRatio: false, // Permet de contrôler la hauteur via le CSS
    plugins: {
      legend: {
        display: false // Cache la légende pour un rendu plus premium
      },
      tooltip: {
        enabled: true // Active les tooltips
      }
    },
    scales: {
      x: {
        ticks: {
          color: '#cbd5e1' // Couleur des labels de l’axe X
        },
        grid: {
          color: 'rgba(255,255,255,0.06)' // Couleur douce de la grille
        }
      },
      y: {
        ticks: {
          color: '#cbd5e1' // Couleur des labels de l’axe Y
        },
        grid: {
          color: 'rgba(255,255,255,0.06)' // Couleur douce de la grille
        }
      }
    }
  };

  ngOnChanges(changes: SimpleChanges): void { // Détecte les nouvelles données reçues du parent
    if (changes['revenueData']) { // Si revenueData change
      this.buildChart(); // Reconstruit les données du graphique
    }
  }

  hasData(): boolean { // Vérifie si le composant possède des données à afficher
    return Array.isArray(this.revenueData) && this.revenueData.length > 0;
  }

  private buildChart(): void { // Construit la configuration finale du graphique
    const labels = this.revenueData.map(item => item.date); // Extrait les dates pour l’axe X
    const values = this.revenueData.map(item => item.revenue); // Extrait les revenus pour l’axe Y

    this.lineChartData = { // Remplace les données actuelles
      labels,
      datasets: [
        {
          data: values, // Valeurs du revenu
          label: 'Revenue', // Libellé interne
          fill: true, // Remplissage sous la ligne
          tension: 0.35, // Courbe lissée
          borderWidth: 3, // Épaisseur de ligne
          pointRadius: 4, // Taille des points
          pointHoverRadius: 6, // Taille au survol
          borderColor: '#93c5fd', // Couleur du trait
          backgroundColor: 'rgba(147, 197, 253, 0.12)', // Couleur du remplissage
          pointBackgroundColor: '#93c5fd', // Couleur des points
          pointBorderColor: '#0f172a' // Couleur de contour des points
        }
      ]
    };
  }

  getTrendLabel(): string { // Retourne un libellé lisible pour la tendance
    if (this.trend === 'upward') {
      return 'Upward trend';
    }

    if (this.trend === 'downward') {
      return 'Downward trend';
    }

    return 'Stable trend';
  }
}