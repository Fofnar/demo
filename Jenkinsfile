pipeline {

    // Utilise l'agent Jenkins par défaut (le conteneur Docker Jenkins)
    agent any

    environment {
        IMAGE_NAME = "demo-app"
        IMAGE_TAG = "latest"
    }

    stages {

        stage('Checkout - Récupération du code') {
            steps {
                checkout scm
            }
        }

        stage('Build - Compilation avec Gradle') {
            steps {
                // ⚠️ Jenkins tourne sous Linux → pas gradlew.bat
                sh 'chmod +x gradlew'
                sh './gradlew clean build'
            }
        }

        stage('Tests - Vérification des tests') {
            steps {
                sh './gradlew test'
            }
        }

        stage('Docker Build - Création de l’image') {
            steps {
                sh "docker build -t ${IMAGE_NAME}:${IMAGE_TAG} ."
            }
        }

        stage('Docker Run - Lancer le conteneur') {
            steps {
                sh 'docker rm -f demo-app || true'
                sh "docker run -d -p 8080:8080 --name demo-app ${IMAGE_NAME}:${IMAGE_TAG}"
            }
        }
    }

    post {
        success {
            echo '✅ Pipeline terminé avec succès !'
        }
        failure {
            echo '❌ Pipeline en échec'
        }
    }
}
