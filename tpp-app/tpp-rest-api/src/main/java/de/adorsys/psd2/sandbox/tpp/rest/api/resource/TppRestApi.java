package de.adorsys.psd2.sandbox.tpp.rest.api.resource;

import de.adorsys.psd2.sandbox.tpp.rest.api.domain.BankCodeStructure;
import de.adorsys.psd2.sandbox.tpp.rest.api.domain.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.iban4j.CountryCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Currency;
import java.util.List;
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
    @GetMapping("/country/codes")
    ResponseEntity<List<CountryCode>> getCountryCodes();

    @ApiOperation(value = "Get country code character type and country code length")
    @GetMapping("/country/codes/structure")
    ResponseEntity<BankCodeStructure> getBankCodeStructure(@RequestParam String countryCode);

    @ApiOperation(value = "Register new TPP")
    @PostMapping("/register")
    ResponseEntity<Void> register(@RequestBody User user);

    @ApiOperation(value = "Remove Tpp", authorizations = @Authorization(value = "apiKey"))
    @DeleteMapping("/self")
    ResponseEntity<Void> remove();

    @ApiOperation(value = "Remove transactions for account in Tpp", authorizations = @Authorization(value = "apiKey"))
    @DeleteMapping("/account/{iban}")
    ResponseEntity<Void> transactions(@PathVariable String iban);
}
