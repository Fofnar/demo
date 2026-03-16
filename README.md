# Intelligent SME Analytics — Dashboard (AI & Data Platform)

**Projet** : Tableau de bord d’analyse opérationnelle pour PME
**Auteur / Lead Dev** : Fodeba Fofana

---

## Présentation

**Intelligent SME Analytics** est une solution end-to-end destinée aux petites et moyennes entreprises pour suivre, analyser et anticiper leurs ventes et leurs niveaux de stock. Le projet couvre la collecte de données, un pipeline de traitement et d’analytique, un service IA pour détection d’anomalies / prévisions / recommandations, une API REST sécurisée et un dashboard pour visualiser KPI, graphiques et alertes. Le produit vise un déploiement *production-ready* (authentification JWT, gestion d’erreurs, tests automatisés, Dockerisation et CI/CD).

---

## Valeur ajoutée

* Solution complète : collecte → pipeline → ML → API → dashboard.
* Architecture microservices réaliste : séparation backend / service IA / frontend.
* Cas d’usage concret : réduction du temps de validation des données et automatisation des alertes stock/ventes.
* Conçue pour la production : sécurité, tests, conteneurisation, pipeline d’intégration/déploiement.

---

## Fonctionnalités clés

* **Authentification sécurisée** : JWT (access + refresh), mots de passe hachés (BCrypt).
* **Gestion Utilisateurs & Ventes** : CRUD, pagination, tri, filtres, recherche dynamique (Specifications).
* **Endpoints Analytics** : chiffre d’affaires total, chiffre par jour, top produits, revenue per day.
* **Détection d’anomalies** : microservice IA dédié.
* **Prévisions & recommandations** : prévisions court terme, recommandations marketing et alertes stock.
* **Health Score** : indicateur synthétique de santé commerciale.
* **Alertes inventaire** et prédiction de rupture de stock.
* **Conteneurisation** : Docker Compose pour dev, images Docker pour prod.
* **CI/CD** : pipeline Jenkins pour build/test/déploiement.
* **Frontend (en cours)** : dashboard réactif pour KPI, graphiques et alertes.

---

## Architecture (conceptuelle)

Backend (API) orchestrateur ↔ Frontend dashboard

↓
Base de données opérationnelle

↓
Microservice IA (analyse & prédiction)

* Le backend orchestre les accès aux données, appelle le microservice IA et expose un JSON standardisé au frontend.
* Le microservice IA est indépendant : reçoit des données sérialisées, renvoie des analyses / anomalies / prédictions / recommandations.

---

## Stack technique

* Backend : Spring Boot (Java 17, JPA / Hibernate, DTO, Controllers).
* Microservice IA : FastAPI (Python 3.11, endpoints d’analyse).
* Frontend : Angular (Angular 16+, composants réactifs).
* Base de données : PostgreSQL (modèle normalisé utilisateurs / ventes / stocks).
* ML & Data libs : scikit-learn, pandas.
* Conteneurisation : Docker (Docker Compose pour dev).
* CI / CD : Jenkins (Jenkinsfile pour pipeline build/test/deploy).
* Dépôt & collaboration : GitHub (repo principal, issues, CI hooks).

> > Remarque : pour la lisibilité, les noms de technologies sont listés ici — le README évite de répéter leurs appellations techniques partout afin de privilégier descriptions et workflows.

---

## Pipeline Data & IA (détails)

1. **Collecte**

   * Ventes, stocks, utilisateurs, événements opérationnels.
   * Extraction depuis la base opérationnelle / repositories du backend.
2. **Transformation & préparation**

   * Nettoyage, agrégation, calcul de métriques (revenue per day, top products).
   * Sérialisation en JSON pour envoi au microservice IA.
3. **Analyse & Machine Learning**

   * Détection d’anomalies (outliers, ventes irrégulières).
   * Prévision des revenus & estimation des ruptures de stock.
   * Génération de recommandations métiers (marketing, réapprovisionnement).
4. **Exposition**

   * Le backend assemble données + résultats IA en JSON standardisé pour le frontend.

---

## Microservice IA & API — workflow exemple

1. L’utilisateur interagit avec le dashboard (frontend).
2. Le frontend appelle un endpoint API (ex. `/api/analysis`).
3. Le backend récupère les données depuis la base, transforme en JSON et appelle le microservice IA.
4. Le microservice IA renvoie : analyses, anomalies, prédictions, recommandations et alertes stock.
5. Le backend renvoie au frontend un JSON uniforme prêt à l’affichage.

---

## Exemple de réponse JSON (contrat API)

