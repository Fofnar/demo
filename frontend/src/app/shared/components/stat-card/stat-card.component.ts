import { Component, Input } from '@angular/core'; // On importe Component pour définir un composant, et Input pour recevoir des données du parent

@Component({ // On déclare ce fichier comme un composant Angular
  selector: 'app-stat-card', // Nom du composant dans le HTML
  templateUrl: './stat-card.component.html', // Fichier HTML du composant
  styleUrls: ['./stat-card.component.css'] // Fichier CSS du composant
})

//Cette classe permet d'afficher une statistique unique comme Users, Sales, Low stock, etc.
export class StatCardComponent { 

  @Input() label: string = ''; // Texte du titre de la carte, envoyé depuis le dashboard
  @Input() value: number | string = 0; // Valeur affichée dans la carte
  @Input() hint: string = ''; // Petit texte descriptif sous la valeur
  @Input() alert: boolean = false; // Permet de changer le style si la carte est une alerte
}