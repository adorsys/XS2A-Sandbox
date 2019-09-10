package de.adorsys.psd2.sandbox.tpp.rest.server.service;

import de.adorsys.psd2.sandbox.tpp.rest.api.domain.DownloadResource;
import de.adorsys.psd2.sandbox.tpp.rest.server.exception.TppException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class DownloadResourceService {
    private static final String CAN_NOT_READ_FILE = "Can't read file from resources";
    private final ResourceLoader resourceLoader;

    public DownloadResource getResourceByTemplate(String template) {
        try {
            Resource resource = resourceLoader.getResource(template);
            return new DownloadResource(resource, resource.getFile().getName());
        } catch (IOException e) {
            log.error(CAN_NOT_READ_FILE+ "msg: {}, stack: {}",e.getMessage(),e.getStackTrace());
        }
        throw new TppException(CAN_NOT_READ_FILE, 400);
    }
}
