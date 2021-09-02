package de.adorsys.psd2.sandbox.cms.connector.api.domain;

import lombok.Data;

@Data
public class PsuIdDataInfo {
    private String psuId;
    private String psuIdType;
    private String psuCorporateId;
    private String psuCorporateIdType;
}
