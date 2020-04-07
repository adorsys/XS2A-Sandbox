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

<div class="divider">
</div>

# General questions

## Why does nothing happen when I press execute?

If you press the execute button and do not even receive any response code, then it is very likely that you have an invalid value in a header field such as a blank space at the beginning of the X-Request-ID.

Solution: Skim the header fields of your request and correct the red highlighted ones.

## What should I do if I still have container conflicts?

Sometimes the regular removing of the docker containers with _docker-compose rm -s -f -v is_ not enough. Still active containers can interfere the building of the services. In this case you should receive a similar error.

```
Creating xs2a-sandbox_certificate-generator_1 ... error
" is already in use by container "ff45d8d0cc3e4745d4b7e2122c750efd11322f6b62a2610cde48923262793444". You have to remove (or rename) that container to be able to reuse that name.

ERROR: for xs2a-sandbox_certificate-generator_1  Cannot start service certificate-generator: driver failed programming external connectiCreating xs2a-sandbox_fakesmtp_1              ... done
.0:8092 failed: port is already allocated

```

or

```
ERROR: for xs2a-consent-management  Cannot create container for service xs2a-consent-management: Conflict. The container name "/xs2a-conCreating ledgers                              ... error
or rename) that container to be able to reuse that name.
```

Solution: First of all you try to remove all containers from docker with following commands step by step

**Warning**: this will delete also all your created local data like registered TPPs:

1._docker rm -vf \$(docker ps -a -q)_

2._docker rmi -f \$(docker images -a -q)_

3._docker volume rm \$(docker volume ls -q)_

4._docker system prune -a -_ input _y_ to confirm

If building still fails, you need to manually kill the existing ports with _kill \$(lsof -t -i :PORT_NUMBER)_. Instead of PORT_NUMBER please write the number of the conflicting port, e.g. 8092 for the "Certificate Generator" or 38080 for the "Consent Management System". The port number should be mentioned in the error message, if there is just the name of the environment, you can simply find the corresponding port number in the ['Links to environments' table.](https://demo-dynamicsandbox-developerportalui.cloud.adorsys.de/getting-started)

In worst case you need to reboot you computer.

**Warning:** This will delete permanently all your local registered TPPs, users, accounts and transactions.

## Why does the PUT endpoint say that the service is not reachable (Error 403)?

This can happen if you decided to use the embedded sca approach for your test but have selected for e.g. the payment the redirect sca approach. Here it is crucial whether you have set _TPP-Redirect-Preferred_ to _true_ or _false_ earlier. In this case, you receive the error 403 as response by executing the PUT endpoint the first time (after entering the password JSON).

```
{
  "tppMessages": [
    {
      "category": "ERROR",
      "code": "SERVICE_BLOCKED",
      "text": "This service is not reachable for the addressed PSU due to a channel independent
       blocking by the ASPSP. Additional information might be given by the ASPSP"
    }
  ]
}

```

Solution: E.g. if in the earlier POST endpoint the _TPP-Redirect-Preferred_ is set on _true_, that means you can just finish this test with the received scaRedirect link. Consequently, you have to restart your test after setting _TPP-Redirect-Preferred_ value correct.

## How long is my TAN valid?

The received TAN is valid for your session. This means if you are automatically logged out, the TAN will be invalid and you have to restart the complete request.

## Why can't I finish my cancellation authorisation process?

You have initiated a payment, but you cannot cancel it successful, because you receive the error 400 as response:

```
{
  "tppMessages": [
    {
      "category": "ERROR",
      "code": "FORMAT_ERROR",
      "text": "Couldn't execute authorisation payment cancellation"
    }
  ]
}
```

Solution: It is very highly probable that the payment has already been executed, the reason can be that (1) the payment-product was set on instant-sepa-credit-transfers - this kind of payment cannot be cancelled because it will be executed directly with finishing the authorisation process - or (2) the server already executed the payment - the server has a constant time interval in which it completes and effectuates the bank transfers. In both cases, the payment has already been transferred and can therefore no longer be cancelled. To be sure, you can check the balance of the debtor account in the TPP UI or check with the GET endpoint /v1/{payment-service}/{payment-product}/{paymentId} the status of the payment you tried to cancel.

## Why does my Developer Portal customization not work?

If you have customized the Developer Portal local by e.g. changing the logo but you cannot see any changes if you run the services, you have to rebuild them first. The best is to use the command _make all_ to rebuild and run them directly. If it is still not working, check if you saved the necessary JSON in the "custom" folder and the correct image names are used in the file.
