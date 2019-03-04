package de.adorsys.ledgers.oba.rest.server.mapper;

import java.time.LocalTime;
import java.time.OffsetDateTime;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public class TimeMapper {
	
    @SuppressWarnings("PMD.ShortMethodName")
	public LocalTime map(OffsetDateTime time){
    	if(time==null) {
    		return null;
    	}
    	return time.toLocalTime();
	}
}
