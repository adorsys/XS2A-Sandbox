package de.adorsys.psd2.sandbox.tpp.rest.api.resource;

import de.adorsys.ledgers.middleware.api.domain.account.AccountDetailsTO;
import io.swagger.annotations.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Api(tags = "TPP Accounts management")
public interface TppAccountsRestApi {

    String BASE_PATH = "/tpp/accounts";

    @ApiOperation(value = "Create account for a given user",
        notes = "Endpoint to a deposit account for a user with given ID",
        authorizations = @Authorization(value = "apiKey"))
    @PostMapping
    ResponseEntity<Void> createAccount(@RequestParam(value = "userId") String userId, @RequestBody AccountDetailsTO account);

    /**
     * Returns the list of accounts that belong to the same branch as STAFF user.
     *
     * @return list of accounts that belongs to the same branch as staff user.
     */
    @ApiOperation(value = "Get list of Accessible Accounts",
        notes = "Returns the list of all accounts connected to the given TPP",
        authorizations = @Authorization(value = "apiKey"))
    @ApiResponses(value = {
        @ApiResponse(code = 200, response = AccountDetailsTO[].class, message = "List of accounts accessible to the TPP.")
    })
    @GetMapping
    ResponseEntity<List<AccountDetailsTO>> getAllAccounts();
}
