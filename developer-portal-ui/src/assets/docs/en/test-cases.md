# Test cases

After loading test data into the system, TPP developer can test XS2ABank endpoints with Swagger or Postman tests.

In **Swagger section** you can find information about usage of Swagger, prepared test data (_Jsons_) for testing and short API documentation. In [Postman tests section](LINK TO POSTMAN SECTION) you can find information about usage of Postman for testing and download prepared Postman tests with testing data.

Both sections provide tests for REDIRECT Strong Customer Authorisation (SCA) approach and EMBEDDED SCA Approach for Payment Initiation Service (PIS), Account Information Service (AIS) and Payment Instrument Issuer Service (PIIS).

### REDIRECT approach

Short overview of REDIRECT aaproach:

- The whole SCA process with two different factors (e.g. username/password as proof of knowledge and a one time password (TAN) as proof of possession) is provided by the ASPSP and executed directly between PSU and ASPSP.
- Therefore, the PSU needs to gets redirected from PISP to ASPSP.
- The SCA of the PSU is executed directly between the ASPSP and the PSU.
- After completion of the SCA the PSU gets redirected back to the PISP.

![REDIRECT](assets/redirect_pis_initiation.svg 'Figure 1.1: Payment initiation in REDIRECT')
\_Figure 1.1: Payment initiation diagram in redirect

### EMBEDDED approach

![REDIRECT](assets/embedded_pis_initiation.svg 'Figure 1.1: Payment initiation in EMBEDDED')
\_Figure 1.1: Payment initiation diagram in redirect

## Test XS2ABank with Swagger

### Testing flows

You can use several typical testing flows which are described in pictures below:

### XS2A Interface API

#### REDIRECT approach

##### PIS

##### AIS

##### PIIS

#### EMBEDDED approach

##### PIS

##### AIS

##### PIIS

## Postman tests

Postman is an easy testing environment, where you can simply import and run prepared tests. Download latest Postman application to use prepared Postman tests [here](https://www.getpostman.com/downloads/).

You can download Postman tests with environmental variables for all the endpoints by clicking "Download" button below.

After downloading the files, import them into Postman and start testing.
