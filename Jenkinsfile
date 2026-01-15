pipeline {

    // Indique que Jenkins peut utiliser n'importe quel agent disponible
    agent any

    // Variables d'environnement utilisables dans le pipeline
    environment {
        // Nom de l'image Docker de ton application
        IMAGE_NAME = "demo-app"
        // Tag de version (ici: latest)
        IMAGE_TAG = "latest"
    }

    stages {

        stage('Checkout - Récupération du code') {
            steps {
                // Récupère le code depuis ton dépôt GitHub
                // Jenkins sait automatiquement cloner le repo configuré dans le job
                checkout scm
            }
        }

        stage('Build - Compilation avec Gradle') {
            steps {
                // Sous Windows, on utilise gradlew.bat
                // "clean" : nettoie l'ancien build
                // "build" : compile + lance les tests
                bat 'gradlew.bat clean build'
            }
        }

        stage('Tests - Vérification des tests') {
            steps {
                // Lance uniquement les tests (sécurité supplémentaire)
                bat 'gradlew.bat test'
            }
        }

        stage('Docker Build - Création de l’image') {
            steps {
                // Construit l'image Docker à partir du Dockerfile
                // -t : nom de l'image
                // .  : contexte = dossier du projet
                bat "docker build -t %IMAGE_NAME%:%IMAGE_TAG% ."
            }
        }

        stage('Docker Run - Lancer le conteneur') {
            steps {
                // Supprime le conteneur s’il existe déjà (évite les conflits)
                bat 'docker rm -f demo-app || exit 0'

                // Lance un nouveau conteneur
                // -d : détaché
                // -p 8080:8080 : expose le port de l’application
                bat "docker run -d -p 8080:8080 --name demo-app %IMAGE_NAME%:%IMAGE_TAG%"
            }
        }
    }

    post {
        success {
            // Message si tout se passe bien
            echo '✅ Pipeline terminé avec succès ! Application déployée.'
        }

        failure {
            // Message si une étape échoue
            echo '❌ Erreur dans le pipeline. Vérifie les logs.'
        }
    }
}
