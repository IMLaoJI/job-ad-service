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
        ARTIFACT_VERSION = "${new Date().format("YYYY-MM-dd")}.${BUILD_NUMBER}"
    }

    stages {

        stage('Initialize') {
            steps {
                sh '''
                  printenv
                  echo "...Building on branch: ${GIT_BRANCH}"
                  echo "PATH = ${PATH}"
                  echo "MAVEN_HOME = ${MAVEN_HOME}"
                  echo ARTIFACT_VERSION = ${ARTIFACT_VERSION}
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
                rtMavenRun(
                        pom: 'pom.xml',
                        goals: 'initialize -DnewVersion=${ARTIFACT_VERSION}',
                        resolverId: "MAVEN_RESOLVER"
                )
                rtMavenRun(
                        pom: 'pom.xml',
                        goals: 'package -DskipTests -DskipITs=true',
                        deployerId: "MAVEN_DEPLOYER",
                        resolverId: "MAVEN_RESOLVER"
                )

                rtPublishBuildInfo(
                        serverId: ARTIFACTORY_SERVER
                )
            }
        }

        stage('Update templates') {
            steps {
                checkout changelog: false,
                        poll: false,
                        scm: [$class                           : 'GitSCM',
                              branches                         : [[name: '*/master']],
                              doGenerateSubmoduleConfigurations: false,
                              extensions                       : [[$class: 'RelativeTargetDirectory', relativeTargetDir: 'ocp-commons']],
                              submoduleCfg                     : [],
                              userRemoteConfigs                : [[credentialsId: 'cicd-alvchbot-at-github',
                                                                   url          : 'https://github.com/alv-ch/ocp-commons.git']]]
                script {
                    openshift.withCluster() {
                        openshift.withProject('jobroom-dev') {
                            def templateNames = [
                                    'microservice-app-build-config-docker-template',
                                    'batch-app-build-config-docker-template'
                            ]

                            templateNames.each {
                                openshift.apply(readFile("${WORKSPACE}/ocp-commons/templates/${it}.yml"))
                            }
                        }
                    }
                }
            }
        }

        stage('SonarQube') {
            // our SonarQube has only master
            when {
                branch 'master'
            }

            steps {
                rtMavenRun(
                        pom: 'pom.xml',
                        goals: 'sonar:sonar -Dsonar.projectKey=job-ad-service -Dsonar.host.url="$SONAR_SERVER" -Dsonar.login=$SONAR_LOGIN',
                        resolverId: "MAVEN_RESOLVER"
                )
            }
        }

//       TODO fix source problem
//        stage('Docker Builds') {
//            parallel {
//
//            }
//        }

        stage('Docker Build job-ad-service in jobroom-dev') {
            when {
                branch 'feature/openshift'
            }

            steps {
                script {
                    openshift.withCluster() {
                        openshift.withProject('jobroom-dev') {
                            def microserviceAppBuildConfigDockerTemplate = openshift.selector("template", "microservice-app-build-config-docker-template").object()
                            openshift.apply(openshift.process(microserviceAppBuildConfigDockerTemplate, [
                                    "-p", "MICROSERVICE_PROJECT_NAME=job-ad-service",
                                    "-p", "APPLICATION_NAME=app-job-ad-service",
                                    "-p", "NAMESPACE=jobroom-dev",
                            ]))

                            def build = openshift.selector('bc', 'app-job-ad-service-docker').startBuild("--from-dir .")
                            result = build.logs('-f')

                            if (result.status == 0) {
                                return true
                            }

                            return false
                        }
                    }
                }
            }
        }

        stage('Docker Build app-external-job-ad-export-task in jobroom-dev') {
            when {
                branch 'feature/openshift'
            }

            steps {
                script {
                    openshift.withCluster() {
                        openshift.withProject('jobroom-dev') {
                            def batchAppBuildConfigDockerTemplate = openshift.selector("template", "batch-app-build-config-docker-template").object()
                            openshift.apply(openshift.process(batchAppBuildConfigDockerTemplate, [
                                    "-p", "MICROSERVICE_PROJECT_NAME=job-ad-service",
                                    "-p", "APPLICATION_NAME=app-external-job-ad-export-task",
                                    "-p", "NAMESPACE=jobroom-dev",
                                    "-p", "IMAGE_LABEL=${ARTIFACT_VERSION}"
                            ]))

                            def build = openshift.selector('bc', 'app-external-job-ad-export-task-docker').startBuild("--from-dir .")
                            result = build.logs('-f')

                            if (result.status == 0) {
                                return true
                            }

                            return false
                        }
                    }
                }
            }
        }

        stage('Docker Build app-external-job-ad-import-task in jobroom-dev') {
            when {
                branch 'feature/openshift'
            }

            steps {
                script {
                    openshift.withCluster() {
                        openshift.withProject('jobroom-dev') {
                            def batchAppBuildConfigDockerTemplate = openshift.selector("template", "batch-app-build-config-docker-template").object()
                            openshift.apply(openshift.process(batchAppBuildConfigDockerTemplate, [
                                    "-p", "MICROSERVICE_PROJECT_NAME=job-ad-service",
                                    "-p", "APPLICATION_NAME=app-external-job-ad-import-task",
                                    "-p", "NAMESPACE=jobroom-dev",
                                    "-p", "IMAGE_LABEL=${ARTIFACT_VERSION}"
                            ]))

                            def build = openshift.selector('bc', 'app-external-job-ad-import-task-docker').startBuild("--from-dir .")
                            result = build.logs('-f')

                            if (result.status == 0) {
                                return true
                            }

                            return false
                        }
                    }
                }
            }
        }

        stage('Deploy to jobroom-dev') {

            when {
                branch 'feature/openshift'
            }

            steps {
                script {
                    openshift.withCluster() {
                        openshift.withProject('jobroom-dev') {
                            def appJobAdServiceDC = openshift.selector('dc', 'app-job-ad-service')
                            appJobAdServiceDC.rollout().latest()
                            appJobAdServiceDC.rollout().status()
                        }
                    }
                }
            }
        }
    }
}
