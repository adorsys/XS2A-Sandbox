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

package de.adorsys.ledgers.oba.rest.server.resource;

import de.adorsys.ledgers.oba.service.api.domain.OnlineBankingResponse;
import de.adorsys.psd2.sandbox.auth.SecurityConstant;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;

@Service
@RequiredArgsConstructor
public class ResponseUtils {
    private static final String LOCATION_HEADER_NAME = "Location";

    public void addAccessTokenHeader(HttpServletResponse response, String accessToken) {
        response.setHeader(SecurityConstant.ACCESS_TOKEN, accessToken);
    }


    @SuppressWarnings("PMD.AvoidReassigningParameters")
    public <T extends OnlineBankingResponse> ResponseEntity<T> redirect(String locationURI, HttpServletResponse httpResp) {
        HttpHeaders headers = new HttpHeaders();

        if (!UrlUtils.isAbsoluteUrl(locationURI)) {
            locationURI = "http://" + locationURI;
        }

        headers.add(LOCATION_HEADER_NAME, locationURI);
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }
}
