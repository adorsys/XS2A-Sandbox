package de.adorsys.psd2.sandbox.tpp.rest.server.controller;

import de.adorsys.psd2.sandbox.tpp.rest.api.resource.TppDataUploaderRestApi;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(TppDataUploaderRestApi.BASE_PATH)
public class TppDataUploaderController implements TppDataUploaderRestApi {

    @Override
    public ResponseEntity<String> uploadData(MultipartFile file) {
        return null;
    }

    @Override
    public ResponseEntity<Resource> generateData() {
        return null;
    }
}
