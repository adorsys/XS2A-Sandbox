# Release notes v.7.6

Current version consists of following components:

1. **Ledgers v.4.22**
2. **XS2A v.15.8**
3. **ASPSP-Profile v.15.8**
4. **Consent-Management v.15.8**
5. **XS2A Connector-Examples v.15.8**

-   Fixed docker-compose files for the project.

-   Dependencies versions were bumped up:

    -   Spring Boot - to 2.7.10
    -   Spring Cloud - to 2021.0.6
    -   Spring Test - to 5.3.26

-   Updated libraries' versions in all frontend applications.

-   Fixed bug with incorrect TAN error message

-   Fixed rollback mechanism for TPP application.

-   SMTP configuration properties for Ledgers were extended in docker-compose file.

-   Fixed bug during account creation with 'DISABLED' or 'BLOCKED' statuses from TPP-UI. 

-   Fixed bug for new account creation with the same IBAN and currency in TPP-UI and admin UI.
