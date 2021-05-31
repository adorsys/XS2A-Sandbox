# Release notes v.5.12

Current version consists of following components:

1. **Ledgers v.4.12**
2. **XS2A v.11.4**
3. **ASPSP-Profile v.11.4**
4. **Consent-Management v.11.4**
5. **XS2A Connector-Examples v.11.4**

-   Security vulnerability: set commons-io version 2.8.0
-   Use pair of tokens (access and refresh) for session
-   Used `OkHttpClient` as a feign client to avoid 302 redirect error
-   TPP-UI. Show specific account details depending on backend
-   Fix OBA: "Back to TPP page" button 403 error
-   Upgraded commons-validator version to 1.7
