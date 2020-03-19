<div class="centeredText">

# Frequently Asked Questions

</div>

<div class="divider">
</div>

# Consents

## Why can't I create a consent?

The role within the QWAC Certificate does not contain the role AIS. In this case you should receive a similar error:

```json
{
  "timestamp": 1549441548991,
  "status": 403,
  "error": "Forbidden",
  "message": "You don't have access to this resource",
  "path": "/v1/consents"
}
```

Solution: Either create a new certificate containing the role AIS and embed this certificate in your request or decode your certificate and check the qcStatement which should contain "PSP_AI".

Another possibility is that the attribute "recurringIndicator" within your request is set to "false", while the attribute "frequencyPerDay" does not equal "1". By default the "recurringIndicator" should be "true" since a TPP wants to access the account data in the addressed period of time. When the TPP however wants to just display a list of possible accounts to the PSU, a one-time access is enough. In this case you should receive the following Error:

```json
{
  "tppMessages": [
    {
      "category": "ERROR",
      "code": "FORMAT_ERROR",
      "path": "null",
      "text": "Format of certain request fields are not matching the XS2A requirements."
    }
  ],
  "_links": null
}
```

Solution: If you want to access the account data using this consent just once, change the value of "frequencyPerDay" to "1". Otherwise assign "true" to the "recurringIndicator".

## Why can't I access my account data?

It is possible that the issued consent does not permit this operation. An AIS-Consent defines three levels of access. The first level allows access to "accounts", the second to "balances" and the third to "transactions". Combinations are possible and a selection of either "balances" or "transactions" grants access to "accounts" as well. A consent granting access to e.g. accounts and transactions does not allow to call the balance endpoint.
Solution Ask the PSU to create a new consent allowing you access the appropriate endpoints.

Another reason might be that the consent status is not valid. A status will e.g. expire automatically when the "expirationDate" defined within the consent is exceeded. It might also be possible that an error occurred while performing SCA. For more detailed information see FAQs on Strong Customer Authentication.

<div class="divider">
</div>

# Strong Customer Authentication

## How can I change the transaction/consent status?

By default a new created transaction/consent has the status RECEIVED. In order to perform SCA using REDIRECT approach, the sandbox provides a Redirect Server where the status gets updated depending on the PSU. Since SCA for REDIRECT is simplified for this sandbox, it is enough to pass a PSU-ID via Query-Parameter to simulate the whole SCA.
The Query Parameter psu-id is mandatory. If it is not provided, the Redirect Server will display a default error message.

## Why can't I change the transaction/consent status using a specific PSU?

If the transaction/consent status is "received", it can be possible that the PSU-ID does not match IBAN in the payment initiation or consent creation request. The mapping between PSU-ID and IBAN(s) is documented in the developer portal. If the status doesn't change even though SCA was performed and the IBAN matches the PSU-ID, check the PSU-ID for typos and case sensitivity.

<div class="divider">
</div>

# Certificates

## How can I create a valid certificate?

In order to access the XS2A API, a TPP needs to have a valid Qualified Website Authentication Certificate (QWAC) which usually gets issued by a registered Trust Service Provider. You get a self signed certificate _(.pem)_ and a corresponding private key _(.key)_ embedded in a _.zip_ file.

## How can I embed a certificate in a request?

Once you received a QWAC you need to embed it to your request. There are many tools to perform REST calls. We explain configuration with two of the most common ones - cURL and Postman. cURL: add the _.pem_ file using _--cert ./certificate.p_ and the private key using _--key ./private.key_. Note that you may need to adapt the relative path to your files. Postman: Navigate to "Preferences > Certificates > Add Certificate" and set the host url. Note that "https://" as well as the port 443 are already set. Then add your _.pem_ and _.key_ file. The passphrase must be left empty.

## Why does my QWAC certificate not work?

Requests might still not work even though a certificate is added. This happens when certificates expire. Each certificate has an attribute "validUntil" with the expiration date. In this case you should receive the following error:

```json
{
  "tppMessages": [
    {
      "category": "ERROR",
      "code": "CONSENT_EXPIRED",
      "path": null,
      "text": "The consent was created by this TPP but has expired and needs to be renewed"
    }
  ],
  "_links": null
}
```

You need the PSU to create a new consent and use the new consentId. Another possibility is that the certificate does not contain the role you need for your request. E.g. having the role "PIS" does not permit you to create consents. In such a case you should receive the following error:

```json
{
  "tppMessages": [
    {
      "category": "ERROR",
      "code": "CONSENT_INVALID",
      "path": null,
      "text": "The consent was created by this TPP but is not valid for the addressed service/resource"
    }
  ],
  "_links": null
}
```
