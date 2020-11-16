package de.adorsys.ledgers.oba.rest.server.ws.controller;

import de.adorsys.ledgers.oba.rest.api.resource.oba.ObaDecoupledAPI;
import de.adorsys.ledgers.oba.rest.server.auth.ObaMiddlewareAuthentication;
import de.adorsys.ledgers.oba.rest.server.ws.domain.DecoupledContext;
import de.adorsys.ledgers.oba.service.api.domain.DecoupledConfRequest;
import de.adorsys.ledgers.oba.service.api.service.DecoupledService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static de.adorsys.ledgers.oba.rest.api.resource.oba.ObaDecoupledAPI.BASE_PATH;
import static de.adorsys.ledgers.oba.rest.server.ws.WebSocketConstants.WS_SUBSCRIPTION_URL;

@Slf4j
@RestController
@RequestMapping(BASE_PATH)
@RequiredArgsConstructor
public class DecoupledController implements ObaDecoupledAPI {
    private final SimpMessagingTemplate template;
    private final DecoupledContext context;
    private final ObaMiddlewareAuthentication authentication;
    private final DecoupledService decoupledService;

    @Override
    public ResponseEntity<Boolean> decoupled(DecoupledConfRequest message) {
        log.info("Confirmation of decoupled operation: {}, id: {}, by: {}", message.getOpType().name(), message.getObjId(), message.getAddressedUser());
        return ResponseEntity.ok(decoupledService.executeDecoupledOpr(message, authentication.getBearerToken().getAccess_token()));
    }

    @Override
    public ResponseEntity<Boolean> sendNotification(DecoupledConfRequest message) {
        message.setConfirmationUrl(BASE_PATH + "/execute");
        message.setHttpMethod(HttpMethod.POST);
        if (context.checkUserIsConnected(message.getAddressedUser())) {
            log.info("Sending decoupled notification to connected user: {}, operation type: {}, opId: {}", message.getAddressedUser(), message.getOpType().name(), message.getObjId());
            template.convertAndSendToUser(message.getAddressedUser(), WS_SUBSCRIPTION_URL, message);
        } else {
            log.info("User: {} is not connected! Adding received message to messages queue.", message.getAddressedUser());
            context.addUndeliveredMessage(message.getAddressedUser(), message);
        }
        return ResponseEntity.ok(true);
    }
}
