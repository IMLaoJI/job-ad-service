FROM redhat-openjdk-18/openjdk18-openshift

ENV SPRING_OUTPUT_ANSI_ENABLED=ALWAYS \
    SLEEP=0

ARG JAVA_OPTS="-Xms1G -Xmx1G"

USER root

RUN mkdir -p /srv/jobroom/

COPY web/target/jobad-service.jar /srv/jobroom/jobad-service.jar

WORKDIR /

ADD openshift/jobadservice-app/bootstrap.sh /
RUN chmod 755 /bootstrap.sh && chmod g=u /etc/passwd && chown -R 1001:0 /srv/jobroom

EXPOSE 8080

# OpenShift requires images to run as non-root by default
USER 1001
ENTRYPOINT ["/bootstrap.sh"]
