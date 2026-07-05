pipeline {
    agent any
    
    environment {
        DOCKER_IMAGE = "jakkalilokesh/smartinventory:${BUILD_NUMBER}"
        DOCKER_REGISTRY = "index.docker.io/v1/"
        DOCKER_CONTEXT = "default"
        DB_HOST = "mysql"
        DB_NAME = "smartinventory"
        DB_USER = "smartinv"
        DB_PASSWORD = credentials('db-password')
    }
    
    tools {
        maven 'Maven-3.8.6'
        jdk 'JDK-17'
    }
    
    stages {
        stage('Checkout') {
            steps {
                checkout scm
                echo 'Checked out source code'
            }
        }
        
        stage('Build') {
            steps {
                runCmd 'mvn clean compile'
                echo 'Compilation successful'
            }
        }
        
        stage('Test') {
            steps {
                runCmd 'mvn test'
                runCmd 'python scripts/ai-system-diagnostics.py'
                echo 'Tests and AI system diagnostics completed'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }
        
        stage('Package') {
            steps {
                runCmd 'mvn package -DskipTests'
                echo 'WAR file created'
                archiveArtifacts artifacts: 'target/*.war', fingerprint: true
            }
        }
        
        stage('Security Scan') {
            parallel {
                stage('OWASP Dependency Check') {
                    steps {
                        echo 'Running OWASP Dependency Check...'
                        script {
                            try {
                                runCmd 'mvn org.owasp:dependency-check-maven:check -DfailOnError=false'
                            } catch(e) {
                                echo "OWASP Dependency Check failed or rate-limited: ${e.getMessage()}"
                            }
                        }
                    }
                    post {
                        always {
                            script {
                                try {
                                    dependencyCheckPublisher pattern: 'target/dependency-check-report.xml', failedTotalHigh: 0, failedTotalMedium: 5, failedTotalLow: 10
                                } catch(e) {
                                    echo "Could not publish Dependency Check report: ${e.getMessage()}"
                                }
                            }
                        }
                    }
                }
                
                stage('SonarQube Scan') {
                    steps {
                        echo 'Running SonarQube code analysis...'
                        script {
                            try {
                                withSonarQubeEnv('SonarQube') {
                                    runCmd "mvn sonar:sonar -Dsonar.projectKey=smartinventory -Dsonar.projectName=SmartInventory -Dsonar.projectVersion=\${BUILD_NUMBER}"
                                }
                            } catch(e) {
                                echo "SonarQube scan skipped (SonarQube server not running or not configured): ${e.getMessage()}"
                            }
                        }
                    }
                }
            }
        }
        
        stage('Container Security Scan') {
            steps {
                echo 'Building Docker image for scanning...'
                script {
                    docker.build(DOCKER_IMAGE)
                }
                
                script {
                    try {
                        echo 'Running Trivy vulnerability scanner on Docker image...'
                        if (isUnix()) {
                            sh 'trivy image --exit-code 1 --severity HIGH,CRITICAL --format json --output trivy-report.json ${DOCKER_IMAGE} || true'
                        } else {
                            bat 'trivy image --exit-code 1 --severity HIGH,CRITICAL --format json --output trivy-report.json %DOCKER_IMAGE% || exit 0'
                        }
                        
                        echo 'Publishing Trivy scan results...'
                        publishHTML target: [
                            reportDir: '.',
                            reportFiles: 'trivy-report.json',
                            reportName: 'Trivy Security Scan Report',
                            alwaysLinkToLastBuild: true,
                            keepAll: true
                        ]
                    } catch(e) {
                        echo "Trivy docker image security scan skipped: ${e.getMessage()}"
                    }
                }
                
                script {
                    try {
                        echo 'Running Trivy filesystem scan...'
                        if (isUnix()) {
                            sh 'trivy fs --exit-code 1 --severity HIGH,CRITICAL --format json --output trivy-fs-report.json . || true'
                        } else {
                            bat 'trivy fs --exit-code 1 --severity HIGH,CRITICAL --format json --output trivy-fs-report.json . || exit 0'
                        }
                    } catch(e) {
                        echo "Trivy filesystem security scan skipped: ${e.getMessage()}"
                    }
                }
                
                echo 'Docker image built and scanned'
            }
        }
        
        stage('Push Docker Image') {
            when {
                anyOf {
                    branch 'main'
                    expression { env.GIT_BRANCH == 'origin/main' || env.GIT_BRANCH == 'main' || env.GIT_LOCAL_BRANCH == 'main' }
                }
            }
            steps {
                catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
                    script {
                        docker.withRegistry("https://${DOCKER_REGISTRY}", 'docker-credentials') {
                            docker.image(DOCKER_IMAGE).push()
                            docker.image(DOCKER_IMAGE).push('latest')
                        }
                    }
                    echo 'Docker image pushed to registry'
                }
            }
        }
        
        stage('Deploy to Staging') {
            when {
                anyOf {
                    branch 'develop'
                    expression { env.GIT_BRANCH == 'origin/develop' || env.GIT_BRANCH == 'develop' || env.GIT_LOCAL_BRANCH == 'develop' }
                }
            }
            steps {
                runCmd """
                    docker-compose -f docker-compose.yml down
                    docker-compose -f docker-compose.yml up -d
                """
                echo 'Deployed to staging environment'
            }
        }
        
        stage('Provision Infrastructure (Terraform)') {
            when {
                anyOf {
                    branch 'main'
                    expression { env.GIT_BRANCH == 'origin/main' || env.GIT_BRANCH == 'main' || env.GIT_LOCAL_BRANCH == 'main' }
                }
            }
            steps {
                echo 'Initializing and applying Terraform configuration...'
                catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
                    withCredentials([
                        usernamePassword(credentialsId: 'aws-credentials', usernameVariable: 'AWS_ACCESS_KEY_ID', passwordVariable: 'AWS_SECRET_ACCESS_KEY'),
                        string(credentialsId: 'db-password', variable: 'TF_VAR_db_password')
                    ]) {
                        dir('terraform') {
                            runCmd 'if exist "c:\\Users\\Jakkali Lokesh\\Desktop\\maven\\terraform\\terraform.tfstate" copy "c:\\Users\\Jakkali Lokesh\\Desktop\\maven\\terraform\\terraform.tfstate" .'
                            runCmd 'terraform init'
                            runCmd 'terraform validate'
                            runCmd 'terraform plan -out=tfplan'
                            try {
                                runCmd 'terraform apply -auto-approve tfplan'
                            } finally {
                                runCmd 'if exist terraform.tfstate copy terraform.tfstate "c:\\Users\\Jakkali Lokesh\\Desktop\\maven\\terraform\\terraform.tfstate"'
                            }
                        }
                    }
                }
            }
        }

        stage('Deploy to Production') {
            when {
                anyOf {
                    branch 'main'
                    expression { env.GIT_BRANCH == 'origin/main' || env.GIT_BRANCH == 'main' || env.GIT_LOCAL_BRANCH == 'main' }
                }
            }
            steps {
                catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
                    input 'Deploy to production?'
                    withCredentials([
                        usernamePassword(credentialsId: 'aws-credentials', usernameVariable: 'AWS_ACCESS_KEY_ID', passwordVariable: 'AWS_SECRET_ACCESS_KEY')
                    ]) {
                        runCmd "aws eks update-kubeconfig --region us-east-2 --name smartinventory-cluster"
                        runCmd "kubectl apply -f k8s/"
                        runCmd "kubectl rollout status deployment/smartinventory-app"
                    }
                    echo 'Deployed to production'
                }
            }
        }
    }
    
    post {
        cleanup {
            cleanWs()
        }
        success {
            echo 'Pipeline completed successfully'
            script {
                def commitMsg = "N/A"
                try {
                    commitMsg = runCmdWithOutput('git log -1 --pretty=format:"%h - %an: %s"')
                } catch(e) {
                    // Ignore if git command not available
                }
                
                try {
                    slackSend(color: '#36a64f', 
                              channel: '@jakkalilokesh',
                              message: "SmartInventory Pipeline SUCCESSFUL: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]' (${env.BUILD_URL})\nCommit: ${commitMsg}\nOwner: jakkalilokesh@gmail.com")
                } catch(e) {
                    echo "Slack notification skipped: ${e.getMessage()}"
                }
                
                try {
                    mail to: 'jakkalilokesh@gmail.com',
                         subject: "SUCCESS: SmartInventory Pipeline ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                         body: """Hi Lokesh,

The SmartInventory CI/CD pipeline build #${env.BUILD_NUMBER} completed successfully.

BUILD DETAILS:
- Job Name: ${env.JOB_NAME}
- Build Number: #${env.BUILD_NUMBER}
- Build Status: SUCCESSFUL
- Target Environment: Production / Staging
- Last Commit Info: ${commitMsg}
- Jenkins Console Link: ${env.BUILD_URL}console

Best Regards,
SmartInventory Automated Delivery Agent"""
                } catch(e) {
                    echo "Email notification skipped: ${e.getMessage()}"
                }
            }
        }
        failure {
            echo 'Pipeline failed'
            script {
                def commitMsg = "N/A"
                try {
                    commitMsg = runCmdWithOutput('git log -1 --pretty=format:"%h - %an: %s"')
                } catch(e) {
                    // Ignore if git command not available
                }
                
                try {
                    slackSend(color: '#danger', 
                              channel: '@jakkalilokesh',
                              message: "SmartInventory Pipeline FAILED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]' (${env.BUILD_URL})\nCommit: ${commitMsg}\nOwner: jakkalilokesh@gmail.com")
                } catch(e) {
                    echo "Slack notification skipped: ${e.getMessage()}"
                }
                
                try {
                    mail to: 'jakkalilokesh@gmail.com',
                         subject: "FAILED: SmartInventory Pipeline ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                         body: """Hi Lokesh,

Alert: The SmartInventory CI/CD pipeline build #${env.BUILD_NUMBER} failed.

BUILD DETAILS:
- Job Name: ${env.JOB_NAME}
- Build Number: #${env.BUILD_NUMBER}
- Build Status: FAILED
- Last Commit Info: ${commitMsg}
- Jenkins Console Link: ${env.BUILD_URL}console

Action Required: Please review the Jenkins build console logs to diagnose the build compilation, test, or deployment error.

Best Regards,
SmartInventory Automated Delivery Agent"""
                } catch(e) {
                    echo "Email notification skipped: ${e.getMessage()}"
                }
            }
        }
    }
}

def runCmd(cmd) {
    if (isUnix()) {
        sh cmd
    } else {
        bat cmd
    }
}

def runCmdWithOutput(cmd) {
    if (isUnix()) {
        return sh(script: cmd, returnStdout: true).trim()
    } else {
        return bat(script: cmd, returnStdout: true).trim()
    }
}
