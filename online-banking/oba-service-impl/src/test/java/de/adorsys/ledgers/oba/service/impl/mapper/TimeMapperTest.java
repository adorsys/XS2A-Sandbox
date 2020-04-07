package de.adorsys.ledgers.oba.service.impl.mapper;

import org.junit.Test;

import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

public class TimeMapperTest {

    @Test
    public void mapTime() {
        TimeMapper mapper = new TimeMapper();

        LocalTime result = mapper.mapTime(OffsetDateTime.of(2020, 1, 1, 1, 1, 1, 1, ZoneOffset.UTC));
        assertThat(result).isEqualTo(LocalTime.of(1, 1, 1, 1));
    }

    @Test
    public void mapTime_null_time() {
        TimeMapper mapper = new TimeMapper();

        LocalTime result = mapper.mapTime(null);
        assertThat(result).isNull();
    }
}
