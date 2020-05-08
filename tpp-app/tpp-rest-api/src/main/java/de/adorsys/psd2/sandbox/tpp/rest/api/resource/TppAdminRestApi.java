package de.adorsys.psd2.sandbox.tpp.rest.api.resource;

import de.adorsys.psd2.sandbox.tpp.rest.api.domain.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Api(tags = "TPP Admin API")
public interface TppAdminRestApi {
    String BASE_PATH = "/admin";

    @ApiOperation(value = "Register new TPP", authorizations = @Authorization(value = "apiKey"))
    @PostMapping("/register")
    ResponseEntity<Void> register(@RequestBody User user);

    @ApiOperation(value = "Remove Tpp", authorizations = @Authorization(value = "apiKey"))
    @DeleteMapping("/tpp")
    ResponseEntity<Void> remove(String tppId);
}
