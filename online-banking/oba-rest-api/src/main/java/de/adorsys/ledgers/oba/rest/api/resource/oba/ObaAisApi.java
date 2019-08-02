package de.adorsys.ledgers.oba.rest.api.resource.oba;

import de.adorsys.psd2.consent.api.ais.AisAccountConsent;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Api(value = ObaAisApi.BASE_PATH, tags = "Online Banking AIS", description = "Provides list of valid consents of current user")
public interface ObaAisApi {
    String BASE_PATH = "/consents";
    /**
     * @param userLogin login of current user
     * @return List of valid AIS Consents for user
     */
    @GetMapping(path = "/{userLogin}")
    @ApiOperation(value = "Get List of valid AIS Consents"/*, authorizations = @Authorization(value = "apiKey")*/) //TODO Refactor this when Security is in place
    ResponseEntity<List<AisAccountConsent>> consents(@PathVariable("userLogin") String userLogin);
}
