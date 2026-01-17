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

        stage('Build - Compilation avec Gradle (docker CLI)') {
            steps {
                // On build dans un conteneur gradle pour éviter les soucis de Java
                script {
                    // On utilise le même UID/GID que l'utilisateur Jenkins pour éviter les problèmes de droits
                    sh '''
                        UID=$(id -u)
                        GID=$(id -g)
                        echo "Running Gradle build as UID:GID = $UID:$GID"

                        docker run --rm -u $UID:$GID \
                          -v "$WORKSPACE":/home/gradle/project \
                          -w /home/gradle/project \
                          gradle:8.14.2-jdk17 \
                          bash -c "chmod +x gradlew && ./gradlew clean build -x test"
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
