FROM redhat-openjdk-18/openjdk18-openshift

ARG ARTIFACTORY_SERVER="https://alvch.jfrog.io/alvch"
ARG GROUP_ID="ch.admin.seco.jobs.services.jobadservice"
ARG ARTIFACT_ID="web"
# TODO resolve hardcoded version
ARG ARTIFACT_VERSION="3462.1-SNAPSHOT"

ENV JAVA_OPTS="-Xms1G -Xmx1G"

USER root


# TODO use satellite server
# yum-config-manager --add-repo repository_url
RUN yum install wget -y && yum clean all -y
RUN wget -O /usr/bin/jq https://github.com/stedolan/jq/releases/download/jq-1.5/jq-linux64 && chmod a+rx /usr/bin/jq

RUN mkdir -p /srv/jobroom/

RUN curl "$ARTIFACTORY_SERVER/api/search/gavc?g=$GROUP_ID&a=$ARTIFACT_ID&v=$ARTIFACT_VERSION&repos=libs-snapshots-local" -s > output.txt
RUN wget -O download.txt $(cat output.txt | /usr/bin/jq -r '.results[0].uri')
RUN wget -O /srv/jobroom/jobad-service.jar $(cat download.txt | /usr/bin/jq -r '.downloadUri')

WORKDIR /

ADD openshift/jobadservice-app/bootstrap.sh /
RUN chmod 755 /bootstrap.sh && chmod g=u /etc/passwd && chown -R 1001:0 /srv/jobroom

EXPOSE 8080

# OpenShift requires images to run as non-root by default
USER 1001
ENTRYPOINT ["/bootstrap.sh"]
