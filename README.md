# job-ad-service

[![Build Status](https://travis-ci.org/alv-ch/job-ad-service.svg?branch=master)](https://travis-ci.org/alv-ch/job-ad-service)

This application was generated using JHipster 4.14.0, you can find documentation and help at [http://www.jhipster.tech/documentation-archive/v4.14.0](http://www.jhipster.tech/documentation-archive/v4.14.0).

This is a "microservice" application intended to be part of a microservice architecture, please refer to the [Doing microservices with JHipster][] page of the documentation for more information.

This application is configured for Service Discovery and Configuration with the JHipster-Registry. On launch, it will refuse to start if it is not able to connect to the JHipster-Registry at [http://localhost:8761](http://localhost:8761). For more information, read our documentation on [Service Discovery and Configuration with the JHipster-Registry][].

## Populate a database with JobAdvertisement data

To populate a local database with a job advertisement data, run the undermentioned *curl* script against REST APIs, under the ```/api``` URL with a corresponding payload.
e.g.
```
curl -H 'Content-Type: application/json' -H 'Accept: application/json' -H 'Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJhdXRoIjoiUk9MRV9VU0VSLFJPTEVfU1lTQURNSU4sUk9MRV9BRE1JTiIsInVzZXJJZCI6IjQ5NDZkMjljLWE1NDMtMTFlOC1iZjdhLTAwMTU1ZGU1MDEwOSIsImZpcnN0TmFtZSI6IkFkbWluaXN0cmF0b3IiLCJsYXN0TmFtZSI6IkFkbWluaXN0cmF0b3IiLCJlbWFpbCI6ImFkbWluQGxvY2FsaG9zdCIsImxhbmdLZXkiOiJkZSIsImV4dGVybmFsSWQiOiJhZG1pbiIsInN1YiI6ImFkbWluIiwiZXhwIjozMjYyODU3OTY1fQ.CCd97AlSNNIzYIAvWqbrRVxQfQX4IkFYp6sjPdisKiabaePwdhd50KE-oauKSbHOz_RJOR0Vh2-xWps59OPw4w' -d @src/curl/create_job_advertisement-payload.json http://localhost:8086/api/jobAdvertisements
```
The payload scripts can be found under ```/src/test-data```. Be aware of already existing job advertisement data and if there is such a need remove the old one.



[JHipster Homepage and latest documentation]: http://www.jhipster.tech
[JHipster 4.14.0 archive]: http://www.jhipster.tech/documentation-archive/v4.14.0
[Doing microservices with JHipster]: http://www.jhipster.tech/documentation-archive/v4.14.0/microservices-architecture/
[Using JHipster in development]: http://www.jhipster.tech/documentation-archive/v4.14.0/development/
[Service Discovery and Configuration with the JHipster-Registry]: http://www.jhipster.tech/documentation-archive/v4.14.0/microservices-architecture/#jhipster-registry
[Using Docker and Docker-Compose]: http://www.jhipster.tech/documentation-archive/v4.14.0/docker-compose
[Using JHipster in production]: http://www.jhipster.tech/documentation-archive/v4.14.0/production/
[Running tests page]: http://www.jhipster.tech/documentation-archive/v4.14.0/running-tests/
[Setting up Continuous Integration]: http://www.jhipster.tech/documentation-archive/v4.14.0/setting-up-ci/


[Gatling]: http://gatling.io/

## Spring Cloud Data Flow
In OpenShift there's installed an instance of Spring Cloud Data Flow. This has a feature that let's you create applications, tasks, jobs, etc. I had an issue when I've created an application for the Job Ad Exporter task: I've created an application using the SCDF's dashboard and afterwards tried to create the task for it. Unfortunatelly this didn't work: "application not found" was the error message.
The trick is to use SCDF API to create your application, afterwards you can create the associated task for it.
Here is an example:
```
 curl 'http://spring-cloud-dataflow-development.apps.admin.arbeitslosenkasse.ch/apps/task/app-external-job-ad-export' \
 -d 'force=true&uri=docker://docker-registry.default.svc:5000/jobroom-dev/app-external-job-ad-export-task'
 ```
 ### Error Messages
Sometimes SCDF shows error messages that are not easy to read. Basicall, it's very difficult to find out the roor cause of the problem. Here is an example, when I've tried to create a schedule for a task:
```
2020-03-03 13:18:41.456  WARN 1 --- [p-nio-80-exec-1] o.s.c.d.s.c.RestControllerAdvice         : Caught exception while handling a request: Failed to create schedule Failed to create schedule job-ad-export-schedule-scdf-app-external-job-ad-export-task
```
There's no way to tell from this message why the operation failed. So you need to increase the log verbosity. Luckily enough, there's a SCDF API for loggers, that uses Spring Actuator, so you can see which loggers are available and play with them a little.
In my case, I've just called
```
curl 'http://spring-cloud-dataflow-development.apps.admin.arbeitslosenkasse.ch/management/loggers'
```
You will see a huge list that contains the loggers, but that won't help you much. You have figure out where the error is generated. In my case was ``RestControllerAdvice`` as you can easily imagine. So, I've changed the log level to ``TRACE``:
```
curl 'http://spring-cloud-dataflow-development.apps.admin.arbeitslosenkasse.ch/management/loggers/org.springframework.cloud.dataflow.server.controller' \
   -d '{"configuredLevel": "TRACE"}' \
   -H 'Content-Type: application/json'
```
You can verify that the log level is the one you wanted:
```
curl 'http://spring-cloud-dataflow-development.apps.admin.arbeitslosenkasse.ch/management/loggers/org.springframework.cloud.dataflow.server.controller'
```
The result was: ```{"configuredLevel":"TRACE","effectiveLevel":"TRACE"}```
Urah..... Now, the error message looks different:
````
Caused by: io.fabric8.kubernetes.client.KubernetesClientException: Failure executing: POST at: https://172.30.0.1/apis/batch/v1beta1/namespaces/jobroom-dev/cronjobs . Message: CronJob.batch "job-ad-export-schedule-scdf-app-external-job-ad-export-task" is invalid: metadata.name: Invalid value: "job-ad-export-schedule-scdf-app-external-job-ad-export-task": must be no more than 52 characters. Received status: Status(apiVersion=v1, code=422, details=StatusDetails(causes=[StatusCause(field=metadata.name, message=Invalid value: "job-ad-export-schedule-scdf-app-external-job-ad-export-task": must be no more than 52 characters, reason=FieldValueInvalid, additionalProperties={})], group=batch, kind=CronJob, name=job-ad-export-schedule-scdf-app-external-job-ad-export-task, retryAfterSeconds=null, uid=null, additionalProperties={}), kind=Status, message=CronJob.batch "job-ad-export-schedule-scdf-app-external-job-ad-export-task" is invalid: metadata.name: Invalid value: "job-ad-export-schedule-scdf-app-external-job-ad-export-task": must be no more than 52 characters, metadata=ListMeta(_continue=null, resourceVersion=null, selfLink=null, additionalProperties={}), reason=Invalid, status=Failure, additionalProperties={}).
	at io.fabric8.kubernetes.client.dsl.base.OperationSupport.requestFailure(OperationSupport.java:476)
	at io.fabric8.kubernetes.client.dsl.base.OperationSupport.assertResponseCode(OperationSupport.java:415)
	at io.fabric8.kubernetes.client.dsl.base.OperationSupport.handleResponse(OperationSupport.java:381)
	at io.fabric8.kubernetes.client.dsl.base.OperationSupport.handleResponse(OperationSupport.java:344)
	at io.fabric8.kubernetes.client.dsl.base.OperationSupport.handleCreate(OperationSupport.java:227)
	at io.fabric8.kubernetes.client.dsl.base.BaseOperation.handleCreate(BaseOperation.java:780)
	at io.fabric8.kubernetes.client.dsl.base.BaseOperation.create(BaseOperation.java:349)
	at org.springframework.cloud.deployer.spi.scheduler.kubernetes.KubernetesScheduler.createCronJob(KubernetesScheduler.java:163)
	at org.springframework.cloud.deployer.spi.scheduler.kubernetes.KubernetesScheduler.schedule(KubernetesScheduler.java:76)
	... 60 common frames omitted
````
Now you can easily identify where the problem comes from.
