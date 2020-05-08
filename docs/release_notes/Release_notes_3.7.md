# Release notes v.3.7

Current version consists of following components:

1. **Ledgers v.3.3** <-------------------  !!!  TO BE UPDATED !!! -------------------------->
2. **XS2A v.7.3**
3. **ASPSP-Profile v.7.3**
4. **Consent-Management v.7.3**
5. **XS2A Connector-Examples v.7.3**

- Added possibility to restrict TPP self-registration through property `app.endpoints.tpp.self.registration.disabled: true` default is `false` - self-registration enabled
- Added `Tpp Administration GUI` with following features: 
        
        - view full list of TPPs and their users and accounts, with all sorts of filters (by tpp id or login / by user login / by country code / by block status)
        - block/unblock TPPs
        - register new TPPs
        - veiw/edit TPPs details
        - reset TPPs passwords
        - remove TPPs and all their contents
