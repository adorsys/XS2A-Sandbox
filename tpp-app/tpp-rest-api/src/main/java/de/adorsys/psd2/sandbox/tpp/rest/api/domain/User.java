package de.adorsys.psd2.sandbox.tpp.rest.api.domain;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class User {
    private String id;
    private String email;
    private String login;
    private String pin;
    private List<ScaUserData> scaUserData = new ArrayList<>();
    private List<UserRole> userRoles = new ArrayList<>();
    private List<AccountAccess> accountAccesses = new ArrayList<>();
}
