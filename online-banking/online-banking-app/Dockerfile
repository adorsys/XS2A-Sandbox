FROM adorsys/java:8

MAINTAINER https://github.com/adorsys/xs2a-connector-examples

ENV SERVER_PORT 8090
ENV JAVA_OPTS -Xmx1024m
ENV JAVA_TOOL_OPTIONS -Xmx1024m -agentlib:jdwp=transport=dt_socket,address=8000,server=y,suspend=n

WORKDIR /opt/xs2a-connector-examples

USER 0
RUN mkdir -p /opt/xs2a-connector-examples/logs/ && chmod 777 /opt/xs2a-connector-examples/logs/
USER 1001

COPY ./target/online-banking-app.jar /opt/xs2a-connector-examples/xs2a-online-banking.jar

EXPOSE 8090 8000

CMD exec $JAVA_HOME/bin/java $JAVA_OPTS -jar /opt/xs2a-connector-examples/xs2a-online-banking.jar
