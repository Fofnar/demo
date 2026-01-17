pipeline {
    // Utilise n'importe quel agent Jenkins disponible
    agent any

    // Variables d'environnement
    environment {
        IMAGE_NAME = "demo-app"   // Nom de l'image Docker
        IMAGE_TAG  = "latest"     // Tag Docker
    }

    stages {

        stage('Checkout - Récupération du code') {
            steps {
                // Récupère le code depuis GitHub
                checkout scm
            }
        }

        stage('Build - Compilation avec Gradle (container)') {
            steps {
                // ⚡ On utilise l'image officielle Gradle + JDK 17
                // ⚡ Permet d'exécuter gradlew clean build même si Jenkins container n'a pas Java
                script {
                    docker.image('gradle:8.14.2-jdk17').inside("-v /var/run/docker.sock:/var/run/docker.sock") {
                        // Donne les droits d'exécution au wrapper Gradle
                        sh 'chmod +x gradlew'
                        // Compile + tests (retire -x test si on veux lancer les tests)
                        sh './gradlew clean build -x test'
                    }
                }
            }
        }

        stage('Docker Build - Création de l’image') {
            steps {
                // Construit l'image Docker de l'application
                sh "docker build -t ${IMAGE_NAME}:${IMAGE_TAG} ."
            }
        }

        stage('Docker Run - Lancer le conteneur') {
            steps {
                // Supprime l'ancien conteneur si présent
                sh 'docker rm -f demo-app || true'
                // Lancer un nouveau conteneur
                sh "docker run -d -p 8080:8080 --name demo-app ${IMAGE_NAME}:${IMAGE_TAG}"
            }
        }
    }

    post {
        success {
            echo '✅ Pipeline terminé avec succès ! Application déployée.'
        }
        failure {
            echo '❌ Pipeline en échec. Vérifie les logs !'
        }
    }
}
