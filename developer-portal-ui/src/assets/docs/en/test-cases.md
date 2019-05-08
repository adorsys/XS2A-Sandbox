# Test cases

After loading test data into the system, TPP developer can test XS2ABank endpoints with Swagger or Postman tests.

In **Swagger section** you can find information about usage of Swagger, prepared test data (_Jsons_) for testing and short API documentation. In [Postman tests section](LINK TO POSTMAN SECTION) you can find information about usage of Postman for testing and download prepared Postman tests with testing data.

Both sections provide tests for REDIRECT Strong Customer Authorisation (SCA) approach and EMBEDDED SCA Approach for Payment Initiation Service (PIS), Account Information Service (AIS) and Payment Instrument Issuer Service (PIIS).

## REDIRECT approach

Short overview of REDIRECT approach:

- The whole SCA process with two different factors (e.g. username/password as proof of knowledge and a one time password (TAN) as proof of possession) is provided by the ASPSP and executed directly between PSU and ASPSP.
- Therefore, the PSU needs to gets redirected from PISP to ASPSP.
- The SCA of the PSU is executed directly between the ASPSP and the PSU.
- After completion of the SCA the PSU gets redirected back to the PISP.

![REDIRECT](assets/redirect_pis_initiation.svg 'Figure 1.1: Payment initiation in REDIRECT')
\_Figure 1.1: Payment initiation diagram in redirect

## EMBEDDED approach

![EMBEDDED](assets/embedded_pis_initiation.svg 'Figure 1.2: Payment initiation in EMBEDDED')
\_Figure 1.2: Payment initiation diagram in embedded

# Test XS2ABank with Swagger

## Testing flows

You can use 3 typical testing flows for REDIRECT and EMBEDDED SCA approaches with Swagger.

