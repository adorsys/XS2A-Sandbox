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
