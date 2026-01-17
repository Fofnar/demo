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

        stage('Build - Compilation avec Gradle (via docker CLI)') {
          steps {
            script {
              // debug : montrer le contenu du workspace côté Jenkins (pour savoir où est gradlew)
              sh 'echo "=== WORKSPACE = $WORKSPACE ==="; ls -la "$WORKSPACE" || true'

              // Lance le conteneur gradle et choisi la bonne commande selon si gradlew existe
              sh '''
                UID=$(id -u)
                GID=$(id -g)
                echo "Running Gradle build as UID:GID = $UID:$GID"

                # commande à exécuter DANS le conteneur : teste gradlew d'abord, sinon utilise gradle CLI
                docker run --rm -u $UID:$GID \
                  -v "$WORKSPACE":/home/gradle/project \
                  -w /home/gradle/project \
                  gradle:8.14.2-jdk17 \
                  bash -lc '
                    echo "Inside container, pwd=$(pwd)"
                    echo "Listing project dir:"; ls -la .
                    if [ -f ./gradlew ]; then
                      echo "Found gradlew -> using wrapper"
                      chmod +x ./gradlew
                      ./gradlew clean build -x test
                    else
                      echo "No gradlew found -> using gradle CLI directly"
                      gradle clean build -x test
                    fi
                  '
              '''
            }
          }
        }


        stage('Docker Build - Création de l’image') {
            steps {
                // Build l'image Docker de l'app avec Docker CLI du host
                sh "docker build -t ${IMAGE_NAME}:${IMAGE_TAG} ."
            }
        }

        stage('Docker Run - Lancer le conteneur') {
            steps {
                // Stoppe et supprime le conteneur existant avant de relancer
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
            echo '❌ Pipeline en échec, vérifie les logs !'
        }
    }
}
