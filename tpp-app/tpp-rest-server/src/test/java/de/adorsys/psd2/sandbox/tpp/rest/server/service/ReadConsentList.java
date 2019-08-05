package de.adorsys.psd2.sandbox.tpp.rest.server.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import de.adorsys.psd2.sandbox.tpp.cms.api.domain.AisConsent;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ReadConsentList {
    private ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

    @Test
    public void readConsentList() throws IOException {
        InputStream resourceAsStream = ClassLoader.getSystemResourceAsStream("consents_template.yml");
        List<AisConsent> consents = mapper.readValue(resourceAsStream, new TypeReference<List<AisConsent>>() {
        });

        consents.forEach(this::assertNoNullFields);
    }

    private void assertNoNullFields(AisConsent consent) {
        assertThat(consent).hasNoNullFieldsOrProperties();
        assertThat(consent.getPsuInfo()).hasNoNullFieldsOrProperties();
        assertThat(consent.getTppInfo()).hasNoNullFieldsOrProperties();
        consent.getAccess().getAccounts().forEach(a -> assertThat(a).hasNoNullFieldsOrProperties());
        consent.getAccess().getBalances().forEach(a -> assertThat(a).hasNoNullFieldsOrProperties());
        consent.getAccess().getTransactions().forEach(a -> assertThat(a).hasNoNullFieldsOrProperties());
    }
}
