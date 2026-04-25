#!/bin/sh
set -e

# Démarre le service IA FastAPI en local dans le conteneur.
# Il n'est pas exposé publiquement : seul le backend Spring Boot l'appelle.
cd /app/ml
/opt/venv/bin/uvicorn main:app --host 127.0.0.1 --port 8000 &

# Retour au dossier principal de l'application.
cd /app

# Configuration par défaut de l'URL IA interne.
# Render peut aussi surcharger cette valeur via ses variables d'environnement.
export ML_SERVICE_URL="${ML_SERVICE_URL:-http://127.0.0.1:8000}"

# Démarre l'API Spring Boot sur le port fourni par Render.
exec java -jar app.jar