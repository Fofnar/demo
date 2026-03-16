Intelligent SME Analytics — Dashboard (AI & Data Platform) 

License: Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0)

Projet : Tableau de bord d’analyse opérationnelle pour PME 

Auteur / Lead Dev : Fodeba Fofana 

 

Présentation 

Intelligent SME Analytics est une solution end-to-end pour aider les PME à suivre, analyser et anticiper leurs ventes et inventaires. Le projet intègre : 

La collecte de données opérationnelles depuis la base. 

Un pipeline de traitement et d’analytique. 

Un service IA pour détection d’anomalies, prévisions et recommandations. 

Une API REST sécurisée exposant les données et insights. 

Un dashboard Angular pour visualiser KPI, graphiques et alertes (en cours d’intégration). 

Ce projet est conçu pour un usage production-ready, avec JWT auth, gestion des erreurs, tests automatisés, Dockerisation et pipeline CI/CD via Jenkins. 

 

Valeur ajoutée 

Projet complet : collecte → pipeline → ML → API → dashboard. 

Architecture microservices réaliste : séparation backend / service IA / frontend. 

Cas d’usage concret : réduction du temps de validation des données et automatisation des alertes stock/ventes. 

Production-ready : sécurité, tests, Docker, CI/CD. 

Ce projet démontre ma capacité à concevoir et déployer une solution SaaS complète, montrant des compétences en Java Spring Boot, Python ML, Angular, DevOps, et architecture microservices. 

 

Fonctionnalités clés 

Authentification sécurisée : JWT (access + refresh), mots de passe hachés avec BCrypt. 

Gestion utilisateurs & ventes : CRUD + pagination + tri + filtres + recherche dynamique via Specifications. 

API de ventes & analytics : chiffre d’affaires total, chiffre par jour, top produits. 

Détection d’anomalies : algorithmes sklearn via microservice FastAPI. 

Prévisions de revenus & tendances : prévisions court terme et recommandations. 

Recommandations métier : actions marketing, alertes stock. 

Health score : indicateur synthétique de santé commerciale. 

Alertes inventaire & prédiction rupture stock. 

Conteneurisation : Docker Compose pour dev, images Docker pour prod. 

Pipeline CI/CD : Jenkins pour build, tests, déploiement. 

Frontend Angular : visualisation de KPI et graphiques (en cours). 

 

Architecture  

[Angular Frontend] <--> [Spring Boot API] <--> [PostgreSQL] 
                                | 
                                +--> [FastAPI AI Service] --> (pandas / scikit-learn) 
 

Spring Boot : API, sécurité, orchestration, DTO, controllers, persistence (JPA). 

FastAPI : service ML, endpoints d’analyse, renvoie JSON standardisé. 

PostgreSQL : stockage opérationnel. 

Docker Compose : orchestration dev, images Docker prod. 

Jenkins : pipeline build/test/deploy. 

 

Stack Technique 

Backend : Spring Boot (Java 17) 

API REST sécurisée (JWT, BCrypt) 

Gestion des entités avec JPA / Hibernate 

Controllers, DTO, Services bien séparés pour architecture propre 

Recherche dynamique et filtres via Specifications 

Pagination et tri avancés 

Microservice IA : FastAPI (Python 3.11) 

Pandas pour manipulation des données 

Scikit-learn pour détection d’anomalies et prévisions 

JSON standardisé pour intégration API 

Frontend : Angular 16+ (en cours d’intégration) 

Visualisation KPI et graphiques 

Composants réactifs, responsive 

Base de données : PostgreSQL 

Tables normalisées pour utilisateurs, ventes, stocks 

Conteneurisation & orchestration : Docker / Docker Compose 

CI/CD : Jenkins pipeline 

Build, tests, déploiement automatisé 

Test coverage & quality gates intégrés 

 

Pipeline Data & IA 

Collecte de données 

Ventes, stocks, utilisateurs, événements opérationnels. 

Extraction depuis PostgreSQL via DAO Spring Boot. 

Transformation & préparation 

Nettoyage, agrégation, calcul de métriques (revenue per day, top products). 

Sérialisation des données vers JSON pour FastAPI. 

Analyse & Machine Learning 

Détection d’anomalies (outliers, ventes irrégulières). 

Prévision des revenus et estimation des ruptures de stock. 

Recommandations métiers automatisées (marketing, réapprovisionnement). 

Exposition via API 

Spring Boot orchestrateur → récupère données + résultats IA 

JSON standardisé prêt pour le dashboard Angular 

Exemple de réponse JSON inclus dans le README 

 

Microservice IA & API 

1️⃣ Architecture IA 

[Angular Frontend] <---> [Spring Boot API] <---> [PostgreSQL] 
                                       | 
                                       +--> [FastAPI AI Service] --> (pandas / scikit-learn) 
 

Spring Boot : orchestrateur central 

Fournit les endpoints REST sécurisés (JWT + refresh token) 

Coordonne les requêtes avec la base de données et le microservice IA 

Sérialisation des entités en DTO pour FastAPI 

FastAPI (Python) : moteur d’analyse et prédiction 

Détection d’anomalies sur les ventes et stocks 

Prévision des revenus (court terme & 3 jours) 

Génération de recommandations métier (marketing, réapprovisionnement) 

JSON standardisé pour intégration simple côté Spring Boot 

PostgreSQL : stockage opérationnel fiable 

Tables utilisateurs, ventes, stocks 

Relations normalisées + contraintes d’intégrité 

 

2️⃣ Exemple de workflow 

L’utilisateur interagit avec le dashboard Angular. 

Angular appelle un endpoint Spring Boot API (/api/sales) 

Spring Boot récupère les données depuis PostgreSQL 

