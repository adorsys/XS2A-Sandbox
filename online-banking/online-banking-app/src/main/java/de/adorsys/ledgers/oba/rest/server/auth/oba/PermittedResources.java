package de.adorsys.ledgers.oba.rest.server.auth.oba;

public class PermittedResources {

    public static final String[] SWAGGER_WHITELIST = {
        "/swagger-resources/**",
        "/swagger-resources",
        "/swagger-ui.html**",
        "/v2/api-docs",
        "/webjars/**",
        "favicon.ico",
        "/error"
    };

    public static final String[] APP_INDEX_WHITELIST = {
        "/",
        "/index.css",
        "/img/*",
        "/favicon.ico"
    };

    public static final String[] APP_SCA_WHITELIST = {
        "/sca/login",
        "/pis/auth/**",
        "/pis/*/authorisation/*/login",
        "/pis-cancellation/*/authorisation/*/login",
        "/ais/auth/**",
        "/ais/*/authorisation/*/login"
    };

    public static final String[] APP_WHITELIST = {
        "/api/v1/login",
        "/api/v1/password",
        "/api/v1/consents/confirm/*/*/*/*",
        "/oauth/authorize",
        "/oauth/token",
        "/oauth/authorization-server"
    };

    public static final String[] ACTUATOR_WHITELIST = {
        "/actuator/health"
    };

    private PermittedResources() {
    }
}
