curl 'http://spring-cloud-dataflow-development.apps.admin.arbeitslosenkasse.ch/apps/task/app-external-job-ad-export' \
 -d 'force=true&uri=docker://docker-registry.default.svc:5000/jobroom-dev/app-external-job-ad-export-task'

# Create the task associated to the application
curl 'http://spring-cloud-dataflow-development.apps.admin.arbeitslosenkasse.ch/tasks/definitions' \
 -d 'name=app-external-job-ad-export-task&definition=app-external-job-ad-export'
