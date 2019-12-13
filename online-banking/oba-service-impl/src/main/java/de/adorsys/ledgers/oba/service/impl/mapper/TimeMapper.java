package de.adorsys.ledgers.oba.service.impl.mapper;

import org.mapstruct.Mapper;

import java.time.LocalTime;
import java.time.OffsetDateTime;

@Mapper(componentModel = "spring")
public class TimeMapper {

    public LocalTime mapTime(OffsetDateTime time) {
        if (time == null) {
            return null;
        }
        return time.toLocalTime();
    }
}
