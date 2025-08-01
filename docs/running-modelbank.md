## Running the ModelBank using our open source code

### Install software

Make sure that the following programs are installed:
- IntelliJ
- Docker Desktop
- Maven
- Java 17

Make sure that $JAVA_HOME Env is set to Version 17

This is an example for ~/.bash_profile MacOS.

export JAVA_HOME=$(/usr/libexec/java_home -v17)

### Edit /etc/hosts file

Open the hosts file
``` bash
    sudo vi /etc/hosts
```

Add one entry in the hosts file
```bash
    127.0.0.1 ledgers-keycloak
```

### Clone all the repositories

- XS2A
   ```bash
    git clone https://github.com/adorsys/xs2a.git
   ```

- Ledgers
   ```bash
    git clone https://github.com/adorsys/ledgers.git
   ```

- Connector
   ```bash
    git clone https://github.com/adorsys/xs2a-connector-examples.git
   ```

- Sandbox
   ```bash
    git clone https://github.com/adorsys/XS2A-Sandbox.git
   ```
  
Important: Check out this Branch in Git:
- xs2a
   ```bash
    git checkout support-18.x
   ```
- ledgers
   ```bash
    git checkout develop
   ```
- connector-examples
   ```bash
    git checkout support-18.x
   ```
- xs2a-sandbox
   ```bash
    git checkout support-10.x
   ```

At first lookup in the sandbox project in the root pom.xml, which versions of xs2a, connector-examples and ledgers are needed?

pom.xml(xs2a-sandbox (18.2-SNAPSHOT)) Important: Check the Version in the pom file!

https://github.com/adorsys/XS2A-Sandbox/blob/support-10.x/pom.xml

```bash
    <xs2a.version>18.2-SNAPSHOT</xs2a.version>
    <connector.version>18.2-SNAPSHOT</connector.version>
    <ledgers.version>6.2-SNAPSHOT</ledgers.version>
```
OR

```bash
    <xs2a.version>18.1</xs2a.version>
    <connector.version>18.1</connector.version>
    <ledgers.version>6.1</ledgers.version>
```

*Getting the projects ready*

- ### Run the database

    Start Docker desktop, make sure no other process is attached to port 5432, and start the Postgres database

    #### Run the command in a terminal

    ```bash
        docker run --name consent-postgres -p 5432:5432 -e POSTGRES_USER=cms -e POSTGRES_PASSWORD=cms -e POSTGRES_DB=consent -d postgres:latest
    ```
- ### Download and run Keycloak (version 26.0.2)

    Ledgers 5.0 use Keycloak version 22.0.3
    Ledgers 6.2 use Keycloak version 26.0.2 (current version on develop branch)

    Download and unzip the Keycloak files from the Keycloak site, take the Distribution powered by Quarkus Version, and start Keycloak:
    
    https://www.keycloak.org/downloads-archive.html
    
    https://www.keycloak.org/archive/downloads-26.0.2.html (Distribution powered by Quarkus)

    - Unzip the keycloak file
      - You'll need to to build the Ledgers project to complete the steps below:

          - After the building of the Ledgers application, copy `keycloak-token-exchange.jar` file from the directory `keycloak/keycloak-token-exchange/target` of the Ledgers project to dir `/providers` of your keycloak directory:

          - Go to the `/bin` folder and run keycloak with the following command in your terminal
              ```java
                    ./kc.sh start-dev
              ```
            Tip: building ledgers when keycloak is not already running? do `mvn clean install -Dmaven.test.skip=true`

