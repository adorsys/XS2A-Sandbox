package de.adorsys.ledgers.oba.rest.api.resource;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import de.adorsys.ledgers.oba.rest.api.domain.AuthorizeResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value = AISApi.BASE_PATH, tags = "PSU AIS", description = "Provides access to online banking payment functionality")
public interface AISApi {
	String BASE_PATH = "/ais";

	@GetMapping(path="/auth", params= {"redirectId","encryptedConsentId"})
	@ApiOperation(value = "Entry point for authenticating ais consent requests.")
	ResponseEntity<AuthorizeResponse> aisAuth(
			@RequestParam(name = "redirectId") String redirectId,
			@RequestParam(name = "encryptedConsentId") String encryptedConsentId);
}
