import { Component, Input } from '@angular/core'; // On importe Component pour créer le composant et Input pour personnaliser le texte

@Component({ // Déclaration du composant
  selector: 'app-loading-spinner', // Nom à utiliser dans le HTML
  templateUrl: './loading-spinner.component.html', // HTML du spinner
  styleUrls: ['./loading-spinner.component.css'] // CSS du spinner
})

//Rôle : montrer visuellement que les stats sont en train d’arriver.
export class LoadingSpinnerComponent { // Classe du composant

  @Input() label: string = 'Loading...'; // Texte affiché sous le spinner
}