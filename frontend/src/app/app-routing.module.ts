import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { LoginComponent } from './login/login.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { AboutComponent } from './about/about.component';
import { ForbiddenComponent } from './forbidden/forbidden.component';
import { RegisterComponent } from './register/register.component';
import { ProfileComponent } from './profile/profile.component';
import { SalesComponent } from './sales/sales.component';

import { authGuard } from './auth/auth.guard';
import { adminGuard } from './auth/admin.guard';
import { guestGuard } from './auth/guest.guard';

import { AdminSalesComponent } from './admin/admin-sales/admin-sales.component';
import { AdminAiPageComponent } from './admin/ai/admin-ai-page/admin-ai-page.component';
import { AiAnomaliesPageComponent } from './admin/ai/pages/ai-anomalies-page/ai-anomalies-page.component';
import { AiHealthPageComponent } from './admin/ai/pages/ai-health-page/ai-health-page.component';
import { AiInventoryPageComponent } from './admin/ai/pages/ai-inventory-page/ai-inventory-page.component';
import { AiPredictionPageComponent } from './admin/ai/pages/ai-prediction-page/ai-prediction-page.component';
import { AiStockPredictionPageComponent } from './admin/ai/pages/ai-stock-prediction-page/ai-stock-prediction-page.component';
import { AiRecommendationsPageComponent } from './admin/ai/pages/ai-recommendations-page/ai-recommendations-page.component';
import { AiStockRecommendationsPageComponent } from './admin/ai/pages/ai-stock-recommendations-page/ai-stock-recommendations-page.component';
import { AiSalesAnalysisPageComponent } from './admin/ai/pages/ai-sales-analysis-page/ai-sales-analysis-page.component';
import { AdminUsersComponent } from './admin/admin-users/admin-users/admin-users.component';

const routes: Routes = [
  { path: 'login', component: LoginComponent, canActivate: [guestGuard] },
  { path: 'register', component: RegisterComponent, canActivate: [guestGuard] },

  { path: 'profile', component: ProfileComponent, canActivate: [authGuard] },
  { path: 'sales', component: SalesComponent, canActivate: [authGuard] },

  { path: 'admin/sales', component: AdminSalesComponent, canActivate: [authGuard, adminGuard] },
  { path: 'admin/users', component: AdminUsersComponent, canActivate: [authGuard, adminGuard] },
  { path: 'dashboard', component: DashboardComponent, canActivate: [authGuard, adminGuard] },

  { path: 'admin/ai', component: AdminAiPageComponent, canActivate: [authGuard, adminGuard] },
  { path: 'admin/ai/sales-analysis', component: AiSalesAnalysisPageComponent, canActivate: [authGuard, adminGuard] },
  { path: 'admin/ai/anomalies', component: AiAnomaliesPageComponent, canActivate: [authGuard, adminGuard] },
  { path: 'admin/ai/health', component: AiHealthPageComponent, canActivate: [authGuard, adminGuard] },
  { path: 'admin/ai/inventory', component: AiInventoryPageComponent, canActivate: [authGuard, adminGuard] },
  { path: 'admin/ai/prediction', component: AiPredictionPageComponent, canActivate: [authGuard, adminGuard] },
  { path: 'admin/ai/stock-prediction', component: AiStockPredictionPageComponent, canActivate: [authGuard, adminGuard] },
  { path: 'admin/ai/stock-recommendations', component: AiStockRecommendationsPageComponent, canActivate: [authGuard, adminGuard] },
  { path: 'admin/ai/recommendations', component: AiRecommendationsPageComponent, canActivate: [authGuard, adminGuard] },

  { path: 'about', component: AboutComponent },
  { path: 'forbidden', component: ForbiddenComponent },
  { path: '', redirectTo: 'login', pathMatch: 'full' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {}