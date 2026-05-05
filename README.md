# Felyxor — AI-Powered Business Intelligence Platform

**Felyxor** is an end-to-end intelligent business analytics platform designed to help companies transform operational data into clear, actionable decisions.

The platform combines a secure Spring Boot backend, a Python/FastAPI AI service, PostgreSQL, and a modern Angular dashboard to deliver business insights such as revenue analytics, anomaly detection, forecasting, inventory monitoring, stockout prediction, and decision-oriented recommendations.

> Felyxor is built with a production mindset: secure APIs, modular architecture, AI-driven analysis, cloud deployment, Docker-based development, CI/CD readiness, and a SaaS-oriented roadmap.

---

## Live Demo

Felyxor V1 is deployed and publicly available online.

| Service | Link |
|---|---|
| Live Application | https://felyxor-frontend.onrender.com |
| Backend API | https://felyxor-backend.onrender.com |
| Swagger / OpenAPI | https://felyxor-backend.onrender.com/swagger-ui/index.html |
| AI Service Health Check | https://felyxor-ai-service.onrender.com/health |

### Demo Access

To access the admin intelligence workspace, use:

```text
Email: demo@felyxor.com
Password: DemoFelyxor2026!
```

This admin demo account gives access to the AI intelligence modules, including sales analysis, anomaly detection, forecasting, inventory analysis, stock prediction, business health scoring, and recommendations.

Visitors can also create a regular user account to test the standard user experience.

---

## Author

**Fodeba Fofana**  
Lead Developer — Fullstack / Data / Machine Learning Engineering

---

## Product Vision

Most small and medium-sized businesses collect operational data but struggle to turn it into fast, reliable decisions.

Felyxor aims to solve that problem by acting as an intelligent decision layer between raw business activity and strategic action.

The platform helps teams answer questions such as:

- Are sales improving or declining?
- Which products generate the most value?
- Are there abnormal revenue patterns?
- Which products are close to stockout?
- What should be restocked first?
- What business risks require immediate attention?
- What actions should be taken next?

Felyxor is not only a dashboard.  
It is designed as a scalable business intelligence platform powered by data, machine learning, and decision support.

---

## Key Capabilities

### Business Analytics

- Total revenue analysis
- Quantity sold analysis
- Average order value
- Revenue per day
- Top-selling products
- Top revenue-generating products
- Sales trend detection
- Business-oriented sales comments

### AI & Machine Learning Intelligence

- Statistical anomaly detection
- Severity classification: low, medium, high, critical
- Revenue forecasting
- Stockout prediction
- Business health scoring
- AI-generated recommendations
- Forecast-based decision support

### Inventory Intelligence

- Low stock detection
- Critical stock alerts
- Out-of-stock detection
- Stock pressure analysis
- Recommended restock quantities
- Related anomaly context per product

### Recommendation Engine

- Product insights
- Trend insights
- Anomaly insights
- Stock recommendations
- Executive summaries
- Actionable business comments

### Secure Platform Features

- JWT authentication
- Access token and refresh token flow
- Role-based access control
- Admin-only dashboards
- User profile management
- Sales management
- User management for administrators

---

## Architecture Overview

Felyxor is structured around three main layers:

```text
Angular Frontend
     |
     | REST API calls
     v
Spring Boot Backend
     |
     | Reads / writes operational data
     v
PostgreSQL Database

Spring Boot Backend
     |
     | Sends serialized business data
     v
FastAPI AI Service
     |
     | Returns analytics, predictions, anomalies and recommendations
     v
Spring Boot Backend
     |
     | Sends standardized response to frontend
     v
Angular Dashboard
```

---

## Production Deployment

Felyxor V1 is deployed on Render using the following architecture:

```text
Render Static Site
Angular Frontend
     |
     v
Render Web Service
Spring Boot Backend
     |
     +--> Render PostgreSQL Database
     |
     +--> Render Web Service
          FastAPI AI Service
```

Production setup:

- Angular frontend deployed as a Render Static Site
- Spring Boot backend deployed as a Render Web Service
- FastAPI AI service deployed as a separate Render Web Service
- PostgreSQL deployed as a managed Render database
- Backend connected to PostgreSQL through environment variables
- Backend connected to the AI service through `ML_SERVICE_URL`
- Frontend connected to the backend through a production API base URL
- CORS configured for the deployed frontend domain

---

## Repository Structure

```text
demo/
├── src/                    # Spring Boot backend
├── ml/                     # Python / FastAPI AI service
├── frontend/               # Angular frontend
├── jenkins/                # Jenkins-related configuration
├── gradle/                 # Gradle wrapper files
├── Dockerfile              # Backend Docker image
├── docker-compose.yml      # PostgreSQL + Spring Boot backend for local development
├── Jenkinsfile             # CI/CD pipeline definition
├── build.gradle            # Backend dependencies and build config
├── requirements.txt        # Python AI service dependencies
└── README.md
```

---

## Technology Stack

### Backend

- Java 17
- Spring Boot
- Spring Security
- JWT Authentication
- Spring Data JPA
- Hibernate
- REST API
- Swagger / OpenAPI documentation

### AI Service

- Python
- FastAPI
- pandas
- NumPy
- scikit-learn style analytics logic
- Prophet
- Forecasting and anomaly detection logic
- Business recommendation engine

### Frontend

- Angular 16
- Reactive Forms
- Route Guards
- HTTP Interceptors
- JWT session handling
- Modular admin intelligence pages
- Responsive dashboard UI

### Database

- PostgreSQL 15
- Managed PostgreSQL in production
- Persistent Docker volume in local development
- JPA-based schema evolution

### DevOps

- Docker
- Docker Compose
- Render
- Jenkins CI/CD
- Gradle
- GitHub

---

## Main Platform Modules

### Admin Dashboard

Central administrator workspace displaying platform statistics:

- Total users
- Total sales
- Sales today
- Total products
- Low stock products
- Quick access to intelligence, sales, users and profile management

### Sales Management

User and admin sales workspaces:

- Create sales records
- Review personal sales history
- Admin search and filtering
- Pagination
- Dynamic filters
- Delete sales records

### Users Management

Admin-only module:

- Search users
- Filter by role
- Update user roles
- Delete users
- Paginated user list

### Felyxor Intelligence Workspace

The AI workspace is split into dedicated modules:

- Overview
- Sales Analysis
- Anomalies
- Business Health
- Inventory
- Prediction
- Stock Prediction
- Stock Recommendations
- Recommendations

This structure keeps the platform readable, scalable and closer to a real SaaS product architecture.

---

## API Documentation

The backend is documented with **Swagger / OpenAPI**.

Production Swagger:

```text
https://felyxor-backend.onrender.com/swagger-ui/index.html
```

Local Swagger:

```text
http://localhost:8080/swagger-ui/index.html
```

OpenAPI JSON is usually available at:

```text
http://localhost:8080/v3/api-docs
```

---

## Backend Configuration

The backend uses PostgreSQL and environment-based configuration for sensitive or environment-specific values.

Main configuration concepts:

- PostgreSQL database connection
- JPA / Hibernate schema evolution
- Server port configuration
- JWT secret from environment variables
- AI service URL from environment variables
- CORS allowed origins from environment variables
- Optional Spring Security debug logs during development

In Docker local development, the database URL uses the Docker Compose database service name because the backend container connects to PostgreSQL through the internal Docker network.

In production, Render injects the PostgreSQL connection, JWT secret, CORS origin, and AI service URL through environment variables.

---

## Docker Compose Setup

The current Docker Compose setup starts:

- PostgreSQL database
- Spring Boot backend

Important note:

The current Docker Compose setup is used for local development.

For local development:

- Backend runs in Docker
- PostgreSQL runs in Docker
- ML service runs locally on port `8000`
- Frontend runs locally on port `4200`

