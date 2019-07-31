package de.adorsys.psd2.sandbox.tpp.rest.server.auth;

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

    public static final String[] INDEX_WHITELIST = {
        "/",
        "/index.css",
        "/img/*",
        "/favicon.ico"
    };

    public static final String[] APP_WHITELIST = {
        "/tpp/login",
        "/tpp/register",
        "/tpp/password"
    };

    private PermittedResources() {
    }
}