To open Swagger page of XS2ABank locally start all the services with docker command from <a href='link'>Getting Started</a>.
After starting all the services, go to your local [XS2A Interface Swagger Page](http://localhost:8080/swagger-ui.html).

To open Swagger page on the cloud follow [this link](LINK_TO_XS2A_SWAGGER_XS2ABANK).

## REDIRECT approach

### Payment initiation flow

#### STEP 1: Create payment

1. Open swagger tab "Payment Initiation Service (PIS)", open **POST** endpoint `/v1/{payment-service}/{payment-product}`.

2. Press _"Try it out"_. Choose `payment-service` and `payment-product`. Default values would be `payments` in `payment-service`
   and `sepa-credit-transfers` in `payment-product`.

3. After choosing payment service and payment product, fill the header fields with the following values:

| Header                               | Value                                |
| ------------------------------------ | ------------------------------------ |
| X-Request-ID                         | 2f77a125-aa7a-45c0-b414-cea25a116035 |
| TPP-Explicit-Authorisation-Preferred | true                                 |
| PSU-ID                               | YOUR_USER_LOGIN                      |
| PSU-IP-Address                       | 1.1.1.1                              |

</br>

Put in **YOUR_USER_IBAN** the name of the user you chose (you can view users and their information in TPP-UI).

4. If you chose to create single payment by setting `payment-service` to `payments`, insert into request body the following json:

```json
{
  "endToEndIdentification": "WBG-123456789",
  "debtorAccount": {
    "currency": "EUR",
    "iban": "YOUR_USER_IBAN"
  },
  "instructedAmount": {
    "currency": "EUR",
    "amount": "20.00"
  },
  "creditorAccount": {
    "currency": "EUR",
    "iban": "DE15500105172295759744"
  },
  "creditorAgent": "AAAADEBBXXX",
  "creditorName": "WBG",
  "creditorAddress": {
    "buildingNumber": "56",
    "city": "Nürnberg",
    "country": "DE",
    "postalCode": "90543",
    "street": "WBG Straße"
  },
  "remittanceInformationUnstructured": "Ref. Number WBG-1222"
}
```

If you chose to create periodic payment by setting `payment-service` to `periodic-payments`, insert into request body the following json:

```json
{
  "creditorAccount": {
    "currency": "EUR",
    "iban": "DE15500105172295759744"
  },
  "creditorAddress": {
    "street": "Breite Gasse",
    "buildingNumber": "34",
    "city": "Nürnberg",
    "postalCode": "90457",
    "country": "DE"
  },
  "creditorAgent": "BCENEVOD",
  "creditorName": "Vodafone",
  "dayOfExecution": "14",
  "debtorAccount": {
    "currency": "EUR",
    "iban": "YOUR_USER_IBAN"
  },
  "endDate": "2019-10-14",
  "endToEndIdentification": "VOD-123456789",
  "executionRule": "following",
  "frequency": "Monthly",
  "instructedAmount": {
    "amount": "44.99",
    "currency": "EUR"
  },
  "remittanceInformationUnstructured": "Ref. Number Vodafone-1222",
  "startDate": "2019-05-26"
}
```

If you chose to create bulk payment by setting `payment-service` to `bulk-payments`, insert into request body the following json:

```json
{
  "batchBookingPreferred": "false",
  "requestedExecutionDate": "2019-12-12",
  "debtorAccount": {
    "currency": "EUR",
    "iban": "YOUR_USER_IBAN"
  },
  "payments": [
    {
      "endToEndIdentification": "WBG-123456789",
      "instructedAmount": {
        "amount": "520.00",
        "currency": "EUR"
      },
      "creditorAccount": {
        "currency": "EUR",
        "iban": "DE15500105172295759744"
      },
      "creditorAgent": "AAAADEBBXXX",
      "creditorName": "WBG",
      "creditorAddress": {
        "buildingNumber": "56",
        "city": "Nürnberg",
        "country": "DE",
        "postalCode": "90543",
        "street": "WBG Straße"
      },
      "remittanceInformationUnstructured": "Ref. Number WBG-1234"
    },
    {
      "endToEndIdentification": "RI-234567890",
      "instructedAmount": {
        "amount": "71.07",
        "currency": "EUR"
      },
      "creditorAccount": {
        "currency": "EUR",
        "iban": "DE03500105172351985719"
      },
      "creditorAgent": "AAAADEBBXXX",
      "creditorName": "Grünstrom",
      "creditorAddress": {
        "buildingNumber": "74",
        "city": "Dresden",
        "country": "DE",
        "postalCode": "01067",
        "street": "Kaisergasse"
      },
      "remittanceInformationUnstructured": "Ref. Number GRUENSTROM-2444"
    }
  ]
}
```

Change placeholder **YOUR_USER_IBAN** in section

```json
"debtorAccount": {
        "currency": "EUR",
        "iban": "YOUR_USER_IBAN"
      }
```

of the json you chose to the IBAN of the user (IBAN should match user login you entered in PSU-ID header above).

5. Execute and get response with code 201 and related information.

#### STEP 2: Authorise payment

Start with the authorisation process. Follow `scaRedirect` link to the online banking page.

### Consent initiation flow

#### STEP 1: Create consent

1. Open swagger tab "Account Information Service (AIS)", open **POST** endpoint `/v1/consents`.

2. Press _"Try it out"_, fill the header fields with the following values:

| Header                               | Value                                |
| ------------------------------------ | ------------------------------------ |
| X-Request-ID                         | 2f77a125-aa7a-45c0-b414-cea25a116035 |
| TPP-Explicit-Authorisation-Preferred | true                                 |
| PSU-ID                               | YOUR_USER_LOGIN                      |
| PSU-IP-Address                       | 1.1.1.1                              |

Put in **YOUR_USER_IBAN** the name of the user you chose (you can view users and their information in TPP-UI).

4. Insert into request body the following json for available accounts consent:

```json
{
  "access": {
    "accounts": [
      {
        "currency": "EUR",
        "iban": "YOUR_USER_IBAN"
      }
    ],
    "balances": [
      {
        "currency": "EUR",
        "iban": "YOUR_USER_IBAN"
      }
    ],
    "transactions": [
      {
        "currency": "EUR",
        "iban": "YOUR_USER_IBAN"
      }
    ]
  },
  "combinedServiceIndicator": true,
  "frequencyPerDay": 15,
  "recurringIndicator": true,
  "validUntil": "2019-10-10"
}
```

Change placeholder **YOUR_USER_IBAN** to the IBAN of the user (IBAN should match user login you entered in PSU-ID header above).

5. Execute and get response with code 201 and related information.

#### STEP 2: Authorise consent

1. Start with the authorisation process for consent: follow the link `scaRedirect` to the online banking page.

### Payment cancellation flow

#### STEP 1: Create payment

1. Open swagger tab "Payment Initiation Service (PIS)", open **POST** endpoint `/v1/{payment-service}/{payment-product}`.

2. Press _"Try it out"_. Choose `payment-service` and `payment-product`. Default values would be `payments` in `payment-service`
   and `sepa-credit-transfers` in `payment-product`.

3. After choosing payment service and payment product, fill the header fields with the following values:

| Header                               | Value                                |
| ------------------------------------ | ------------------------------------ |
| X-Request-ID                         | 2f77a125-aa7a-45c0-b414-cea25a116035 |
| TPP-Explicit-Authorisation-Preferred | true                                 |
| PSU-ID                               | YOUR_USER_LOGIN                      |
| PSU-IP-Address                       | 1.1.1.1                              |

</br>

Put in **YOUR_USER_IBAN** the name of the user you chose (you can view users and their information in TPP-UI).

4. If you chose to create single payment by setting `payment-service` to `payments`, insert into request body the following json:

```json
{
  "endToEndIdentification": "WBG-123456789",
  "debtorAccount": {
    "currency": "EUR",
    "iban": "YOUR_USER_IBAN"
  },
  "instructedAmount": {
    "currency": "EUR",
    "amount": "20.00"
  },
  "creditorAccount": {
    "currency": "EUR",
    "iban": "DE15500105172295759744"
  },
  "creditorAgent": "AAAADEBBXXX",
  "creditorName": "WBG",
  "creditorAddress": {
    "buildingNumber": "56",
    "city": "Nürnberg",
    "country": "DE",
    "postalCode": "90543",
    "street": "WBG Straße"
  },
  "remittanceInformationUnstructured": "Ref. Number WBG-1222"
}
```

If you chose to create periodic payment by setting `payment-service` to `periodic-payments`, insert into request body the following json:

```json
{
  "creditorAccount": {
    "currency": "EUR",
    "iban": "DE15500105172295759744"
  },
  "creditorAddress": {
    "street": "Breite Gasse",
    "buildingNumber": "34",
    "city": "Nürnberg",
    "postalCode": "90457",
    "country": "DE"
  },
  "creditorAgent": "BCENEVOD",
  "creditorName": "Vodafone",
  "dayOfExecution": "14",
  "debtorAccount": {
    "currency": "EUR",
    "iban": "YOUR_USER_IBAN"
  },
  "endDate": "2019-10-14",
  "endToEndIdentification": "VOD-123456789",
  "executionRule": "following",
  "frequency": "Monthly",
  "instructedAmount": {
    "amount": "44.99",
    "currency": "EUR"
  },
  "remittanceInformationUnstructured": "Ref. Number Vodafone-1222",
  "startDate": "2019-05-26"
}
```

If you chose to create bulk payment by setting `payment-service` to `bulk-payments`, insert into request body the following json:

```json
{
  "batchBookingPreferred": "false",
  "requestedExecutionDate": "2019-12-12",
  "debtorAccount": {
    "currency": "EUR",
    "iban": "YOUR_USER_IBAN"
  },
  "payments": [
    {
      "endToEndIdentification": "WBG-123456789",
      "instructedAmount": {
        "amount": "520.00",
        "currency": "EUR"
      },
      "creditorAccount": {
        "currency": "EUR",
        "iban": "DE15500105172295759744"
      },
      "creditorAgent": "AAAADEBBXXX",
      "creditorName": "WBG",
      "creditorAddress": {
        "buildingNumber": "56",
        "city": "Nürnberg",
        "country": "DE",
        "postalCode": "90543",
        "street": "WBG Straße"
      },
      "remittanceInformationUnstructured": "Ref. Number WBG-1234"
    },
    {
      "endToEndIdentification": "RI-234567890",
      "instructedAmount": {
        "amount": "71.07",
        "currency": "EUR"
      },
      "creditorAccount": {
        "currency": "EUR",
        "iban": "DE03500105172351985719"
      },
      "creditorAgent": "AAAADEBBXXX",
      "creditorName": "Grünstrom",
      "creditorAddress": {
        "buildingNumber": "74",
        "city": "Dresden",
        "country": "DE",
        "postalCode": "01067",
        "street": "Kaisergasse"
      },
      "remittanceInformationUnstructured": "Ref. Number GRUENSTROM-2444"
    }
  ]
}
```

Change placeholder **YOUR_USER_IBAN** in section

```json
"debtorAccount": {
        "currency": "EUR",
        "iban": "YOUR_USER_IBAN"
      }
```

of the json you chose to the IBAN of the user (IBAN should match user login you entered in PSU-ID header above).

5. Execute and get response with code 201 and related information.

#### STEP 2: Cancel payment

Start with the authorisation process. Follow `scaRedirect` link to the online banking page.

1. Initiate cancellation process with **DELETE** endpoint `/v1/{payment-service}/{payment-product}/{paymentId}`.
   Choose the same `payment-service` and `payment-product` you chose in **STEP 1**.

2. Enter payment id you have got from the previous response in **STEP 1** in the field `paymentId`.

3. After choosing payment service and payment product, fill the header fields with the following values:

| Header         | Value                                |
| -------------- | ------------------------------------ |
| X-Request-ID   | 2f77a125-aa7a-45c0-b414-cea25a116035 |
| PSU-IP-Address | 1.1.1.1                              |

4. Hit "Execute" button and get 200 HTTP code and related information.

#### STEP 3: Authorise payment

1. Start with the authorisation process for cancellation. Follow `scaRedirect` link to the online banking page.

## EMBEDDED approach

### Payment initiation flow

#### STEP 1: Create payment

1. Open swagger tab "Payment Initiation Service (PIS)", open **POST** endpoint `/v1/{payment-service}/{payment-product}`.

2. Press _"Try it out"_. Choose `payment-service` and `payment-product`. Default values would be `payments` in `payment-service`
   and `sepa-credit-transfers` in `payment-product`.

3. After choosing payment service and payment product, fill the header fields with the following values:

| Header                               | Value                                |
| ------------------------------------ | ------------------------------------ |
| X-Request-ID                         | 2f77a125-aa7a-45c0-b414-cea25a116035 |
| TPP-Explicit-Authorisation-Preferred | true                                 |
| PSU-ID                               | YOUR_USER_LOGIN                      |
| PSU-IP-Address                       | 1.1.1.1                              |

</br>

Put in **YOUR_USER_IBAN** the name of the user you chose (you can view users and their information in TPP-UI).

4. If you chose to create single payment by setting `payment-service` to `payments`, insert into request body the following json:

```json
{
  "endToEndIdentification": "WBG-123456789",
  "debtorAccount": {
    "currency": "EUR",
    "iban": "YOUR_USER_IBAN"
  },
  "instructedAmount": {
    "currency": "EUR",
    "amount": "20.00"
  },
  "creditorAccount": {
    "currency": "EUR",
    "iban": "DE15500105172295759744"
  },
  "creditorAgent": "AAAADEBBXXX",
  "creditorName": "WBG",
  "creditorAddress": {
    "buildingNumber": "56",
    "city": "Nürnberg",
    "country": "DE",
    "postalCode": "90543",
    "street": "WBG Straße"
  },
  "remittanceInformationUnstructured": "Ref. Number WBG-1222"
}
```

If you chose to create periodic payment by setting `payment-service` to `periodic-payments`, insert into request body the following json:

```json
{
  "creditorAccount": {
    "currency": "EUR",
    "iban": "DE15500105172295759744"
  },
  "creditorAddress": {
    "street": "Breite Gasse",
    "buildingNumber": "34",
    "city": "Nürnberg",
    "postalCode": "90457",
    "country": "DE"
  },
  "creditorAgent": "BCENEVOD",
  "creditorName": "Vodafone",
  "dayOfExecution": "14",
  "debtorAccount": {
    "currency": "EUR",
    "iban": "YOUR_USER_IBAN"
  },
  "endDate": "2019-10-14",
  "endToEndIdentification": "VOD-123456789",
  "executionRule": "following",
  "frequency": "Monthly",
  "instructedAmount": {
    "amount": "44.99",
    "currency": "EUR"
  },
  "remittanceInformationUnstructured": "Ref. Number Vodafone-1222",
  "startDate": "2019-05-26"
}
```

If you chose to create bulk payment by setting `payment-service` to `bulk-payments`, insert into request body the following json:

```json
{
  "batchBookingPreferred": "false",
  "requestedExecutionDate": "2019-12-12",
  "debtorAccount": {
    "currency": "EUR",
    "iban": "YOUR_USER_IBAN"
  },
  "payments": [
    {
      "endToEndIdentification": "WBG-123456789",
      "instructedAmount": {
        "amount": "520.00",
        "currency": "EUR"
      },
      "creditorAccount": {
        "currency": "EUR",
        "iban": "DE15500105172295759744"
      },
      "creditorAgent": "AAAADEBBXXX",
      "creditorName": "WBG",
      "creditorAddress": {
        "buildingNumber": "56",
        "city": "Nürnberg",
        "country": "DE",
        "postalCode": "90543",
        "street": "WBG Straße"
      },
      "remittanceInformationUnstructured": "Ref. Number WBG-1234"
    },
    {
      "endToEndIdentification": "RI-234567890",
      "instructedAmount": {
        "amount": "71.07",
        "currency": "EUR"
      },
      "creditorAccount": {
        "currency": "EUR",
        "iban": "DE03500105172351985719"
      },
      "creditorAgent": "AAAADEBBXXX",
      "creditorName": "Grünstrom",
      "creditorAddress": {
        "buildingNumber": "74",
        "city": "Dresden",
        "country": "DE",
        "postalCode": "01067",
        "street": "Kaisergasse"
      },
      "remittanceInformationUnstructured": "Ref. Number GRUENSTROM-2444"
    }
  ]
}
```

Change placeholder **YOUR_USER_IBAN** in section

```json
"debtorAccount": {
        "currency": "EUR",
        "iban": "YOUR_USER_IBAN"
      }
```

of the json you chose to the IBAN of the user (IBAN should match user login you entered in PSU-ID header above).

5. Execute and get response with code 201 and related information.
6. Start with the authorisation process.

Pay attention to the amount of SCA methods user has: if user has no SCA methods,
test payment initiation with the flow described below in "Payment authorisation for user with no SCA methods". For user with single SCA method check
"Payment authorisation for user with single SCA method", and for user with multiple SCA methods check "Payment authorisation for user with multiple SCA methods".

#### STEP 2: Authorise payment

**Payment authorisation for user with no SCA methods**

1. Start authorisation process with _POST_ endpoint `/v1/{payment-service}/{payment-product}/{paymentId}/authorisations`.
   Choose the same `payment-service` and `payment-product` you chose in **STEP 1**.

2. Enter payment id you have got from the previous response in **STEP 1** in the field `paymentId`.

3. After choosing payment service and payment product, fill the header fields with the following values:

| Header       | Value                                |
| ------------ | ------------------------------------ |
| X-Request-ID | 2f77a125-aa7a-45c0-b414-cea25a116035 |
| PSU-ID       | YOUR_USER_LOGIN                      |

Put in **YOUR_USER_IBAN** the name of the user you chose (you can view users and their information in TPP-UI).

4. Start authorisation by hitting "Execute" button and get 201 HTTP code and related information.

5. Continue payment authorisation with call to **PUT** endpoint `/v1/{payment-service}/{payment-product}/{paymentId}/authorisations/{authorisationId}`.

6. Enter payment id in the field `paymentId` and authorisation id you received from POST endpoint (start authorisation) in `authorisationId` field.

7. Fill the header fields with the following values:

| Header         | Value                                |
| -------------- | ------------------------------------ |
| X-Request-ID   | 2f77a125-aa7a-45c0-b414-cea25a116035 |
| PSU-ID         | YOUR_USER_LOGIN                      |
| PSU-IP-Address | 1.1.1.1                              |

Put in **YOUR_USER_IBAN** the name of the user you chose (you can view users and their information in TPP-UI).

8. Insert into request body the following json:

```json
{
  "psuData": {
    "password": "YOUR_USER_PASSWORD"
  }
}
```

Put in **YOUR_USER_PASSWORD** the password of the user you chose (you can view users and their information in TPP-UI).

9. Execute endpoint and get `200 HTTP code` and `scaStatus: Finalised` in response.

10. Try GET endpoint `/v1/{payment-service}/{payment-product}/{paymentId}` to get payment information and GET endpoint
    `/v1/{payment-service}/{payment-product}/{paymentId}/status` to check current status of the payment.
11. Try GET endpoint `/v1/{payment-service}/{payment-product}/{paymentId}/authorisations` to get authorisation information
    and GET endpoint `/v1/{payment-service}/{payment-product}/{paymentId}/authorisations/{authorisationId}` to check current status of the authorisation.

**Payment authorisation for user with single SCA method**

1. Start authorisation process with _POST_ endpoint `/v1/{payment-service}/{payment-product}/{paymentId}/authorisations`.
   Choose the same `payment-service` and `payment-product` you chose in **STEP 1**.

2. Enter payment id you have got from the previous response in **STEP 1** in the field `paymentId`.

3. After choosing payment service and payment product, fill the header fields with the following values:

| Header       | Value                                |
| ------------ | ------------------------------------ |
| X-Request-ID | 2f77a125-aa7a-45c0-b414-cea25a116035 |
| PSU-ID       | YOUR_USER_LOGIN                      |

Put in **YOUR_USER_IBAN** the name of the user you chose (you can view users and their information in TPP-UI).

4. Start authorisation by hitting "Execute" button and get 201 HTTP code and related information.

5. Continue payment authorisation with call to **PUT** endpoint `/v1/{payment-service}/{payment-product}/{paymentId}/authorisations/{authorisationId}`.

6. Enter payment id in the field `paymentId` and authorisation id you received from POST endpoint (start authorisation) in `authorisationId` field.

7. Fill the header fields with the following values:

| Header         | Value                                |
| -------------- | ------------------------------------ |
| X-Request-ID   | 2f77a125-aa7a-45c0-b414-cea25a116035 |
| PSU-ID         | YOUR_USER_LOGIN                      |
| PSU-IP-Address | 1.1.1.1                              |

Put in **YOUR_USER_IBAN** the name of the user you chose (you can view users and their information in TPP-UI).

8. Insert into request body the following json:

```json
{
  "psuData": {
    "password": "YOUR_USER_PASSWORD"
  }
}
```

Put in **YOUR_USER_PASSWORD** the password of the user you chose (you can view users and their information in TPP-UI).

9. Execute endpoint and get `200 HTTP code` and `scaStatus: ScaMethodSelected` in response.

10. Execute this PUT endpoint again, but this time update request body: fill it with the following json:

```json
{
  "scaAuthenticationData": "123456"
}
```

User has only one SCA method, so test TAN `123456` was sent to the user, and now you need to enter this data and execute PUT command.
The response should be 200 and `scaStatus: Finalised`.

11. Try GET endpoint `/v1/{payment-service}/{payment-product}/{paymentId}` to get payment information and GET endpoint
    `/v1/{payment-service}/{payment-product}/{paymentId}/status` to check current status of the payment.
12. Try GET endpoint `/v1/{payment-service}/{payment-product}/{paymentId}/authorisations` to get authorisation information
    and GET endpoint `/v1/{payment-service}/{payment-product}/{paymentId}/authorisations/{authorisationId}` to check current status of the authorisation.

**Payment authorisation for user with multiple SCA methods**

1. Start authorisation process with _POST_ endpoint `/v1/{payment-service}/{payment-product}/{paymentId}/authorisations`.
   Choose the same `payment-service` and `payment-product` you chose in **STEP 1**.

2. Enter payment id you have got from the previous response in **STEP 1** in the field `paymentId`.

3. After choosing payment service and payment product, fill the header fields with the following values:

| Header       | Value                                |
| ------------ | ------------------------------------ |
| X-Request-ID | 2f77a125-aa7a-45c0-b414-cea25a116035 |
| PSU-ID       | YOUR_USER_LOGIN                      |

Put in **YOUR_USER_IBAN** the name of the user you chose (you can view users and their information in TPP-UI).

4. Start authorisation by hitting "Execute" button and get 201 HTTP code and related information.

5. Continue payment authorisation with call to **PUT** endpoint `/v1/{payment-service}/{payment-product}/{paymentId}/authorisations/{authorisationId}`.

6. Enter payment id in the field `paymentId` and authorisation id you received from POST endpoint (start authorisation) in `authorisationId` field.

7. Fill the header fields with the following values:

| Header         | Value                                |
| -------------- | ------------------------------------ |
| X-Request-ID   | 2f77a125-aa7a-45c0-b414-cea25a116035 |
| PSU-ID         | YOUR_USER_LOGIN                      |
| PSU-IP-Address | 1.1.1.1                              |

Put in **YOUR_USER_IBAN** the name of the user you chose (you can view users and their information in TPP-UI).

8. Insert into request body the following json:

```json
{
  "psuData": {
    "password": "YOUR_USER_PASSWORD"
  }
}
```

Put in **YOUR_USER_PASSWORD** the password of the user you chose (you can view users and their information in TPP-UI).

9. Execute endpoint and get `200 HTTP code` and list of authorisation methods in response.

10. Execute this PUT endpoint again, but this time update request body: fill it with the following json:

```json
{
  "authenticationMethodId": "YOUR_AUTHENTICATION_METHOD_ID"
}
```

Put in placeholder "YOUR_AUTHENTICATION_METHOD_ID" any value of authenticationMethodId of your choice.
The response should be 200 and `scaStatus: ScaMethodSelected`.

11. Execute this PUT endpoint again, update request body: fill it with the following json:

```json
{
  "scaAuthenticationData": "123456"
}
```

SCA method was chosen, so test TAN `123456` was sent to the user, and now you need to enter this data and execute PUT command.
The response should be 200 and `scaStatus: Finalised`.

12. Try GET endpoint `/v1/{payment-service}/{payment-product}/{paymentId}` to get payment information and GET endpoint
    `/v1/{payment-service}/{payment-product}/{paymentId}/status` to check current status of the payment.

13. Try GET endpoint `/v1/{payment-service}/{payment-product}/{paymentId}/authorisations` to get authorisation information
    and GET endpoint `/v1/{payment-service}/{payment-product}/{paymentId}/authorisations/{authorisationId}` to check current status of the authorisation.

### Consent initiation flow

#### STEP 1: Create consent

1. Open swagger tab "Account Information Service (AIS)", open **POST** endpoint `/v1/consents`.

2. Press _"Try it out"_, fill the header fields with the following values:

| Header                               | Value                                |
| ------------------------------------ | ------------------------------------ |
| X-Request-ID                         | 2f77a125-aa7a-45c0-b414-cea25a116035 |
| TPP-Explicit-Authorisation-Preferred | true                                 |
| PSU-ID                               | YOUR_USER_LOGIN                      |
| PSU-IP-Address                       | 1.1.1.1                              |

Put in **YOUR_USER_IBAN** the name of the user you chose (you can view users and their information in TPP-UI).

4. Insert into request body the following json for available accounts consent:

```json
{
  "access": {
    "accounts": [
      {
        "currency": "EUR",
        "iban": "YOUR_USER_IBAN"
      }
    ],
    "balances": [
      {
        "currency": "EUR",
        "iban": "YOUR_USER_IBAN"
      }
    ],
    "transactions": [
      {
        "currency": "EUR",
        "iban": "YOUR_USER_IBAN"
      }
    ]
  },
  "combinedServiceIndicator": true,
  "frequencyPerDay": 15,
  "recurringIndicator": true,
  "validUntil": "2019-10-10"
}
```

Change placeholder **YOUR_USER_IBAN** to the IBAN of the user (IBAN should match user login you entered in PSU-ID header above).

5. Execute and get response with code 201 and related information.
6. Start with the authorisation process.

Pay attention to the amount of SCA methods user has: if user has no SCA methods,
test consent initiation with the flow described below in "Consent authorisation for user with no SCA methods". For user with single SCA method check
"Consent authorisation for user with single SCA method", and for user with multiple SCA methods check "Consent authorisation for user with multiple SCA methods".

#### STEP 2: Consent authorisation

**Consent authorisation for user with no SCA methods**

1. Start authorisation process with **POST** endpoint `/v1/consents/{consentId}/authorisations`.

2. Enter consent id you have got from the previous response in **STEP 1** in the field `consentId`.

3. After choosing payment service and payment product, fill the header fields with the following values:

| Header       | Value                                |
| ------------ | ------------------------------------ |
| X-Request-ID | 2f77a125-aa7a-45c0-b414-cea25a116035 |
| PSU-ID       | YOUR_USER_LOGIN                      |

Put in **YOUR_USER_IBAN** the name of the user you chose (you can view users and their information in TPP-UI).

4. Start authorisation by hitting "Execute" button and get 201 HTTP code and related information.

5. Continue payment authorisation with call to **PUT** endpoint `/v1/consents/{consentId}/authorisations/{authorisationId}`.

6. Enter consent id in the field `consentId` and authorisation id you received from POST endpoint (start authorisation) in `authorisationId` field.

7. Fill the header fields with the following values:

| Header         | Value                                |
| -------------- | ------------------------------------ |
| X-Request-ID   | 2f77a125-aa7a-45c0-b414-cea25a116035 |
| PSU-ID         | YOUR_USER_LOGIN                      |
| PSU-IP-Address | 1.1.1.1                              |

Put in **YOUR_USER_IBAN** the name of the user you chose (you can view users and their information in TPP-UI).

8. Insert into request body the following json:

```json
{
  "psuData": {
    "password": "YOUR_USER_PASSWORD"
  }
}
```

Put in **YOUR_USER_PASSWORD** the password of the user you chose (you can view users and their information in TPP-UI).

9. Execute endpoint and get `200 HTTP code` and `scaStatus: Finalised` in response.

10. Try GET endpoint `/v1/consents/{consentId}` to get consent information and GET endpoint
    `/v1/consents/{consentId}/status` to check current status of the consent.
11. Try GET endpoint `/v1/consents/{consentId}/authorisations` to get authorisation information
    and GET endpoint `/v1/consents/{consentId}/authorisations/{authorisationId}` to check current status of the authorisation.

**Consent authorisation for user with single SCA method**

1. Start authorisation process with **POST** endpoint `/v1/consents/{consentId}/authorisations`.

2. Enter consent id you have got from the previous response in **STEP 1** in the field `consentId`.

3. After choosing payment service and payment product, fill the header fields with the following values:

| Header       | Value                                |
| ------------ | ------------------------------------ |
| X-Request-ID | 2f77a125-aa7a-45c0-b414-cea25a116035 |
| PSU-ID       | YOUR_USER_LOGIN                      |

Put in **YOUR_USER_IBAN** the name of the user you chose (you can view users and their information in TPP-UI).

4. Start authorisation by hitting "Execute" button and get 201 HTTP code and related information.

5. Continue payment authorisation with call to **PUT** endpoint `/v1/consents/{consentId}/authorisations/{authorisationId}`.

6. Enter consent id in the field `consentId` and authorisation id you received from POST endpoint (start authorisation) in `authorisationId` field.

7. Fill the header fields with the following values:

| Header         | Value                                |
| -------------- | ------------------------------------ |
| X-Request-ID   | 2f77a125-aa7a-45c0-b414-cea25a116035 |
| PSU-ID         | YOUR_USER_LOGIN                      |
| PSU-IP-Address | 1.1.1.1                              |

Put in **YOUR_USER_IBAN** the name of the user you chose (you can view users and their information in TPP-UI).

8. Insert into request body the following json:

```json
{
  "psuData": {
    "password": "YOUR_USER_PASSWORD"
  }
}
```

Put in **YOUR_USER_PASSWORD** the password of the user you chose (you can view users and their information in TPP-UI).

9. Execute endpoint and get `200 HTTP code` and `scaStatus: ScaMethodSelected` in response.

10. Execute this PUT endpoint again, but this time update request body: fill it with the following json:

```json
{
  "scaAuthenticationData": "123456"
}
```

User has only one SCA method, so test TAN `123456` was sent to the user, and now you need to enter this data and execute PUT command.
The response should be 200 and `scaStatus: Finalised`.

11. Try GET endpoint `/v1/consents/{consentId}` to get consent information and GET endpoint
    `/v1/consents/{consentId}/status` to check current status of the consent.
12. Try GET endpoint `/v1/consents/{consentId}/authorisations` to get authorisation information
    and GET endpoint `/v1/consents/{consentId}/authorisations/{authorisationId}` to check current status of the authorisation.

**Consent authorisation for user with multiple SCA methods**

1. Start authorisation process with **POST** endpoint `/v1/consents/{consentId}/authorisations`.

2. Enter consent id you have got from the previous response in **STEP 1** in the field `consentId`.

3. After choosing payment service and payment product, fill the header fields with the following values:

| Header       | Value                                |
| ------------ | ------------------------------------ |
| X-Request-ID | 2f77a125-aa7a-45c0-b414-cea25a116035 |
| PSU-ID       | YOUR_USER_LOGIN                      |

Put in **YOUR_USER_IBAN** the name of the user you chose (you can view users and their information in TPP-UI).

4. Start authorisation by hitting "Execute" button and get 201 HTTP code and related information.

5. Continue payment authorisation with call to **PUT** endpoint `/v1/consents/{consentId}/authorisations/{authorisationId}`.

6. Enter consent id in the field `consentId` and authorisation id you received from POST endpoint (start authorisation) in `authorisationId` field.

7. Fill the header fields with the following values:

| Header         | Value                                |
| -------------- | ------------------------------------ |
| X-Request-ID   | 2f77a125-aa7a-45c0-b414-cea25a116035 |
| PSU-ID         | YOUR_USER_LOGIN                      |
| PSU-IP-Address | 1.1.1.1                              |

8. Insert into request body the following json:

```json
{
  "psuData": {
    "password": "YOUR_USER_PASSWORD"
  }
}
```

Put in **YOUR_USER_PASSWORD** the password of the user you chose (you can view users and their information in TPP-UI).

9. Execute endpoint and get `200 HTTP code` and list of authorisation methods in response.

10. Execute this PUT endpoint again, but this time update request body: fill it with the following json:

```json
{
  "authenticationMethodId": "YOUR_AUTHENTICATION_METHOD_ID"
}
```

Put in placeholder "YOUR_AUTHENTICATION_METHOD_ID" any value of authenticationMethodId of your choice.
The response should be 200 and `scaStatus: ScaMethodSelected`.

11. Execute this PUT endpoint again, update request body: fill it with the following json:

```json
{
  "scaAuthenticationData": "123456"
}
```

SCA method was chosen, so test TAN `123456` was sent to the user, and now you need to enter this data and execute PUT command.
The response should be 200 and `scaStatus: Finalised`.

12 Try GET endpoint `/v1/consents/{consentId}` to get consent information and GET endpoint
`/v1/consents/{consentId}/status` to check current status of the consent.

13. Try GET endpoint `/v1/consents/{consentId}/authorisations` to get authorisation information
    and GET endpoint `/v1/consents/{consentId}/authorisations/{authorisationId}` to check current status of the authorisation.

### Payment cancellation flow

#### STEP 1: Create payment

1. Open swagger tab "Payment Initiation Service (PIS)", open **POST** endpoint `/v1/{payment-service}/{payment-product}`.

2. Press _"Try it out"_. Choose `payment-service` and `payment-product`. Default values would be `payments` in `payment-service`
   and `sepa-credit-transfers` in `payment-product`.

3. After choosing payment service and payment product, fill the header fields with the following values:

| Header                               | Value                                |
| ------------------------------------ | ------------------------------------ |
| X-Request-ID                         | 2f77a125-aa7a-45c0-b414-cea25a116035 |
| TPP-Explicit-Authorisation-Preferred | true                                 |
| PSU-ID                               | YOUR_USER_LOGIN                      |
| PSU-IP-Address                       | 1.1.1.1                              |

Put in **YOUR_USER_IBAN** the name of the user you chose (you can view users and their information in TPP-UI).

4. If you chose to create single payment by setting `payment-service` to `payments`, insert into request body the following json:

```json
{
  "endToEndIdentification": "WBG-123456789",
  "debtorAccount": {
    "currency": "EUR",
    "iban": "YOUR_USER_IBAN"
  },
  "instructedAmount": {
    "currency": "EUR",
    "amount": "20.00"
  },
  "creditorAccount": {
    "currency": "EUR",
    "iban": "DE15500105172295759744"
  },
  "creditorAgent": "AAAADEBBXXX",
  "creditorName": "WBG",
  "creditorAddress": {
    "buildingNumber": "56",
    "city": "Nürnberg",
    "country": "DE",
    "postalCode": "90543",
    "street": "WBG Straße"
  },
  "remittanceInformationUnstructured": "Ref. Number WBG-1222"
}
```

If you chose to create periodic payment by setting `payment-service` to `periodic-payments`, insert into request body the following json:

```json
{
  "creditorAccount": {
    "currency": "EUR",
    "iban": "DE15500105172295759744"
  },
  "creditorAddress": {
    "street": "Breite Gasse",
    "buildingNumber": "34",
    "city": "Nürnberg",
    "postalCode": "90457",
    "country": "DE"
  },
  "creditorAgent": "BCENEVOD",
  "creditorName": "Vodafone",
  "dayOfExecution": "14",
  "debtorAccount": {
    "currency": "EUR",
    "iban": "YOUR_USER_IBAN"
  },
  "endDate": "2019-10-14",
  "endToEndIdentification": "VOD-123456789",
  "executionRule": "following",
  "frequency": "Monthly",
  "instructedAmount": {
    "amount": "44.99",
    "currency": "EUR"
  },
  "remittanceInformationUnstructured": "Ref. Number Vodafone-1222",
  "startDate": "2019-05-26"
}
```

If you chose to create bulk payment by setting `payment-service` to `bulk-payments`, insert into request body the following json:

```json
{
  "batchBookingPreferred": "false",
  "requestedExecutionDate": "2019-12-12",
  "debtorAccount": {
    "currency": "EUR",
    "iban": "YOUR_USER_IBAN"
  },
  "payments": [
    {
      "endToEndIdentification": "WBG-123456789",
      "instructedAmount": {
        "amount": "520.00",
        "currency": "EUR"
      },
      "creditorAccount": {
        "currency": "EUR",
        "iban": "DE15500105172295759744"
      },
      "creditorAgent": "AAAADEBBXXX",
      "creditorName": "WBG",
      "creditorAddress": {
        "buildingNumber": "56",
        "city": "Nürnberg",
        "country": "DE",
        "postalCode": "90543",
        "street": "WBG Straße"
      },
      "remittanceInformationUnstructured": "Ref. Number WBG-1234"
    },
    {
      "endToEndIdentification": "RI-234567890",
      "instructedAmount": {
        "amount": "71.07",
        "currency": "EUR"
      },
      "creditorAccount": {
        "currency": "EUR",
        "iban": "DE03500105172351985719"
      },
      "creditorAgent": "AAAADEBBXXX",
      "creditorName": "Grünstrom",
      "creditorAddress": {
        "buildingNumber": "74",
        "city": "Dresden",
        "country": "DE",
        "postalCode": "01067",
        "street": "Kaisergasse"
      },
      "remittanceInformationUnstructured": "Ref. Number GRUENSTROM-2444"
    }
  ]
}
```

Change placeholder **YOUR_USER_IBAN** in section

```json
"debtorAccount": {
        "currency": "EUR",
        "iban": "YOUR_USER_IBAN"
      }
```

of the json you chose to the IBAN of the user (IBAN should match user login you entered in PSU-ID header above).

5. Execute and get response with code 201 and related information.
6. Start with the authorisation process.  
   If the payment is not authorised yet - authorise payment with the flows above (depending on the amount of SCA methods chosen user has).

7. If the payment is already authorised - proceed with payment cancellation.

Pay attention to the ASPSP-Profile option **paymentCancellationAuthorisationMandated**. If this option is set to `false`, then check
**Payment cancellation without authorisation** flow.

If the option is set to `true`, pay attention to the amount of SCA methods user has: if user has no SCA methods,
test payment initiation with the flow described below in **Payment cancellation authorisation for user with no SCA methods**. For user with single SCA method check
**Payment cancellation authorisation for user with single SCA method**, and for user with multiple SCA methods check **Payment cancellation authorisation for user with multiple SCA methods**.

#### STEP 2: Payment cancellation authorisation

**Payment cancellation without authorisation**

1. Initiate cancellation process with **DELETE** endpoint `/v1/{payment-service}/{payment-product}/{paymentId}`.
   Choose the same `payment-service` and `payment-product` you chose in **STEP 1**.

2. Enter payment id you have got from the previous response in **STEP 1** in the field `paymentId`.

3. After choosing payment service and payment product, fill the header fields with the following values:

| Header         | Value                                |
| -------------- | ------------------------------------ |
| X-Request-ID   | 2f77a125-aa7a-45c0-b414-cea25a116035 |
| PSU-IP-Address | 1.1.1.1                              |

4. Hit "Execute" button and get 200 HTTP code and related information.

5. Try GET endpoint `/v1/{payment-service}/{payment-product}/{paymentId}/status` to check current status of the payment.
   The status should be `CANC` (Cancelled).

**Payment cancellation authorisation for user with no SCA methods**

1. Initiate cancellation process with **DELETE** endpoint `/v1/{payment-service}/{payment-product}/{paymentId}`.
   Choose the same `payment-service` and `payment-product` you chose in **STEP 1**.

2. Enter payment id you have got from the previous response in **STEP 1** in the field `paymentId`.

3. After choosing payment service and payment product, fill the header fields with the following values:

| Header         | Value                                |
| -------------- | ------------------------------------ |
| X-Request-ID   | 2f77a125-aa7a-45c0-b414-cea25a116035 |
| PSU-IP-Address | 1.1.1.1                              |

4. Start payment cancellation authorisation with **POST** endpoint `/v1/{payment-service}/{payment-product}/{paymentId}/cancellation-authorisations`.
   Choose the same `payment-service` and `payment-product` you chose in **STEP 1**.

5. Enter payment id you have got from the previous response in **STEP 1** in the field `paymentId`.

6. Fill the header fields with the following values:

| Header         | Value                                |
| -------------- | ------------------------------------ |
| X-Request-ID   | 2f77a125-aa7a-45c0-b414-cea25a116035 |
| PSU-ID         | YOUR_USER_LOGIN                      |
| PSU-IP-Address | 1.1.1.1                              |

Put in **YOUR_USER_IBAN** the name of the user you chose (you can view users and their information in TPP-UI).

7. Execute the command and get 201 response and related information.

8. Continue payment cancellation authorisation with call to **PUT** endpoint `/v1/{payment-service}/{payment-product}/{paymentId}/cancellation-authorisations/{cancellationId}`.
   Choose the same `payment-service` and `payment-product` you chose in **STEP 1**. Fill headers with the same values as in previous POST command.

9. Enter payment id you have got from **STEP 1** in the field `paymentId`.
   Enter cancellation id you have got from the previous POST endpoint in the field `cancellationId`.

10. Insert into request body the following json:

```json
{
  "psuData": {
    "password": "YOUR_USER_PASSWORD"
  }
}
```

Put in **YOUR_USER_PASSWORD** the password of the user you chose (you can view users and their information in TPP-UI).

11. Execute endpoint and get `200 HTTP code`.

12. Try GET endpoint `/v1/{payment-service}/{payment-product}/{paymentId}/cancellation-authorisations/{cancellationId}` to check current status of the cancellation authorisation.
13. Try GET endpoint `/v1/{payment-service}/{payment-product}/{paymentId}/status` to get current payment status (in should be `CANC`, cancelled).

**Payment cancellation authorisation for user with single SCA method**

1. Initiate cancellation process with **DELETE** endpoint `/v1/{payment-service}/{payment-product}/{paymentId}`.
   Choose the same `payment-service` and `payment-product` you chose in **STEP 1**.

2. Enter payment id you have got from the previous response in **STEP 1** in the field `paymentId`.

3. After choosing payment service and payment product, fill the header fields with the following values:

| Header         | Value                                |
| -------------- | ------------------------------------ |
| X-Request-ID   | 2f77a125-aa7a-45c0-b414-cea25a116035 |
| PSU-IP-Address | 1.1.1.1                              |

4. Start payment cancellation authorisation with **POST** endpoint `/v1/{payment-service}/{payment-product}/{paymentId}/cancellation-authorisations`.
   Choose the same `payment-service` and `payment-product` you chose in **STEP 1**.

5. Enter payment id you have got from the previous response in **STEP 1** in the field `paymentId`.

6. Fill the header fields with the following values:

| Header         | Value                                |
| -------------- | ------------------------------------ |
| X-Request-ID   | 2f77a125-aa7a-45c0-b414-cea25a116035 |
| PSU-ID         | YOUR_USER_LOGIN                      |
| PSU-IP-Address | 1.1.1.1                              |

Put in **YOUR_USER_IBAN** the name of the user you chose (you can view users and their information in TPP-UI).

7. Execute the command and get 201 response and related information.

8. Continue payment cancellation authorisation with call to **PUT** endpoint `/v1/{payment-service}/{payment-product}/{paymentId}/cancellation-authorisations/{cancellationId}`.
   Choose the same `payment-service` and `payment-product` you chose in **STEP 1**. Fill headers with the same values as in previous POST command.

9. Enter payment id you have got from **STEP 1** in the field `paymentId`.
   Enter cancellation id you have got from the previous POST endpoint in the field `cancellationId`.

10. Insert into request body the following json:

```json
{
  "psuData": {
    "password": "YOUR_USER_PASSWORD"
  }
}
```

Put in **YOUR_USER_PASSWORD** the password of the user you chose (you can view users and their information in TPP-UI).

11. Execute endpoint and get `200 HTTP code`.

12. Execute this PUT endpoint again, but this time update request body: fill it with the following json:

```json
{
  "scaAuthenticationData": "123456"
}
```

User has only one SCA method, so test TAN `123456` was sent to the user, and now you need to enter this data and execute PUT command.
The response should be 200.

13. Try GET endpoint `/v1/{payment-service}/{payment-product}/{paymentId}/cancellation-authorisations/{cancellationId}` to check current status of the cancellation authorisation.
14. Try GET endpoint `/v1/{payment-service}/{payment-product}/{paymentId}/status` to get current payment status (in should be `CANC`, cancelled).

**Payment cancellation authorisation for user with multiple SCA methods**

1. Initiate cancellation process with **DELETE** endpoint `/v1/{payment-service}/{payment-product}/{paymentId}`.
   Choose the same `payment-service` and `payment-product` you chose in **STEP 1**.

2. Enter payment id you have got from the previous response in **STEP 1** in the field `paymentId`.

3. After choosing payment service and payment product, fill the header fields with the following values:

| Header         | Value                                |
| -------------- | ------------------------------------ |
| X-Request-ID   | 2f77a125-aa7a-45c0-b414-cea25a116035 |
| PSU-IP-Address | 1.1.1.1                              |

4. Start payment cancellation authorisation with **POST** endpoint `/v1/{payment-service}/{payment-product}/{paymentId}/cancellation-authorisations`.
   Choose the same `payment-service` and `payment-product` you chose in **STEP 1**.

5. Enter payment id you have got from the previous response in **STEP 1** in the field `paymentId`.

6. Fill the header fields with the following values:

| Header         | Value                                |
| -------------- | ------------------------------------ |
| X-Request-ID   | 2f77a125-aa7a-45c0-b414-cea25a116035 |
| PSU-ID         | YOUR_USER_LOGIN                      |
| PSU-IP-Address | 1.1.1.1                              |

Put in **YOUR_USER_IBAN** the name of the user you chose (you can view users and their information in TPP-UI).

7. Execute the command and get 201 response and related information.

8. Continue payment cancellation authorisation with call to **PUT** endpoint `/v1/{payment-service}/{payment-product}/{paymentId}/cancellation-authorisations/{cancellationId}`.
   Choose the same `payment-service` and `payment-product` you chose in **STEP 1**. Fill headers with the same values as in previous POST command.

9. Enter payment id you have got from **STEP 1** in the field `paymentId`.
   Enter cancellation id you have got from the previous POST endpoint in the field `cancellationId`.

10. Insert into request body the following json:

```json
{
  "psuData": {
    "password": "YOUR_USER_PASSWORD"
  }
}
```

Put in **YOUR_USER_PASSWORD** the password of the user you chose (you can view users and their information in TPP-UI).

11. Execute endpoint and get `200 HTTP code` and list of authorisation methods in response.

12. Execute this PUT endpoint again, but this time update request body: fill it with the following json:

```json
{
  "authenticationMethodId": "YOUR_AUTHENTICATION_METHOD_ID"
}
```

Put in placeholder "YOUR_AUTHENTICATION_METHOD_ID" any value of authenticationMethodId of your choice.
The response should be 200.

13. Execute this PUT endpoint again, update request body: fill it with the following json:

```json
{
  "scaAuthenticationData": "123456"
}
```

User has only one SCA method, so test TAN `123456` was sent to the user, and now you need to enter this data and execute PUT command.
The response should be 200.

14. Try GET endpoint `/v1/{payment-service}/{payment-product}/{paymentId}/cancellation-authorisations/{cancellationId}` to check current status of the cancellation authorisation.
15. Try GET endpoint `/v1/{payment-service}/{payment-product}/{paymentId}/status` to get current payment status (in should be `CANC`, cancelled).

# Postman tests

Postman is an easy testing environment, where you can simply import and run prepared tests. Download latest Postman application to use prepared Postman tests [here](https://www.getpostman.com/downloads/).

You can download Postman tests with environmental variables for all the endpoints by clicking "Download" button below.

After downloading the files, import them into Postman and start testing.
