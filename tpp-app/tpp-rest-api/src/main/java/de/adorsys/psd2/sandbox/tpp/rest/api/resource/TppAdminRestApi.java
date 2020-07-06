package de.adorsys.psd2.sandbox.tpp.rest.api.resource;

import de.adorsys.ledgers.middleware.api.domain.account.AccountDetailsExtendedTO;
import de.adorsys.ledgers.middleware.api.domain.um.UserExtendedTO;
import de.adorsys.ledgers.middleware.api.domain.um.UserRoleTO;
import de.adorsys.ledgers.middleware.api.domain.um.UserTO;
import de.adorsys.ledgers.util.domain.CustomPageImpl;
import de.adorsys.psd2.sandbox.tpp.rest.api.domain.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Api(tags = "TPP Admin API")
public interface TppAdminRestApi {
    String BASE_PATH = "/tpp/admin";
    String COUNTRY = "country";
    String TPP_ID = "tppId";
    String USER_ID = "userId";
    String TPP_LOGIN = "tppLogin";
    String USER_LOGIN = "userLogin";
    String ROLE = "role";
    String BLOCKED = "blocked";
    String IBAN_PARAM = "ibanParam";

    @ApiOperation(value = "Get users",
        notes = "Retrieves Page of Users with filters",
        authorizations = @Authorization(value = "apiKey"))
    @GetMapping("/users")
    ResponseEntity<CustomPageImpl<UserExtendedTO>> users(@RequestParam(value = COUNTRY, defaultValue = "", required = false) String countryCode,
                                                         @RequestParam(value = TPP_ID, defaultValue = "", required = false) String tppId,
                                                         @RequestParam(value = TPP_LOGIN, defaultValue = "", required = false) String tppLogin,
                                                         @RequestParam(value = USER_LOGIN, defaultValue = "", required = false) String userLogin,
                                                         @RequestParam(value = ROLE, required = false) UserRoleTO role,
                                                         @RequestParam(value = BLOCKED, required = false) Boolean blocked,
                                                         @RequestParam("page") int page,
                                                         @RequestParam("size") int size);

    @ApiOperation(value = "Update user",
        notes = "Update user",
        authorizations = @Authorization(value = "apiKey"))
    @PutMapping("/users")
    ResponseEntity<Void> user(@RequestBody UserTO user);

    @ApiOperation(value = "Retrieves Page of accounts with filters", authorizations = @Authorization(value = "apiKey"))
    @GetMapping("/account")
    ResponseEntity<CustomPageImpl<AccountDetailsExtendedTO>> accounts(@RequestParam(value = COUNTRY, defaultValue = "", required = false) String countryCode,
                                                                      @RequestParam(value = TPP_ID, defaultValue = "", required = false) String tppId,
                                                                      @RequestParam(value = TPP_LOGIN, defaultValue = "", required = false) String tppLogin,
                                                                      @RequestParam(value = IBAN_PARAM, required = false, defaultValue = "") String ibanParam,
                                                                      @RequestParam(value = BLOCKED, required = false) Boolean isBlocked,
                                                                      @RequestParam("page") int page,
                                                                      @RequestParam("size") int size);

    @ApiOperation(value = "Register new User", authorizations = @Authorization(value = "apiKey"))
    @PostMapping("/register")
    ResponseEntity<Void> register(@RequestBody User user,
                                  @RequestParam(value = TPP_ID) String tppId);

    @ApiOperation(value = "Register new Admin", authorizations = @Authorization(value = "apiKey"))
    @PostMapping("/register/admin")
    ResponseEntity<Void> admin(@RequestBody User user);

    @ApiOperation(value = "Retrieves Users with Admin rights", authorizations = @Authorization(value = "apiKey"))
    @GetMapping("/admins")
    ResponseEntity<CustomPageImpl<UserTO>> admins(@RequestParam("page") int page,
                                                  @RequestParam("size") int size);

    @ApiOperation(value = "Remove Tpp", authorizations = @Authorization(value = "apiKey"))
    @DeleteMapping()
    ResponseEntity<Void> remove(String tppId);

    @ApiOperation(value = "Set password for Tpp",
        notes = "Changes password for given Tpp",
        authorizations = @Authorization(value = "apiKey"))
    @PutMapping("/password")
    ResponseEntity<Void> updatePassword(@RequestParam(value = TPP_ID) String tppId, @RequestParam("pin") String password);

    @ApiOperation(value = "Block/Unblock User",
        notes = "Changes block state for given user, returns status being set to the block",
        authorizations = @Authorization(value = "apiKey"))
    @PostMapping("/status")
    ResponseEntity<Boolean> changeStatus(@RequestParam(value = USER_ID) String userId);
}
