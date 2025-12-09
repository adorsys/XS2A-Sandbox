/*
 * Copyright 2018-2025 adorsys GmbH & Co KG
 *
 * This program is free software: you can redistribute it and/o    @PostMapping("/bulk-party-verifications")
    public ResponseEntity<Object> bulkPartyVerification(@RequestBody Map<String, Object> request, HttpServletRequest servletRequest) {
        log.info("VOP Bulk party verification request received: {}", request);
        // Return mock response for bulk verification
        Map<String, Object> mockResponse = createBulkVerificationMockResponse();
        return ResponseEntity.ok(mockResponse);
    }

    @PostMapping("/bulk-verifications-of-pay")
    public ResponseEntity<Object> createBulkVerificationOfPay(@RequestBody Map<String, Object> request, HttpServletRequest servletRequest) {
        log.info("VOP Create bulk verification of pay request received: {}", request);
        // Return mock response with the bulk ID for tracking
        String bulkId = "aaaaaaaa-ad88-49ec-a2ad-99ddcb1f7798";
        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("bulkVerificationId", bulkId);
        mockResponse.put("status", "RECEIVED");
        mockResponse.put("creationDateTime", new Date());
        return ResponseEntity.ok(mockResponse);
    }

    @GetMapping("/bulk-verifications/{bulkId}/status")it
 * under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version. This program is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see https://www.gnu.org/licenses/.
 *
 * This project is also available under a separate commercial license. You can
 * contact us at sales@adorsys.com.
 */

package de.adorsys.psd2.sandbox.tpp.rest.server.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
@SuppressWarnings("PMD")
public class VopController {

    @Value("${vop.connector.url:http://localhost:8081}")
    private String connectorBaseUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    @PostMapping("/payee-verifications")
    public ResponseEntity<Object> payeeVerification(@RequestBody Map<String, Object> request, HttpServletRequest servletRequest) {
        log.info("VOP Payee verification request received: {}", request);
        // Return mock response for name match
        Map<String, Object> mockResponse = createNameMatchMockResponse();
        return ResponseEntity.ok(mockResponse);
    }

    @PostMapping("/payer-verifications")
    public ResponseEntity<Object> payerVerification(@RequestBody Map<String, Object> request, HttpServletRequest servletRequest) {
        log.info("VOP Payer verification request received: {}", request);
        // Return mock response for name match
        Map<String, Object> mockResponse = createNameMatchMockResponse();
        return ResponseEntity.ok(mockResponse);
    }

    @PostMapping("/party-verifications")
    public ResponseEntity<Object> partyVerification(@RequestBody Map<String, Object> request, HttpServletRequest servletRequest) {
        log.info("VOP Party verification request received: {}", request);
        // Return mock response for identification match
        Map<String, Object> mockResponse = createIdentificationMatchMockResponse();
        return ResponseEntity.ok(mockResponse);
    }

    @PostMapping("/verification-of-party")
    public ResponseEntity<Object> verificationOfParty(@RequestBody Map<String, Object> request, HttpServletRequest servletRequest) {
        log.info("VOP Verification of party request received: {}", request);
        // Return mock response for name match (used by name-match and identification-match components)
        Map<String, Object> mockResponse = createNameMatchMockResponse();
        return ResponseEntity.ok(mockResponse);
    }

    @PostMapping("/bulk-verification-of-party")
    public ResponseEntity<Object> bulkVerificationOfParty(@RequestBody Map<String, Object> request, HttpServletRequest servletRequest) {
        log.info("VOP Bulk Verification of party request received: {}", request);
        // Return mock response for bulk verification
        Map<String, Object> mockResponse = createBulkVerificationMockResponse();
        return ResponseEntity.ok(mockResponse);
    }

    @PostMapping("/bulk-payee-verifications")
    public ResponseEntity<Object> bulkPayeeVerification(@RequestBody Map<String, Object> request, HttpServletRequest servletRequest) {
        log.info("VOP Bulk Payee verification request received: {}", request);
        // Return mock response for bulk verification
        Map<String, Object> mockResponse = createBulkVerificationMockResponse();
        return ResponseEntity.ok(mockResponse);
    }

