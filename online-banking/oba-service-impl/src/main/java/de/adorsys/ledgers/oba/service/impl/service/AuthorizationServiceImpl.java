package de.adorsys.ledgers.oba.service.impl.service;

import de.adorsys.ledgers.oba.service.api.service.AuthorizationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthorizationServiceImpl implements AuthorizationService {

    @Override
    public String resolveAuthConfirmationCodeRedirectUri(String redirectUri, String code) {
        if (StringUtils.isNotBlank(code)) {
            String paramPrefix = redirectUri.contains("?") ? "&" : "?";
            return redirectUri + paramPrefix + "authConfirmationCode=" + code;

        }
        return redirectUri;
    }
}
