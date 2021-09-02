FROM adorsys/java:11
LABEL maintainer="https://github.com/adorsys/xs2a-sandbox"

ENV SERVER_PORT 8094
ENV JAVA_OPTS -Xmx1024m
ENV JAVA_TOOL_OPTIONS -Xmx1024m -agentlib:jdwp=transport=dt_socket,address=8000,server=y,suspend=n

WORKDIR /opt/admin-app

USER 0
RUN mkdir -p /opt/admin-app/logs/ && chmod 777 /opt/admin-app/logs/
USER 1001

COPY ./target/admin-rest-server*jar /opt/admin-app/admin-rest-server.jar

EXPOSE 8094 8000
# hadolint ignore=DL3025
CMD exec $JAVA_HOME/bin/java $JAVA_OPTS -jar /opt/admin-app/admin-rest-server.jar
