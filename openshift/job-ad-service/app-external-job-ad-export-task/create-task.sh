#!/bin/bash

SCDF_URL=http://spring-cloud-dataflow-development.apps.admin.arbeitslosenkasse.ch
VERSION_LABEL=2020-03.05.45

# Create the SCDF application first, make sure you specify a version, otherwise the :latest will be used
curl "${SCDF_URL}/apps/task/app-external-job-ad-export" \
 -d "force=true&uri=docker://docker-registry.default.svc:5000/jobroom-dev/app-external-job-ad-export-task:${VERSION_LABEL}"

# Create the task associated to the created application
curl "${SCDF_URL}/tasks/definitions" \
 -d 'name=app-external-job-ad-export-task&definition=app-external-job-ad-export'
 
 # Schedule task to run every day at 18:40
 # Be aware that scheduler's name will be concatendated, i.e. resulting to 'schedule-scdf-app-external-job-ad-export-task'
 # IF THE SCHEDULER'S NAME IS LONGER THAN 52 CHARACTERS, YOU WILL NOT BE ABLE TO CREATE THE SCHEDULE AT ALL BECAUSE OF
 # 2020-03-03 13:18:41.456  WARN 1 --- [p-nio-80-exec-1] o.s.c.d.s.c.RestControllerAdvice         : Caught exception while handling a request: Failed to create schedule Failed to create schedule job-ad-export-schedule-scdf-app-external-job-ad-export-task
curl "${SCDF_URL}/tasks/schedules" \
	-d 'scheduleName=schedule&taskDefinitionName=app-external-job-ad-export-task&properties=scheduler.cron.expression%3D40+18+%3F+%2A+%2A'
