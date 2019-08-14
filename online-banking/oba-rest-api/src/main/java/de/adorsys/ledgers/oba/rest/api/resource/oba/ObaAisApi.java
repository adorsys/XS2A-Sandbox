package de.adorsys.ledgers.oba.rest.api.resource.oba;

import de.adorsys.ledgers.middleware.api.domain.account.AccountDetailsTO;
import de.adorsys.ledgers.middleware.api.domain.account.TransactionTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Api(value = ObaAisApi.BASE_PATH, tags = "Online Banking Account Information")
public interface ObaAisApi {
    String BASE_PATH = "/api/v1/ais";
    String LOCAL_DATE_YYYY_MM_DD_FORMAT = "yyyy-MM-dd";
    String DATE_TO_QUERY_PARAM = "dateTo";
    String DATE_FROM_QUERY_PARAM = "dateFrom";

    /**
     * @param userLogin login of current user
     * @return List of accounts for user
     */
    @GetMapping(path = "/accounts/{userLogin}")
    @ApiOperation(value = "Get List of users accounts", authorizations = @Authorization(value = "apiKey"))
    ResponseEntity<List<AccountDetailsTO>> accounts(@PathVariable("userLogin") String userLogin);

    /**
     * @param accountId selected accounts id
     * @param dateFrom  date from which the user requests to see transactions
     * @param dateTo    date until which user requests to see transactions
     * @return List of transactions for account
     */
    @GetMapping(path = "/transactions/{accountId}")
    @ApiOperation(value = "Get List of transactions for queried account per dates selected", authorizations = @Authorization(value = "apiKey"))
    ResponseEntity<List<TransactionTO>> transactions(@PathVariable(name = "accountId") String accountId,
                                                     @RequestParam(name = DATE_FROM_QUERY_PARAM, required = false) @DateTimeFormat(pattern = LOCAL_DATE_YYYY_MM_DD_FORMAT) LocalDate dateFrom,
                                                     @RequestParam(name = DATE_TO_QUERY_PARAM, required = false) @DateTimeFormat(pattern = LOCAL_DATE_YYYY_MM_DD_FORMAT) LocalDate dateTo);
}
