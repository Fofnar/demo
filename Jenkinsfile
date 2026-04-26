/**
 * Jenkins Pipeline — Felyxor Continuous Integration (CI)
 *
 * Rôle :
 * Ce pipeline valide automatiquement que l’image Docker de production
 * (Dockerfile.render) peut être construite correctement avant déploiement.
 *
 * Objectif :
 * - Vérifier l’intégrité du projet après chaque push
 * - Contrôler que le backend Spring Boot + service IA FastAPI buildent ensemble
 * - Reproduire localement la logique de build utilisée par Render
 * - Détecter rapidement les erreurs de configuration Docker
 *
 * Important :
 * - Ce fichier gère principalement la CI (Continuous Integration)
 * - Le CD (Continuous Deployment) est actuellement assuré par Render
 *   via l’auto-deploy connecté à la branche principale GitHub
 *
 * Architecture validée :
 * Frontend Render -> Backend Spring Boot + FastAPI (Dockerfile.render) -> PostgreSQL
 *
 * @author Fodeba Fofana
 * @project Felyxor — AI-Powered Business Intelligence Platform
 * @version 1.0
 */
pipeline {

    /**
     * Utilise n’importe quel agent Jenkins disponible
     * capable d’exécuter Docker CLI.
     */
    agent any

    /**
     * Variables globales du pipeline.
     *
     * IMAGE_NAME :
     * Nom logique de l’image Docker générée.
     *
     * IMAGE_TAG :
     * Tag appliqué à l’image (latest = dernière version validée).
     */
    environment {
        IMAGE_NAME = "felyxor-backend"
        IMAGE_TAG  = "latest"
    }

    stages {

        /**
         * Stage 1 — Checkout
         *
         * Récupère automatiquement le code source
         * depuis le dépôt Git configuré dans Jenkins.
         */
        stage('Checkout - Récupération du code') {
            steps {
                checkout scm
            }
        }

        /**
         * Stage 2 — Docker Build Validation
         *
         * Vérifie que Dockerfile.render construit correctement :
         * - Spring Boot backend
         * - FastAPI ML service
         * - Script de démarrage combiné
         *
         * Cette étape sert de contrôle qualité
         * avant déploiement automatique Render.
         */
        stage('Docker Build - Validation de l’image Render') {
            steps {
                sh '''
                  echo "=== Building Felyxor Render image ==="
                  echo "=== Workspace: $WORKSPACE ==="

                  # Construction de l’image Docker de production
                  docker build -f Dockerfile.render -t ${IMAGE_NAME}:${IMAGE_TAG} .
                '''
            }
        }
    }

    /**
     * Actions post-pipeline
     *
     * success :
     * Confirmation que l’image de production est valide
     *
     * failure :
     * Indique qu’une erreur de build ou de configuration existe
     */
    post {

        success {
            echo '✅ CI completed successfully: Render Docker image built.'
        }

        failure {
            echo '❌ CI failed: check Jenkins logs.'
        }
    }
}