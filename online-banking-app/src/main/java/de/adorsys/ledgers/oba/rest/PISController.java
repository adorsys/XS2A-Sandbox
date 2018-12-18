package de.adorsys.ledgers.oba.rest;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import de.adorsys.ledgers.oba.consentref.ConsentReference;
import de.adorsys.ledgers.oba.consentref.ConsentReferencePolicy;
import de.adorsys.ledgers.oba.consentref.InvalidConsentException;
import de.adorsys.ledgers.oba.consentref.ServicePort;
import de.adorsys.ledgers.oba.domain.AuthorizeResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController(PISController.BASE_PATH)
@RequestMapping(PISController.BASE_PATH)
@Api(value = PISController.BASE_PATH, tags = "PSU PIS", description = "Provides access to online banking payment functionality")
public class PISController {
	private static final String LOCATION_HEADER_NAME = "Location";

	static final String BASE_PATH = "/pis";
	
	@Autowired
	private ConsentReferencePolicy referencePolicy;
	
	@Autowired
	private ResponseUtils responseUtils;
	
	@GetMapping("/auth/{paymentId}")
	@ApiOperation(value = "Entry point for authenticating payment requests.")
	public ResponseEntity<AuthorizeResponse> pisAuth(
			@PathVariable(name = "paymentId") String paymentId,
			HttpServletRequest request,
			HttpServletResponse response) {
		AuthorizeResponse authResponse = new AuthorizeResponse();
		// load payment by id
		ConsentReference consentReference;
		try {
			consentReference = referencePolicy.fromURL(ServicePort.PIS, paymentId);
		} catch (InvalidConsentException e) {
			return responseUtils.unknownCredentials(authResponse);
		}
		authResponse.setScaId(consentReference.getScaId());
		String basePath = StringUtils.substringBefore(request.getRequestURL().toString(), BASE_PATH);
		Map<String, Object> map = new HashMap<>();
		map.put(SCAController.SCA_ID_REQUEST_PARAM, consentReference.getScaId());
		URI locationHeader = UriComponentsBuilder.fromUriString(basePath)
				.path(SCAController.BASE_PATH)
				.path(SCAController.LOGIN_SCA_ID)
				.build(map);
		response.addHeader(LOCATION_HEADER_NAME, locationHeader.toString());
		responseUtils.setCookies(response, consentReference, null, null);
		return ResponseEntity.status(302).build();
	}
}
