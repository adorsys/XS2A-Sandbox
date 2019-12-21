package de.adorsys.ledgers.oba.app.config;

import de.adorsys.ledgers.oba.service.api.service.PaymentConverter;
import de.adorsys.ledgers.oba.service.impl.mapper.PaymentMapper;
import de.adorsys.ledgers.oba.service.impl.service.PaymentConverterImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class PaymentConverterConfig {
    private final PaymentMapper paymentMapper;

    @Bean
    public PaymentConverter paymentConverter() {
        return new PaymentConverterImpl(paymentMapper);
    }
}
