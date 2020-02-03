pipeline {
    agent { label 'maven' }

    options {
        buildDiscarder(logRotator(numToKeepStr: '5'))
        timeout(time: 10, unit: 'MINUTES')
    }

    environment {
        DEFAULT_JVM_OPTS = '-Dhttp.proxyHost=l98fppx1.admin.arbeitslosenkasse.ch -Dhttp.proxyPort=8080 -Dhttps.proxyHost=l98fppx1.admin.arbeitslosenkasse.ch -Dhttps.proxyPort=8080 -Dhttp.nonProxyHosts="*.admin.arbeitslosenkasse.ch|172.27.97.10|172.27.97.11|172.27.97.12|172.30.0.1"'
        JAVA_TOOL_OPTIONS = "$JAVA_TOOL_OPTIONS $DEFAULT_JVM_OPTS"
        SERVER_URL = "https://alvch.jfrog.io/alvch"
        CREDENTIALS = "artifactory-deploy"
        ARTIFACTORY_SERVER = "alv-ch"
        MAVEN_HOME = "/opt/rh/rh-maven35/root/usr/share/xmvn"
        SONAR_LOGIN = credentials('SONAR_TOKEN')
        SONAR_SERVER = "${env.SONAR_HOST_URL}"
        JR_DEV = "jobroom-dev"
        DOCKER_BUILD_NAME = "jobad-service-docker"
        DEPLOYMENT_CONFIG = "jobad-service"
    }

    stages {

        stage('Initialize') {
            steps {
                sh '''
                  echo "PATH = ${PATH}"
                  echo "MAVEN_HOME = ${MAVEN_HOME}"
              '''
            }
        }

        stage('Artifactory configuration') {
            steps {
                rtServer(
                    id: ARTIFACTORY_SERVER,
                    url: SERVER_URL,
                    credentialsId: CREDENTIALS
                )

                rtMavenDeployer(
                    id: "MAVEN_DEPLOYER",
                    serverId: ARTIFACTORY_SERVER,
                    releaseRepo: "libs-releases-local",
                    snapshotRepo: "libs-snapshots-local"
                )

                rtMavenResolver(
                    id: "MAVEN_RESOLVER",
                    serverId: ARTIFACTORY_SERVER,
                    releaseRepo: "libs-releases-ocp",
                    snapshotRepo: "libs-snapshots"
                )

                rtMavenResolver(
                    id: "MAVEN_RESOLVER",
                    serverId: ARTIFACTORY_SERVER,
                    releaseRepo: "plugin-releases-ocp",
                    snapshotRepo: "plugins-snapshots"
                )
            }
        }

        stage('Exec Maven') {
            steps {
                // TODO apply release number concept DF-2094
                rtMavenRun(
                    pom: 'pom.xml',
                    goals: 'initialize -DnewVersion=${BUILD_NUMBER}',
                    resolverId: "MAVEN_RESOLVER"
                )
                rtMavenRun(
                    pom: 'pom.xml',
                    goals: 'package -Popenshift -DskipTests -DskipITs=true',
                    deployerId: "MAVEN_DEPLOYER",
                    resolverId: "MAVEN_RESOLVER"
                )
            }
        }

        stage('SonarQube') {
            steps {
                rtMavenRun(
                    pom: 'pom.xml',
                    goals: 'sonar:sonar -Dsonar.projectKey=JobadService -Dsonar.host.url="$SONAR_SERVER" -Dsonar.login=$SONAR_LOGIN',
                    resolverId: "MAVEN_RESOLVER"
                )
            }
        }

        stage('Publish build info') {
            steps {
                rtPublishBuildInfo(
                    serverId: ARTIFACTORY_SERVER
                )
            }
        }

        stage('Docker Build in dev') {
            steps {
                sh '''
                    oc start-build -F $DOCKER_BUILD_NAME --from-dir . -n $JR_DEV
                '''
            }
        }

        stage('Deploy to jobroom-dev') {
            steps {
                openshiftDeploy(
                        namespace: '$JR_DEV',
                        depCfg: '$DEPLOYMENT_CONFIG',
                        waitTime: '300000'
                )

                openshiftVerifyDeployment(
                        namespace: '$JR_DEV',
                        depCfg: '$DEPLOYMENT_CONFIG',
                        replicaCount: '1',
                        verifyReplicaCount: 'true',
                        waitTime: '300000')
            }
        }
    }
}