IMPORTANT: If you are running Keycloak for the first time, please open the Keycloak console URL (by default it is http://localhost:8080) and create an Admin user with login: admin and password: admin. These credentials are used by Ledgers to create a realm and test user data

- ### Ledgers

    - In the **LedgersApplication** add Active profiles
  
    ```java
        develop, postgres
    ```
    - Save the changes and exit.
    - Open the Database Tab (on the right hand side of IntelliJ) and add a new Databasesource/Postgres. And Test the connection, if everything works fine, apply and save.

    ```java
        User:cms
        Password:cms
        Database: consent
    ```

    - At the bottom left, test the db connectivity and then make sure it is okay before you exit.
    - In the Database Console, create a new schema with the following commands
    ```sql
            CREATE SCHEMA consent;
            CREATE SCHEMA public;
    ```

- ### XS2A

    Now edit the Configurations for the separate services, and add the parameter.
    
    Sometimes Intellij does not recognize the services, then you can add new Debug/Run configurations, choose Spring

    - In the **AspsProfileApplication** add the Active profiles:

        ```java
                debug_mode
        ```
    - In the **ConsentManagementStandaloneApp** add the Environment variables:
        ```java
              server_key=mySecretKey
        ```
      
    -  Save and exit

    -  Open the Database Tab (on the right hand side of IntelliJ) and add a new Databasesource/Postgres. And Test the connection, if everything works fine, apply and save.
              
        ```java
                User:cms
                Password:cms
                Database: consent
          ```
        
    - At the bottom left, test the DB connectivity and ensure it is okay before you exit.
        
    - In the Database Console, create a new schema with the following commands
        
      ```java
              CREATE SCHEMA consent;
              CREATE SCHEMA public;
      ```
        
    - Before you start, search the file `consent-management/cms-db-schema/liquibase.example.properties`, copy the file and rename the new file to `liquibase.properties`, The content should be the following.
      ```properties
                        url=jdbc:postgresql://localhost/consent?currentSchema=consent
                        username=cms
                        password=cms
        
                        changeLogFile=src/main/resources/master.xml
      ```

    Tip: You can search a file with a double shift in Intelli
    
    ```properties
              cd consent-management/cms-db-schema
              mvn liquibase:update
    ```

    Tip: If the build fails, close IntelliJ or Use invalidate Caches/Restart from the menu

-  ### XS2A-connector-example

 - In the **LedgersXs2aGatewayApplication** add the active profiles

    ```java
        mock-qwac
    ```

    and the Environment variables
    ```java
        server_key=123
    ```

    Save the changes and exit.

- ### Maven Build

    Open each of these projects in IntelliJ, checkout to the proper branch version from the previous step, run the terminal in each project and start a maven build

    - #### XS2A, Connector, Ledgers, Sandbox

    ```bash
          mvn clean install
    ```

    For faster build time, you can try

    ```bash
            mvn clean install -Dmaven.test.skip=true
    ```

  - ### Run the applications

      When the clean install is done for all the projects,

      - In **XS2A**, run the two services(`AspsProfileApplication` and `ConsentManagementStandaloneApp`) via the Services Tab in IntelliJ.

      - In **Ledgers**, run the `LedgersApplication` still via the service tap in IntelliJ

      - Start the `LedgerXS2aGatewayapplication` in the **xs2a-connector-examples**
    
    Tips: 
    - run keycloak before running Ledgers. 
    - run Ledgers before running xs2a-connector-examples.
    - run xs2a, Ledgers, xs2a-connector-examples before running XS2A-Sandbox.

- ### XS2A-SANDBOX

    Set up the tpp-database

    Open the Database Tab (on the right hand side of IntelliJ) and add a new Databasesource/Postgres. And Test the connection, if everything works fine, apply and save.

    ```java
            User:cms
            Password:cms
            Database: consent
    ```

  - In the Database Console, create a new schema with the following commands

    ```java
            CREATE SCHEMA tpp;
    ```

  **Then you can start the three services:**

  - AdminApplication
  - LedgersXs2aObaApplication
  - TppApplication

  #### **You can now start the UIs.** 

  - Open the folders:
  - developer-portal-ui
  - admin-ui
  - oba-ui
  - tpp-ui
  
  run `npm ci` to build UI app 
    - open the package.json and click the play button right beside "start".

After all, you may check the web-pages in your browser:

- Developer Portal: http://localhost:4206
- TPP UI: http://localhost:4205
- Admin UI: http://localhost:4207
- Online-Banking UI: http://localhost:4400

- ### Possible solutions in case of errors

    - Restart Intellij
    - Invalidate Caches, and set all hooks. Run `mvn clean install` or `mvn clean install -Dmaven.test.skip=true`
    - Reindexing Mvn → Reload mvn projects, Generate Source and update all folders
    - Check the Java Version, if mulitble versions installed switch to V17
    - export JAVA_HOME=`/usr/libexec/java_home -v 17`

## Links to local Swagger Interfaces

Following URLs will access the swagger interfaces for all ModelBank components (except the Keycloak - it uses its own web UI):

### XS2A Interface

External endpoints for TPPs in accordance with BerlinGroup API specification.

```
http://localhost:8089/swagger-ui.html
```

### ASPSP-profile

ASPSP-profile is a module where bank-specific settings are stored.

```
http://localhost:48080/swagger-ui.html
```

### CMS

Consent Management System is a module where XS2A database is running.

```
http://localhost:38080/swagger-ui.html
```

### Ledgers

Ledgers is a core banking emulating system used to handle accounts and payments. Security and user management is processed by Keycloak identity provider (IDP).

```
http://localhost:8088/swagger-ui.html
```

### Keycloak

Identity provider used to handle ModelBank users (admins, TPPs and customers). Ledgers is using Keycloak under the hood, so there is no need to configure the IDP manually, thus its web UI is available at:

```
http://localhost:8080
```

## Links to local User Interfaces

### Developer portal UI

Developer portal is the main information resource on how to get started, how to test and work with ModelBank. You can try the main ModelBank functionality here also:

-   create the payment in redirect approach and authorise it with SCA in Online Banking Application;
-   create the AIS consent in redirect approach and authorise it with SCA in Online Banking Application;
-   read payment details and accounts/balances/transactions for the consent.

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
