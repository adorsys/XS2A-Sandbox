package de.adorsys.psd2.sandbox.tpp.rest.api.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
    private String id;
    private String email;
    private String login;
    private String pin;
    private List<ScaUserData> scaUserData = new ArrayList<>();
    private List<UserRole> userRoles = new ArrayList<>();
    private List<AccountAccess> accountAccesses = new ArrayList<>();
}
