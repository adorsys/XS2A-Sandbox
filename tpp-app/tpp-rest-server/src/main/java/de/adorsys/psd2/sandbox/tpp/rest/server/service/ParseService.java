package de.adorsys.psd2.sandbox.tpp.rest.server.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import de.adorsys.ledgers.middleware.api.domain.um.UserTO;
import de.adorsys.psd2.sandbox.tpp.rest.server.model.DataPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ParseService {
    private static ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
    private static final String DEFAULT_TEMPLATE_YML = "classpath:NISP_Testing_Default_Template.yml";

    private final ResourceLoader resourceLoader;

    public DataPayload getDataFromFile(MultipartFile input) {
        try {
            DataPayload payload = objectMapper.readValue(input.getInputStream(), DataPayload.class);
            return checkPayload(payload)
                       ? payload
                       : null;
        } catch (IOException e) {
            log.error("Could not map file to Object. \n {}", e.getMessage());
            return null;
        }
    }

    public Optional<DataPayload> getDefaultData() {
        try {
            return Optional.of(objectMapper.readValue(loadDefaultTemplate(), DataPayload.class));
        } catch (IOException e) {
            log.error("Could not readout default NISP file template");
            return Optional.empty();
        }
    }

    private InputStream loadDefaultTemplate() {
        Resource resource = resourceLoader.getResource(DEFAULT_TEMPLATE_YML);
        try {
            return resource.getInputStream();
        } catch (IOException e) {
            log.error("PSD2 api file is not found", e);
            throw new IllegalArgumentException("PSD2 api file is not found");
        }
    }

    public byte[] getFile(DataPayload data) {
        try {
            return objectMapper.writeValueAsBytes(data);
        } catch (IOException e) {
            log.error("Could not write bytes");
            return new byte[]{};
        }
    }

    private boolean checkPayload(DataPayload payload) {
        return containsNotNullObjs(payload.getAccounts())
                   && containsNotNullObjs(payload.getBalancesList())
                   && containsNotNullObjs(payload.getUsers())
                   && payload.getUsers().stream()
                          .noneMatch(UserTO::userHasRoles);
    }

    private boolean containsNotNullObjs(Collection collection) {
        return collection == null || !collection.contains(null);
    }
}