Les données sont transformées en JSON et envoyées à FastAPI IA 

FastAPI renvoie : 

Analyse des ventes (total_revenue, top_selling_product) 

Anomalies détectées 

Prédictions de revenu 

Recommandations métiers 

Alertes stock et prédiction rupture 

Spring Boot assemble le tout dans un format uniforme JSON et renvoie au frontend 

Angular affiche les KPI, graphiques et alertes dynamiquement 

 

3️⃣ Exemple de réponse JSON (Swagger / contrat API) 

{ 

  "sales_analysis": { 

    "total_revenue": 4450, 

    "total_quantity_sold": 19, 

    "top_selling_product": "Mouse", 

    "revenue_per_day": [ 

      { 

        "date": "2025-01-01", 

        "revenue": 1000 

      }, 

      { 

        "date": "2025-01-02", 

        "revenue": 200 

      }, 

      { 

        "date": "2025-01-03", 

        "revenue": 2000 

      }, 

      { 

        "date": "2025-01-04", 

        "revenue": 250 

      }, 

      { 

        "date": "2025-01-05", 

        "revenue": 1000 

      } 

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

  "health_score": { 

    "health_score": 30 

  }, 

  "inventory": { 

    "low_stock_alerts": [ 

      { 

        "product": "Keyboard", 

        "stock": 9, 

        "warning": "Low stock" 

      }, 

      { 

        "product": "Mouse", 

        "stock": 5, 

        "warning": "Low stock" 

      } 

    ] 

  }, 

  "stock_prediction": { 

    "stock_prediction": [ 

      { 

        "product": "Laptop", 

        "current_stock": 70, 

        "avg_daily_sales": 1.3333333333333333, 

        "estimated_days_before_stockout": 52.5 

      }, 

      { 

        "product": "Mouse", 

        "current_stock": 5, 

        "avg_daily_sales": 10, 

        "estimated_days_before_stockout": 0.5 

      }, 

      { 

        "product": "Keyboard", 

        "current_stock": 9, 

        "avg_daily_sales": 5, 

        "estimated_days_before_stockout": 1.8 

      } 

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

 

Installation 

1️⃣ Prérequis 

Java 17 (pour Spring Boot) 

Gradle 8+ 

Python 3.10+ (pour microservice IA FastAPI) 

Node.js 18+ et Angular CLI 16+ (pour le frontend Angular) 

Docker & Docker Compose (pour dev et production) 

PostgreSQL 14+ 

 

2️⃣ Installation en mode développement 

Cloner le projet : 

git clone https://github.com/Fofnar/demo 
cd demo/demo 
 

Créer un environnement Python pour IA : 

python -m venv venv 
venv\Scripts\activate  # Windows 
pip install -r requirements.txt 
 

Configurer PostgreSQL 

Créer une base demo_db 

Mettre à jour application.properties (Spring Boot) avec le user / password 

Lancer le backend Spring Boot : 

./gradlew bootRun 
 

Lancer le microservice IA (FastAPI) : 

uvicorn ml.main:app --reload 
 

Lancer le frontend Angular (en cours d’intégration) : 

cd frontend 
npm install 
ng serve 
 

L’accès local : http://localhost:4200 (Angular), http://localhost:8080 (Spring Boot), http://localhost:8000 (FastAPI) 

 

3️⃣ Installation avec Docker (dev / prod) 

Docker Compose dev : 

docker-compose -f docker-compose.yml up --build 
 

Images Docker séparées pour prod : 

demo-backend (Spring Boot) 

demo-frontend (Angular) 

demo-ml (FastAPI) 

demo-db (PostgreSQL) 

Pipeline CI/CD avec Jenkins 

Jenkinsfile présent pour build / test / déploiement 

Tests unitaires et d’intégration automatiques 

Déploiement sur dev / staging / prod possible via Docker 

 

Roadmap SaaS (Pro / Startup-ready) 

Phase 1 – MVP : 

Dashboard Angular fonctionnel (KPI, graphiques, alertes) 

Backend Spring Boot avec JWT, CRUD utilisateurs & ventes, pagination / tri / filtres / recherche dynamique 

Microservice IA FastAPI opérationnel (anomalies, prédiction, recommandations) 

Conteneurisation Docker & CI/CD Jenkins 

Phase 2 – SaaS Multi-tenant : 

Gestion multi-PME avec isolation des données 

Authentification OAuth2 / SSO 

Tableaux de bord personnalisables par entreprise 

Phase 3 – Intelligence avancée : 

Algorithmes ML supplémentaires (prévisions long terme, clustering clients, segmentation) 

Alertes automatiques par email / webhook 

Analyses prédictives et recommandations marketing plus fines 

Phase 4 – Scalabilité & Production : 

Déploiement sur cloud (AWS / GCP / Azure) 

Load balancing & microservices scalables 

Monitoring & logging centralisé (Prometheus / Grafana / ELK) 

Phase 5 – Monétisation & Start-up : 

Dashboard SaaS public pour PME 

Abonnement mensuel / premium features 

API REST ouverte pour intégration avec ERP / CRM 

 

Licence 

© 2026 Fodeba Fofana 

Ce projet est licencié sous la Apache License 2.0. 

Vous pouvez consulter le texte complet de la licence ici: http://www.apache.org/licenses/LICENSE-2.0 

Copyright 2026 Fodeba Fofana 
 
Licensed under the Apache License, Version 2.0 (the "License"); 
you may not use this file except in compliance with the License. 
You may obtain a copy of the License at 
 
   http://www.apache.org/licenses/LICENSE-2.0 
 
Unless required by applicable law or agreed to in writing, software 
distributed under the License is distributed on an "AS IS" BASIS, 
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
See the License for the specific language governing permissions and 
limitations under the License. 