    @PostMapping("/bulk-payer-verifications")
    public ResponseEntity<Object> bulkPayerVerification(@RequestBody Map<String, Object> request, HttpServletRequest servletRequest) {
        log.info("VOP Bulk Payer verification request received: {}", request);
        // Return mock response for bulk verification
        Map<String, Object> mockResponse = createBulkVerificationMockResponse();
        return ResponseEntity.ok(mockResponse);
    }

    @PostMapping("/bulk-party-verifications")
    public ResponseEntity<Object> bulkPartyVerification(@RequestBody Map<String, Object> request, HttpServletRequest servletRequest) {
        log.info("VOP Bulk Party verification request received: {}", request);
        // Return mock response for bulk verification
        Map<String, Object> mockResponse = createBulkVerificationMockResponse();
        return ResponseEntity.ok(mockResponse);
    }

    @GetMapping("/bulk-verifications/{bulkId}/status")
    public ResponseEntity<Object> getBulkVerificationStatus(@PathVariable String bulkId, HttpServletRequest servletRequest) {
        log.info("VOP Bulk verification status request for bulkId: {}", bulkId);
        // Return mock response for bulk status
        Map<String, Object> mockResponse = createBulkStatusMockResponse(bulkId);
        return ResponseEntity.ok(mockResponse);
    }

    @GetMapping("/bulk-verifications/{bulkId}")
    public ResponseEntity<Object> getBulkVerificationResult(@PathVariable String bulkId, HttpServletRequest servletRequest) {
        log.info("VOP Bulk verification result request for bulkId: {}", bulkId);
        // Return mock response for bulk verification results
        Map<String, Object> mockResponse = createBulkRetrieveMockResponse(bulkId);
        return ResponseEntity.ok(mockResponse);
    }

    @GetMapping("/bulk-verifications-of-pay/{bulkId}/status")
    public ResponseEntity<Object> getVopBulkVerificationStatus(@PathVariable String bulkId, HttpServletRequest servletRequest) {
        log.info("VOP bulk verifications of pay status request for bulkId: {}", bulkId);
        // Return mock response for bulk status
        Map<String, Object> mockResponse = createBulkStatusMockResponse(bulkId);
        return ResponseEntity.ok(mockResponse);
    }

    @GetMapping("/bulk-verifications-of-pay/{bulkId}")
    public ResponseEntity<Object> getVopBulkVerificationResult(@PathVariable String bulkId, HttpServletRequest servletRequest) {
        log.info("VOP bulk verifications of pay result request for bulkId: {}", bulkId);
        // Return mock response for bulk verification results
        Map<String, Object> mockResponse = createBulkRetrieveMockResponse(bulkId);
        return ResponseEntity.ok(mockResponse);
    }

    @GetMapping("/bulk-verifications-of-pay")
    public ResponseEntity<Object> getVopBulkVerifications(HttpServletRequest servletRequest) {
        log.info("VOP bulk verifications of pay request (no bulkId)");
        // Return mock response for bulk verification results with default bulkId
        Map<String, Object> mockResponse = createBulkRetrieveMockResponse(null);
        return ResponseEntity.ok(mockResponse);
    }

    @GetMapping("/bulk-verifications-of-pay/status")
    public ResponseEntity<Object> getVopBulkVerificationsStatus(HttpServletRequest servletRequest) {
        log.info("VOP bulk verifications of pay status request (no bulkId)");
        // Return mock response for bulk status with default bulkId
        Map<String, Object> mockResponse = createBulkStatusMockResponse(null);
        return ResponseEntity.ok(mockResponse);
    }

