import { Component, Input } from '@angular/core'; // On importe Component pour créer le composant et Input pour recevoir des valeurs du parent

@Component({ // On déclare ce fichier comme un composant Angular
  selector: 'app-action-card', // Nom de la balise HTML du composant
  templateUrl: './action-card.component.html', // Fichier HTML du composant
  styleUrls: ['./action-card.component.css'] // Fichier CSS du composant
})
export class ActionCardComponent { // Classe du composant

  @Input() title: string = ''; // Titre affiché sur la carte
  @Input() description: string = ''; // Description affichée sur la carte
  @Input() route: string = ''; // Route de navigation quand on clique sur la carte
}