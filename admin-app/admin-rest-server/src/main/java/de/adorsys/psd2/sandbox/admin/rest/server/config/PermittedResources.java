/*
 * Copyright 2018-2023 adorsys GmbH & Co KG
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version. This program is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see https://www.gnu.org/licenses/.
 *
 * This project is also available under a separate commercial license. You can
 * contact us at psd2@adorsys.com.
 */

package de.adorsys.psd2.sandbox.admin.rest.server.config;

public class PermittedResources {

    protected static final String[] SWAGGER_WHITELIST = {
        "/swagger-resources/**",
        "/swagger-resources",
        "/swagger-ui/**",
        "/v3/api-docs/**",
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