The backend reaches the local ML service through the configured `ML_SERVICE_URL`.

---

## Running the Project Locally

### Prerequisites

Install:

- Java 17
- Gradle or use the included Gradle wrapper
- Python 3.10+
- Node.js 18+
- Angular CLI 16.x
- Docker
- Docker Compose

---

## 1. Clone the Repository

```bash
git clone https://github.com/Fofnar/demo.git
cd demo/demo
```

---

## 2. Start the AI Service

Create and activate a Python virtual environment:

```bash
python -m venv venv
```

Windows:

```bash
venv\Scripts\activate
```

Linux / macOS:

```bash
source venv/bin/activate
```

Install dependencies:

```bash
pip install -r requirements.txt
```

Start the FastAPI AI service:

```bash
uvicorn ml.main:app --reload --host 0.0.0.0 --port 8000
```

The AI service should be available at:

```text
http://localhost:8000
```

Health check:

```text
http://localhost:8000/health
```

---

## 3. Build and Start Backend + Database with Docker

Build Docker images without cache:

```bash
docker compose build --no-cache
```

Start containers:

```bash
docker compose up -d
```

Check running containers:

```bash
docker ps
```

Backend should be available at:

```text
http://localhost:8080
```

Swagger should be available at:

```text
http://localhost:8080/swagger-ui/index.html
```

---

## 4. Start the Angular Frontend

Go to the frontend folder:

```bash
cd frontend
```

Install dependencies:

```bash
npm install
```

Start the Angular development server:

```bash
ng serve
```

Frontend should be available at:

```text
http://localhost:4200
```

---

## Current Local Development Ports

```text
Frontend Angular:      http://localhost:4200
Spring Boot Backend:   http://localhost:8080
FastAPI AI Service:    http://localhost:8000
PostgreSQL:            localhost:5432
Swagger UI:            http://localhost:8080/swagger-ui/index.html
```

---

## Environment Variables

The backend expects the following environment variables:

```env
JWT_SECRET=your-secure-jwt-secret
ML_SERVICE_URL=http://localhost:8000
SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/demo_db
SPRING_DATASOURCE_USERNAME=demo_user
SPRING_DATASOURCE_PASSWORD=demo_pass
CORS_ALLOWED_ORIGINS=http://localhost:4200
```

In production, secrets must not be hardcoded.

Use environment variables from the hosting provider.

---

## API Response Standard

The backend uses a standardized response wrapper:

```json
{
  "success": true,
  "data": {},
  "message": "Operation completed successfully",
  "timestamp": "2026-04-23T12:00:00"
}
```

This makes frontend integration more predictable and keeps error handling consistent.

---

## AI Service Output

The AI service currently returns a rich business intelligence payload containing multiple analysis blocks, including:

- `sales_analysis`
- `anomalies`
- `prediction`
- `recommendations`
- `health_score`
- `inventory`
- `stock_prediction`
- `stock_recommendations`

The actual response is intentionally not fully embedded in this README because the current payload is large and business-oriented.

For the most accurate contract, use:

- Swagger documentation
- Backend DTOs / TypeScript models
- FastAPI service response structure

This keeps the README clean while preserving the source of truth inside the application code and API documentation.

---

## Frontend Intelligence Pages

The Angular frontend provides a modular intelligence workspace:

```text
/admin/ai
/admin/ai/sales-analysis
/admin/ai/anomalies
/admin/ai/health
/admin/ai/inventory
/admin/ai/prediction
/admin/ai/stock-prediction
/admin/ai/stock-recommendations
/admin/ai/recommendations
```

A frontend facade service caches the full AI analysis response to avoid unnecessary calls to the backend when navigating between intelligence modules.

---

## Security

Current security features:

- JWT authentication
- Refresh token flow
- Role-based guards on frontend
- Admin-only routes
- Spring Security backend protection
- Password hashing with BCrypt
- Centralized HTTP interceptor
- Automatic token refresh on protected requests
- Logout on invalid session
- CORS configured for frontend/backend communication

