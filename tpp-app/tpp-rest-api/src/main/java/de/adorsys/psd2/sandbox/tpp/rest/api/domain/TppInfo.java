package de.adorsys.psd2.sandbox.tpp.rest.api.domain;

import lombok.Data;

@Data
public class TppInfo {
    private String id;
    private String login;
    private String pin;
    private String email;
}
