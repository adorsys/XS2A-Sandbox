package de.adorsys.ledgers.oba.mapper;

import java.time.LocalTime;
import java.time.OffsetDateTime;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public class TimeMapper {
	
    @SuppressWarnings("PMD.ShortMethodName")
	public LocalTime map(OffsetDateTime time){
    	return time.toLocalTime();
	}
}
