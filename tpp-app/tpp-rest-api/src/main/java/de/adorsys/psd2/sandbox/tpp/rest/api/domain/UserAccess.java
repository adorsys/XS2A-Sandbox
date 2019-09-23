package de.adorsys.psd2.sandbox.tpp.rest.api.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAccess {
    private String userLogin;
    private int scaWeight;
}
