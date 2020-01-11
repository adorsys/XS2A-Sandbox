package de.adorsys.psd2.sandbox.tpp.rest.server.auth;

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

    String[] INDEX_WHITELIST = {
        "/",
        "/index.css",
        "/img/*",
        "/favicon.ico"
    };

    String[] APP_WHITELIST = {
        "/tpp/login",
        "/tpp/register",
        "/tpp/password",
        "/tpp/consent",
        "/tpp/country/**"
    };
}
