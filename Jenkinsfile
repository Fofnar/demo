pipeline {
    agent any

    environment {
        IMAGE_NAME = "demo-app"
        IMAGE_TAG  = "latest"
    }

    stages {
        stage('Checkout - Récupération du code') {
            steps {
                // Récupère le code depuis Git
                checkout scm
            }
        }

        stage('Build - Compilation avec Gradle (Docker Agent)') {
          agent {
            docker {
              image 'gradle:8.14.2-jdk17'
              args '-u gradle:gradle'
            }
          }
          steps {
            sh '''
              echo "=== WORKSPACE = $WORKSPACE ==="
              pwd
              ls -la

              chmod +x gradlew
              ./gradlew clean build -x test
            '''
          }
        }





        stage('Docker Build - Création de l’image') {
            steps {
                // Build l'image Docker de l'app avec Docker CLI du host
                sh "docker build -t ${IMAGE_NAME}:${IMAGE_TAG} ."
            }
        }

        /*
        stage('Docker Run - Lancer le conteneur') {
            steps {
                // Stoppe et supprime le conteneur existant avant de relancer
                sh 'docker rm -f demo-app || true'
                sh "docker run -d -p 8080:8080 --name demo-app ${IMAGE_NAME}:${IMAGE_TAG}"
            }
        }
        */
    }

    post {
        success {
            echo '✅ Pipeline terminé avec succès !'
        }
        failure {
            echo '❌ Pipeline en échec, vérifie les logs !'
        }
    }
}