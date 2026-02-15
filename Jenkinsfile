pipeline {
    agent {
        label 'ptit-server'
    }

    environment {
        IMAGE_NAME = "toannd135/shoeshop-backend"
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
        stage('Get version from Git') {
            steps {
                script {
                    echo " Getting version information from Git..."
                    sh 'git log --oneline -n 10'

                    sh 'git tag --list'

                    def tagVersion = sh (
                        script: "git describe --tags --exact-match 2>/dev/null || git describe --tags --abrev=0 || git rev-parse --short HEAD",
                        returnStdout: true
                    ).trim()

                    env.IMAGE_TAG = tagVersion
                    echo " Version determined: ${env.IMAGE_TAG}"
                    echo " Docker image will be tagged as: ${env.IMAGE_NAME}:${env.IMAGE_TAG}"
                }
            }
        }
        stage('Build Docker Image') {
            steps {
                echo "Building Docker image ${env.IMAGE_NAME}:${env.IMAGE_TAG}..."

                sh " docker build -t ${env.IMAGE_NAME}:${env.IMAGE_TAG} ."
                echo "Docker image built successfully!"
                sh " docker tag ${env.IMAGE_NAME}:${env.IMAGE_TAG} ${env.IMAGE_NAME}:latest"
                echo "Docker image tagged as latest!"

                sh " docker images | grep ${env.IMAGE_NAME}"
            }
        }
        stage('Push Docker Image to Registry') {
            steps {
                script {
                    echo "Pushing Docker image to registry..."

                    withCredentials([usernamePassword(
                        credentialsId: env.DOCKER_CREDENTIALS,
                        usernameVariable: 'DOCKER_USERNAME',
                        passwordVariable: 'DOCKER_PASSWORD'
                    )]) {
                        sh " echo ${env.DOCKER_PASSWORD} | docker login -u ${env.DOCKER_USERNAME} --password-stdin"
                        sh " docker push ${env.IMAGE_NAME}:${env.IMAGE_TAG}"
                    }
                    echo "Docker image pushed successfully!"
                }
            }
        }
    }
}