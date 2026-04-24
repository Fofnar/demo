import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule, provideHttpClient, withInterceptors } from '@angular/common/http';
import { NgChartsModule } from 'ng2-charts';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { LoginComponent } from './login/login.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { AboutComponent } from './about/about.component';
import { ForbiddenComponent } from './forbidden/forbidden.component';
import { SharedModule } from './shared/shared.module';
import { authInterceptor } from './auth/auth.interceptor';
import { RegisterComponent } from './register/register.component';
import { ProfileComponent } from './profile/profile.component';
import { SalesComponent } from './sales/sales.component';
import { AdminSalesComponent } from './admin/admin-sales/admin-sales.component';
import { AdminAiPageComponent } from './admin/ai/admin-ai-page/admin-ai-page.component';
import { AiKpiSummaryComponent } from './admin/ai/components/ai-kpi-summary/ai-kpi-summary.component';
import { AiRevenueChartComponent } from './admin/ai/components/ai-revenue-chart/ai-revenue-chart.component';
import { AiAnomaliesPanelComponent } from './admin/ai/components/ai-anomalies-panel/ai-anomalies-panel.component';
import { AiAnomaliesPageComponent } from './admin/ai/pages/ai-anomalies-page/ai-anomalies-page.component';
import { AiHealthPageComponent } from './admin/ai/pages/ai-health-page/ai-health-page.component';
import { AiInventoryPageComponent } from './admin/ai/pages/ai-inventory-page/ai-inventory-page.component';
import { AiPredictionPageComponent } from './admin/ai/pages/ai-prediction-page/ai-prediction-page.component';
import { AiStockPredictionPageComponent } from './admin/ai/pages/ai-stock-prediction-page/ai-stock-prediction-page.component';
import { AiRecommendationsPageComponent } from './admin/ai/pages/ai-recommendations-page/ai-recommendations-page.component';
import { AiStockRecommendationsPageComponent } from './admin/ai/pages/ai-stock-recommendations-page/ai-stock-recommendations-page.component';
import { AiSalesAnalysisPageComponent } from './admin/ai/pages/ai-sales-analysis-page/ai-sales-analysis-page.component';
import { AiNavComponent } from './admin/ai/components/ai-nav/ai-nav/ai-nav.component';
import { AdminUsersComponent } from './admin/admin-users/admin-users/admin-users.component';

@NgModule({
  declarations: [ // Composants de ce module
    AppComponent,
    LoginComponent,
    DashboardComponent,
    AboutComponent,
    ForbiddenComponent,
    RegisterComponent,
    ProfileComponent,
    SalesComponent,
    AdminSalesComponent,
    AdminAiPageComponent,
    AiKpiSummaryComponent,
    AiRevenueChartComponent,
    AiAnomaliesPanelComponent,
    AiAnomaliesPageComponent,
    AiHealthPageComponent,
    AiInventoryPageComponent,
    AiPredictionPageComponent,
    AiStockPredictionPageComponent,
    AiRecommendationsPageComponent,
    AiStockRecommendationsPageComponent,
    AiSalesAnalysisPageComponent,
    AiNavComponent,
    AdminUsersComponent,
  ],
  imports: [ // Modules importés
    BrowserModule,
    AppRoutingModule,
    ReactiveFormsModule,
    SharedModule,
    NgChartsModule
  ],
  providers: [ // Configuration HTTP moderne
    provideHttpClient(
      withInterceptors([authInterceptor])
    )
  ],
  bootstrap: [AppComponent] // Démarrage de l'app
})
export class AppModule { }