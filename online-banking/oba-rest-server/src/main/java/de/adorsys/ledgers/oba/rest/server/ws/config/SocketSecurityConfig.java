package de.adorsys.ledgers.oba.rest.server.ws.config;

import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;
import org.springframework.stereotype.Component;

@Component
public class SocketSecurityConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {
    @Override
    protected boolean sameOriginDisabled() {
        return true;
    }

    @Override
    protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
        messages.simpTypeMatchers(SimpMessageType.SUBSCRIBE, SimpMessageType.CONNECT, SimpMessageType.DISCONNECT,
                                  SimpMessageType.HEARTBEAT, SimpMessageType.OTHER).permitAll()
            .anyMessage().authenticated();
    }
}
