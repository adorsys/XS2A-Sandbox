package de.adorsys.ledgers.oba.rest.server.config.security;

public class PermittedResources {

    protected static final String[] SWAGGER_WHITELIST = {
        "/swagger-resources/**",
        "/swagger-resources",
        "/swagger-ui.html**",
        "/v2/api-docs",
        "/webjars/**",
        "favicon.ico",
        "/error"
    };

    protected static final String[] APP_INDEX_WHITELIST = {
        "/",
        "/index.css",
        "/img/*",
        "/favicon.ico"
    };

    protected static final String[] APP_SCA_WHITELIST = {
        "/sca/login",
        "/pis/auth/**",
        "/pis/*/authorisation/*/login",
        "/pis-cancellation/*/authorisation/*/login",
        "/ais/auth/**",
        "/ais/*/authorisation/*/login"
    };

    protected static final String[] APP_WHITELIST = {
        "/api/v1/login",
        "/api/v1/password",
        "/api/v1/consents/confirm/*/*/*/*",
        "/oauth/authorise/**",
        "/oauth/token/**",
        "/oauth/authorization-server/**"
    };

    protected static final String[] ACTUATOR_WHITELIST = {
        "/actuator/info",
        "/actuator/health"
    };

    private PermittedResources() {}
}
