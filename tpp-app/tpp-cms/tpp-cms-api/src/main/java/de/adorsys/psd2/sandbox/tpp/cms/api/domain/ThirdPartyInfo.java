package de.adorsys.psd2.sandbox.tpp.cms.api.domain;

import lombok.Data;

import java.util.List;

@Data
public class ThirdPartyInfo {
    private String authorisationNumber;
    private String tppName;
    private List<ThirdPartyRole> tppRoles;
    private String authorityId;
    private String authorityName;
    private String country;
    private String organisation;
    private String organisationUnit;
    private String city;
    private String state;
    private String issuerCN;
    private ThirdPartyRedirectUri cancelTppRedirectUri;
}
