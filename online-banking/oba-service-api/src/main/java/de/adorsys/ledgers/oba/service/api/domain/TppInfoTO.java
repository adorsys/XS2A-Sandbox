package de.adorsys.ledgers.oba.service.api.domain;

import de.adorsys.psd2.xs2a.core.tpp.TppRole;
import lombok.Data;

import java.util.List;

@Data
public class TppInfoTO {
    private Long id;
    private String authorisationNumber;
    private String tppName;
    private List<TppRole> tppRoles;
    private String authorityId;
    private String authorityName;
    private String country;
    private String organisation;
    private String organisationUnit;
    private String city;
    private String state;
}
