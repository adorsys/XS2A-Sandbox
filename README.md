# XS2ASandbox

Implementation of the dynamic sandbox based on the adorsys ledgers core banking system and XS2A services.

## Running the XS2ASandbox

1.Download the project and go to the project directory:

```sh
$ git clone https://github.com/adorsys/XS2A-Sandbox
$ cd XS2A-Sandbox
```

2.This sandbox runs with the docker-compose that can be found at:  [docker-compose.yml](docker-compose.yml) and [Makefile](Makefile).
But before you run this, first of all you should check if all build dependencies are installed:

```sh
$ make check
```

If something is missing, install it to your local machine, otherwise the build will fail. 
List of dependencies that are required to use XS2ASandbox: **nodeJs**, **AngularCLI**, **asciidoctor**, **jq**, **docker**, **docker-compose**, **maven**, **plantuml**.
Here are links where you can install needed dependencies:

| Dependency         | Link                                    |                                                     
|--------------------|-----------------------------------------|
| nodeJs             | https://nodejs.org/en/download          | 
| AngularCLI         | https://angular.io/guide/quickstart     |                                                                                                        
| asciidoctor        | https://asciidoctor.org                 |
| jq                 | https://stedolan.github.io/jq/download  |
| docker             | https://www.docker.com/get-started      |
| docker-compose     | https://docs.docker.com/compose/install |
| maven              | https://maven.apache.org/download.cgi   |
| planuml            | http://plantuml.com/en/starting         |


**Note**: check amount of memory given to **Docker** (Open Docker -> Preferences -> Advanced -> Memory). For a fast and painless start of all the services it should be not less than 5 GB.

3.Build and run the project with Makefile:
  
```sh 
$ make run
```

4.Open [Developer Portal](http://localhost:4206) and follow the manual to start working with XS2ASandbox.
## Links to local Swagger Interfaces

Following urls will access the swagger interfaces:

### XS2A Interface

```
http://localhost:8089/swagger-ui.html
```

### Consent Management System

The consent management API is only designed to be used internally. It is not exposed to the external world.

```
http://localhost:38080/swagger-ui.html
```

### ASPSP-profile

ASPSP-profile is a module where bank-specific settings are stored.

```
http://localhost:48080/swagger-ui.html
```

### Online Banking backend

This is the rest api of the PSU interface for the redirect approach. 

```
http://localhost:8090/swagger-ui.html
```

### Ledgers

Ledgers is the core banking system used to service the XS2A interface. It actually implements a deposit account module. Visit the readme to see which tests users can be used to access the system.

```
http://localhost:8088/swagger-ui.html
```

### Certificate Generator

Certificate Generator is a module that can produce test TPP certificates.

```
http://localhost:8092/swagger-ui.html
```

## Links to local User Interfaces

### Developer portal UI

Developer portal is the main information resource on how to get started, how to test and work with XS2ASandbox.

```
http://localhost:4206
```
### Online banking UI

Online banking UI is an Angular application, developed to provide consents, payment confirmations and cancellation from PSU to ASPSP
 in case of redirect SCA approach.

```
http://localhost:4400
```

### TPP UI

TPP UI is an Angular application, which provides a user interface to TPP and allows to register, get test certificate and 
manage users and accounts.

```
http://localhost:4205
```