    private ResponseEntity<Object> forwardToConnector(String path, HttpMethod method, Object body, HttpServletRequest servletRequest) {
        String url = connectorBaseUrl + path;

        HttpHeaders headers = new HttpHeaders();
        // copy headers from incoming request
        Enumeration<String> headerNames = servletRequest.getHeaderNames();
        if (headerNames != null) {
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                // skip host header to avoid mismatches
                if ("host".equalsIgnoreCase(headerName)) {
                    continue;
                }
                Enumeration<String> values = servletRequest.getHeaders(headerName);
                List<String> list = new ArrayList<>();
                while (values.hasMoreElements()) {
                    list.add(values.nextElement());
                }
                headers.put(headerName, list);
            }
        }

        // ensure content-type
        if (!headers.containsKey(HttpHeaders.CONTENT_TYPE)) {
            headers.setContentType(MediaType.APPLICATION_JSON);
        }

        HttpEntity<Object> httpEntity = new HttpEntity<>(body, headers);

        ResponseEntity<String> responseEntity;
        try {
            responseEntity = restTemplate.exchange(url, method, httpEntity, String.class);
        } catch (Exception e) {
            log.error("Error while forwarding VOP request to connector: {}", e.getMessage());
            // return 502 Bad Gateway when connector can't be reached or returns unexpected error
            Map<String, Object> error = new HashMap<>();
            error.put("timestamp", new Date().toString());
            error.put("status", HttpStatus.BAD_GATEWAY.value());
            error.put("error", HttpStatus.BAD_GATEWAY.getReasonPhrase());
            error.put("message", e.getMessage());
            error.put("path", path);
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(error);
        }

        // build response with same status and body (as string). Try to preserve content-type if present
        HttpHeaders respHeaders = new HttpHeaders();
        MediaType contentType = responseEntity.getHeaders().getContentType();
        if (contentType != null) {
            respHeaders.setContentType(contentType);
        } else {
            respHeaders.setContentType(MediaType.APPLICATION_JSON);
        }

