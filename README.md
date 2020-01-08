# XS2ASandbox
[![Build Status](https://travis-ci.com/adorsys/xs2a.svg?branch=develop)](https://travis-ci.com/adorsys/XS2A-Sandbox)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=adorsys_XS2A-Sandbox&metric=alert_status)](https://sonarcloud.io/dashboard?id=adorsys_XS2A-Sandbox)

Implementation of the dynamic sandbox based on the adorsys ledgers core banking system and XS2A services.

## Running the XS2ASandbox

For build and run XS2ASandbox requires GNU Make to be installed on your local machine. Please, make sure it is installed.

1.Download the project and go to the project directory:

```sh
> git clone https://github.com/adorsys/XS2A-Sandbox
> cd XS2A-Sandbox
```

2.This sandbox runs with the docker-compose that can be found at:  [docker-compose.yml](docker-compose.yml) and [Makefile](Makefile).
But before you run this, first of all you should check if all build dependencies are installed:

```sh
> make check
```

If something is missing, install it to your local machine, otherwise the build will fail. 
List of dependencies that are required to use XS2ASandbox: **Java 8**, **nodeJs**, **AngularCLI**, **asciidoctor**, **jq**, **docker**, **docker-compose**, **maven**, **plantuml**.
Here are links where you can install needed dependencies:

| Dependency         | Link                                    |                                                     
|--------------------|-----------------------------------------|
| Java 8             | https://openjdk.java.net/install/       | 
| Node.js 11.x        | https://nodejs.org/en/download         | 
| Angular CLI 7.x     | https://angular.io/guide/quickstart    |                                                                                                        
| Asciidoctor 2.0    | https://asciidoctor.org                 |
| jq 1.6             | https://stedolan.github.io/jq/download  |
| Docker    1.17     | https://www.docker.com/get-started      |
| Docker Compose 1.24| https://docs.docker.com/compose/install |
| Maven    3.5       | https://maven.apache.org/download.cgi   |
| PlantUML 1.2019.3  | http://plantuml.com/en/starting         |


**Note 1**: please, use **Node.js** version lower than **12** (e.g. **10.x.x** or **11.x.x**). Otherwise angular applications would not be built due to
version conflicts.

**Note 2**: check amount of memory given to **Docker** (Open Docker Desktop -> Preferences -> Advanced -> Memory).
For a fast and painless start of all the services it should be not less than 5 GB.

3.Build and run the project:

You can build and run XS2ASandbox in two ways - with a docker command or with Makefile commands.

If you want use a first way:

Build all the services with command:

```sh 
> make
```

After building services you can run XS2ASandbox with a simple docker command:

```sh 
> docker-compose up
```

In Makefile you can use one of three commands:
* Run services from Docker Hub without build:

```sh
> make run
```

* Build and run services:

```sh
> make all
```

* Run services without build:

```sh
> make start
```

Once you have built the project you can run it without build next time - command **docker-compose up** or **make start** from Makefile.

Remember that after you update the project you should rebuild it - command **make** or **make all** from Makefile.

4.Open [Developer Portal](http://localhost:4206) and follow the manual to start working with XS2ASandbox.

5.Stop running containers in terminal with key combination **Control + C**.

6.Afterwards you can remove all the Sandbox containers from Docker with the following command:

```sh 
> docker-compose rm -s -f -v
```

## Links to local Swagger Interfaces

Following urls will access the swagger interfaces:

### XS2A Interface

```
http://localhost:8089/swagger-ui.html
```

### ASPSP-profile

ASPSP-profile is a module where bank-specific settings are stored.

```
http://localhost:48080/swagger-ui.html
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
