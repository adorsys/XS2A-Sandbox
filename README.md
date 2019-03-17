# PSD2 Dynamic Sandbox

Sample implementation of the dyn amic sandbox based on the adorsys ledgers core banking system.

## Running the Sandbox

This sandbox runs with the docker-compose found at:  [integration-tests/docker-compose.yml](integration-tests/docker-compose.yml). 
But befor you run this, first proceed with: 

```
> mvn clean install
> cd instegration-tests
> docker-compose build
> docker-compose up

```

## Developing the online banking module

THe online banking module provides a spring boot application class that can be used to start the module in your IDE, so you can even run it in debug mode. In order to do so, use the trimmed docker compose file at [integration-tests/docker-compose-no-oba.yml](integration-tests/docker-compose-no-oba.yml)  

```
> mvn clean install
> cd instegration-tests
> docker-compose -f integration-tests/docker-compose-no-oba.yml build
> docker-compose -f integration-tests/docker-compose-no-oba.yml up

```
Then separately start the online banking application in you ide using following properties. Most of them are default. So the minimum configuration required is:

on the console
```
> cd instegration-tests
> java -jar target/online-banking/online-banking-app.jar --online-banking.sca.uiRedirect=true --online-banking.sca.loginpage=http://localhost:4400/

```
Or from the ide running the spring application class: de.adorsys.ledgers.oba.rest.server.LedgersXs2aObaApplication

An extensive configuration will contains.

```
java -jar target/online-banking/online-banking-app.jar \
    --spring.profiles.active=mockspi \
    --ledgers.url=http://localhost:8088 \
    --xs2a.url=http://localhost:8089 \
    --cms.url=http://localhost:38080    
	--online-banking.sca.uiRedirect=true \
    --online-banking.sca.loginpage=http://localhost:4400/ \
    --online-banking.sca.uiRedirect=true
    --feign.client.config.default.loggerLevel=NONE \
    --log4j.logger.org.springframework=WARN \
    --logging.level.root=WARN \
	--logging.level.org.springframework.boot=WARN 

```

After starting the sandbox, you can run the following class to initialize a consent and display the login screen:

[de.adorsys.ledgers.xs2a.test.ctk.redirect.ConsentRedirectOneScaApp](integration-tests/src/test/java/de/adorsys/ledgers/xs2a/test/ctk/redirect/ConsentRedirectOneScaApp.java)

You can also run the test from an ide using:

```
> cd instegration-tests
> mvn test -Dtest=de.adorsys.ledgers.xs2a.test.ctk.redirect.ConsentRedirectOneScaApp

```
This test assumes chrome is installed on you computer.

## Accessing Swagger Interfaces

Following urls will access the swagger interfaces:

### For XS2A

```
> curl http://localhost:8089/swagger-ui.html

```

### For Consent Management Rest API

The consent management API is only designed to be used internally. It is not exposed to the external world.

```
> curl http://localhost:38080/swagger-ui.html

```

### For Online Banking Interface

This is the rest api of the PSU interface for the redirect approach. In a normal case this interface will be services by a web front end.

```
> http://localhost:8090/swagger-ui.html

```

### For Ledgers

Ledgers is the core banking system used to service the XS2A interface. It actually implements a deposit account module. Visit the readme to see which tests users can be used to access the system.

```
> http://localhost:8088/swagger-ui.html

```



