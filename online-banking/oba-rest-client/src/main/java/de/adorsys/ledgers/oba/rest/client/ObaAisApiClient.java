package de.adorsys.ledgers.oba.rest.client;

import org.springframework.cloud.openfeign.FeignClient;

import de.adorsys.ledgers.oba.rest.api.resource.AISApi;
import de.adorsys.ledgers.oba.rest.utils.RemoteURLs;

@FeignClient(value = "obaAisApi", url = RemoteURLs.OBA_URL, path = AISApi.BASE_PATH, configuration=FeignConfig.class)
public interface ObaAisApiClient extends AISApi {}
