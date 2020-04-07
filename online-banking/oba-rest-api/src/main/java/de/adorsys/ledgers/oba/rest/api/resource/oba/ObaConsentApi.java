package de.adorsys.ledgers.oba.rest.api.resource.oba;

import de.adorsys.ledgers.oba.service.api.domain.CreatePiisConsentRequestTO;
import de.adorsys.ledgers.oba.service.api.domain.ObaAisConsent;
import de.adorsys.ledgers.oba.service.api.domain.TppInfoTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(value = ObaConsentApi.BASE_PATH, tags = "Online Banking Consents")
public interface ObaConsentApi {
    String BASE_PATH = "/api/v1/consents";

    /**
     * @param userLogin login of current user
     * @return List of valid AIS Consents for user
     */
    @GetMapping(path = "/{userLogin}")
    @ApiOperation(value = "Get List of valid AIS Consents", authorizations = @Authorization(value = "apiKey"))
    ResponseEntity<List<ObaAisConsent>> consents(@PathVariable("userLogin") String userLogin);

    /**
     * @param consentId identifier of consent
     */
    @PutMapping(path = "/{consentId}")
    @ApiOperation(value = "Revoke consent by ID", authorizations = @Authorization(value = "apiKey"))
    ResponseEntity<Boolean> revokeConsent(@PathVariable String consentId);

    /**
     * @param consentId       decrypted consent id
     * @param authorizationId authorization id
     * @param tan             TAN for single operation
     * @return 200 OK if operation was successful, or an error with msg on the failure reason
     */
    @GetMapping(path = "/confirm/{userLogin}/{consentId}/{authorizationId}/{tan}")
    @ApiOperation(value = "Confirm AIS Consent for Decoupled Approach")
    ResponseEntity<Void> confirm(@PathVariable("userLogin") String userLogin,
                                 @PathVariable("consentId") String consentId,
                                 @PathVariable("authorizationId") String authorizationId,
                                 @PathVariable("tan") String tan);

    @PostMapping(path = "/piis")
    @ApiOperation(value = "Create PIIS consent", authorizations = @Authorization(value = "apiKey"))
    ResponseEntity<Void> createPiis(@RequestBody CreatePiisConsentRequestTO request);

    @GetMapping(path = "/tpp")
    @ApiOperation(value = "Retrieves list of TPPs registered at the ASPSPs CMS", authorizations = @Authorization(value = "apiKey"))
    ResponseEntity<List<TppInfoTO>> tpps();
}
