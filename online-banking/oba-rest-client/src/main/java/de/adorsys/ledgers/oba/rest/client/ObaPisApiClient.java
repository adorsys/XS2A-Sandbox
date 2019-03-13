package de.adorsys.ledgers.oba.rest.client;

import org.springframework.cloud.openfeign.FeignClient;

import de.adorsys.ledgers.oba.rest.api.resource.PISApi;
import de.adorsys.ledgers.oba.rest.utils.RemoteURLs;

@FeignClient(value = "obaPisApi", url = RemoteURLs.OBA_URL, path = PISApi.BASE_PATH, configuration=FeignConfig.class)
public interface ObaPisApiClient extends PISApi{}
