pipeline {
    agent {
        label 'ptit-server'
    }

    environment {
        IMAGE_NAME = "toannd/shoeshop-backend"
        DOCKER_CREDENTIALS = credentials('dockerhub-credentials')
        GITHUB_CREDENTIALS = credentials('github-token')
        CONFIG_REPO_URL = "https://github.com/toannd135/BTL_WEB_BACKEND.git"
    }
    stages {
        stage ('Agent information') {
            steps {
                echo " Running on agent: ${env.NODE_NAME}"
                echo " Workspace: ${env.WORKSPACE}"
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
        
    }
}