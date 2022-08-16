package com.nlmk.kb.server.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nlmk.attestation.product.api.AttestationDto;
import com.nlmk.attestation.product.api.Group;
import com.nlmk.attestation.product.api.ProductDto;
import com.nlmk.kb.server.service.result.sending.VerificationResultsAdapter;
import com.nlmk.kb.server.service.result.sending.VerificationResultsAdapterImpl;
import nlmk.l3.apcs.VerificationResults;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class VerificationResultTest {

    private final VerificationResultsAdapter adapter = new VerificationResultsAdapterImpl();

    @Test
    void verificationProduct() throws IOException {
        ProductDto product = new ObjectMapper()
                //.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"))
                .readValue(
                        getClass().getClassLoader().getResourceAsStream("json/att_result_product_98_20210518.json"),
                        ProductDto.class
                );
        product.setId(1000L);

        List<AttestationDto> commonAttestation = product.getRequests().get(0).getAttestations().stream()
                .filter(attestation ->
                        !(attestation.getGroup().equals(Group.HIM) ||
                                attestation.getGroup().equals(Group.MEH) ||
                                attestation.getGroup().equals(Group.MET))
                ).collect(Collectors.toList());

        List<AttestationDto> chemicalAttestation = product.getRequests().get(0).getAttestations().stream()
                .filter(attestation -> attestation.getGroup().equals(Group.HIM))
                .collect(Collectors.toList());

        List<AttestationDto> mechAttestation = product.getRequests().get(0).getAttestations().stream()
                .filter(attestation -> attestation.getGroup().equals(Group.MEH))
                .collect(Collectors.toList());

        List<AttestationDto> metallAttestation = product.getRequests().get(0).getAttestations().stream()
                .filter(attestation -> attestation.getGroup().equals(Group.MET))
                .collect(Collectors.toList());

        VerificationResults results = adapter.adapt(product, true);

        final var resultMech = results.getData().getMechanical().stream()
                .map(l -> l.getSpecifications().size()).mapToInt(i -> i).sum();
        final var resultMet = results.getData().getMetallographic().stream()
                .map(l -> l.getSpecifications().size()).mapToInt(i -> i).sum();

        assertNotNull(results);
        assertEquals(commonAttestation.size(), results.getData().getCommons().size());
        assertEquals(chemicalAttestation.size(), results.getData().getChemical().size());
        assertEquals(mechAttestation.size(), resultMech);
        assertEquals(metallAttestation.size(), resultMet);
    }

}
