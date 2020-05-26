package de.adorsys.ledgers.oba.rest.utils;

import feign.RequestTemplate;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class NullHeaderInterceptorTest {

    @Test
    void apply() {
        RequestTemplate template = new RequestTemplate();
        template.header("123", "123");
        new NullHeaderInterceptor().apply(template);
        assertTrue(template.headers().containsKey("123"));
    }
}
