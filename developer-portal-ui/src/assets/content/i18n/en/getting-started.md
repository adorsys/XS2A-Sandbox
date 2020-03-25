<div class="divider">
</div>

# Introduction

The [Payment Service Directive 2 (PSD2)](https://eur-lex.europa.eu/legal-content/EN/TXT/PDF/?uri=CELEX:32015L2366&from=EN) instructs banks (Account Servicing Payment Service Providers or ASPSPs) to provide a fully productive Access-to-Account (XS2A) interface to Third Party Providers (TPPs) until March 2020. XS2A itself consists of banking services to initiate payments (PIS), request account data (AIS) and get the confirmation of the availability of funds (PIIS). In order to guarantee the compliance of this deadline due to adaptions and bugs, PSD2 claims the banks to provide a functional dynamic sandbox offering the XS2A services in a non-productive environment until June 2019.

The **XS2ASandbox** is a dynamic sandbox environment that fully meets the PSD2 requirements for providing APIs for Third-Party Providers (TPP). Based on the Berlin Group’s NextGen PSD2 specification for access to accounts (XS2A),

This developer portal is created to help TPP developers start working with XS2ASandbox.

<div class="divider">
</div>

# XS2ASandbox architecture and modules

Components of XS2ASandbox with their connections to each other are shown in Figure 1.1.

![Figure 1.1](../../assets/images/Graphic_XS2A_Sandbox.jpg)

Figure 1.1: Components of the XS2ASandbox

<div class="divider">
</div>

# XS2A Interface

Central component of the **XS2ASandbox** is the XS2A interface which meets the requirements of the Berlin Group's specification [NextGenPSD2](https://www.berlin-group.org/psd2-access-to-bank-accounts) (Version 1.3) and is based on test data. You can visit our [XS2A Swagger UI](https://demo-dynamicsandbox-xs2a.cloud.adorsys.de/) or find full [OpenSource XS2A Interface on Github](https://github.com/adorsys/xs2a).

<div class="divider">
</div>

# ASPSP-Profile

Besides the actual interface, PSD2 instructs ASPSPs to offer a technical documentation free of charge containing amongst others, information about supported payment products and payment services. This information is stored in **ASPSP-profile** (bank profile), a service based on yaml file where a bank can provide available payment products, payment services, supported SCA approaches and other bank-specific settings.

<div class="divider">
</div>

# TPP Certificate Service

Usually, before accessing the XS2A services a TPP would need to register at its National Competent Authority (NCA) and request an [eIDAS](https://eur-lex.europa.eu/legal-content/EN/TXT/PDF/?uri=CELEX:32014R0910&from=EN) certificate at an appropriate Trust Service Provider (TSP). Issuing a real certificate just for testing purposes would be too much effort, which is why the **XS2ASandbox** is additionally simulating a fictional TSP issuing Qualified Website Authentication Certificates (QWAC). A QWAC is part of eIDAS and might be better known [X.509](https://www.ietf.org/rfc/rfc3739.txt) certificate. For PSD2-purposes the certificate gets extended by the QcStatement containing appropriate values such as the role(s) of the Payment Service Provider (PSP) (see [ETSI](https://www.etsi.org/deliver/etsi_ts/119400_119499/119495/01.01.02_60/ts_119495v010102p.pdf)).

After embedding the QWAC in the actual XS2A request, the role and the signature get validated at a central reverse proxy before it gets finally passed to the interface where the banking logic happens.

<div class="divider">
</div>

# TPP User Interface

TPP developers can register themselves into the system, obtain certificate and download test data for their TPP application using generated certificate and prepared data in TPP UI.

## How to create an account for TPP:

1. Open TPP UI login page.

2. Choose option “Register” below.

3. Provide all needed information: TPP authorization number, email, login and password.

4. You can generate test QWAC certificate for using it with XS2A Interface if needed.

## How to create users:

Users could be created manually or via uploading .yaml file.

### To create user manually:

1. Go to “My users list” and click on “Create new user”.

2. Enter email, login and pin for a new user.

3. Choose authentication method (only email is valid for now) and enter a valid email. If you choose option "Use Static TAN in SandboxMode", mock static TAN would be used for testing. Then you have to enter your mock TAN also. You can add multiple SCA methods to every user.

### To create accounts manually:

1. Choose a user in your list of users and click "Create deposit account".

2. Select account type, usage and account status. IBAN could be generated automatically. Please, note, that only valid ibans are accepted by XS2A Interface.

3. After account is created, you can use function "Deposit cash" to add any amount to the balances for testing purposes. All the created accounts are available in "Users accounts" tab.

To create users and accounts automatically, generate test data with the tab "Generate Test Data", and NISP compliant test data would be generated. If you want to upload your owm `.yaml` testing file, use tab "Upload files".

## How to create test payments, consents and transactions:

To create test payments, you have to generate test payment data on tab "Generate Test Data", checking option "Generate Payment Data" as true. Also, the upload of a custom .yaml file with payments is possible in tab "Upload files". Consents and transactions could be created only with upload of the proper `.yaml` file.

<div class="divider">
</div>

# Online banking

In case of REDIRECT SCA approach a user wants to provide consent for using their account information or for payment confirmation/cancellation. Online banking is a user interface to provide consent to a bank. Links for a consent confirmation and payment confirmation or cancellation are provided in the response of the corresponding endpoints.

<div class="divider">
</div>

# Links to environments

| Service                   |           Local environment            |                                                                       Demo environment |
| ------------------------- | :------------------------------------: | -------------------------------------------------------------------------------------: |
| XS2A Interface Swagger    | http://localhost:8089/swagger-ui.html  |                                     https://demo-dynamicsandbox-xs2a.cloud.adorsys.de/ |
| Developer portal          |         http://localhost:4206          |                        https://demo-dynamicsandbox-developerportalui.cloud.adorsys.de/ |
| Consent management system | http://localhost:38080/swagger-ui.html |                                       https://demo-dynamicsandbox-cms.cloud.adorsys.de |
| Ledgers                   | http://localhost:8088/swagger-ui.html  |                                   https://demo-dynamicsandbox-ledgers.cloud.adorsys.de |
| ASPSP-Profile Swagger     | http://localhost:48080/swagger-ui.html |                              https://demo-dynamicsandbox-aspspprofile.cloud.adorsys.de |
| TPP User Interface        |         http://localhost:4205          |                               https://demo-dynamicsandbox-tppui.cloud.adorsys.de/login |
| Online banking UI         |         http://localhost:4400          | https://demo-dynamicsandbox-onlinebankingui.cloud.adorsys.de/account-information/login |
| Online banking backend    | http://localhost:8090/swagger-ui.html  |             https://demo-dynamicsandbox-onlinebanking.cloud.adorsys.de/swagger-ui.html |
| Certificate Generator     | http://localhost:8092/swagger-ui.html  |      https://demo-dynamicsandbox-certificategenerator.cloud.adorsys.de/swagger-ui.html |

<div class="divider">
</div>

# How to download, setup and run the project

## Prerequisites

This sandbox runs with the docker-compose that can be found at docker-compose.yml and Makefile in project directory. But before you run XS2ASandbox, first check if all build dependencies are installed: 

_make check_

If something is missing, install it to your local machine, otherwise the build will fail. List of dependencies that are required to use XS2ASandbox: Java 8, NodeJs, Angular CLI, Asciidoctor, jq, Docker, Docker Compose, Maven, PlantUML. Here are links where you can install needed dependencies:

| Dependency          |                  Link                   |
| ------------------- | :-------------------------------------: |
| Java 8              |    https://openjdk.java.net/install/    |
| Node.js 11.x        |     https://nodejs.org/en/download      |
| Angular CLI 7.x     |   https://angular.io/guide/quickstart   |
| Asciidoctor 2.0     |         https://asciidoctor.org         |
| jq 1.6              | https://stedolan.github.io/jq/download  |
| Docker 1.17         |   https://www.docker.com/get-started    |
| Docker Compose 1.24 | https://docs.docker.com/compose/install |
| Maven 3.5           |  https://maven.apache.org/download.cgi  |
| PlantUML 1.2019.3   |     http://plantuml.com/en/starting     |

Stop running containers in terminal with key combination _Control + C_.

You can remove all the Sandbox containers from Docker with the following command:

_docker-compose rm -s -f -v_

## Note 1

Please, use Node.js version lower than 12 (e.g. 10.x.x or 11.x.x). Otherwise angular applications would not be built due to version conflicts.

## Note 2

Check amount of memory given to Docker (Open Docker Desktop -> Preferences -> Advanced -> Memory). For a fast and painless start of all the services it should be not less than 5 GB.

## Download XS2ASandbox

Download the project directly from GitHub or use command:

_git clone https://github.com/adorsys/XS2A-Sandbox_

## Build and run XS2ASandbox

After downloading the project go to the project directory:

_cd XS2A-Sandbox_

After that you can build and run XS2ASandbox in two ways - with a docker command or with Makefile commands.

If you want use a first way:

1. Build all the services with the command:

_make_

2. After building services you can run XS2ASandbox with a simple docker command:

_docker-compose up_

In Makefile you can use one of four commands:

• Run release version of services from Docker Hub without build:

_make run-release_

• Run services from Docker Hub without build:

_make run_

• Build and run services:

_make all_

• Run services without build:

_make start_

Once you have built the project you can run it without build next time - command docker-compose up or make start from Makefile.

Remember that after you update the project you should rebuild it - command make or make all from Makefile.

<div class="divider">
</div>

# Troubleshooting

These are common mistakes you can get during the start of XS2ASandbox and an instruction how to get rid of it:

## Liquibase changelog error

This error can be produced if you had unsuccessful start of XS2ASandbox earlier. Example of possible stack trace:

```yaml
ledgers | 2019-05-02 13:54:29.410 INFO 1 --- [ main] liquibase.executor.jvm.JdbcExecutor : SELECT LOCKED FROM ledgers.databasechangeloglock WHERE ID=1
ledgers | 2019-05-02 13:54:39.697 INFO 1 --- [ main] l.lockservice.StandardLockService : Waiting for changelog lock....
ledgers | 2019-05-02 13:54:55.137 INFO 1 --- [ main] liquibase.executor.jvm.JdbcExecutor : SELECT LOCKED FROM ledgers.databasechangeloglock WHERE ID=1
ledgers | 2019-05-02 13:55:35.940 INFO 1 --- [ main] l.lockservice.StandardLockService : Waiting for changelog lock....
ledgers | 2019-05-02 13:55:45.995 INFO 1 --- [ main] liquibase.executor.jvm.JdbcExecutor : SELECT LOCKED FROM ledgers.databasechangeloglock WHERE ID=1
ledgers | 2019-05-02 13:55:46.967 INFO 1 --- [ main] l.lockservice.StandardLockService : Waiting for changelog lock....
ledgers | 2019-05-02 13:55:59.167 INFO 1 --- [ main] liquibase.executor.jvm.JdbcExecutor : SELECT LOCKED FROM ledgers.databasechangeloglock WHERE ID=1
ledgers | 2019-05-02 13:57:38.705 INFO 1 --- [ main] l.lockservice.StandardLockService : Waiting for changelog lock....
ledgers exited with code 137
```

Possible solution:

Find and delete all folders "ledgerdbs" and "xs2adbs". Clear all docker containers with command:

_docker-compose rm -s -f -v_

Restart all services.

## Node version error

This error can be produced because of wrong NodeJs version (version higher than 11.x). Example of possible stack trace:

```yaml
gyp ERR! build error
gyp ERR! stack Error: `make` failed with exit code: 2
gyp ERR! stack at ChildProcess.onExit (/Users/rpo/XS2A-Sandbox/developer-portal-ui/node_modules/node-gyp/lib/build.js:262:23)
gyp ERR! stack at ChildProcess.emit (events.js:196:13)
gyp ERR! stack at Process.ChildProcess.\_handle.onexit (internal/child_process.js:256:12)
gyp ERR! System Darwin 17.7.0
gyp ERR! command "/usr/local/Cellar/node/12.1.0/bin/node" "/Users/rpo/XS2A-Sandbox/developer-portal-ui/node_modules/node-gyp/bin/node-gyp.js" "rebuild" "--verbose" "--libsass_ext=" "--libsass_cflags=" "--libsass_ldflags=" "--libsass_library="
gyp ERR! cwd /Users/rpo/XS2A-Sandbox/developer-portal-ui/node_modules/@angular-devkit/build-angular/node_modules/node-sass
gyp ERR! node -v v12.1.0
gyp ERR! node-gyp -v v3.8.0
gyp ERR! not ok
```

Possible solution:

First, check your version of NodeJs with the command:

_node -v_

If version is higher than 11.x - change version of NodeJs to earlier one.

<div class="divider">
</div>

# How to register TPP and start testing

1. Open TPP User Interface login page.
2. If you have no login and password - register yourself by clicking "Register" button.
3. Register yourself, create a certificate and log into the system. Note: TPP ID should consist of at least 8 digits, no letters or other signs allowed.
4. Upload the test data and start testing.

Whole flow for TPPs to start their work with XS2ASandbox is displayed in Figure 1.2:

![Figure 1.2](../../assets/images/Flow.png)

Figure 1.2: TPP flow step-by-step

<div class="divider">
</div>

# How to customize UI of developer portal

It is possible to customize texts, navigation, amount and content of pages and styling of all the elements of Developer Portal. To find out how to do it, please, read [Customization Guide](../../../../assets/files/UIs_customization_guide.pdf).

<div class="divider">
</div>

# Google Analytics support 

To connect your Google Analytics account, in docker-compose.yml in Developer Portal UI section add property TRACKING_ID with your Google Analytics account ID. Then run docker-compose normally, Google Analytics account would be connected automatically.

![Google Analytics Docker](../../assets/images/googleAnalyticsDocker.png)

The example of docker-compose.yaml with enabled Google Analytics support can be fount in [docker-compose-google-analytics-enabled.yml](https://github.com/adorsys/XS2A-Sandbox/blob/master/docker-compose-google-analytics-enabled.yml) file in root of the Sandbox repository.

The application gives to Google Analytics the information about every page visit and some events. These event are every try out of API endpoint in Test Cases section (event is triggered by clicking on `Submit` button) and Postman tests download (event is triggered by clicking on `Download` button).

<div class="divider">
</div>

# What's next?

When you are done with all steps from Getting started manual, check Test cases section for further testing. There you will find prepared Postman tests, XS2A Interface API description and instructions how to test XS2ASandbox with Swagger.
