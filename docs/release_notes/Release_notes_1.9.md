# Release notes v.1.9

Current version consists of following components:
1. **Ledgers v.2.3**
2. **XS2A v.5.5**
3. **ASPSP-Profile v.5.5**
4. **Consent-Management v.5.5**
4. **XS2A Connector-Examples v.5.5**
 
- Fixed double TAN sending during payment cancellation
- Added docker compose with embedded connector/consent-management/aspsp-profile (approximate RAM usage reduced by 1,5 Gb)
- Added multi-currency support to Sandbox
- Fixed bug with impossibility to register TPP after deleting the old one
- Added configuration of approaches and languages for Developer Portal
- Made jsons for Developer Portal configurable
- Migrated to XS2A ver 5.5
- Added more documentation on customisation for Developer Portal
- Added drop-down select menu for embedded consents at Play With Data on Developer Portal
- Migrated to Ledgers 2.3
- Added country selection for TPP creation, to provide valid IBAN generation afterwards
- Support of different currencies for accounts in TPP-UI
- TPP-UI is now capable to generate IBANs for different countries (mostly EU) depending on TPP selection upon creation of new TPP
- Added currency selection to Generate Test Data section
- Made currencies configurable at Developer Portal
- Refactored Online Banking back-end to provide more flexibility and modularity
- Added Sonar support for Sandbox back-end components
