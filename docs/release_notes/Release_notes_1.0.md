# Release notes v.1.0
This is a first release of **PSD2 Dynamic Sandbox**

Current version consists of following components:
1. **Ledgers v.0.3.6.1**
2. **XS2A v.2.1.1**
3. **ASPSP-Profile v.2.1.1**
4. **Consent-Management v.2.1.1**
5. **XS2A-Gateway v.0.3**
6. **TPP-UI v.1.0**
7. **Online-Banking-UI v.1.0**
8. **TPP-Certificate-Generator**
9. **Developer Portal**

## Features
###Ledgers
Ledgers represent a dynamic emulation of a banking back-end
#### Features:
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
###XS2A
See xs2a release notes
###ASPSP-Profile
See aspsp-profile release notes
###Consent-Management
See cms release notes
###XS2A-Gateway
Implementation of xs2a interface for Ledgers banking back-end
* All currently supported xs2a features from v.2.1.1 except: Multi-currency accounting and Multi Level SCA
###TPP-UI
Angular front-end application for TPP services of Ledgers
###Online-Banking-UI
Angular front-end application for implementation of Redirect approach
###Features:
* AIS Bank Offered Consent authorisation and validation
###TPP-Certificate-Generator
Utility to generate TPP Qualified Website Authentication Certificate to grant TPP the access to XS2A API
