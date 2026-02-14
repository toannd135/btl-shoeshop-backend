pipeline {
    agent any

    environment {
        IMAGE_NAME = "toannd/shoeshop-backend:v4"
        DOCKER_CREDENTIALS = credentials('dockerhub-credentials')
        GITHUB_CREDENTIALS = credentials('github-token')
        CONFIG_REPO_URL = "https://gitlab.com/btl-web/shoeshop-backend.git"
    }
    stages {
        stage ('Agent information') {
            steps {
                sh 'whoami'
                sh 'pwd'
                sh 'uname -a'
            }
        }
        stage ('Checkout source code') {
            steps {
                echo " Cloning source code..."
                checkout scm
                
                echo " Clone completed!"
                sh 'ls -la'
            }
        }
        stage('Build Docker Image') {
            steps {
                sh "docker build -t ${IMAGE_NAME} ."
            }
        }
        stage('Push to Docker Hub') {
            steps {
                sh "echo \$DOCKER_CREDENTIALS_PSW | docker login -u \$DOCKER_CREDENTIALS_USR --password-stdin"
                sh "docker push ${IMAGE_NAME}"
            }
        }
    }
}