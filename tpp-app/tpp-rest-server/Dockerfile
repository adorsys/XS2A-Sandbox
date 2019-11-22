FROM adorsys/java:8
LABEL maintainer="https://github.com/adorsys/xs2a-sandbox"

ENV SERVER_PORT 8093
ENV JAVA_OPTS -Xmx1024m
ENV JAVA_TOOL_OPTIONS -Xmx1024m -agentlib:jdwp=transport=dt_socket,address=8000,server=y,suspend=n

WORKDIR /opt/tpp-app

USER 0
RUN mkdir -p /opt/tpp-app/logs/ && chmod 777 /opt/tpp-app/logs/
USER 1001

COPY ./target/tpp-rest-server*jar /opt/tpp-app/tpp-rest-server.jar

EXPOSE 8093 8000

CMD exec $JAVA_HOME/bin/java $JAVA_OPTS -jar /opt/tpp-app/tpp-rest-server.jar