        return new ResponseEntity<>(responseEntity.getBody(), respHeaders, responseEntity.getStatusCode());
    }

    /**
     * Creates mock response for name match verification requests
     */
    private Map<String, Object> createNameMatchMockResponse() {
        Map<String, Object> response = new HashMap<>();
        response.put("partyNameMatch", "MTCH");
        response.put("partyIdMatch", "MTCH");
        response.put("matchedName", "Mocked matched name from ASPSP");
        response.put("additionalInformation", "Mocked verification additional information");
        return response;
    }

    /**
     * Creates mock response for identification match verification requests
     */
    private Map<String, Object> createIdentificationMatchMockResponse() {
        Map<String, Object> response = new HashMap<>();
        response.put("partyNameMatch", "MTCH");
        response.put("partyIdMatch", "MTCH");
        response.put("matchedName", "Mocked matched name from ASPSP");
        response.put("additionalInformation", "Mocked verification additional information");
        return response;
    }

    /**
     * Creates mock response for bulk verification requests
     */
    private Map<String, Object> createBulkVerificationMockResponse() {
        Map<String, Object> response = new HashMap<>();
        response.put("vopBulkStatus", "completed");

        List<Map<String, Object>> verificationResponses = new ArrayList<>();

        // First verification response
        Map<String, Object> firstResponse = new HashMap<>();
        firstResponse.put("uetr", "4c83da56-893e-47e3-9a92-c27e4866dabe");
        firstResponse.put("partyNameMatch", "MTCH");
        firstResponse.put("partyIdMatch", "MTCH");
        firstResponse.put("matchedName", "Mocked matched name from ASPSP");
        firstResponse.put("additionalInformation", "Mocked verification additional information");
        verificationResponses.add(firstResponse);

        // Second verification response
        Map<String, Object> secondResponse = new HashMap<>();
        secondResponse.put("uetr", "a533db18-9571-4279-8eb7-0c5dbad9a039");
        secondResponse.put("partyNameMatch", "MTCH");
        secondResponse.put("partyIdMatch", "MTCH");
        secondResponse.put("matchedName", "Mocked matched name from ASPSP");
        secondResponse.put("additionalInformation", "Mocked verification additional information");
        verificationResponses.add(secondResponse);

        response.put("partyVerificationResponses", verificationResponses);
        return response;
    }

    /**
     * Creates mock response for bulk verification status requests
     */
    private Map<String, Object> createBulkStatusMockResponse(String bulkId) {
        Map<String, Object> response = new HashMap<>();
        response.put("vopBulkId", "aaaaaaaa-ad88-49ec-a2ad-99ddcb1f7798");
        response.put("vopBulkStatus", "completed");
        return response;
    }

    /**
     * Creates mock response for bulk verification retrieve requests
     */
    private Map<String, Object> createBulkRetrieveMockResponse(String bulkId) {
        Map<String, Object> response = new HashMap<>();
        response.put("vopBulkId", "aaaaaaaa-ad88-49ec-a2ad-99ddcb1f7798");
        response.put("vopBulkStatus", "completed");

        List<Map<String, Object>> requestsAndResults = new ArrayList<>();

        // First verification request and result
        Map<String, Object> firstRequest = new HashMap<>();
        firstRequest.put("uetr", "348f5522-a603-452a-b8ed-c694bd768539");
        firstRequest.put("partyAccount", createEmptyPartyAccount());
        firstRequest.put("partyAgent", createEmptyPartyAgent());
        firstRequest.put("party", createEmptyParty());
        firstRequest.put("partyNameMatch", "MTCH");
        firstRequest.put("partyIdMatch", "MTCH");
        firstRequest.put("matchedName", "Mocked matched name from ASPSP 1");
        firstRequest.put("additionalInformation", "Mocked verification additional information for request 1");
        requestsAndResults.add(firstRequest);

        // Second verification request and result
        Map<String, Object> secondRequest = new HashMap<>();
        secondRequest.put("uetr", "27252c05-a716-4914-8c34-19e5fb0401ad");
        secondRequest.put("partyAccount", createEmptyPartyAccount());
        secondRequest.put("partyAgent", createEmptyPartyAgent());
        secondRequest.put("party", createEmptyParty());
        secondRequest.put("partyNameMatch", "MTCH");
        secondRequest.put("partyIdMatch", "MTCH");
        secondRequest.put("matchedName", "Mocked matched name from ASPSP 2");
        secondRequest.put("additionalInformation", "Mocked verification additional information for request 2");
        requestsAndResults.add(secondRequest);

        // Third verification request and result
        Map<String, Object> thirdRequest = new HashMap<>();
        thirdRequest.put("uetr", "246c2dfc-a038-45da-8b6c-b54aff0a4069");
        thirdRequest.put("partyAccount", createEmptyPartyAccount());
        thirdRequest.put("partyAgent", createEmptyPartyAgent());
        thirdRequest.put("party", createEmptyParty());
        thirdRequest.put("partyNameMatch", "MTCH");
        thirdRequest.put("partyIdMatch", "MTCH");
        thirdRequest.put("matchedName", "Mocked matched name from ASPSP 3");
        thirdRequest.put("additionalInformation", "Mocked verification additional information for request 3");
        requestsAndResults.add(thirdRequest);

        response.put("partyVerificationRequestsAndResults", requestsAndResults);
        return response;
    }

    /**
     * Creates empty party account structure
     */
    private Map<String, Object> createEmptyPartyAccount() {
        Map<String, Object> partyAccount = new HashMap<>();
        partyAccount.put("other", new HashMap<>());
        return partyAccount;
    }

    /**
     * Creates empty party agent structure
     */
    private Map<String, Object> createEmptyPartyAgent() {
        Map<String, Object> partyAgent = new HashMap<>();
        partyAgent.put("financialInstitutionId", new HashMap<>());
        return partyAgent;
    }

    /**
     * Creates empty party structure
     */
    private Map<String, Object> createEmptyParty() {
        Map<String, Object> party = new HashMap<>();
        party.put("identification", new HashMap<>());
        return party;
    }
}
