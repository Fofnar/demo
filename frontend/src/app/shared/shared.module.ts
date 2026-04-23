import { NgModule } from '@angular/core'; 
import { CommonModule } from '@angular/common'; // Donne accès à *ngIf, *ngFor, etc.
import { StatCardComponent } from './components/stat-card/stat-card.component'; // Carte KPI
import { ActionCardComponent } from './components/action-card/action-card.component'; // Carte action
import { LoadingSpinnerComponent } from './components/loading-spinner/loading-spinner.component'; // Spinner de chargement
import { RouterModule } from '@angular/router';

@NgModule({
  declarations: [ // Les composants déclarés dans ce module
    StatCardComponent,
    ActionCardComponent,
    LoadingSpinnerComponent
  ],
  imports: [ // Les modules dont ces composants ont besoin
    CommonModule,
    RouterModule
  ],
  exports: [ // Les composants rendus visibles aux autres modules
    StatCardComponent,
    ActionCardComponent,
    LoadingSpinnerComponent
  ]
})
export class SharedModule {} // Module partagé réutilisable