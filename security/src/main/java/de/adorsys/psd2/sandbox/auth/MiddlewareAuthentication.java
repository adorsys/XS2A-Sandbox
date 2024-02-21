/*
 * Copyright 2018-2024 adorsys GmbH & Co KG
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
 * contact us at sales@adorsys.com.
 */

package de.adorsys.psd2.sandbox.auth;

import de.adorsys.ledgers.middleware.api.domain.um.BearerTokenTO;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class MiddlewareAuthentication extends UsernamePasswordAuthenticationToken {

    private static final long serialVersionUID = -778888356552035882L;

    public MiddlewareAuthentication(Object principal, Object credentials) {
        super(principal, credentials);
    }

    public MiddlewareAuthentication(Object principal, BearerTokenTO credentials, Collection<? extends GrantedAuthority> authorities) {
        super(principal, credentials, authorities);
    }

    public BearerTokenTO getBearerToken() {
        return (BearerTokenTO) getCredentials();
    }
}
