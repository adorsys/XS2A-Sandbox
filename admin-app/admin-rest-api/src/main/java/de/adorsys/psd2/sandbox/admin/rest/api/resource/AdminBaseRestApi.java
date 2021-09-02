package de.adorsys.psd2.sandbox.admin.rest.api.resource;

import de.adorsys.psd2.sandbox.admin.rest.api.domain.BankCodeStructure;
import de.adorsys.psd2.sandbox.admin.rest.api.domain.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.iban4j.CountryCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Currency;
import java.util.Map;
import java.util.Set;

@Api(tags = "Admin main API")
public interface AdminBaseRestApi {
    String BASE_PATH = "/admin";

    @ApiOperation(value = "Login for Admin")
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
    ResponseEntity<BankCodeStructure> getBankCodeStructure(@RequestParam(value = "countryCode") String countryCode);

    @ApiOperation(value = "Generate random TPP-ID")
    @PostMapping("/id")
    ResponseEntity<String> getRandomTppId(@RequestParam(value = "countryCode") String countryCode);

    @ApiOperation(value = "Register new TPP")
    @PostMapping("/register")
    ResponseEntity<Void> register(@RequestBody User user);

    @ApiOperation(value = "Remove Tpp", authorizations = @Authorization(value = "apiKey"))
    @DeleteMapping("/self")
    ResponseEntity<Void> remove();

    @ApiOperation(value = "Remove transactions for account in Tpp", authorizations = @Authorization(value = "apiKey"))
    @DeleteMapping("/transactions/{accountId}")
    ResponseEntity<Void> transactions(@PathVariable(value = "accountId") String accountId);

    @ApiOperation(value = "Remove account", authorizations = @Authorization(value = "apiKey"))
    @DeleteMapping("/account/{accountId}")
    ResponseEntity<Void> account(@PathVariable(value = "accountId") String accountId);

    @ApiOperation(value = "Remove user", authorizations = @Authorization(value = "apiKey"))
    @DeleteMapping("/user/{userId}")
    ResponseEntity<Void> user(@PathVariable(value = "userId") String userId);

    @ApiOperation(value = "Consume TAN notification from CoreBanking")
    @PutMapping("/push/tan")
    ResponseEntity<Void> consumeTan(@RequestBody String tan);
}
