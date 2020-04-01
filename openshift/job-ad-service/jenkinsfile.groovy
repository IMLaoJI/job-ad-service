@Library('alv-ch-shared-library') _

node('maven') {

    withEnv(initEnv()) {

        checkoutScm()

        mavenBuild()

        sonarQube([projectKey: 'job-ad-service'])

        updateOpenshiftTemplates()

        parallel([
                appJobAdService                 : {
                    microserviceDockerBuild([
                            project            : 'job-ad-service',
                            application        : 'app-job-ad-service',
                            buildSourceOverride: [
                                    "${WORKSPACE}/web/target/app-job-ad-service.jar"
                            ]])
                },
                appExternalJobAdExportImportTask: {
                    batchApplicationDockerBuild([project: 'job-ad-service', application: 'app-external-job-ad-export-task'])
                    batchApplicationDockerBuild([project: 'job-ad-service', application: 'app-external-job-ad-import-task'])
                }
        ])

        scdfDeployment([
                application  : 'app-external-job-ad-export-task',
                version      : ARTIFACT_VERSION,
                cron         : '40 18 * * *',
                taskArguments: ['--spring.cloud.config.name': 'app-prisme-job-ad-export']
        ])

        scdfDeployment([
                application  : 'app-external-job-ad-import-task',
                version      : ARTIFACT_VERSION,
                cron         : '0 2 * * *',
                taskArguments: ['--spring.cloud.config.name': 'app-prisme-job-ad-import']
        ])

        //The application value is referring the application in the deployment config
        openshiftDeployment([application: 'app-job-ad-service'])

    }
}
