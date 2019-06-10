package de.adorsys.psd2.sandbox.tpp.rest.server.controller;

import de.adorsys.psd2.sandbox.tpp.rest.api.resource.TppDataUploaderApi;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class TppDataUploaderController implements TppDataUploaderApi {

    @Override
    public ResponseEntity<String> uploadYamlData(MultipartFile file) {
        return null;
    }

    @Override
    public ResponseEntity<Resource> generateFile() {
        return null;
    }
}
