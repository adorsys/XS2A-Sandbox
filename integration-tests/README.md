# Integration Test Suite for the XS2A Connector for the Ledgers Project

This integration test suite describe how to run default XS2A Cases for Embedded and Redirect approaches.

## Running Integration Tests

By default integration tests will run only while releasing the module. Use the property -DskipITs=false to activate integration tests.s  

```
> mvn clean install -DskipITs=false

```

## Starting all Servers for Development Purpose

You can use this application to start server for development purpose. Use the property -DwaitAtGateway=true option to put the maven process on hold. 

```
> mvn clean install -DskipITs=false -DwaitAtGateway=true

```
This will start all dependent microservices in the order displayed in the pom. You will later need to hit the enter command to proceed with the build process.

if you want to develop and debug the connector (Gateway) in your IDE, then stop the process after starting the consent management system by using the option -DwaitAtCMS=true. Then you can start and debug the connector in your IDE.

```
> mvn clean install -DskipITs=false -DwaitAtCMS=true

```
You can check the pom to see how to hold the process after starting each microservice. 

For example, use the following command to sikp online banking and wait after start of the SMTP server.

```
>  mvn clean install -DskipITs=false -DskipOBA=true -DwaitAtSMTP=true

```

You can skip any microservice using the property defined in the pom.

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

## Visiting Test Cases
 
To see provided test cases, visit the following interfaces:

### Embedded Test Cases

* Test Cases: [src/test/java/de/adorsys/ledgers/xs2a/test/ctk/embedded](src/test/java/de/adorsys/ledgers/xs2a/test/ctk/embedded)
* Associated Test Data: [src/test/resources/de/adorsys/ledgers/xs2a/test/ctk/embedded](src/test/resources/de/adorsys/ledgers/xs2a/test/ctk/embedded)

### Redirect Test Cases

* Test Cases: [src/test/java/de/adorsys/ledgers/xs2a/test/ctk/redirect](src/test/java/de/adorsys/ledgers/xs2a/test/ctk/redirect)
* Associated Test Data: [src/test/resources/de/adorsys/ledgers/xs2a/test/ctk/redirect](src/test/resources/de/adorsys/ledgers/xs2a/test/ctk/redirect)

### Preparing the Ledger for Testing

The ledger needs to be filled up with data so we can run tests using those data:

[Yaml Files used to prefill the ledger database](https://github.com/adorsys/ledgers/tree/develop/ledgers-app/src/main/resources/de/adorsys/ledgers/app/mock) containing following files:

  -- [Sample Core Banking Configuration](https://github.com/adorsys/ledgers/blob/develop/ledgers-app/src/main/resources/de/adorsys/ledgers/app/mock/aspsps-config.yml)
  -- [Sample Chart of Account](https://github.com/adorsys/ledgers/blob/develop/ledgers-app/src/main/resources/de/adorsys/ledgers/app/mock/sample_coa_banking.yml)
  -- [Sample Users, Accounts and Payments](https://github.com/adorsys/ledgers/blob/develop/ledgers-app/src/main/resources/de/adorsys/ledgers/app/mock/mockbank-simple-init-data.yml)

Remember that you will have to start the ledgers module with the option '--ledgers.mockbank.data.load=true' to load those test data.
 




