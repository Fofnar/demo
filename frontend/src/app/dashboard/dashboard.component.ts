import { Component, OnInit } from '@angular/core';
import { AdminStatsDTO } from '../models/admin-stats.model';
import { AdminService } from '../admin/admin.service';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {

  // Objet qui contient les statistiques admin
  stats: AdminStatsDTO | null = null;

  // Indique si les données sont en cours de chargement
  isLoading: boolean = true;

  // Message d'erreur éventuel
  errorMessage: string = '';

  // Injection du service admin
  constructor (private adminService: AdminService) {}

  // Méthode appelée automatiquement au chargement du composant
  ngOnInit(): void {
    this.loadStats();
  }

  //Methode qui appelle le backend
  loadStats(): void{

    // On passe l'etat en chargement
    this.isLoading = true;

    //On vide les anciens messages d'erreur
    this.errorMessage = '';

    //Appel API
    this.adminService.getStats().subscribe({
      next:(response) => {
        //On verifie que le backend a bien repond avec data
        if(response && response.data ){
          this.stats = response.data;
        }else{
          //Sinon on affiche un message fallback
          this.errorMessage = 'No statistics available.';
        }

        this.isLoading = false;
      },
      error: () => {
        this.errorMessage = 'Unable to load statistics.';
        this.isLoading = false;
      }
    });
  }
}