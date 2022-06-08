# ModelBank

[![Build Status](https://api.travis-ci.com/adorsys/XS2A-Sandbox.svg?branch=master)](https://travis-ci.com/adorsys/XS2A-Sandbox)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=adorsys_XS2A-Sandbox&metric=alert_status)](https://sonarcloud.io/dashboard?id=adorsys_XS2A-Sandbox)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=adorsys_XS2A-Sandbox&metric=coverage)](https://sonarcloud.io/dashboard?id=adorsys_XS2A-Sandbox)

## Changes in ModelBank release policy

For the time being, version 5.14 will be the current try-out version of ModelBank that adorsys publishes on GitHub.
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

## Licensing model change to dual license: AGPL v.3 or commercial license

**Attention: This open-source project will change its licensing model as of 01.01.2022!**

Constantly evolving and extending scope, production traffic and support in open banking
world call for high maintenance and service investments on our part.

Henceforth, adorsys will offer all versions higher than v.5.14 &amp; v.4.11 of XS2A-Sandbox /
ModelBank under a dual-license model. Thus, this repository will be available either under
Affero GNU General Public License v.3 (AGPL v.3) or alternatively under a commercial license
agreement.

We always strive to provide the highest quality solutions for our users and customers. Our
solutions have helped many financial institutions to achieve the necessary regulatory
compliance, which places us among the most proven and reliable service providers in the
market.

We would like to thank all our users for their trust so far and are convinced that we will be
able to provide an even better service going forward.
For more information, advice for your XS2A implementation project or if your use case
requires more time to adapt this change, please contact us at psd2@adorsys.com.

For additional details please see the section **“FAQ on Licensing Change”**.

## What is it

With PSD2 Directive (EU) 2015/2366 of the European Parliament and of the Council on Payment Services in the Internal Market, published 25 November 2016
the European Union has forced Banking Market to open the Banking Services to Third Party Service Providers (TPP). These services are accessible by TPP on behalf of a Payment Service User (PSU).

The 'Berlin Group' is a pan-European payments interoperability standards and harmonisation initiative. Based on the PSD2 and EBA RTS requirements, Berlin Group NextGenPSD2 has worked on a detailed [Access to Account (XS2A) Framework](https://www.berlin-group.org/psd2-access-to-bank-accounts) with data model (at conceptual, logical and physical data levels) and associated messaging.

ModelBank is a dynamic sandbox environment that allows to emulate and test ASPSP's OpenAPI PSD2 services.

ModelBank is based on [XS2A Service](https://github.com/adorsys/xs2a), [XS2A-connector-examples](https://github.com/adorsys/xs2a-connector-examples) and [Ledgers](https://github.com/adorsys/ledgers).

With ModelBank [TPP UI](https://github.com/adorsys/XS2A-Sandbox/tree/master/tpp-ui), you as a testing TPP can access banking APIs directly, get TPP certificates and manage testing accounts.

[Developer Portal](https://github.com/adorsys/XS2A-Sandbox/tree/master/developer-portal-ui) contains testing instructions and all of the necessary documentation.

All four SCA approaches are supported: REDIRECT, OAUTH, EMBEDDED, DECOUPLED. Two of them (REDIRECT, EMBEDDED) are directly testable on Developer portal. For the Redirect SCA Approach an [Online Banking UI](https://github.com/adorsys/XS2A-Sandbox/tree/master/oba-ui) is used for authorisation.

![ModelBank structure](https://github.com/adorsys/XS2A-Sandbox/blob/master/XS2ASandbox.png)

## Project documentation

-   [Release notes](https://github.com/adorsys/XS2A-Sandbox/tree/master/docs/release_notes) contain information about changes included into releases.
-   [User Guide](https://github.com/adorsys/XS2A-Sandbox/tree/master/docs/user-guide.md) describes how to configure the ModelBank.
-   [UI Customization guide](https://github.com/adorsys/XS2A-Sandbox/blob/master/docs/customization_guide/UIs_customization_guide.md) describes how to customize Developer Portal, Online Banking UI and TPP UI.
-   [Architecture Documentation](https://github.com/adorsys/XS2A-Sandbox/blob/master/docs/arc42/README.adoc) describes how to instal, create and use tools for our diagrams.

## How to try it

-   [Running ModelBank instructions](https://github.com/adorsys/XS2A-Sandbox/tree/master/docs/running-modelbank.md) will help you getting you a copy of the project up and running on your local machine.

## Version policy

In general ModelBank Team follows [SemVer](https://semver.org/) for versioning. This means our versions follow the model A.B.C, where:

-   A - is the major version, pointing out mainline.

-   B - is the minor version, pointing out the next release in the mainline.

-   C - is the hotfix version, used to deliver patches between releases when needed. If omitted, version 4.5 will be considered equal to 4.5.0.

## Development and contributing

Any person is free to join us by implementing some parts of code or fixing some bugs and making a merge requests for them.

[Contribution Guidelines](https://github.com/adorsys/XS2A-Sandbox/tree/master/docs/Contribution-Guidelines.md) describe internal development process and how to contribute to the ModelBank.

## Contact

If you think that our system behaves in an unexpected way or incorrect, or you need some clarifications, to contact ModelBank Team please [create an issue](https://github.com/adorsys/XS2A-Sandbox/issues). Team will provide comments and feedback there.

For commercial support please contact [adorsys Team](https://adorsys-platform.de/solutions/).

## License

This project is licensed under the Apache License version 2.0 **(until 01.01.2022)** - see the [LICENSE](https://github.com/adorsys/XS2A-Sandbox/blob/master/LICENSE) file for details.

## FAQ on Licensing Change

**What is a dual-licensing model?**

Under a dual-licensing model, our product is available under two licenses:

1. The Affero GNU General Public License v3 (AGPL v3).
2. A proprietary commercial license.

If you are a developer or business that would like to review our products in detail, test and
implement in your open-source projects and share the changes back to the community, the product
repository is freely available under AGPL v3.

If you are a business that would like to implement our products in a commercial setting and would
like to protect your individual changes, we offer the option to license our products under a
commercial license.

This change will still allow free access and ensure openness under AGPL v3 but with assurance of
committing any alterations or extensions back to the project and preventing redistribution of such
implementations under commercial license.

**Will there be any differences between the open-source and commercially licensed versions of your
products?**

Our public release frequency will be reduced as our focus shifts towards the continuous
maintenance of the commercial version. Nevertheless, we are committed to also provide
open-source releases of our products on a regular basis as per our release policy.

For customers with a commercial license, we will offer new intermediate releases in a more
frequent pace.

**Does this mean that this product is no longer open source?**

No, the product will still be published and available on GitHub under an OSI-approved open-source
license (AGPL v3).

**What about adorsys’ commitment to open source? Will adorsys provide future product releases on
GitHub?**

We at adorsys are committed to continue actively participating in the open-source community. Our
products remain licensed under OSI-approved open-source licenses, and we are looking forward to
expanding our product portfolio on GitHub even further.

**How does the change impact me if I already use the open-source edition of your product?**

All currently published versions until v.5.14 &amp; v.4.11 will remain under their current Apache 2.0
license and its respective requirements and you may continue using it as-is. To upgrade to future
versions, you will be required to either abide by the requirements of AGPL v3, including documenting
and sharing your implemented changes to the product when distributing, or alternatively approach
us to obtain a commercial license.

**What if I cannot adjust to the new licensing model until 01.01.2022? Can I extend the deadline?**

We understand that adjustment to licensing changes can take time and therefore are open to discuss
extension options on an individual basis. For inquiries please contact us as psd2@adorsys.com.

**Which versions of the product are affected?**

All versions of XS2A-Sandbox / Modelbank starting after v.5.14 &amp; v.4.11 will be affected by the
licensing changes and move to a dual-licensing model.

**What will happen to older, Apache 2.0 licensed product versions?**

All older Apache 2.0 licensed versions prior and including v.5.14 &amp; v.4.11 will remain available
under their existing license.

**What open-source products from Adorsys are affected by the licensing change?**
The following products are affected:

-   XS2A Core,
-   XS2A Sandbox and ModelBank,
-   Open Banking Gateway incl. XS2A Adapters,
-   SmartAnalytics,
-   Datasafe.

**I’m using one of these products indirectly via some software integrator. How does the licensing
change affect me?**

The licensing change does not affect you as user, but it is relevant to your provider who has used our
product in their solution implementation. In case of uncertainty please contact your service provider
or approach us at psd2@adorsys.com.
