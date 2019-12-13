#!/bin/bash

set -m

if ! whoami &> /dev/null; then
  if [ -w /etc/passwd ]; then
    echo "${USER_NAME:-dummy}:x:$(id -u):0:${USER_NAME:-openshift} user:${HOME}:/usr/sbin/nologin" >> /etc/passwd
  fi
fi

java $JAVA_OPTS -server -jar /srv/jobroom/jobad-service.jar \
  --spring.profiles.active=development,swagger \
  --spring.cloud.config.uri="http://admin:admin@registry-development.apps.admin.arbeitslosenkasse.ch/config" \
  --jhipster.registry.password=admin \
  --spring.cloud.config.label=openshift \
  --server.port=8080
