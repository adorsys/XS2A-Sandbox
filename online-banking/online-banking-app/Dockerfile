FROM adorsys/openjdk-jre-base:8-minideb

MAINTAINER https://github.com/adorsys/xs2a-connector-examples

ENV SERVER_PORT 8090
ENV JAVA_OPTS -Xmx1024m
ENV JAVA_TOOL_OPTIONS -Xmx1024m -agentlib:jdwp=transport=dt_socket,address=8000,server=y,suspend=n

WORKDIR /opt/xs2a-connector-examples

COPY ./target/online-banking-app.jar /opt/xs2a-connector-examples/xs2a-online-banking.jar

EXPOSE 8090 8000

CMD exec $JAVA_HOME/bin/java $JAVA_OPTS -jar /opt/xs2a-connector-examples/xs2a-online-banking.jar
