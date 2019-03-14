# Developer portal

# Getting started

## Introduction

The [Payment Service Directive 2 (PSD2)](https://eur-lex.europa.eu/legal-content/EN/TXT/PDF/?uri=CELEX:32015L2366&from=EN)
instructs banks (Account Servicing Payment Service Providers or ASPSPs) to provide a fully productive Access-to-Account (XS2A)
interface to Third Party Providers (TPPs) until September 2019. XS2A
itself consists of banking services to initiate payments (PIS), request
account data (AIS) and get the confirmation of the availability of funds
(PIIS). In order to guarantee the compliance of this deadline due to
adaptions and bugs, PSD2 claims the banks to provide a functional dynamic
sandbox offering the XS2A services in a non-productive environment until
June 2019.

The **XS2ABank** is a dynamic sandbox environment that fully meets the PSD2 requirements for providing APIs for Third-Party Providers (TPP). Based on the Berlin Group’s NextGen PSD2 specification for access to accounts (XS2A), **XS2ABank** meets all the regulatory requirements and is NISP compliant.

This developer portal is created to help TPP developers start working with XS2ABank.

## XS2ABank architecture and modules

Components of XS2ABank with their connections to each other:

![XS2ABank](assets/xs2abank.svg 'Figure 1.1: Components of XS2ABank')
_Figure 1.1: Components of the XS2ABank_

### XS2A Interface

Central component of the **XS2ABank** is the **XS2A interface** which
meets the requirements of the [Berlin Group](https://www.berlin-group.org/)
(Version 1.3) and is based on test data. You can
visit our documentation here [XS2A Swagger UI](/swagger-ui.html) or find full [OpenSource XS2A Interface on Github](https://github.com/adorsys/xs2a).

### ASPSP-Profile

Besides the actual interface, PSD2 instructs ASPSPs to offer a technical documentation free of charge containing
amongst others, information about supported payment products and payment
services. This information is stored in **ASPSP-profile** (bank profile), a service based on yaml file where a bank can provide available payment products, payment services, supported SCA approaches and other bank-specific settings.

#### Active XS2A Configuration (Bank Profile) (EDIT ALL AFTER ENV CREATION)

- Supported SCA-Approach: Redirect
- Available payment types and payment products for each type:
  - Single (sepa-credit-transfers)
  - Future-Dated (sepa-credit-transfers)
  - Periodic (sepa-credit-transfers)
- Confirmation of Funds: supported
- Redirect URLs to Online Banking:
  - Redirect URL for Payment confirmation: ** url **
  - Redirect URL for Payment cancellation: ** url **
  - Redirect URL for Account Consent confirmation: ** url **
- Supported types of Consents:
  - Dedicated: Yes
  - Bank-Offered: Yes
  - Global: No
  - Available Accounts: No

Disabled features:

- Signing Basket
- Bulk Payments
- Delta-Reports
- Multi-Level SCA

#### Corresponding ASPSP-Profile settings in .yaml file:

```yaml
setting:
  frequencyPerDay: 5
  combinedServiceIndicator: false
  scaApproaches:
    - REDIRECT
  tppSignatureRequired: false
  bankOfferedConsentSupport: true
  pisRedirectUrlToAspsp: http://localhost:8080/v1/online-banking/init/pis/{redirect-id}
  pisPaymentCancellationRedirectUrlToAspsp: http://localhost:8080/v1/online-banking/cancel/pis/{redirect-id}
  aisRedirectUrlToAspsp: http://localhost:8080/v1/online-banking/init/ais/{redirect-id}
  multicurrencyAccountLevel: SUBACCOUNT
  availableBookingStatuses:
    - BOOKED
    - PENDING
  supportedAccountReferenceFields:
    - IBAN
  consentLifetime: 0
  transactionLifetime: 0
  allPsd2Support: false
  transactionsWithoutBalancesSupported: false
  signingBasketSupported: false
  paymentCancellationAuthorizationMandated: true
  piisConsentSupported: false
  deltaReportSupported: false
  redirectUrlExpirationTimeMs: 600000
  notConfirmedConsentExpirationPeriodMs: 86400000
  notConfirmedPaymentExpirationPeriodMs: 86400000
  supportedPaymentTypeAndProductMatrix:
    SINGLE:
      - sepa-credit-transfers
    PERIODIC:
      - sepa-credit-transfers
  paymentCancellationRedirectUrlExpirationTimeMs: 600000
  availableAccountsConsentSupported: false
  scaByOneTimeAvailableAccountsConsentRequired: true
  psuInInitialRequestMandated: false
```

### TPP Certificate Service

Usually, before accessing the XS2A services a TPP would need to register
at its National Competent Authority (NCA) and request an
[eIDAS](https://eur-lex.europa.eu/legal-content/EN/TXT/PDF/?uri=CELEX:32014R0910&from=EN)
certificate at an appropriate Trust Service Provider (TSP). Issuing a
real certificate just for testing purposes would be too much effort,
which is why the **XS2ABank** is additionally simulating a fictional
TSP issuing Qualified Website Authentication Certificates (QWAC). A QWAC
is part of eIDAS and might be better known as [X.509](https://www.ietf.org/rfc/rfc3739.txt)
certificate. For PSD2-purposes the certificate gets extended by the QcStatement
containing appropriate values such as the role(s) of the PSP (see
[ETSI](https://www.etsi.org/deliver/etsi_ts/119400_119499/119495/01.01.02_60/ts_119495v010102p.pdf)).

After embedding the QWAC in the actual XS2A request, the role and the
signature get validated at a central reverse proxy before it gets
finally passed to the interface where the banking logic happens.

### TPP User Interface

A TPP developers can download test data for their TPP application using generated certificate and [prepared data](link to data!).

### Online banking

In case of REDIRECT SCA approach a user wants to provide consent for using their account information or for payment confirmation/cancellation. Online banking is a user interface to provide consent to a bank. Links for a consent confirmation and payment confirmation or cancellation are provided in the response of the corresponding endpoints.

## Links to environments

| Service                 | Local environment                      | XS2ABank environment |
| ----------------------- | -------------------------------------- | -------------------- |
| XS2A Interface Swagger  | http://localhost:8080/swagger-ui.html  |                      |
| ASPSP-Profile Swagger   | http://localhost:48080/swagger-ui.html |                      |
| TPP Certificate Service | http://localhost:48080/swagger-ui.html |                      |
| TPP User Interface      | http://localhost:48080/swagger-ui.html |                      |
| Online banking          | http://localhost:8080/swagger-ui.html  |                      |

## How to download, setup and run the project (UPDATE AFTER CLARIFICATION)

### Prerequisites

To run **XS2ABank** locally you are required to have **Docker** installed on your machine. You can download it [here](https://www.docker.com/get-started).

### Download XS2ABank

Download the project directly [here](LINK TO XS2ABANK) or use command:

`git clone LINK_XS2ABANK`

### Run XS2ABank

After downloading the project go to the project directory:

`cd PROJECT_DIR`

You can run all the services with a simple docker command:

`docker-compose -f docker-compose-all.yml up`

## How to register TPP and start testing

1. Open [TPP User Interface login page](LINK TO PAGE).
2. If you have no login and password - register yourself by clicking "Register" button.
3. Register yourself and log into the system.
4. Upload the test data and start testing.

Whole flow for TPPs to start their work with XS2ABank is displayed in Figure 1.2:

![TPPflow](assets/tppflow.svg 'Figure 1.2: TPP flow step-by-step')
_Figure 1.2: TPP flow step-by-step_

## What's next?

When you are done with all steps from `Getting started` manual, check [Test cases](LINK TO TEST CASES) section for further testing. There you will find prepared **Postman tests**, XS2A Interface API description and instructions how to test XS2ABank with **Swagger**.
