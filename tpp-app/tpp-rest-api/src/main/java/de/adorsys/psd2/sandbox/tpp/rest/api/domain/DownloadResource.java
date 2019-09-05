package de.adorsys.psd2.sandbox.tpp.rest.api.domain;

import lombok.Value;
import org.springframework.core.io.Resource;

@Value
public class DownloadResource {
    private Resource resource;
    private String fileName;
}
