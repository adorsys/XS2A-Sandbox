package de.adorsys.ledgers.oba.rest.server.ws.controller;

import de.adorsys.ledgers.oba.rest.server.ws.domain.DecoupledContext;
import de.adorsys.ledgers.oba.service.api.domain.DecoupledConfRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.messaging.AbstractSubProtocolEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import static de.adorsys.ledgers.oba.rest.server.ws.WebSocketConstants.WS_SUBSCRIPTION_URL;

@Slf4j
@Service
@RequiredArgsConstructor
public class DecoupledSessionListener {
    private final SimpMessagingTemplate template;
    private final DecoupledContext context;

    @EventListener
    public void handleSubscribeEvent(SessionSubscribeEvent event) {
        String user = Optional.ofNullable(event)
                          .map(AbstractSubProtocolEvent::getUser)
                          .map(Principal::getName)
                          .orElse("UNKNOWN");
        log.info("User: {} connected", user);
        List<DecoupledConfRequest> undeliveredMessages = context.getUndeliveredMessages(user);
        if (CollectionUtils.isNotEmpty(undeliveredMessages)) {
            log.info("Delivering {} messages to recently connected user: {}", undeliveredMessages.size(), user);
            undeliveredMessages.forEach(m -> template.convertAndSendToUser(m.getAddressedUser(), WS_SUBSCRIPTION_URL, m));
            context.clearUndeliveredMessages(user);
        }
    }

    @EventListener
    public void handleUnsubscribeEvent(SessionDisconnectEvent event) {
        String user = Optional.ofNullable(event)
                          .map(AbstractSubProtocolEvent::getUser)
                          .map(Principal::getName)
                          .orElse("UNKNOWN");
        log.info("User {} disconnected", user);
    }
}
