package de.adorsys.ledgers.oba.rest.server.auth.oba;

public interface PermittedResources {

    String[] SWAGGER_WHITELIST = {
        "/swagger-resources/**",
        "/swagger-resources",
        "/swagger-ui.html**",
        "/v2/api-docs",
        "/webjars/**",
        "favicon.ico",
        "/error"
    };

    String[] APP_INDEX_WHITELIST = {
        "/",
        "/index.css",
        "/img/*",
        "/favicon.ico"
    };

    String[] APP_SCA_WHITELIST = {
        "/sca/login",
        "/pis/auth/**",
        "/pis/*/authorisation/*/login",
        "/pis-cancellation/*/authorisation/*/login",
        "/ais/auth/**",
        "/ais/*/authorisation/*/login"
    };

    String[] APP_WHITELIST = {
        "/api/v1/login",
        "/api/v1/password",
        "/api/v1/consents/confirm/*/*/*/*",
        "/oauth/authorise/**",
        "/oauth/token/**",
        "/oauth/authorization-server/**"
    };

    String[] ACTUATOR_WHITELIST = {
        "/actuator/health"
    };
}
