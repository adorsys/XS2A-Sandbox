# XS2ASandbox
[![Build Status](https://api.travis-ci.com/adorsys/XS2A-Sandbox.svg?branch=master)](https://travis-ci.com/adorsys/XS2A-Sandbox)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=adorsys_XS2A-Sandbox&metric=alert_status)](https://sonarcloud.io/dashboard?id=adorsys_XS2A-Sandbox)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=adorsys_XS2A-Sandbox&metric=coverage)](https://sonarcloud.io/dashboard?id=adorsys_XS2A-Sandbox)


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

* [Release notes](https://github.com/adorsys/XS2A-Sandbox/tree/master/docs/release_notes) contain information about changes included into releases.
* [User Guide](https://github.com/adorsys/XS2A-Sandbox/tree/master/docs/user-guide.md) describes how to configure XS2ASandbox.
* [UI Customization guide](https://github.com/adorsys/XS2A-Sandbox/blob/master/docs/customization_guide/UIs_customization_guide.md) describes how to customize Developer Portal, Online Banking UI and TPP UI.
* [Architecture Documentation](https://github.com/adorsys/XS2A-Sandbox/tree/master/docs/arc42/README.adoc) describes how to instal, create and use tools for our diagrams. 


## How to try it

* [Running XS2ASandbox instructions](https://github.com/adorsys/XS2A-Sandbox/tree/master/docs/running-xs2asandbox.md) will help you getting you a copy of the project up and running on your local machine.

## Development and contributing

Any person are free to join us by implementing some parts of code or fixing some bugs and making a merge requests for them.

[Contribution Guidelines](https://github.com/adorsys/XS2A-Sandbox/tree/master/docs/Contribution-Guidelines.md) describe internal development process and how to contribute to XS2ASandbox.

## Contact

If you think that our system behaves in an unexpected way or incorrect, or you need some clarifications, to contact XS2ASandbox Team please [create an issue](https://github.com/adorsys/XS2A-Sandbox/issues). Team will provide comments and feedback there.

For commercial support please contact [adorsys Team](https://adorsys-platform.de/solutions/).

## License

This project is licensed under the Apache License version 2.0 - see the [LICENSE](https://github.com/adorsys/XS2A-Sandbox/blob/master/LICENSE) file for details.
