package de.adorsys.ledgers.oba.rest.api.resource.oba;

import de.adorsys.ledgers.middleware.api.domain.um.UserCredentialsTO;
import de.adorsys.ledgers.oba.rest.api.domain.AuthorizeResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Api(value = ObaAuthorizationApi.BASE_PATH, tags = "Online Banking Authorization", description = "Provides access to online banking")
public interface ObaAuthorizationApi {
    String BASE_PATH = "/login";

    /**
     * @param userCredentials users login and pin
     * @return authorization response containing authorization status and token
     */
    @PostMapping
    @ApiOperation(value = "Perform Online Banking Login")
    ResponseEntity<AuthorizeResponse> login(@RequestBody UserCredentialsTO userCredentials);
}
