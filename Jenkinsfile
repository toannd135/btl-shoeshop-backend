pipeline {
    agent {
        label 'ptit-server'
    }

    environment {
        IMAGE_NAME = "toannd135/shoeshop-backend"
        DOCKER_CREDENTIALS = 'dockerhub-credentials'
        GITHUB_CREDENTIALS = 'my-github'
        CONFIG_REPO_URL = "https://github.com/toannd135/btl-shoeshop-backend.git"
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
                        script: "git describe --tags --exact-match 2>/dev/null || git describe --tags --abrev=0 2>/dev/null || git rev-parse --short HEAD",
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
        stage('Test Docker Image') {
            steps {
                echo "Testing Docker image..."

                sh " docker run --rm ${env.IMAGE_NAME}:${env.IMAGE_TAG} java -version"
                echo "Docker image test completed successfully!"
            }
        }
        stage('Push Docker Image to Registry') {
            steps {
                script {
                    echo "Pushing Docker image to registry..."

                    withCredentials([usernamePassword(
                        credentialsId: env.DOCKER_CREDENTIALS ,
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
        stage('Clone Config Repo') {
            steps {
                script {
                    echo " Cloning config repository..."
                    
                    
                    sh 'mkdir -p config-repo'
                    
                    dir('config-repo') {
                    
                        withCredentials([gitUsernamePassword(credentialsId: env.GITHUB_CREDENTIALS, gitToolName: 'Default')]) {
                            sh """
                                git clone ${env.CONFIG_REPO_URL} .
                                git config user.email "nguyenductoan123@gmail.com"
                                git config user.name "Jenkins CI/CD Backend"
                            """
                        }
                        
                        echo " Config repo cloned successfully!"
                        sh 'ls -la'
                    }
                }
            }
        }
        
        stage('Update Helm Manifest') {
            steps {
                script {
                    dir('config-repo') {
                        def tagName = env.IMAGE_TAG
                        echo "Updating with tag: ${tagName}"
                        
                        sh """
                            sed -i 's/^  tag:.*/  tag: "${tagName}"/' backend-chart/values.yaml
                        """
                        
                        sh "grep 'tag:' backend-chart/values.yaml"
                    }
                }
            }
        }
        stage('Push Config Changes') {
            steps {
                script {
                    echo "Pushing changes to config repository..."
                    
                    dir('config-repo') {
                        
                        def gitStatus = sh(
                            script: 'git status --porcelain',
                            returnStdout: true
                        ).trim()
                        
                        if (gitStatus) {
                            echo "Changes detected, committing and pushing..."
                            
                            sh """
                                git add .
                                git commit -m " Update image version to ${env.IMAGE_TAG}
                                
                                - Updated helm-values/values-prod.yaml
                                - Image: ${env.IMAGE_NAME}:${env.IMAGE_TAG}
                                - Build: ${env.BUILD_NUMBER}
                                - Jenkins Job: ${env.JOB_NAME}"
                            """
                            
                            withCredentials([gitUsernamePassword(credentialsId: env.GITHUB_CREDENTIALS, gitToolName: 'Default')]) {
                                sh "git pull origin main --rebase"
                                sh 'git push origin main'
                            }
                            
                            echo " Config changes pushed successfully!"
                        } else {
                            echo " No changes detected in config repo"
                        }
                    }
                }
            }
        }
    }
    post {
        always {
            echo " Cleaning up..."
            sh """
                docker rmi ${env.IMAGE_NAME}:${env.IMAGE_TAG} || true
                docker rmi ${env.IMAGE_NAME}:latest || true
                docker system prune -f || true
            """
            
          
            cleanWs()
        }
        success {
            echo "BUILD SUCCESS!"
            echo "Source code built and pushed: ${env.IMAGE_NAME}:${env.IMAGE_TAG}"
            echo "Config repository updated with new version"
            echo "Docker Hub: https://hub.docker.com/repositories/toannd135/shoeshop-backend"
        }
        failure {
            echo "BUILD FAILED!"
            echo "Please check the logs above"
        }
    }
}