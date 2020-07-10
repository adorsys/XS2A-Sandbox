with psuIds as
       (select distinct id from psu_data where psu_id in ?1),

     consentIdsFilteredByPsuIds as
       (select distinct consent_id
        from consent_psu_data
        where psu_data_id in
              (select * from psuIds)),

     deleteAccountAccess as
       (delete from account_access where consent_id in
                                         (select * from consentIdsFilteredByPsuIds)),
     deleteAisAccountAccess as
       (delete from ais_account_access where consent_id in
                                             (select * from consentIdsFilteredByPsuIds)),
     deleteAisAspspAccountAccess as
       (delete from ais_aspsp_account_access where consent_id in
                                                   (select * from consentIdsFilteredByPsuIds)),
     deleteAisConsent as
       (delete from ais_consent where id in
                                      (select * from consentIdsFilteredByPsuIds)),

     consentCommonPsuDataIds as
       (select psu_data_id
        from consent_psu_data
        where consent_id in
              (select * from consentIdsFilteredByPsuIds)),
     deleteConsentCommonPsuData as
       (delete from psu_data where id in (select * from consentCommonPsuDataIds)),

     deleteAisConsentPsuData as
       (delete from ais_consent_psu_data where ais_consent_id in
                                               (select * from consentIdsFilteredByPsuIds)),
     deleteAisConsentTransaction as
       (delete from ais_consent_transaction where consent_id in
                                                  (select * from consentIdsFilteredByPsuIds)),
     deleteAisConsentUsage as
       (delete from ais_consent_usage where consent_id in
                                            (select * from consentIdsFilteredByPsuIds)),
     deleteAspspAccountAccess as
       (delete from aspsp_account_access where consent_id in
                                               (select * from consentIdsFilteredByPsuIds)),

     externalConsentIds as (select external_id
                            from consent c
                            where c.consent_id in (select * from consentIdsFilteredByPsuIds)),

     deleteAspspConsentData as
       (delete from aspsp_consent_data acd where acd.consent_id in
                                                 (select * from externalConsentIds)),

     authorisationIds as
       (select authorisation_id
        from authorisation
        where parent_id in
              (select * from externalConsentIds)),

     deleteAuthAvailableScaMethod as
       (delete from auth_available_sca_method where authorisation_id in
                                                    (select * from authorisationIds)),
     deleteAuthorisation as
       (delete from authorisation where authorisation_id in
                                        (select * from authorisationIds)),

     deleteConsentPsuData as
       (delete from consent_psu_data where consent_id in
                                           (select * from consentIdsFilteredByPsuIds)),
     deleteConsentUsage as
       (delete from consent_usage where consent_id in
                                        (select * from consentIdsFilteredByPsuIds)),

     authorisationTemplateIds as
       (select authorisation_template_id
        from consent
        where consent_id in (select * from consentIdsFilteredByPsuIds)),

     deleteAuthorisationTemplate as
       (delete from authorisation_template where authorisation_template_id in (select * from authorisationTemplateIds))

delete
from consent
where consent_id in (select * from consentIdsFilteredByPsuIds);
