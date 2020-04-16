package de.adorsys.ledgers.oba.service.impl.mapper;


import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;


class TimeMapperTest {

    @Test
    void mapTime() {
        TimeMapper mapper = new TimeMapper();

        LocalTime result = mapper.mapTime(OffsetDateTime.of(2020, 1, 1, 1, 1, 1, 1, ZoneOffset.UTC));
        assertEquals(LocalTime.of(1, 1, 1, 1), result);
    }

    @Test
    void mapTime_null_time() {
        TimeMapper mapper = new TimeMapper();

        LocalTime result = mapper.mapTime(null);
        assertNull(result);
    }
}
