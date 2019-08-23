package de.adorsys.psd2.sandbox.tpp.rest.api.resource;

import de.adorsys.ledgers.middleware.api.domain.um.UserTO;
import de.adorsys.psd2.sandbox.tpp.rest.api.domain.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Api(tags = "TPP Users management")
public interface TppUsersRestApi {
    String BASE_PATH = "/tpp/users";

    @ApiOperation(value = "Create users for a given TPP",
        notes = "Endpoint to create a user for a given TPP",
        authorizations = @Authorization(value = "apiKey"))
    @PostMapping
    ResponseEntity<UserTO> createUser(@RequestBody User user);

    @ApiOperation(value = "List users for a given TPP",
        notes = "Endpoint to lists users for a given TPP",
        authorizations = @Authorization(value = "apiKey"))
    @GetMapping
    ResponseEntity<List<UserTO>> getAllUsers();

    @ApiOperation(value = "Update user for a given TPP",
        notes = "Endpoint to update a user for a given TPP",
        authorizations = @Authorization(value = "apiKey"))
    @PutMapping
    ResponseEntity<Void> updateUser(@RequestBody User user);
}
