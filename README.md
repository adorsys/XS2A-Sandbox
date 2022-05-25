# XS2ASandbox

[![Build Status](https://api.travis-ci.com/adorsys/XS2A-Sandbox.svg?branch=master)](https://travis-ci.com/adorsys/XS2A-Sandbox)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=adorsys_XS2A-Sandbox&metric=alert_status)](https://sonarcloud.io/dashboard?id=adorsys_XS2A-Sandbox)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=adorsys_XS2A-Sandbox&metric=coverage)](https://sonarcloud.io/dashboard?id=adorsys_XS2A-Sandbox)

## Changes in ModelBank release policy

For the time being, version 4.11 will be the current try-out version of ModelBank that adorsys publishes on GitHub.
With XS2A standards and our solutions having grown to maturity in production, our continuous investment in development
and maintenance of our XS2A solutions forces us to focus on our commercial engagements.
We are committed to continuous active development of our XS2A solutions to ensure constant adherence to the latest Berlin Group
specifications and to support OpenFinance initiatives.
Existing published versions will remain available under their respective open-source licenses.
If you are a user of our XS2A solutions and would like to either start or extend cooperation with us, we would be glad
to provide you with the latest versions of ModelBank and support you in its implementation.
For that or any other inquiries please contact us under psd2@adorsys.com.

**Note that our commercial release of ModelBank has the following improvements:**

| Module                           | ModelBank commercial version features                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  |
| -------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| TPP-UI, Admin-UI                 | <ul><li> **1.** Refactored TPP-UI and TPP backend application (admin functionality was moved to new modules), updated documentation on Developer Portal.<li> **2.** Added new modules:<ul><li>`admin-app` - backend module for Admin UI <li>`admin-ui` - frontend component for managing administrators, TPPs and users<li>`cms-connector` - common functionality for `admin-app` and `tpp-app` for accessing CMS DB</li></ul></ul>The above changes by separating TPP-UI and Admin-UI were made in architecture to improve the security of ModelBank. |
| Ledgers                          | <ul><li>**3.** Ledgers updates:<ul><li>added logs during startup <li>renamed `EMAIL` SCA method to `SMTP_OTP` <li> added ability to create payments without `endToEndIdentification` <li> added new fields to payments and transactions data models</ul>                                                                                                                                                                                                                                                                                               |
| Online Banking App (OBA), TPP-UI | <ul><li>**4.** New features: <ul><li> payment initiation without IBAN with further IBAN selection via Online Banking App <li>support of PIIS ASPSP consent in TPP-UI and Online Banking App</ul></ul>                                                                                                                                                                                                                                                                                                                                                  |
| Misc.                            | <ul><li>**5.** Numerous UI improvements and bug fixes across all applications. </ul>                                                                                                                                                                                                                                                                                                                                                                                                                                                                   |

## What is it

With PSD2 Directive (EU) 2015/2366 of the European Parliament and of the Council on Payment Services in the Internal Market, published 25 November 2016
the European Union has forced Banking Market to open the Banking Services to Third Party Service Providers (TPP). These services are accessible by TPP on behalf of a Payment Service User (PSU).

The 'Berlin Group' is a pan-European payments interoperability standards and harmonisation initiative. Based on the PSD2 and EBA RTS requirements, Berlin Group NextGenPSD2 has worked on a detailed [Access to Account (XS2A) Framework](https://www.berlin-group.org/psd2-access-to-bank-accounts) with data model (at conceptual, logical and physical data levels) and associated messaging.

XS2ASandbox is a dynamic sandbox environment that allows to emulate and test ASPSP's OpenAPI PSD2 services.

XS2ASandbox is based on [XS2A Service](https://github.com/adorsys/xs2a), [XS2A-connector-examples](https://github.com/adorsys/xs2a-connector-examples) and [Ledgers](https://github.com/adorsys/ledgers).

With XS2ASandbox [TPP UI](https://github.com/adorsys/XS2A-Sandbox/tree/master/tpp-ui), you as a testing TPP can access banking APIs directly, get TPP certificates and manage testing accounts.

[Developer Portal](https://github.com/adorsys/XS2A-Sandbox/tree/master/developer-portal-ui) contains testing instructions and all of the necessary documentation.

All four SCA approaches are supported: REDIRECT, OAUTH, EMBEDDED, DECOUPLED. Two of them (REDIRECT, EMBEDDED) are directly testable on Developer portal. For the Redirect SCA Approach an [Online Banking UI](https://github.com/adorsys/XS2A-Sandbox/tree/master/oba-ui) is used for authorisation.

![XS2ASandbox structure](https://github.com/adorsys/XS2A-Sandbox/blob/master/XS2ASandbox.png)

## Project documentation

-   [Release notes](https://github.com/adorsys/XS2A-Sandbox/tree/master/docs/release_notes) contain information about changes included into releases.
-   [User Guide](https://github.com/adorsys/XS2A-Sandbox/tree/master/docs/user-guide.md) describes how to configure XS2ASandbox.
-   [UI Customization guide](https://github.com/adorsys/XS2A-Sandbox/blob/master/docs/customization_guide/UIs_customization_guide.md) describes how to customize Developer Portal, Online Banking UI and TPP UI.
-   [Architecture Documentation](https://github.com/adorsys/XS2A-Sandbox/blob/master/docs/arc42/README.adoc) describes how to instal, create and use tools for our diagrams.

## How to try it

-   [Running XS2ASandbox instructions](https://github.com/adorsys/XS2A-Sandbox/tree/master/docs/running-xs2asandbox.md) will help you getting you a copy of the project up and running on your local machine.

## Version policy

In general XS2ASandbox Team follows [SemVer](https://semver.org/) for versioning. This means our versions follow the model A.B.C, where:

-   A - is the major version, pointing out mainline.

-   B - is the minor version, pointing out the next release in the mainline.

-   C - is the hotfix version, used to deliver patches between releases when needed. If omitted, version 4.5 will be considered equal to 4.5.0.

## Development and contributing

Any person are free to join us by implementing some parts of code or fixing some bugs and making a merge requests for them.

[Contribution Guidelines](https://github.com/adorsys/XS2A-Sandbox/tree/master/docs/Contribution-Guidelines.md) describe internal development process and how to contribute to XS2ASandbox.

## Contact

If you think that our system behaves in an unexpected way or incorrect, or you need some clarifications, to contact XS2ASandbox Team please [create an issue](https://github.com/adorsys/XS2A-Sandbox/issues). Team will provide comments and feedback there.

For commercial support please contact [adorsys Team](https://adorsys-platform.de/solutions/).

## License

This project is licensed under the Apache License version 2.0 - see the [LICENSE](https://github.com/adorsys/XS2A-Sandbox/blob/master/LICENSE) file for details.
