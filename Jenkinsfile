pipeline {
    agent {
        label 'docker_builder'
    }

    environment {
        IMAGE_NAME = "toannd/shoeshop-backend:v4"
        DOCKER_CREDENTIALS = credentials('dockerhub-credentials')
        GITHUB_CREDENTIALS = credentials('github-token')
        CONFIG_REPO_URL = "https://github.com/toannd135/BTL_WEB_BACKEND.git"
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
        stage ()
    }
}