# Release notes v.1.0
This is a first release of **PSD2 Dynamic Sandbox**

Current version consists of following components:
1. **Ledgers v.0.3.8**
2. **XS2A v.2.1.1**
3. **ASPSP-Profile v.2.1.1**
4. **Consent-Management v.2.1.1**
5. **XS2A-Gateway v.0.3**
6. **TPP-UI v.1.0**
7. **Online-Banking-UI v.1.0**
8. **TPP-Certificate-Generator v.1.0**
9. **Developer Portal v.1.0**

# Features
## Ledgers
Ledgers represent a dynamic emulation of a banking back-end
### Features:
* User Management System:
    * Creation of users with roles (CUSTOMER, STAFF, TECHNICAL, SYSTEM)
    * Assigning account accesses
    * Adding/Modifying account accesses and SCA methods
* Security Service:
    * Bearer Token based Security (refresh token is not supported)
    * SCA validation (Currently only e-mail TAN method supported)
* Accounting Service:
    * A full Ledger Bookkeeping system with separate accounting. Supporting Postings with PostingLines.
    * PostingLines secured with blockchain mechanism
    * Multiple accounts per user supported
    * Retrieving of account balances and transactions by dates/account/transaction id
    * Funds confirmation service
    * Cash depositing
* Payment Service:
    * Instant/Delayed payments are supported
    * Single/Periodic/Bulk payments supported
    * Bulk payments with batch and separate booking supported
    * Payment execution scheduler for future/delayed/periodic payments featuring on demand business dates scheduling and execution
    * Payment cancellation supporting multi step authorisation  
* TPP Service:
    * Support of Branches (Separate branches inside Ledgers for different TPPs with access limited to accounts and users created by current TPP). 
    * User/Account creation on behalf of TPP 
    * Additional features like: get all Accounts/Users for current branch.
    * Bulk Test data upload for TPP (Users/Accounts/Balances for testing purposes. Only YAML format currently supported. **Warning!** Data can not be overwritten due to blockchain transaction security, data is only updated if already present, in case of balances money is deposited if value at ledger is less than one set in uploaded file).
    * NISP Test data generation with automatic application of generated data over ledgers in on behalf of current TPP. A file is also exposed for downloading. 

#### Updates in version v.0.3.8:
* Multi-level SCA for PIS and AIS
* Added **develop** profile:
   * TAN generation Service is mocked with “123456” TAN.
   * Test Users and accounts are created at Ledgers
If **develop** profile is not found among active stat profiles Ledgers will start in production/demo mode with Production TAN Generation Service (random TAN) and without test users and accounts.

## XS2A
See xs2a release notes
## ASPSP-Profile
See aspsp-profile release notes
## Consent-Management
See cms release notes
## XS2A-Gateway
Implementation of xs2a interface for Ledgers banking back-end
* All currently supported xs2a features from v.2.1.1 except: Multi-currency accounting and Multi Level SCA
## TPP-UI
Angular front-end application for TPP services of Ledgers
## Online-Banking-UI
Angular front-end application for implementation of Redirect approach.

### Support of dedicated and bank-offered account consent

UI for AIS supports both dedicated and bank-offered account consent. In case of dedicated accounts consent UI shows only account accesses
stored in consent. In case of bank-offered account consent user can choose ibans and accesses for the consent. The status of the consent 
is updated from 'RECEIVED' to 'VALID' in Consent-Management in case of successful authorisation.


###TPP-Certificate-Generator
Utility to generate TPP Qualified Website Authentication Certificate to grant TPP the access to XS2A API
