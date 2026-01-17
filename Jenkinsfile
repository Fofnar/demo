pipeline {
  agent any

  environment {
    IMAGE_NAME = "demo-app"
    IMAGE_TAG  = "latest"
  }

  stages {
    stage('Checkout') {
      steps {
        checkout scm
      }
    }

    stage('Build - Compilation avec Gradle (via docker CLI)') {
      steps {
        // On lance un conteneur gradle sur le host Docker via docker CLI.
        // On monte le workspace Jenkins dedans pour que le build écrive les fichiers dans le workspace.
        // On exécute le conteneur avec le même UID:GID que l'utilisateur Jenkins pour éviter les problèmes de droits.
        sh '''
          UID=$(id -u)
          GID=$(id -g)
          echo "Running build as UID:GID = $UID:$GID"
          docker run --rm -u $UID:$GID -v "$WORKSPACE":/home/gradle/project -w /home/gradle/project gradle:8.14.2-jdk17 bash -c "chmod +x gradlew && ./gradlew clean build -x test"
        '''
      }
    }

    stage('Docker Build - Création image') {
      steps {
        sh "docker build -t ${IMAGE_NAME}:${IMAGE_TAG} ."
      }
    }

    stage('Docker Run') {
      steps {
        sh 'docker rm -f demo-app || true'
        sh "docker run -d -p 8080:8080 --name demo-app ${IMAGE_NAME}:${IMAGE_TAG}"
      }
    }
  }

  post {
    success { echo '✅ Pipeline terminé !' }
    failure { echo '❌ Pipeline en échec' }
  }
}