```json
{
  "sales_analysis": {
    "total_revenue": 4450,
    "total_quantity_sold": 19,
    "top_selling_product": "Mouse",
    "revenue_per_day": [
      { "date": "2025-01-01", "revenue": 1000 },
      { "date": "2025-01-02", "revenue": 200 },
      { "date": "2025-01-03", "revenue": 2000 },
      { "date": "2025-01-04", "revenue": 250 },
      { "date": "2025-01-05", "revenue": 1000 }
    ]
  },
  "anomalies": {
    "lower_bound": -875,
    "upper_bound": 2125,
    "anomalies": []
  },
  "prediction": {
    "predicted_next_day_revenue": 1083.3333333333333,
    "trend_next": "stable",
    "next_3_days_prediction": 1083.3333333333333,
    "trend_next_3_days": "upward"
  },
  "recommendations": {
    "trend": "stable",
    "recommendations": [
      "Mouse is the top selling product. Highlight it in marketing.",
      "Sales stable. Monitor performance."
    ]
  },
  "health_score": { "health_score": 30 },
  "inventory": {
    "low_stock_alerts": [
      { "product": "Keyboard", "stock": 9, "warning": "Low stock" },
      { "product": "Mouse", "stock": 5, "warning": "Low stock" }
    ]
  },
  "stock_prediction": {
    "stock_prediction": [
      { "product": "Laptop", "current_stock": 70, "avg_daily_sales": 1.3333333333333333, "estimated_days_before_stockout": 52.5 },
      { "product": "Mouse", "current_stock": 5, "avg_daily_sales": 10, "estimated_days_before_stockout": 0.5 },
      { "product": "Keyboard", "current_stock": 9, "avg_daily_sales": 5, "estimated_days_before_stockout": 1.8 }
    ]
  },
  "stock_recommendations": {
    "stock_recommendations": [
      "Laptop:  Stock level Healthy",
      "Mouse: Critical stock. Restock imediately.",
      "Keyboard: Critical stock. Restock imediately."
    ]
  }
}
```

---

## Installation

### Prérequis

* Java 17 (backend)
* Gradle 8+
* Python 3.10+ (microservice IA)
* Node.js 18+ & Angular CLI 16+ (frontend)
* Docker & Docker Compose
* PostgreSQL 14+

### Développement (local)

```bash
# Cloner le repo
git clone https://github.com/Fofnar/demo
cd demo/demo
```

**Configurer le microservice Python (IA)** :

```bash
python -m venv venv
# Windows
venv\Scripts\activate
# Unix/macOS
source venv/bin/activate
pip install -r requirements.txt
```

**Configurer PostgreSQL**

* Créer une base `demo_db`.
* Mettre à jour `application.properties` (backend) avec user / password / url.

**Lancer les services**

* Backend Spring Boot (dev) : `./gradlew bootRun`
* Microservice IA (FastAPI) : `uvicorn ml.main:app --reload`
* Frontend (Angular, en cours d’intégration) :

```bash
cd frontend
npm install
ng serve
```

Accès local : frontend `http://localhost:4200`, backend `http://localhost:8080`, IA `http://localhost:8000`.

### Avec Docker (dev / prod)

```bash
# Build & up (dev)
docker-compose -f docker-compose.yml up --build
```

Images (prod) séparées : `demo-backend`, `demo-frontend`, `demo-ml`, `demo-db`.

---

## CI / CD

* `Jenkinsfile` inclus pour build, tests unitaires et d’intégration, et déploiement.
* Gates de qualité et couverture de tests intégrés au pipeline.
* Déploiement possible sur environnements dev / staging / prod via Docker images.

---

## Roadmap SaaS (vision)

* **Phase 1 – MVP** : dashboard fonctionnel, backend JWT & CRUD, microservice IA, Docker & CI/CD.
* **Phase 2 – Multi-tenant** : isolation données, OAuth2 / SSO, dashboards personnalisables.
* **Phase 3 – Intelligence avancée** : prévisions long terme, clustering, segmentation, alertes webhook/email.
* **Phase 4 – Scalabilité** : déploiement cloud (AWS / GCP / Azure), load balancing, monitoring (Prometheus / Grafana / ELK).
* **Phase 5 – Monétisation** : offre SaaS publique, abonnements, API pour intégration ERP/CRM.

---

## Contribuer

1. Fork → branche feature → Pull Request.
2. Respecter le guide de contribution : tests unitaires obligatoires et documentation des endpoints.
3. Ouvrir des issues pour bugs / features / questions.

---

## Contacts & ressources

* Repo principal (ex. pour clonage & issues) : `https://github.com/Fofnar/demo`
* Email : fofanafodeba411@gmail.com 
* Documentation API (Swagger) : disponible via le backend en dev.

---

## Licence

© 2026 l'auteur

Ce projet est licencié sous la **Apache License 2.0**.
Vous pouvez consulter le texte complet de la licence ici : [http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)

```
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
