package de.adorsys.psd2.sandbox.admin.rest.server.config;

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
        "/admin/login",
        "/admin/register",
        "/admin/password",
        "/admin/consent",
        "/admin/codes",
        "/admin/id",
        "/admin/country/**",
        "/admin/sca/email",
        "/admin/users/reset/password/*",
        "/admin/push/tan"
    };

    protected static final String[] ACTUATOR_WHITELIST = {
        "/actuator/info",
        "/actuator/health"
    };

    private PermittedResources() {
    }
}
