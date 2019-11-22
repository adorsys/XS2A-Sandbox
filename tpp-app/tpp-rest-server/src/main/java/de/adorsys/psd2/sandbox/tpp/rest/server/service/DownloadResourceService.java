package de.adorsys.psd2.sandbox.tpp.rest.server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DownloadResourceService {
    private static final String CLASSPATH_PREFIX = "classpath:";

    private final ResourceLoader resourceLoader;

    public Resource getResourceByTemplate(String fileName) {
        return resourceLoader.getResource(CLASSPATH_PREFIX + fileName);
    }
}
