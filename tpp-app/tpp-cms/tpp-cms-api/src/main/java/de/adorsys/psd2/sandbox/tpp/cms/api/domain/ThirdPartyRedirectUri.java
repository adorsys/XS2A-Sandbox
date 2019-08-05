package de.adorsys.psd2.sandbox.tpp.cms.api.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThirdPartyRedirectUri {
    private String uri;
    private String nokUri;
}
