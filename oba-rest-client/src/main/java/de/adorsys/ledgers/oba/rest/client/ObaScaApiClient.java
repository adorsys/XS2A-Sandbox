package de.adorsys.ledgers.oba.rest.client;

import org.springframework.cloud.openfeign.FeignClient;

import de.adorsys.ledgers.oba.rest.api.resource.SCAApi;
import de.adorsys.ledgers.oba.rest.utils.RemoteURLs;

@FeignClient(value = "obaScaApi", url = RemoteURLs.OBA_URL, path = SCAApi.BASE_PATH, configuration=FeignConfig.class)
public interface ObaScaApiClient extends SCAApi{}
