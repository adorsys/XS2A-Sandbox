package de.adorsys.psd2.sandbox.tpp.rest.server.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import de.adorsys.psd2.sandbox.tpp.rest.server.exception.TppException;
import de.adorsys.psd2.sandbox.tpp.rest.server.model.DataPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ParseService {
    private static final String DEFAULT_TEMPLATE_YML = "classpath:NISP_Testing_Default_Template.yml";
    private static final ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());

    private final ResourceLoader resourceLoader;

    public <T> Optional<T> getDataFromFile(MultipartFile input, TypeReference<T> typeReference) {
        try {
            return Optional.ofNullable(objectMapper.readValue(input.getInputStream(), typeReference));
        } catch (IOException e) {
            log.error("Could not map file to Object. \n {}", e.getMessage());
            return Optional.empty();
        }
    }

    public Optional<DataPayload> getDefaultData() {
        try {
            return Optional.ofNullable(objectMapper.readValue(loadDefaultTemplate(), DataPayload.class));
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

    public byte[] generateFileByPayload(DataPayload data) {
        try {
            return objectMapper.writeValueAsBytes(data);
        } catch (IOException e) {
            log.error("Could not write bytes");
            return new byte[]{};
        }
    }

    public <T> List<T> convertFileToTargetObject(MultipartFile file, Class<T> target) {
        if (file == null) {
            throw new TppException("No file uploaded!", 400);
        }
        try {
            InputStreamReader streamReader = new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8);
            CsvToBean<T> csvToBean = new CsvToBeanBuilder<T>(streamReader)
                                         .withType(target)
                                         .withIgnoreLeadingWhiteSpace(true)
                                         .build();
            return csvToBean.parse();
        } catch (IOException e) {
            log.error("Can't convert file to target class: {}", target.getSimpleName(), e);
            throw new TppException("Can't convert file to target class", 400);
        }
    }
}
