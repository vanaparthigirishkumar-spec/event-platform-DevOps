pipeline {
    agent any

    options {
        timestamps()
        timeout(time: 60, unit: 'MINUTES')
        buildDiscarder(logRotator(numToKeepStr: '20', artifactNumToKeepStr: '5'))
    }

    environment {
        // SonarQube credentials - configure in Jenkins credentials
        SONAR_TOKEN = credentials('sonarqube-token')
        SONAR_URL = 'http://sonarqube:9000'
        
        // Docker registry credentials - configure in Jenkins credentials
        DOCKER_REGISTRY = 'docker.io'
        DOCKER_USERNAME = credentials('docker-username')
        DOCKER_PASSWORD = credentials('docker-password')
        
        // Image names
        BACKEND_IMAGE = "flm-cloud-native-platform-backend"
        FRONTEND_IMAGE = "flm-cloud-native-platform-frontend"
        
        // Git commit SHA for immutable tags
        GIT_COMMIT_SHORT = "${env.GIT_COMMIT?.take(7) ?: 'local'}"
        BUILD_NUMBER = "${env.BUILD_NUMBER ?: '0'}"
        IMAGE_TAG = "${GIT_COMMIT_SHORT}-${BUILD_NUMBER}"
        
        // Maven options
        MAVEN_OPTS = "-Dmaven.repo.local=.m2/repository -Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn"
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
                script {
                    // Set commit SHA if not provided by SCM
                    if (!env.GIT_COMMIT) {
                        env.GIT_COMMIT = sh(script: 'git rev-parse HEAD', returnStdout: true).trim()
                    }
                    env.GIT_COMMIT_SHORT = env.GIT_COMMIT.take(7)
                    env.IMAGE_TAG = "${env.GIT_COMMIT_SHORT}-${env.BUILD_NUMBER}"
                    echo "Build tag: ${env.IMAGE_TAG}"
                }
            }
        }

        stage('Backend Build') {
            agent { label 'maven' }
            steps {
                dir('backend') {
                    sh '''
                        ./mvnw -B clean compile \
                            -Dmaven.test.skip=true \
                            -Dmaven.javadoc.skip=true \
                            -Dmaven.source.skip=true
                    '''
                }
            }
        }

        stage('Backend Unit Tests') {
            agent { label 'maven' }
            steps {
                dir('backend') {
                    sh '''
                        ./mvnw -B test \
                            -Dmaven.test.failure.ignore=false
                    '''
                }
            }
            post {
                always {
                    junit 'backend/target/surefire-reports/*.xml'
                }
            }
        }

        stage('Frontend Install') {
            agent { label 'node' }
            steps {
                dir('frontend') {
                    sh '''
                        npm ci --legacy-peer-deps
                    '''
                }
            }
        }

        stage('Frontend Typecheck') {
            agent { label 'node' }
            steps {
                dir('frontend') {
                    sh '''
                        npm run typecheck
                    '''
                }
            }
        }

        stage('Frontend Tests') {
            agent { label 'node' }
            steps {
                dir('frontend') {
                    sh '''
                        npm run test
                    '''
                }
            }
            post {
                always {
                    publishHTML([
                        allowMissing: false,
                        alwaysLinkToLastBuild: true,
                        keepAll: true,
                        reportDir: 'frontend/coverage',
                        reportFiles: 'index.html',
                        reportName: 'Frontend Coverage'
                    ])
                }
            }
        }

        stage('Frontend Build') {
            agent { label 'node' }
            steps {
                dir('frontend') {
                    sh '''
                        npm run build
                    '''
                }
            }
        }

        stage('SonarQube Analysis') {
            agent any
            when {
                expression { return env.SONAR_TOKEN != null && env.SONAR_TOKEN != '' }
            }
            steps {
                script {
                    // Backend analysis
                    dir('backend') {
                        withSonarQubeEnv('SonarQube') {
                            sh '''
                                ./mvnw -B sonar:sonar \
                                    -Dsonar.projectKey=flm-cloud-native-platform-backend \
                                    -Dsonar.projectName="Event Platform Backend" \
                                    -Dsonar.sources=src/main/java \
                                    -Dsonar.tests=src/test/java \
                                    -Dsonar.java.binaries=target/classes \
                                    -Dsonar.junit.reportPaths=target/surefire-reports \
                                    -Dsonar.jacoco.reportPaths=target/site/jacoco/jacoco.xml \
                                    -Dsonar.exclusions=**/generated/**,**/target/**,**/*.xml
                            '''
                        }
                    }
                    
                    // Frontend analysis
                    dir('frontend') {
                        withSonarQubeEnv('SonarQube') {
                            sh '''
                                npx sonar-scanner \
                                    -Dsonar.projectKey=flm-cloud-native-platform-frontend \
                                    -Dsonar.projectName="Event Platform Frontend" \
                                    -Dsonar.sources=src \
                                    -Dsonar.tests=src/tests \
                                    -Dsonar.test.inclusions=**/*.test.tsx,**/*.test.ts \
                                    -Dsonar.exclusions=**/node_modules/**,**/dist/**,**/coverage/**,**/*.d.ts,**/vite.config.ts,**/vitest.config.ts \
                                    -Dsonar.typescript.lcov.reportPaths=coverage/lcov.info \
                                    -Dsonar.javascript.lcov.reportPaths=coverage/lcov.info
                            '''
                        }
                    }
                }
            }
        }

        stage('Docker Image Build') {
            agent { label 'docker' }
            steps {
                script {
                    // Build backend image
                    dir('backend') {
                        sh """
                            docker build \
                                -t ${BACKEND_IMAGE}:${IMAGE_TAG} \
                                -t ${BACKEND_IMAGE}:latest \
                                .
                        """
                    }
                    
                    // Build frontend image
                    dir('frontend') {
                        sh """
                            docker build \
                                -t ${FRONTEND_IMAGE}:${IMAGE_TAG} \
                                -t ${FRONTEND_IMAGE}:latest \
                                .
                        """
                    }
                }
            }
        }

        stage('Trivy Image Scan') {
            agent { label 'docker' }
            when {
                expression { return env.SCAN_IMAGES == 'true' || env.SCAN_IMAGES == null }
            }
            steps {
                script {
                    // Scan backend image
                    sh """
                        trivy image --exit-code 1 --severity HIGH,CRITICAL \
                            --format table \
                            ${BACKEND_IMAGE}:${IMAGE_TAG}
                    """
                    
                    // Scan frontend image
                    sh """
                        trivy image --exit-code 1 --severity HIGH,CRITICAL \
                            --format table \
                            ${FRONTEND_IMAGE}:${IMAGE_TAG}
                    """
                }
            }
        }

        stage('Image Tagging & Push') {
            agent { label 'docker' }
            when {
                branch 'main'
                expression { return env.DOCKER_USERNAME != null && env.DOCKER_PASSWORD != null }
            }
            steps {
                script {
                    docker.withRegistry("https://${DOCKER_REGISTRY}", 'docker-credentials') {
                        sh """
                            docker push ${DOCKER_USERNAME}/${BACKEND_IMAGE}:${IMAGE_TAG}
                            docker push ${DOCKER_USERNAME}/${BACKEND_IMAGE}:latest
                            docker push ${DOCKER_USERNAME}/${FRONTEND_IMAGE}:${IMAGE_TAG}
                            docker push ${DOCKER_USERNAME}/${FRONTEND_IMAGE}:latest
                        """
                    }
                }
            }
        }
    }

    post {
        always {
            cleanWs()
        }
        success {
            echo "Pipeline completed successfully!"
        }
        failure {
            echo "Pipeline failed!"
        }
        unstable {
            echo "Pipeline unstable - check test results and quality gates"
        }
    }
}