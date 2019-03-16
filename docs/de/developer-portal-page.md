# Developer Portal

---

## Einleitung

Die [Payment Service Directive 2 (PSD2)](https://eur-lex.europa.eu/legal-content/EN/TXT/PDF/?uri=CELEX:32015L2366&from=EN)
schreibt Banken vor eine standardisierte Account-Schnittstelle für Drittanbieter (TPPs) bereitzustellen. Im weiteren Text wird diese Account-Schnittstelle als "XS2A" bezeichnet, was für Access-to-Account steht.
Der Produktivgang dieser Live-Schnittstelle ist für September 2019 vorgeschrieben. XS2A
setzt sich aus folgende Banking-Funktionalitäten: Zahlungsinitiierung (PIS = Payment Initiation Service), Kontodatenabfrage (AIS = Account Information Service) und Abfrage von Zahlungsfähigkeit (FCS = Funds Confirmation Service).
Um die Einhaltung der Frist zu gewährleisten und Fehlerbehebungen und Anpassungen berücksichtigen zu können, verpflichtet PSD2 die Banken eine Testversion der Schnittstelle bereits im März 2019 zur Verfügung zu stellen.

Zentrale Komponente des PSD2 Accelerator ist die XS2A-Schnittstelle, die gemäß der Spezifikation der [Berlin Group](https://www.berlin-group.org/)
(Version 1.3) implementiert wurde und auf Testdaten basiert. Neben der Schnittstelle ist es für Banken ebenfalls vorgeschrieben eine kostenfreie technische Dokumentation bereitzustellen, die über unterstützte Produkte und Zahlungsservices informiert.

Zukünftig wird es für einen Drittanbieter notwendig sein, sich bei der BAFIN zu registrieren und sich von einem Treuhanddienstleister (TSP = Trust Service Provider) ein [eIDAS](https://eur-lex.europa.eu/legal-content/EN/TXT/PDF/?uri=CELEX:32014R0910&from=EN) Zertifikat ausstellen zu lassen, um XS2A-Funktionalitäten bei Banken nutzen zu können.
Da das Ausstellen dieses echten Zertifikates nur für Testzwecke einen sehr hohen Aufwand bedeuten würde, wird im Rahmen der Sandbox ein TSP simuliert und eine Funktion zur Erstellung eines qualifizierten Website-Authentifizierungs-Zertifikates (QWAC) bereitgestellt. Ein QWAC ist Teil der eIDAS und wird auch als [X.509](https://www.ietf.org/rfc/rfc3739.txt)
Zertifikat bezeichnet. Für PSD2-Zwecke wird das Zertfikat anhand des QcStatement um Inhalte wie die Rolle des Drittanbieters erweitert (Siehe auch [ETSI](https://www.etsi.org/deliver/etsi_ts/119400_119499/119495/01.01.02_60/ts_119495v010102p.pdf)).

Nach der Einbindung des QWAC in einem XS2A Request werdem Rolle und Signatur an einem zentralen Reverse Proxy validiert, bevor die Anfrage an die XS2A-Schnittstelle weitergeleitet wird, wo sich die Bankenlogik abspielt. Unsere Dokumentation finden Sie hier: [XS2A Swagger UI](/swagger-ui.html).

Die beschriebenen Komponenten und das Zusammenspiel dieser, werden in Abbildung 1.1 dargestellt:

![PSD2 Accelerator](assets/accelerator.svg 'Figure 1.1: Components of the PSD2 Accelerator')
_Figure 1.1: Components of the PSD2 Accelerator_

### Aktive XS2A Konfiguration (Bank Profil)

- SCA-Approach: Redirect
- Payment-Types
  - Single (sepa-credit-transfers)
  - Future-Dated (sepa-credit-transfers)
  - Periodic (sepa-credit-transfers)
- Confirmation of Funds: Ja
- Redirect-URLs
  - PIS Redirect-URL: _https://sandbox-api.dev.adorsys.de/v1/online-banking/init/pis/:redirect-id_
  - PIS Cancellation Redirect-URL: _https://sandbox-api.dev.adorsys.de/v1/online-banking/cancel/pis/:redirect-id_
  - AIS Redirect-URL: _https://sandbox-api.dev.adorsys.de/v1/online-banking/init/ais/:redirect-id_
- Unterstützte Consents
  - Dedicated: Ja
  - Bank-Offered: Ja
  - Global: Nein
  - Available Accounts: Nein

Deaktivierte Features:

- Signing Basket
- Bulk Payments
- Delta-Reports
- Multi-Level SCA

Technische Konfiguration der XS2A API:

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

## Erste Schritte

Um XS2A Funktionalitäten testen zu können, führen Sie die folgenen Schritte aus:

- Generieren Sie sich das benötigte QWAC Zertifikat [Certificate Service UI](./certificate-service) und fügen Sie die .pem und .key Dateien im Header Ihres Requests ein.
- Im nächsten Abschnitt finden Sie eine genaue Beschreibung der relevantesten Anwendungsfälle.

---

## Anwendungsfälle mit Nutzerdaten

Zum Testen der XS2A-Schnittstelle mit der Authentifizierungsmethode "Embedded", benutzen Sie bitte für alle PSUs diese Nutzerdaten:

- Passwort: 12345
- TAN: 54321
  Die REST API ist unter folgendem Link verfügbar: [https://sandbox-api.dev.adorsys.de](https://sandbox-api.dev.adorsys.de).

Jeder Anwendungsfall beinhaltet eine Tabelle mit den entsprechenden Zugangsdaten (ID) eines Nutzers. Mit diesen Daten ist es anschließend möglich zu überprüfen, ob das erwartete Ergebnis erzielt wurde. Zusätzlich beinhalten einige Anwendungsfälle beispielhafte Requests und die zugehörige Response.
Ein Nutzer wird im Folgenden als PSU (Payment Service User) bezeichnet. Jeder Anwendungsfall beinhaltet eine Tabelle mit den entsprechenden Zugangsdaten (PSU-ID) eines Nutzers.

### Simulation der Nutzerauthentifizierung (SCA)

Eine starke Nutzerauthentifizierung ist für Single Payments, die Löschung von Payments sowie die Anlage von Consents vorgeschrieben. Diese Nutzerauthentifizierung wird in der weiteren Dokumentation als SCA (Strong Customer Authentification) bezeichnet. Um verschiedene SCA-Methoden simulieren zu können, sind verschiedene PSU-IDs mit zugehörigen IBANs definiert.
Eine erfolgreiche SCA kann demzufolge nur mit der IBAN "DE11760365688833114935" und der zugehörigen PSU-ID "PSU-Successful" durchgeführt werden. Das gleiche gilt für alle weiteren Kombinationen von PSUs.
Fügen Sie diese URL in Ihren Browser ein und hängen Sie Ihre PSU-ID als Query-Parameter an.
Ein Beispiel dafür wäre:`?psu-id=PSU-Successful`.
"PSU-Successful" kann mit anderen PSU-IDs ersetzt werden. Wenn die IBAN in Ihrem Request nicht zu der PSU-ID passt, hat die SCA keine Auswirkung auf den Transaction- oder Consent-Status. Ohne das Anfügen einer PSU-ID wird ein Format-Error auftreten.

### Zahlungsinitiierung

Um ein Payment zu initiieren, ersetzen Sie die Debitor-IBAN in Ihrem Request mit der gewünschten IBAN von einem der definierten PSUs. Um das Payment zu authorisieren, benutzen Sie den SCA Redirect-Link, wie im vorherigen Absatz beschrieben.

`POST https://sandbox-api.dev.adorsys.de/v1/payments/sepa-credit-transfers`

Der nachfolgende Code beschreibt einen beispielhaften cURL Command, der ein Single Payment für den PSU "PSU-Successful" anlegt:

```sh
curl -v "https://sandbox-api.dev.adorsys.de/v1/payments/sepa-credit-transfers" \
  -H "accept: application/json" \
  -H "X-Request-ID: 99391c7e-ad88-49ec-a2ad-99ddcb1f7721" \
  -H "PSU-IP-Address: 192.168.8.78" \
  -H "Content-Type: application/json" \
  -H "tpp-redirect-uri: https://adorsys.de/" \
  --cert certificate.pem \
  --key private.key \
  -d '{
    "endToEndIdentification": "WBG-123456789",
    "debtorAccount": {
      "currency": "EUR",
      "iban": "DE11760365688833114935"
    },
    "instructedAmount": {
      "currency": "EUR",
      "amount": "520.00"
    },
    "creditorAccount":{
      "currency": "EUR",
      "iban": "DE15500105172295759744"
    },
    "creditorName": "WBG",
    "creditorAddress": {
      "buildingNumber": "56",
      "city": "Nürnberg",
      "country": "DE",
      "postalCode": "90543",
      "street": "WBG Straße"
    },
    "remittanceInformationUnstructured": "Ref. Number WBG-1222"
  }'
```

Der nachfolgende Code stellt eine beispielhafte Antwort für eine erfolgreiche Zahlungsinitiierung dar:

```json
{
  "transactionStatus": "RCVD",
  "paymentId": "FHQ0W-JVRLEuMwDXAYnRaRiEY5gFzU333uIo9CrgAxU6bEHR4m6hs_rkUaqcWwJqfPlpOwr468RhuFoTl0Y5Kg==_=_bS6p6XvTWI",
  "transactionFees": null,
  "transactionFeeIndicator": false,
  "scaMethods": null,
  "chosenScaMethod": null,
  "challengeData": null,
  "_links": {
    "scaRedirect": "https://sandbox-api.dev.adorsys.de/v1/online-banking/init/pis/65458d7e-2181-4e28-83cc-2a1900d1f727",
    "self": "https://sandbox-api.dev.adorsys.de/v1/payments/sepa-credit-transfers/FHQ0W-JVRLEuMwDXAYnRaRiEY5gFzU333uIo9CrgAxU6bEHR4m6hs_rkUaqcWwJqfPlpOwr468RhuFoTl0Y5Kg==_=_bS6p6XvTWI",
    "status": "https://sandbox-api.dev.adorsys.de/v1/payments/sepa-credit-transfers/FHQ0W-JVRLEuMwDXAYnRaRiEY5gFzU333uIo9CrgAxU6bEHR4m6hs_rkUaqcWwJqfPlpOwr468RhuFoTl0Y5Kg==_=_bS6p6XvTWI/status",
    "scaStatus": "https://sandbox-api.dev.adorsys.de/v1/payments/sepa-credit-transfers/FHQ0W-JVRLEuMwDXAYnRaRiEY5gFzU333uIo9CrgAxU6bEHR4m6hs_rkUaqcWwJqfPlpOwr468RhuFoTl0Y5Kg==_=_bS6p6XvTWI/authorisations/65458d7e-2181-4e28-83cc-2a1900d1f727"
  },
  "psuMessage": null,
  "tppMessages": null
}
```

| PSU-ID            | Iban                   | SCA Status                  | Transaction Status                  |
| :---------------- | :--------------------- | :-------------------------- | :---------------------------------- |
| PSU-Successful    | DE11760365688833114935 | finalised                   | ACTC/ACSC\*                         |
| PSU-Rejected\*\*  | DE06760365689827461249 | failed                      | RJCT                                |
| PSU-Blocked       | DE13760365681209386222 | _(no SCA Status available)_ | _(no Transaction Status available)_ |
| PSU-InternalLimit | DE91760365683491763002 | finalised                   | RJCT                                |

(\*) Der Status ist abhängig vom Payment-Type. Ein Single Payment erhält nach seiner Ausführung den Transaction Status "executed" im PSD2 accelerator. Ein Future-Dated Payment wird ausgeführt, wenn das definierte Datum "requestedExecutionDate" erreicht wurde. Ein ähnliches Verhalten ist für Periodic-Payments implementiert, die vom Enddatum "endDate" abhängen.

(\*\*) Das gleiche Ergebnis wird erzielt, wenn der PSU und die verwendete IBAN nicht zusammenpassen oder wenn die PSU-ID unbekannt ist.

### Löschen eines Payments

Um ein Payment zu löschen, fügen Sie Ihre Payment-Id in den Delete Payment Endpoint ein. Das Löschen eines Payments
benötigt den _expliziten Start des Authorisierungsvorgangs_. Das bedeutet, dass dem `startAuthorisation` Link in der
Response gefolgt werden muss, um die Authorisierung zu starten. Benutzen Sie danach den
[SCA Redirect-Link wie im vorherigen Absatz beschrieben](developer-portal#simulation-der-nutzerauthentifizierung-sca).

`DELETE https://sandbox-api.dev.adorsys.de/v1/payments/sepa-credit-transfers/paymentId`

| PSU-ID                    | Iban                   | SCA Status | Transaction Status |
| :------------------------ | :--------------------- | :--------- | :----------------- |
| PSU-Successful            | DE11760365688833114935 | finalised  | CANC\*             |
| PSU-Cancellation-Rejected | DE68760365687914626923 | failed     | ACTC               |

(\*) Es ist nur möglich Payments zu löschen, die noch nicht ausgeführt wurden. Da Single Payments direkt ausführt werden, ist eine Löschung nur bei Future-Dated Payments oder Periodic-Payments möglich.

### Payment abfragen

Um Zahlungsdaten abzufragen, fügen Sie Ihre Payment-Id in den GET Payment Data Endpoint ein.

`GET https://sandbox-api.dev.adorsys.de/v1/payments/paymentId`

Um den Transaction Status eines Payments abzufragen, fügen Sie Ihre Payment-Id in den GET Payment Status Endpoint ein.

`GET https://sandbox-api.dev.adorsys.de/v1/payments/paymentId/status`

| PSU-ID         | Iban                   | SCA Status | Transaction Status |
| :------------- | :--------------------- | :--------- | :----------------- |
| PSU-Successful | DE11760365688833114935 | finalised  | ACTC/ACSC          |

### Erstellung eines Dedicated Consent

Um Accounts abfragen zu können, ist es vorab notwendig einen sogenannten Dedicated Consent anzulegen. Dabei handelt es sich um eine Art Einverständniserklärung mit einer definierten Gültigkeit, die es erlaubt je nach Art des Consents Accounts für einen festgelegten Gültigkeitszeitraum abzufragen. Um einen Dedicated Consent anzulegen, ersetzen Sie die IBAN in Ihrem Request mit der IBAN.Ihres gewünschten PSU. Den Consent können Sie, wie im vorherigen Absatz beschrieben, mit dem SCA Redirect-Link authorisieren.

`POST https://sandbox-api.dev.adorsys.de/v1/consents`

Der nachfolgende Code beschreibt einen beispielhaften cURL Command, der einen Dedicated Consent für PSU "PSU-Successful" anlegt:

```sh
curl -v "https://sandbox-api.dev.adorsys.de/v1/consents" \
  -H "accept: application/json" \
  -H "X-Request-ID: 99391c7e-ad88-49ec-a2ad-99ddcb1f7721" \
  -H "Content-Type: application/json" \
  -H "tpp-redirect-uri: https://adorsys.de/" \
  --cert certificate.pem \
  --key private.key \
  -d '{
  "access": {
    "accounts": [
      {
        "iban": "DE11760365688833114935",
        "currency": "EUR"
     }
    ],
    "balances": [
     {
        "iban": "DE11760365688833114935",
        "currency": "EUR"
     }
    ],
    "transactions": [
      {
        "iban": "DE11760365688833114935",
        "currency": "EUR"
    }
    ]
},
  "recurringIndicator": true,
  "validUntil": "2020-12-31",
  "frequencyPerDay": 4,
  "combinedServiceIndicator": true
}'
```

| PSU-ID                  | Iban                   | SCA Status                  | Consent Status                  |
| :---------------------- | :--------------------- | :-------------------------- | :------------------------------ |
| PSU-Successful          | DE11760365688833114935 | finalised                   | valid                           |
| PSU-Rejected            | DE06760365689827461249 | failed                      | rejected                        |
| PSU-Blocked             | DE13760365681209386222 | _(no SCA Status available)_ | _(no Consent Status available)_ |
| PSU-ConsentExpired      | DE12760365687895439876 | finalised                   | expired                         |
| PSU-ConsentRevokedByPsu | DE89760365681729983660 | finalised                   | revokedByPsu                    |

### Erstellung eines Bank Offered Consent

Um einen Bank Offered Consent anzulegen, ersetzen Sie die Iban in Ihrem Request mit der Ihres gewünschten PSU. Um den Consent zu authorisieren, benutzen Sie bitte den SCA Redirect-Link, wie im vorherigen Absatz beschrieben.

`POST https://sandbox-api.dev.adorsys.de/v1/consents`

Der nachfolgende Code beschreibt einen beispielhaften cURL Command, der einen Bank Offered Consent für PSU "PSU-Successful" anlegt:

```sh
curl -v "https://sandbox-api.dev.adorsys.de/v1/consents" \
  -H "accept: application/json" \
  -H "X-Request-ID: 99391c7e-ad88-49ec-a2ad-99ddcb1f7721" \
  -H "Content-Type: application/json" \
  -H "tpp-redirect-uri: https://adorsys.de/" \
  --cert certificate.pem \
  --key private.key \
  -d '{
  "access": {
    "accounts": [],
    "balances": [],
    "transactions": []
},
  "recurringIndicator": true,
  "validUntil": "2020-12-31",
  "frequencyPerDay": 4,
  "combinedServiceIndicator": true
}'
```

Die Anlage eines Bank Offered Consent, funktioniert nur für den PSU-Successful. Mit der ConsentId, die bei der Erstellung eines Bank Offered Consent generiert wird, können Sie über den GET Accounts Endpunkt die ersten beiden Accounts des PSU-Successful abfragen. Die Response Ihres Bank Offered Consent können Sie mit der Tabelle im vorherigen Abschnitt "Erstellung eines Dedicated Consent" abgleichen.

### Consent Löschung

Um einen Consent zu löschen, fügen Sie Ihre Consent-ID in den Delete Consent Endpoint ein. Um die Authorisierung der Löschung durchzuführen, benutzen Sie den SCA Redirect-Link wie im vorherigen Absatz beschrieben.

`DELETE https://sandbox-api.dev.adorsys.de/v1/consents/consentId`

| PSU-ID            | Iban                   | Consent Status                  |
| :---------------- | :--------------------- | :------------------------------ |
| PSU-Successful    | DE11760365688833114935 | terminatedByTpp                 |
| PSU-Rejected      | DE06760365689827461249 | _(no Consent Status available)_ |
| PSU-Blocked       | DE13760365681209386222 | _(no Consent Status available)_ |
| PSU-InternalLimit | DE91760365683491763002 | terminatedByTpp                 |

### Abfragen von Accounts

Um Accounts abzufragen, ersetzen Sie die IBAN in Ihrem Request mit der gewünschten IBAN von einem der definierten PSUs.

`GET https://sandbox-api.dev.adorsys.de/v1/accounts`

Der nachfolgende Code beschreibt einen beispielhaften cURL Command, der alle Accounts des PSU "PSU-Successful" abfragt:

```sh
curl -v "https://sandbox-api.dev.adorsys.de/v1/accounts" \
  -H "accept: application/json" \
  -H "X-Request-ID: 99391c7e-ad88-49ec-a2ad-99ddcb1f7721" \
  -H "Content-Type: application/json" \
  -H "consent-id: I58hV2nWPJVJEvuw0dzl8qBkGcz40Qo_BCd_CjnTf_vsx7DeU-pL5sFaqwNUzbAThuXzrcFlVLs6eEVHdoFgKQ==_=_bS6p6XvTWI" \
  -H "tpp-redirect-uri: https://adorsys.de/" \
  --cert certificate.pem \
  --key private.key \
```

Der nachfolgende Code beschreibt eine beispielhafte Response für eine erfolgreiche Abfrage der Accounts:

```json
{
  "accounts": [
    {
      "resourceId": "8660d175-2c79-4b68-a175-93b1866dc7e3",
      "iban": "DE11760365688833114935",
      "bban": "",
      "msisdn": "",
      "currency": "EUR",
      "name": "",
      "product": "Current Account",
      "cashAccountType": "CACC",
      "status": null,
      "bic": "",
      "linkedAccounts": "",
      "usage": null,
      "details": "",
      "balances": [
        {
          "balanceAmount": {
            "currency": "EUR",
            "amount": "1500"
          },
          "balanceType": null,
          "lastChangeDateTime": null,
          "referenceDate": null,
          "lastCommittedTransaction": null
        }
      ],
      "_links": {
        "viewTransactions": "https://sandbox-api.dev.adorsys.de/v1/accounts/8660d175-2c79-4b68-a175-93b1866dc7e3/transactions"
      }
    }
  ]
}
```

| PSU-ID                  | Iban                     | Consent Status |
| :---------------------- | :----------------------- | :------------- |
| PSU-Successful          | DE11760365688833114935\* | valid          |
| PSU-ConsentExpired      | DE12760365687895439876   | expired        |
| PSU-ConsentRevokedByPsu | DE89760365681729983660   | revokedByPsu   |

(\*) Der PSU-Successful besitzt weitere Konten, um folgendes Verhalten zu testen:

- Ein Konto verfügt über einen negativen Buchungssaldo und eine abweichende positive Balance. (Eine positive Balance berücksichtigt vorgemerkte Umsätze, die noch nicht gebucht wurden, wie beispielsweise Kartenzahlungen)
  (DE89760365681134661389)
- Ein Konto enthält keine Transactions und die Balance ist 0
  (DE07760365680034562391)
- Ein Konto hat eine geringere verfügbare Balance als der aktuelle Buchungssaldo
  (DE71760365681257681381)
- Die Währung eines Kontos ist in USD angegeben (DE56760365681650680255)
