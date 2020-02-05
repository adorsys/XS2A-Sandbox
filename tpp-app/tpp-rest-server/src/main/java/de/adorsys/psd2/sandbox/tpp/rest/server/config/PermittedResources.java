package de.adorsys.psd2.sandbox.tpp.rest.server.config;

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

    protected static final String[] INDEX_WHITELIST = {
        "/",
        "/index.css",
        "/img/*",
        "/favicon.ico"
    };

    protected static final String[] APP_WHITELIST = {
        "/tpp/login",
        "/tpp/register",
        "/tpp/password",
        "/tpp/consent",
        "/tpp/codes",
        "/tpp/country/**",
        "/tpp/sca/email"
    };

    private PermittedResources() {}
}
