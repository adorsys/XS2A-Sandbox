package de.adorsys.ledgers.oba.service.api.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LoginFailedCount {
    private int failedCount;

    public LoginFailedCount(int failedCount) {
        this.failedCount = failedCount;
    }
}
