pipeline {
    agent any

    tools {
        jdk 'JDK17'
        maven 'M3'
    }

    options {
        timestamps()
        disableConcurrentBuilds()
        buildDiscarder(logRotator(numToKeepStr: '20'))
    }

    stages {

        stage('Checkout') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/Poojithamodala/Utility-Billing-System'
            }
        }

        stage('Check Environment') {
            steps {
                bat 'java -version'
                bat 'mvn -version'
                bat 'docker --version'
                bat 'docker-compose --version'
            }
        }

        stage('Build Microservices') {
            parallel {

                stage('Eureka Server') {
                    steps {
                        dir('backend/eureka-server') {
                            bat 'mvn package'
                        }
                    }
                }

                stage('Config Server') {
                    steps {
                        dir('backend/config-server') {
                            bat 'mvn package'
                        }
                    }
                }

                stage('API Gateway') {
                    steps {
                        dir('backend/api-gateway') {
                            bat 'mvn package'
                        }
                    }
                }

                stage('Auth Service') {
                    steps {
                        dir('backend/auth-service') {
                            bat 'mvn package'
                        }
                    }
                }

                stage('Consumer Service') {
                    steps {
                        dir('backend/consumer-service') {
                            bat 'mvn package'
                        }
                    }
                }

                stage('Connection Service') {
                    steps {
                        dir('backend/connection-service') {
                            bat 'mvn package'
                        }
                    }
                }

                stage('Meter Reading Service') {
                    steps {
                        dir('backend/meter-reading-service') {
                            bat 'mvn package'
                        }
                    }
                }

                stage('Billing Service') {
                    steps {
                        dir('backend/billing-service') {
                            bat 'mvn package'
                        }
                    }
                }

                stage('Payment Service') {
                    steps {
                        dir('backend/payment-service') {
                            bat 'mvn package'
                        }
                    }
                }

                stage('Notification Service') {
                    steps {
                        dir('backend/notification-service') {
                            bat 'mvn package'
                        }
                    }
                }
            }
        }

        stage('Docker Build Images') {
            parallel {

                stage('Eureka Image') {
                    steps {
                        dir('backend/eureka-server') {
                            bat 'docker build -t eureka-server .'
                        }
                    }
                }

                stage('Config Server Image') {
                    steps {
                        dir('backend/config-server') {
                            bat 'docker build -t config-server .'
                        }
                    }
                }

                stage('API Gateway Image') {
                    steps {
                        dir('backend/api-gateway') {
                            bat 'docker build -t api-gateway .'
                        }
                    }
                }

                stage('Auth Service Image') {
                    steps {
                        dir('backend/auth-service') {
                            bat 'docker build -t auth-service .'
                        }
                    }
                }

                stage('Consumer Service Image') {
                    steps {
                        dir('backend/consumer-service') {
                            bat 'docker build -t consumer-service .'
                        }
                    }
                }

                stage('Connection Service Image') {
                    steps {
                        dir('backend/connection-service') {
                            bat 'docker build -t connection-service .'
                        }
                    }
                }

                stage('Meter Reading Image') {
                    steps {
                        dir('backend/meter-reading-service') {
                            bat 'docker build -t meter-reading-service .'
                        }
                    }
                }

                stage('Billing Service Image') {
                    steps {
                        dir('backend/billing-service') {
                            bat 'docker build -t billing-service .'
                        }
                    }
                }

                stage('Payment Service Image') {
                    steps {
                        dir('backend/payment-service') {
                            bat 'docker build -t payment-service .'
                        }
                    }
                }

                stage('Notification Service Image') {
                    steps {
                        dir('backend/notification-service') {
                            bat 'docker build -t notification-service .'
                        }
                    }
                }
            }
        }

        stage('Deploy Using Docker Compose') {
            steps {
                dir('backend') {
                    bat 'docker-compose down'
                    bat 'docker-compose up -d'
                }
            }
        }
    }

    post {
        success {
            echo '✅ Utility Billing System built and deployed successfully!'
        }
        failure {
            echo '❌ Pipeline failed. Check Jenkins logs for details.'
        }
    }
}
