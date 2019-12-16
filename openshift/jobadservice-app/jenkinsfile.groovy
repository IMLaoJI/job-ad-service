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
    }

    triggers {
        pollSCM('H/15 * * * *')
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
            }
        }

        stage('Exec Maven') {
            steps {
                rtMavenRun(
                    pom: 'web/pom.xml',
                    goals: 'clean install package -DskipTests -DskipITs=true',
                    deployerId: "MAVEN_DEPLOYER",
                    resolverId: "MAVEN_RESOLVER"
                )
            }
        }

        stage('SonarQube') {
            steps {
                rtMavenRun(
                    pom: 'web/pom.xml',
                    goals: 'sonar:sonar -Dsonar.projectKey=ReferenceService -Dsonar.host.url="http://test-1.apps.admin.arbeitslosenkasse.ch" -Dsonar.login=d457dcaba72c92ec41bdd2ae6f60ce9e1623d094',
                    deployerId: "MAVEN_DEPLOYER",
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
    }
}

