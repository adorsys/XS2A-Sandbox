# Frequently Asked Questions

---

## Consents

**Why can't I create a consent?**\
The role within the QWAC Certificate does not contain the role AIS.
In this case you should receive a similar error:

```json
{
  "timestamp": 1549441548991,
  "status": 403,
  "error": "Forbidden",
  "message": "You don't have access to this resource",
  "path": "/v1/consents"
}
```

Solution: Either create a new certificate containing the role AIS and
embed this certificate in your request or decode your certificate and
check the qcStatement which should contain "PSP_AI".

Another possibility is that the attribute "recurringIndicator" within
your request is set to "false", while the attribute "frequencyPerDay"
does not equal "1". By default the "recurringIndicator" should be "true"
since a TPP wants to access the account data in the addressed period of
time. When the TPP however wants to just display a list of possible
accounts to the PSU, a one-time access is enough.
In this case you should receive the following Error:

```json
{
  "tppMessages": [
    {
      "category": "ERROR",
      "code": "FORMAT_ERROR",
      "path": null,
      "text": "Format of certain request fields are not matching the XS2A requirements."
    }
  ],
  "_links": null
}
```

Solution: If you want to access the account data using this consent just
once, change the value of "frequencyPerDay" to "1". Otherwise assign
"true" to the "recurringIndicator".

**Why can't I access my account data?**\
It is possible that the issued consent does not permit this operation.
An AIS-Consent defines three levels of access. The first level allows
access to "accounts", the second to "balances" and the third to
"transactions". Combinations are possible and a selection of either
"balances" or "transactions" grants access to "accounts" as well. A
consent granting access to e.g. accounts and transactions does not allow
to call the balance endpoint.

In this case you should receive the following error:

TODO

Solution: Ask the PSU to create a new consent allowing you access the
appropriate endpoints.

Another reason might be that the consent status is not valid. A status
will e.g. expire automatically when the "expirationDate" defined within
the consent is exceeded. It might also be possible that an error
occurred while performing SCA. For more detailed information see FAQs on
Strong Customer Authentication.

## Strong Customer Authentication

**How can I change the transaction/consent status?**\
By default a new created transaction/consent has the status "received".
In order to perform SCA using REDIRECT approach, the sandbox provides a
Redirect Server where the status gets updated depending on the PSU.
Since SCA for REDIRECT is simplified for this sandbox, it is enough to
pass a PSU-ID via Query-Parameter to simulate the whole SCA. An example
is provided in the following:

`GET https://sandbox-api.dev.adorsys.de/v1/online-banking/init/pis/5239763c-8641-41cc-a354-d52d35da0c9e?psu-id=PSU-Successful`

The Query Parameter psu-id is mandatory. If it is not provided, the
Redirect Server will display a default error message.

**Why can't I change the transaction/consent status using a specific PSU?**\
If the transaction/consent status is "received", it can be possible that
the PSU-ID does not match IBAN in the payment initiation or consent
creation request. The mapping between PSU-ID and IBAN(s) is documented
in the developer portal.
If the status doesn't change even though SCA was performed and the IBAN
matches the PSU-ID, check the PSU-ID for typos and case sensitivity.

## Certificates

**How can I create a valid certificate?**\
In order to access the XS2A API, a TPP needs to have a valid Qualified
Website Authentication Certificate (QWAC) which usually gets issued by a
registered Trust Service Provider. For the use of this sandbox you can
issue test certificates here:
[https://sandbox-portal.dev.adorsys.de/app/certificate-service](https://sandbox-portal.dev.adorsys.de/app/certificate-service).
You get a self signed certificate (.pem) and a corresponding private
key (.key) embedded in a .zip file.

**How can I embed a certificate in a request?**\
Once you received a QWAC you need to embed it to your request. There are
many tools to perform REST calls. We explain configuration with two of
the most common ones - cURL and Postman.
cURL: add the .pem file using `--cert ./certificate.p` and the private
key using `--key ./private.key`. Note that you may need to adapt the
relative path to your files.
Postman: Navigate to "Preferences > Certificates > Add Certificate" and
set the host url (in our examples this would be
[https://sandbox-api.dev.adorsys.de](https://sandbox-api.dev.adorsys.de)).
Note that "https://" as well as the port 443 are already set. Then add
your .pem and .key file. The passphrase must be left empty.

**Why does my QWAC certificate not work?**\
Requests might still not work even though a certificate is added. This
happens when certificates expire. Each certificate has an attribute
"validUntil" with the expiration date.
In this case you should receive the following error:

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

You need the PSU to create a new consent and use the new consentId.
Another possibility is that the certificate does not contain the role
you need for your request. E.g. having the role "PIS" does not permit
you to create consents.
In such a case you should receive the following error:

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

Solution: Either create a new certificate containing the role AIS and
embed this certificate in your request or decode your certificate and
check the qcStatement which should contain "PSP_AI" in this case.