Admin-only areas include:

- Admin dashboard
- Admin sales management
- Admin users management
- AI intelligence workspace

---

## CI/CD

The project includes a `Jenkinsfile` for pipeline automation.

Current CI/CD direction:

- Build backend
- Run tests
- Build Docker image
- Prepare deployment flow

Planned improvements:

- Add frontend build stage
- Add ML service test stage
- Add Docker image build for frontend and ML service
- Add staging deployment
- Add production deployment workflow

---

## Roadmap

### Phase 1 — MVP Platform

- Secure authentication
- Sales management
- Admin dashboard
- AI analysis service
- Angular frontend
- PostgreSQL integration
- Dockerized backend and database

### Phase 2 — V1 Demo Deployment

- Render PostgreSQL configured
- FastAPI AI service deployed
- Spring Boot backend deployed
- Angular frontend deployed
- Backend connected to PostgreSQL
- Backend connected to AI service
- Frontend connected to production backend
- Authentication, sales, dashboard and AI modules tested online

### Phase 3 — Full Containerization

- Dockerize Angular frontend
- Dockerize FastAPI ML service
- Add full multi-service Docker Compose
- Add environment-specific configurations

### Phase 4 — SaaS Foundations

- Multi-tenant architecture
- Tenant-aware data isolation
- Organization accounts
- User roles per tenant
- Billing-ready structure

### Phase 5 — Advanced Intelligence

- More advanced forecasting
- Product segmentation
- Customer behavior analytics
- Automated alerts
- Email / webhook notifications
- Advanced recommendation scoring

### Phase 6 — Production & Scale

- Monitoring
- Logging
- CI/CD deployment gates
- Production-grade security hardening
- Scalability improvements
- SaaS onboarding workflow

---

## Why This Project Matters

Felyxor demonstrates the ability to design and build a real-world data product from end to end:

- Backend engineering
- Data modeling
- API design
- Authentication and authorization
- Machine learning service integration
- Business intelligence workflows
- Frontend dashboard development
- Cloud deployment
- Docker-based development
- CI/CD readiness
- Product thinking

It is built to showcase not only technical implementation, but also the ability to transform a business problem into a scalable software product.

---

## Project Evaluation Context

This project was developed as part of the course:

**Sciences du logiciel par l’expérimentation**  
Université Côte d’Azur — Academic year 2025–2026  
Supervisor: Stéphane Jeannin

---

### Academic Objective

The goal of this project is to design, implement, and evaluate a complete software system that transforms operational data into decision-support insights.

The project emphasizes:

- modular software architecture
- backend / frontend separation
- secure API design
- integration of a machine learning service
- cloud deployment and reproducibility
- experimental validation approach

---

### Skills Demonstrated

This project demonstrates practical competencies in:

- Fullstack development (Angular + Spring Boot)
- API design and standardization
- Authentication and security (JWT, RBAC)
- Data engineering and structured data handling
- Integration of ML services (FastAPI)
- Business intelligence workflows
- Cloud deployment (Render)
- Docker-based environments
- CI/CD preparation (Jenkins)

---

### Reproducibility

The project can be executed locally by following the instructions provided in this README and in the academic report.

All dependencies are managed via:

- `requirements.txt`
- `package.json`
- `build.gradle`
- `docker-compose.yml`

---

### Live Demonstration

A deployed version of the platform is available:

```text
https://felyxor-frontend.onrender.com
```

Admin demo access:

```text
Email: demo@felyxor.com
Password: DemoFelyxor2026!
```

---

## License

© 2026 Fodeba Fofana

This project is licensed under the **Apache License 2.0**.

You may obtain a copy of the license at:

```text
http://www.apache.org/licenses/LICENSE-2.0
```

Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an **"AS IS" BASIS**, without warranties or conditions of any kind.