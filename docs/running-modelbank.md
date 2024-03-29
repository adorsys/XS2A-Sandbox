## Running the ModelBank

For build and run ModelBank requires GNU Make to be installed on your local machine. Please, make sure it is installed.

1.Download the project and go to the project directory:

```sh
> git clone https://github.com/adorsys/XS2A-Sandbox.git
> cd XS2A-Sandbox
```

2.This ModelBank runs with the docker-compose that can be found at: [docker-compose.yml](../docker-compose.yml) and [Makefile](../Makefile).
But before you run this, first of all you should check if all build dependencies are installed:

```sh
> make check
```

As our Modelbank features Keycloak as IDP in docker containers in its proprietary sub-network it is required to update your `hosts` file with keycloak container mapping in order to run it properly.
The typical localtion of hosts file for:

```sh
MacOS & Linux: etc/hosts
Windows: C:\Windows\System32\drivers\etc\hosts
```

Line to add:

```sh
127.0.0.1 ledgers-keycloak
```

If something is missing, install it to your local machine, otherwise the build will fail.
List of dependencies that are required to use ModelBank: **Java 11**, **nodeJs**, **AngularCLI**, **asciidoctor**, **jq**, **docker**, **docker-compose**, **maven**, **plantuml**.
Here are links where you can install needed dependencies:

| Dependency          | Link                                    |
|---------------------| --------------------------------------- |
| Java 11             | https://openjdk.java.net/install/       |
| Node.js 18.x        | https://nodejs.org/en/download          |
| Angular CLI 15.x    | https://angular.io/guide/quickstart     |
| Asciidoctor 2.0     | https://asciidoctor.org                 |
| jq 1.6              | https://stedolan.github.io/jq/download  |
| Docker 1.17         | https://www.docker.com/get-started      |
| Docker Compose 1.24 | https://docs.docker.com/compose/install |
| Maven 3.5           | https://maven.apache.org/download.cgi   |
| PlantUML 1.2019.3   | http://plantuml.com/en/starting         |

---

**Note1:**
Check amount of memory given to **Docker** (Open Docker Desktop -> Preferences -> Advanced -> Memory).
For a fast and painless start of all the services it should be not less than 5 GB.

---

3.Build and run the project:

You can build and run ModelBank in two ways - with a docker command or with Makefile commands.

If you want use a first way:

Build all the services with command:

```sh
> make
```

After building services you can run ModelBank with a simple docker command.

**Please note:**
To be able to use adorsys' applications docker images you have to build them on your local first.

```sh
> docker-compose up
```

In Makefile you can use one of three commands:

-   Run services from Docker Hub registry without build:

```sh
> make run
```

-   Build, make docker images and run services:

```sh
> make all
```

-   Make docker images and run services without build:

```sh
> make start
```

Once you have built the project you can run it without build next time - command **docker-compose up** or **make start** from Makefile.

Remember that after you update the project you should rebuild it - command **make** or **make all** from Makefile.

4.Open [Developer Portal](http://localhost:4206) and follow the manual to try XS2A endpoints. Using Developer Portal you can try the Payment flow and Consent flow in Redirect mode. This means you will be redirected to Online Banking application to process your payments and consents. Developer Portal is provided for supporting the convenient way to send requests to XS2A system with testing purposes. To test the main flows, you may use out-of-box bank customers which are listed below.

5.By opening [TPP UI](http://localhost:4205) you can register your own TPP and manage its users and their accounts. Before using TPP UI functionality, you need to create your own TPP and then login with its credentials.

6.Opening [Online Banking](http://localhost:4400) allows you to login as a bank customer. Default users are provided out-of-box, their logins are:

-   marion.mueller (no SCA methods)
-   anton.brueckner (1 SCA method)
-   max.musterman (several SCA methods)

with the password `12345` for all those users.

7.Stop running containers in terminal with key combination **Control + C**. Please note, that it takes some time to stop all the modules, so be patient and do not kill them with `-force` key.

8.Afterwards you can remove all the ModelBank containers from Docker with the following command:

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

Developer portal is the main information resource on how to get started, how to test and work with ModelBank.

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
manage users and accounts. Admins have no access to this application.

```
http://localhost:4205
```

### Admin UI

Admin UI is an Angular application, which provides a user interface to manage administrators of the Modelbank, its users and TPPs. Please note, that TPPs cannot login to this application.

```
http://localhost:4207
```
