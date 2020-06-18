package de.adorsys.psd2.sandbox.tpp.rest.api.resource;

import de.adorsys.ledgers.middleware.api.domain.general.RevertRequestTO;
import de.adorsys.psd2.sandbox.tpp.rest.api.domain.BankCodeStructure;
import de.adorsys.psd2.sandbox.tpp.rest.api.domain.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.iban4j.CountryCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Currency;
import java.util.Map;
import java.util.Set;

@Api(tags = "TPP main API")
public interface TppRestApi {
    String BASE_PATH = "/tpp";

    @ApiOperation(value = "Login for TPP")
    @PostMapping(value = "/login")
    void login(@RequestHeader(value = "login") String login, @RequestHeader(value = "pin") String pin);

    @ApiOperation(value = "Get supported currencies list", authorizations = @Authorization(value = "apiKey"))
    @GetMapping("/currencies")
    ResponseEntity<Set<Currency>> getCurrencies();

    @ApiOperation(value = "Get country codes")
    @GetMapping("/codes")
    ResponseEntity<Map<CountryCode, String>> getSupportedCountryCodes();

    @ApiOperation(value = "Get country code character type and country code length")
    @GetMapping("/country/codes/structure")
    ResponseEntity<BankCodeStructure> getBankCodeStructure(@RequestParam String countryCode);

    @ApiOperation(value = "Generate random TPP-ID")
    @PostMapping("/id")
    ResponseEntity<String> getRandomTppId(@RequestParam String countryCode);

    @ApiOperation(value = "Register new TPP")
    @PostMapping("/register")
    ResponseEntity<Void> register(@RequestBody User user);

    @ApiOperation(value = "Remove Tpp", authorizations = @Authorization(value = "apiKey"))
    @DeleteMapping("/self")
    ResponseEntity<Void> remove();

    @ApiOperation(value = "Remove transactions for account in Tpp", authorizations = @Authorization(value = "apiKey"))
    @DeleteMapping("/transactions/{accountId}")
    ResponseEntity<Void> transactions(@PathVariable String accountId);

    @ApiOperation(value = "Remove account", authorizations = @Authorization(value = "apiKey"))
    @DeleteMapping("/account/{accountId}")
    ResponseEntity<Void> account(@PathVariable String accountId);

    @ApiOperation(value = "Remove user", authorizations = @Authorization(value = "apiKey"))
    @DeleteMapping("/user/{userId}")
    ResponseEntity<Void> user(@PathVariable String userId);

    @ApiOperation(value = "Revert TPP data by certain timestamp", authorizations = @Authorization(value = "apiKey"))
    @PostMapping("/revert")
    ResponseEntity<Void> revert(@RequestBody RevertRequestTO revertRequest);
}
