// =============================================================================
// Enterprise DevSecOps Pipeline for SmartInventory
// =============================================================================
// Author: Principal DevSecOps Architect
// Purpose: Multi-environment build, test, secure, scan, package, sign, and 
//          deploy pipeline using state-of-the-art open-source DevSecOps tools.
// =============================================================================

pipeline {
    agent {
        label 'jenkins-agent' // Production-ready dynamic builder agent
    }
    
    options {
        timeout(time: 2, unit: 'HOURS') // Global pipeline timeout protect
        retry(1)                        // Auto-retry once on transient node failures
        buildDiscarder(logRotator(numToKeepStr: '30', artifactNumToKeepStr: '15'))
        disableConcurrentBuilds()       // Avoid race conditions in stateful deployments
        ansiColor('xterm')
    }
    
    parameters {
        choice(name: 'DEPLOY_ENV', choices: ['dev', 'qa', 'staging', 'prod'], description: 'Target deployment environment')
        booleanParam(name: 'RUN_DAST', defaultValue: false, description: 'Run OWASP ZAP DAST scan (Staging/Prod only)')
        booleanParam(name: 'PROMOTE_TO_PROD', defaultValue: false, description: 'Promote Staging build to Production')
        booleanParam(name: 'ROLLBACK_DEPLOYMENT', defaultValue: false, description: 'Rollback current deployment to previous stable version')
        string(name: 'ROLLBACK_VERSION', defaultValue: '', description: 'Specific application version/tag for rollback (mandatory if ROLLBACK_DEPLOYMENT is true)')
    }
    
    environment {
        // --- Registry & Images Configuration ---
        HARBOR_HOST       = "harbor.smartinventory.com"
        HARBOR_PROJECT    = "smartinventory"
        IMAGE_NAME        = "smartinventory"
        APP_VERSION       = "1.0.${env.BUILD_NUMBER}"
        FULL_IMAGE_TAG    = "${HARBOR_HOST}/${HARBOR_PROJECT}/${IMAGE_NAME}:${APP_VERSION}"
        
        // --- Security Tool Settings ---
        SEMGREP_RULES     = "security/semgrep/semgrep-rules.yml"
        TRIVY_CONFIG      = "security/trivy/trivy.yaml"
        CHECKOV_CONFIG    = "security/checkov/.checkov.yaml"
        KUBESCAPE_CONFIG  = "security/kubescape/kubescape-config.yaml"
        ZAP_CONFIG        = "security/zap/zap-automation.yaml"
        
        // --- Credentials Binding IDs ---
        DOCKER_CREDS      = "harbor-robot-creds"
        COSIGN_KEY_CREDS  = "cosign-private-key"
        SONAR_TOKEN_CREDS = "sonarqube-token"
        AWS_CREDS         = "aws-eks-deployment-creds"
        NVD_API_KEY_CREDS = "nvd-api-key"
        SLACK_WEBHOOK     = credentials('slack-webhook-url')
        
        // --- Slack Notification Channels ---
        SLACK_CHANNEL_ALERTS      = "#smartinventory-alerts"
        SLACK_CHANNEL_DEPLOYMENTS = "#smartinventory-deployments"
    }
    
    stages {
        // =====================================================================
        // Stage 1: Setup & Initialization
        // =====================================================================
        stage('Initialization') {
            steps {
                script {
                    echo "Initializing DevSecOps Pipeline for SmartInventory..."
                    echo "Target Environment: ${params.DEPLOY_ENV}"
                    echo "Build Tag: ${env.APP_VERSION}"
                    
                    if (params.ROLLBACK_DEPLOYMENT && !params.ROLLBACK_VERSION) {
                        error "Rollback version must be specified when ROLLBACK_DEPLOYMENT is enabled."
                    }
                }
            }
        }
        
        // =====================================================================
        // Stage 2: Checkout Source Code
        // =====================================================================
        stage('Checkout') {
            steps {
                checkout scm
                script {
                    env.GIT_COMMIT_HASH = runCmdWithOutput("git rev-parse --short HEAD")
                    env.GIT_COMMIT_MSG  = runCmdWithOutput("git log -1 --pretty=format:%s")
                    env.GIT_AUTHOR      = runCmdWithOutput("git log -1 --pretty=format:%an")
                }
                echo "Checked out commit ${env.GIT_COMMIT_HASH} by ${env.GIT_AUTHOR}"
            }
        }
        
        // =====================================================================
        // Stage 3: Static Secret Scanning (Gitleaks)
        // =====================================================================
        stage('Gitleaks Scan') {
            steps {
                echo "Running Gitleaks Secret Scanner..."
                script {
                    try {
                        runCmd("gitleaks detect --config security/gitleaks/.gitleaks.toml --source . --verbose --report-path target/gitleaks-report.json")
                    } catch (Exception e) {
                        // Fail pipeline if secrets are found
                        publishHTML([
                            allowMissing: true,
                            alwaysLinkToLastBuild: true,
                            keepAll: true,
                            reportDir: 'target',
                            reportFiles: 'gitleaks-report.json',
                            reportName: 'Gitleaks Secret Report'
                        ])
                        error "Gitleaks detected secrets/credentials in source code. Build aborted."
                    }
                }
            }
        }
        
        // =====================================================================
        // Stage 4: Static Application Security Testing (Semgrep SAST)
        // =====================================================================
        stage('Semgrep SAST') {
            steps {
                echo "Running Semgrep SAST scan on Java files..."
                script {
                    try {
                        runCmd("semgrep scan --config ${env.SEMGREP_RULES} --json -o target/semgrep-report.json --error")
                    } catch (Exception e) {
                        publishHTML([
                            allowMissing: true,
                            alwaysLinkToLastBuild: true,
                            keepAll: true,
                            reportDir: 'target',
                            reportFiles: 'semgrep-report.json',
                            reportName: 'Semgrep SAST Report'
                        ])
                        error "Semgrep detected critical vulnerability/quality issues. Build aborted."
                    }
                }
            }
        }
        
        // =====================================================================
        // Stage 5: Run Unit & Integration Tests
        // =====================================================================
        stage('Unit Tests') {
            steps {
                echo "Compiling and running unit tests..."
                runCmd "mvn clean test"
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }
        
        // =====================================================================
        // Stage 6: Code Quality Gates (SonarQube)
        // =====================================================================
        stage('SonarQube Code Analysis') {
            steps {
                echo "Initiating SonarQube Code Analysis..."
                withCredentials([string(credentialsId: env.SONAR_TOKEN_CREDS, variable: 'SONAR_TOKEN')]) {
                    withSonarQubeEnv('SonarQube') {
                        runCmd "mvn sonar:sonar -Dsonar.projectKey=smartinventory -Dsonar.projectName=SmartInventory -Dsonar.projectVersion=${env.APP_VERSION} -Dsonar.login=${SONAR_TOKEN}"
                    }
                }
                timeout(time: 15, unit: 'MINUTES') {
                    script {
                        def qg = waitForQualityGate()
                        if (qg.status != 'OK') {
                            error "Pipeline failed due to SonarQube Quality Gate Failure: ${qg.status}"
                        }
                    }
                }
            }
        }
        
        // =====================================================================
        // Stage 7: Software Composition Analysis (OWASP Dependency Check)
        // =====================================================================
        stage('OWASP Dependency Check') {
            steps {
                echo "Running OWASP Dependency Check (SCA)..."
                withCredentials([string(credentialsId: env.NVD_API_KEY_CREDS, variable: 'NVD_API_KEY')]) {
                    script {
                        try {
                            runCmd "mvn org.owasp:dependency-check-maven:check -DfailBuildOnCVSS=7.0 -DnvdApiKey=${NVD_API_KEY}"
                        } catch (Exception e) {
                            error "OWASP Dependency Check failed due to high/critical CVSS vulnerabilities (> 7.0)."
                        }
                    }
                }
            }
            post {
                always {
                    dependencyCheckPublisher pattern: 'target/dependency-check-report.xml'
                }
            }
        }
        
        // =====================================================================
        // Stage 8: Hardened Container Build
        // =====================================================================
        stage('Docker Build') {
            steps {
                echo "Building hardened Docker container..."
                runCmd "docker build -t ${env.FULL_IMAGE_TAG} -f Dockerfile ."
            }
        }
        
        // =====================================================================
        // Stage 9: Container Vulnerability Scan (Trivy)
        // =====================================================================
        stage('Trivy Image Scan') {
            steps {
                echo "Performing container security scan with Trivy..."
                script {
                    // 1. Run Trivy with exit code 0 to generate HTML/JSON reports
                    runCmd "trivy image --config ${env.TRIVY_CONFIG} --format json --output target/trivy-image-report.json ${env.FULL_IMAGE_TAG}"
                    
                    // 2. Fail the build if CRITICAL vulnerabilities exist
                    try {
                        runCmd "trivy image --exit-code 1 --severity CRITICAL --config ${env.TRIVY_CONFIG} ${env.FULL_IMAGE_TAG}"
                    } catch (Exception e) {
                        publishHTML([
                            allowMissing: true,
                            alwaysLinkToLastBuild: true,
                            keepAll: true,
                            reportDir: 'target',
                            reportFiles: 'trivy-image-report.json',
                            reportName: 'Trivy Security Report'
                        ])
                        error "Trivy found CRITICAL vulnerabilities in the container image. Deployment aborted."
                    }
                }
            }
        }
        
        // =====================================================================
        // Stage 10: SBOM Generation (Syft)
        // =====================================================================
        stage('SBOM Generation') {
            steps {
                echo "Generating Software Bill of Materials (SBOM) with Syft..."
                runCmd "syft ${env.FULL_IMAGE_TAG} -o cyclonedx-json --config security/syft/syft-config.yaml > target/sbom.cdx.json"
                archiveArtifacts artifacts: 'target/sbom.cdx.json', fingerprint: true
            }
        }
        
        // =====================================================================
        // Stage 11: Image Cryptographic Signing & Attestation (Cosign)
        // =====================================================================
        stage('Cosign Image Signing') {
            steps {
                echo "Signing container image with Cosign..."
                withCredentials([string(credentialsId: env.COSIGN_KEY_CREDS, variable: 'COSIGN_PRIVATE_KEY')]) {
                    script {
                        // Sign the image
                        runCmd "cosign sign --key env://COSIGN_PRIVATE_KEY --yes --annotations build.pipeline=jenkins --annotations build.number=${env.BUILD_NUMBER} ${env.FULL_IMAGE_TAG}"
                        
                        // Attest the SBOM to the image registry
                        runCmd "cosign attest --key env://COSIGN_PRIVATE_KEY --yes --predicate target/sbom.cdx.json --type cyclonedx ${env.FULL_IMAGE_TAG}"
                        
                        echo "Container image cryptographic signature and SBOM attestation attached successfully."
                    }
                }
            }
        }
        
        // =====================================================================
        // Stage 12: Push Signed Image to Harbor Secure Registry
        // =====================================================================
        stage('Push Image to Harbor') {
            steps {
                echo "Logging into Harbor registry..."
                withCredentials([usernamePassword(credentialsId: env.DOCKER_CREDS, usernameVariable: 'HARBOR_USER', passwordVariable: 'HARBOR_PASSWORD')]) {
                    runCmd "docker login -u ${HARBOR_USER} -p ${HARBOR_PASSWORD} ${env.HARBOR_HOST}"
                    echo "Pushing signed container image to Harbor..."
                    runCmd "docker push ${env.FULL_IMAGE_TAG}"
                }
            }
        }
        
        // =====================================================================
        // Stage 13: Infrastructure-as-Code Security & Policy Checks
        // =====================================================================
        stage('IaC Security (Checkov, kube-score, Kubescape)') {
            parallel {
                stage('Checkov IaC Scan') {
                    steps {
                        echo "Running Checkov on Terraform & Kubernetes manifests..."
                        script {
                            try {
                                runCmd "checkov --config-file ${env.CHECKOV_CONFIG} --output-file-path target/checkov-report.json"
                            } catch (Exception e) {
                                error "Checkov detected critical/high misconfigurations in IaC templates."
                            }
                        }
                    }
                }
                
                stage('kube-score Manifest Check') {
                    steps {
                        echo "Running kube-score static validation on manifests..."
                        script {
                            // Render the Helm chart templates to perform static analysis on final manifests
                            runCmd "helm template smartinventory helm/smartinventory --values helm/smartinventory/values-${params.DEPLOY_ENV}.yaml > target/rendered-manifests.yaml"
                            
                            try {
                                runCmd "kube-score score target/rendered-manifests.yaml --output-format json > target/kube-score-report.json"
                            } catch (Exception e) {
                                echo "kube-score detected warnings in rendered manifests. Reviewing required."
                            }
                        }
                    }
                }
                
                stage('Kubescape Scan') {
                    steps {
                        echo "Running Kubescape security posture check..."
                        script {
                            try {
                                runCmd "kubescape scan --config ${env.KUBESCAPE_CONFIG} --output target/kubescape-report.json"
                            } catch (Exception e) {
                                echo "Kubescape scan completed with warnings. Check results."
                            }
                        }
                    }
                }
            }
        }
        
        // =====================================================================
        // Stage 14: Helm Charts Packaging
        // =====================================================================
        stage('Helm Package') {
            steps {
                echo "Packaging Helm Chart..."
                runCmd "helm package helm/smartinventory --version ${env.APP_VERSION} --destination target/"
                archiveArtifacts artifacts: 'target/*.tgz', fingerprint: true
            }
        }
        
        // =====================================================================
        // Stage 15: GitOps Deploy via ArgoCD Synchronization
        // =====================================================================
        stage('GitOps Deployment') {
            steps {
                script {
                    if (params.ROLLBACK_DEPLOYMENT) {
                        echo "Initiating ROLLBACK deployment to version ${params.ROLLBACK_VERSION}..."
                        updateGitOpsImageTag(params.DEPLOY_ENV, params.ROLLBACK_VERSION)
                    } else {
                        echo "Updating deployment target tags to ${env.APP_VERSION} in GitOps repo..."
                        updateGitOpsImageTag(params.DEPLOY_ENV, env.APP_VERSION)
                    }
                    
                    echo "Triggering ArgoCD out-of-band sync for namespace smartinventory-${params.DEPLOY_ENV}..."
                    withCredentials([usernamePassword(credentialsId: env.AWS_CREDS, usernameVariable: 'AWS_ACCESS_KEY_ID', passwordVariable: 'AWS_SECRET_ACCESS_KEY')]) {
                        runCmd "aws eks update-kubeconfig --region us-east-2 --name smartinventory-cluster"
                        runCmd "argocd app sync smartinventory-${params.DEPLOY_ENV} --prune"
                        runCmd "argocd app wait smartinventory-${params.DEPLOY_ENV} --health --timeout 300"
                    }
                    echo "GitOps synchronization verified. App is fully deployed and healthy."
                }
            }
        }
        
        // =====================================================================
        // Stage 16: Dynamic Application Security Testing (OWASP ZAP DAST)
        // =====================================================================
        stage('OWASP ZAP DAST Scan') {
            when {
                expression { return (params.RUN_DAST && (params.DEPLOY_ENV == 'staging' || params.DEPLOY_ENV == 'prod')) }
            }
            steps {
                echo "Initiating dynamic runtime scan on deployed staging endpoint..."
                script {
                    try {
                        runCmd "zap.sh -cmd -autorun ${env.ZAP_CONFIG}"
                    } catch (Exception e) {
                        publishHTML([
                            allowMissing: true,
                            alwaysLinkToLastBuild: true,
                            keepAll: true,
                            reportDir: '/zap/reports',
                            reportFiles: 'zap-report.html',
                            reportName: 'OWASP ZAP DAST Report'
                        ])
                        error "OWASP ZAP dynamic scan failed. Critical runtime security gaps detected."
                    }
                }
            }
        }
        
        // =====================================================================
        // Stage 17: Promote Build to Production
        // =====================================================================
        stage('Promotion to Production') {
            when {
                expression { return (params.DEPLOY_ENV == 'staging' && params.PROMOTE_TO_PROD) }
            }
            steps {
                script {
                    checkpoint "staging-approved"
                    input message: "Promote build version ${env.APP_VERSION} to Production?", ok: "Approve Promotion"
                    
                    echo "Promoting build tag to Production via GitOps..."
                    updateGitOpsImageTag('prod', env.APP_VERSION)
                    
                    withCredentials([usernamePassword(credentialsId: env.AWS_CREDS, usernameVariable: 'AWS_ACCESS_KEY_ID', passwordVariable: 'AWS_SECRET_ACCESS_KEY')]) {
                        runCmd "aws eks update-kubeconfig --region us-east-2 --name smartinventory-cluster"
                        runCmd "argocd app sync smartinventory-prod --prune"
                        runCmd "argocd app wait smartinventory-prod --health"
                    }
                    
                    echo "Promotion complete. Production deployment is running and verified."
                }
            }
        }
    }
    
    post {
        always {
            script {
                echo "Running cleanup processes..."
                cleanWs()
            }
        }
        success {
            script {
                notifySlack("SUCCESSFUL", '#36a64f', env.SLACK_CHANNEL_DEPLOYMENTS)
                notifyEmail("SUCCESSFUL")
            }
        }
        failure {
            script {
                notifySlack("FAILED", '#danger', env.SLACK_CHANNEL_ALERTS)
                notifyEmail("FAILED")
            }
        }
    }
}

// =============================================================================
// Helper Shared Functions
// =============================================================================

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

def updateGitOpsImageTag(envName, tag) {
    // Standard GitOps tag commit workflow
    echo "Updating values-${envName}.yaml image.tag with ${tag} and updating k8s/deployment.yaml..."
    runCmd "git config --global user.email 'devsecops-pipeline@smartinventory.com'"
    runCmd "git config --global user.name 'DevSecOps Automation Bot'"
    runCmd "git checkout main"
    runCmd "git pull origin main"
    
    // Parse and replace image tag in values-<env>.yaml and k8s/deployment.yaml
    if (isUnix()) {
        sh "sed -i 's/tag: .*/tag: \"${tag}\"/g' helm/smartinventory/values-${envName}.yaml"
        sh "sed -i 's|image: .*smartinventory:.*|image: harbor.smartinventory.com/smartinventory/smartinventory:${tag}|g' k8s/deployment.yaml"
    } else {
        powershell "(Get-Content helm/smartinventory/values-${envName}.yaml) -replace 'tag: .*', 'tag: \"${tag}\"' | Set-Content helm/smartinventory/values-${envName}.yaml"
        powershell "(Get-Content k8s/deployment.yaml) -replace 'image: .*smartinventory:.*', 'image: harbor.smartinventory.com/smartinventory/smartinventory:${tag}' | Set-Content k8s/deployment.yaml"
    }
    
    runCmd "git add helm/smartinventory/values-${envName}.yaml k8s/deployment.yaml"
    runCmd "git commit -m 'chore(gitops): promote image version ${tag} to ${envName} environment [skip ci]'"
    
    // In production pipelines, authenticate via SSH key or token
    retry(3) {
        runCmd "git push origin main"
    }
}

def notifySlack(status, color, channel) {
    try {
        slackSend(
            color: color,
            channel: channel,
            message: "*SmartInventory Pipeline ${status}*\nJob: `${env.JOB_NAME}` [Build #${env.BUILD_NUMBER}]\nAuthor: ${env.GIT_AUTHOR}\nLast Commit: _${env.GIT_COMMIT_MSG}_\nConsole Log: ${env.BUILD_URL}console"
        )
    } catch (Exception e) {
        echo "Slack notification skipped or failed: ${e.getMessage()}"
    }
}

def notifyEmail(status) {
    try {
        mail(
            to: 'jakkalilokesh@gmail.com',
            subject: "[${status}] SmartInventory Delivery Pipeline #${env.BUILD_NUMBER}",
            body: """Hi Lokesh,

The SmartInventory CI/CD/DevSecOps pipeline run #${env.BUILD_NUMBER} has finished with status: ${status}.

BUILD PARAMETERS:
- Job Name: ${env.JOB_NAME}
- Target Environment: ${params.DEPLOY_ENV}
- Build Number: #${env.BUILD_NUMBER}
- Target Image Tag: ${env.FULL_IMAGE_TAG}
- Commit Author: ${env.GIT_AUTHOR}
- Commit Details: ${env.GIT_COMMIT_HASH} - ${env.GIT_COMMIT_MSG}
- Jenkins Console Link: ${env.BUILD_URL}console

Best Regards,
SmartInventory Automated DevSecOps Platform"""
        )
    } catch (Exception e) {
        echo "Email notification skipped or failed: ${e.getMessage()}"
    }
}
