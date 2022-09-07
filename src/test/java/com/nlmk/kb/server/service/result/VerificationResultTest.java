package com.nlmk.kb.server.service.result;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nlmk.attestation.product.api.*;
import com.nlmk.attestation.product.api.specification.SpecCode;
import com.nlmk.kb.server.service.result.sending.pgp.VerificationResultsPgpAdapter;
import com.nlmk.kb.server.service.result.sending.pgp.VerificationResultsPgpAdapterImpl;
import nlmk.l3.apcs.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class VerificationResultTest {

    private final VerificationResultsPgpAdapter adapter = new VerificationResultsPgpAdapterImpl();

    @Test
    void simpleVerificationProduct() throws IOException {
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

    @Test
    void verifyResult() {
        final var result = adapter.adapt(certifiedProduct(), false);
        Assertions.assertEquals(expectedVerificationResultsPgp(), result);
    }

    /**
     * Результат Аттестации, условный, только обрабатываемые поля!
     */
    private ProductDto certifiedProduct() {
        return ProductDto.builder()
                .id(123L)
                .requests(List.of(
                        RequestDto.builder()
                                .attestationTs(new Date(1_000_000_000L))
                                .primeID("1234567890")
                                .kceh(12)
                                .status(Status.MATCHED)
                                .attestations(List.of(
                                        AttestationDto.builder().group(Group.COMMON)
                                                .code(SpecCode.EDGE_CHARACTER.getValue())
                                                .value("X").status(Status.MATCHED_MANUALLY).equal("X")
                                                .comment("Согласно требованиям заказа 2")
                                                .build(),
                                        AttestationDto.builder().group(Group.HIM)
                                                .code(SpecCode.MASS_FRACTION_H.getValue())
                                                .value("1.5").status(Status.MATCHED).max(2.0)
                                                .comment("Согласно ГОСТ 1")
                                                .build(),
                                        AttestationDto.builder().group(Group.MEH)
                                                .code(SpecCode.TEMPORARY_RESISTANCE.getValue())
                                                .value("100").status(Status.MATCHED).min(90.0).max(110.0)
                                                .comment("Согласно ГОСТ 2")
                                                .params(Params.builder().signAnalysis(10).build())
                                                .build(),
                                        AttestationDto.builder().group(Group.MEH)
                                                .code(SpecCode.IMPACT_WORK_1.getValue())
                                                .value("50").status(Status.MATCHED).min(40.0).max(70.0)
                                                .comment("Согласно ГОСТ 2")
                                                .params(Params.builder().signAnalysis(11)
                                                        .knctrator("V").temp("20").analysisId(3).build())
                                                .build(),
                                        AttestationDto.builder().group(Group.MET)
                                                .code(SpecCode.SULPHIDES.getValue())
                                                .value("3.4").status(Status.MATCHED).min(3.0)
                                                .comment("Согласно ГОСТ 3")
                                                .params(Params.builder().signAnalysis(12).build())
                                                .build(),
                                        AttestationDto.builder().group(Group.MET)
                                                .code(SpecCode.SILICATES.getValue())
                                                .value("2.3").status(Status.MATCHED).max(3.0)
                                                .comment("Согласно ГОСТ 3")
                                                .params(Params.builder().signAnalysis(13).build())
                                                .build()
                                ))
                                .build()
                ))
                .build();
    }

    /**
     * Ожидаемые результат для цеха ЦГП
     */
    private VerificationResults expectedVerificationResultsPgp() {
        return VerificationResults.newBuilder()
                .setTs("1970-01-12T13:46:40.000Z")
                .setPk(RecordPk.newBuilder().setId(123L).setSystemCode("31").build())
                .setOp(EnumOp.U)
                .setData(RecordPgpData.newBuilder()
                        .setPrimeId("1234567890").setKceh(12L).setMismatch(Status.MATCHED.getValue())
                        .setCommons(List.of(
                                RecordPgpCommons.newBuilder()
                                        .setSpecCode(SpecCode.EDGE_CHARACTER.getValue())
                                        .setSpecTypeCode(SpecCode.EDGE_CHARACTER.getTypeCode().getValue())
                                        .setSpecTypeName(SpecCode.EDGE_CHARACTER.getTypeCode().getDesc())
                                        .setSpecValue("X").setMismatch(Status.MATCHED_MANUALLY.getValue())
                                        .setDefectSuggestion("Согласно требованиям заказа 2")
                                        .setNorms(RecordPgpComNorms.newBuilder()
                                                .setListAccValues(List.of("X"))
                                                .build())
                                        .build()
                        ))
                        .setChemical(List.of(
                                RecordPgpChemical.newBuilder()
                                        .setSpecCode(SpecCode.MASS_FRACTION_H.getValue())
                                        .setSpecTypeCode(SpecCode.MASS_FRACTION_H.getTypeCode().getValue())
                                        .setSpecTypeName(SpecCode.MASS_FRACTION_H.getTypeCode().getDesc())
                                        .setSpecValue("1.5").setMismatch(Status.MATCHED.getValue())
                                        .setNote("Согласно ГОСТ 1")
                                        .setNorms(RecordPgpChemNorms.newBuilder()
                                                .setValueMax(2.0)
                                                .build())
                                        .build()
                        ))
                        .setMechanical(List.of(
                                RecordPgpMechanical.newBuilder()
                                        .setSignAnalysis(10)
                                        .setSpecifications(List.of(
                                                RecordPgpMechSpecs.newBuilder()
                                                        .setSpecCode(SpecCode.TEMPORARY_RESISTANCE.getValue())
                                                        .setSpecTypeCode(SpecCode.TEMPORARY_RESISTANCE.getTypeCode().getValue())
                                                        .setSpecTypeName(SpecCode.TEMPORARY_RESISTANCE.getTypeCode().getDesc())
                                                        .setSpecValue("100").setMismatch(Status.MATCHED.getValue())
                                                        .setNote("Согласно ГОСТ 2")
                                                        .setNorms(RecordPgpMechNorms.newBuilder()
                                                                .setValueMin(90.0).setValueMax(110.0)
                                                                .build())
                                                        .setParameters(List.of())
                                                        .build()
                                        )).build(),
                                RecordPgpMechanical.newBuilder()
                                        .setSignAnalysis(11)
                                        .setSpecifications(List.of(
                                                RecordPgpMechSpecs.newBuilder()
                                                        .setSpecCode(SpecCode.IMPACT_WORK_1.getValue())
                                                        .setSpecTypeCode(SpecCode.IMPACT_WORK_1.getTypeCode().getValue())
                                                        .setSpecTypeName(SpecCode.IMPACT_WORK_1.getTypeCode().getDesc())
                                                        .setSpecValue("50").setMismatch(Status.MATCHED.getValue())
                                                        .setNote("Согласно ГОСТ 2")
                                                        .setNorms(RecordPgpMechNorms.newBuilder()
                                                                .setValueMin(40.0).setValueMax(70.0)
                                                                .build())
                                                        .setParameters(List.of(
                                                                RecordPgpMechParams.newBuilder()
                                                                        .setCode(SpecCode.CONCENTRATOR.getValue())
                                                                        .setName(SpecCode.CONCENTRATOR.getDesc())
                                                                        .setValue("V")
                                                                        .setTypeCode(SpecCode.CONCENTRATOR.getTypeCode().getValue())
                                                                        .setTypeName(SpecCode.CONCENTRATOR.getTypeCode().getDesc())
                                                                        .build(),
                                                                RecordPgpMechParams.newBuilder()
                                                                        .setCode(SpecCode.TEMPERATURE.getValue())
                                                                        .setName(SpecCode.TEMPERATURE.getDesc())
                                                                        .setValue("20")
                                                                        .setTypeCode(SpecCode.TEMPERATURE.getTypeCode().getValue())
                                                                        .setTypeName(SpecCode.TEMPERATURE.getTypeCode().getDesc())
                                                                        .build(),
                                                                RecordPgpMechParams.newBuilder()
                                                                        .setCode(SpecCode.ANALYSIS_ID.getValue())
                                                                        .setName(SpecCode.ANALYSIS_ID.getDesc())
                                                                        .setValue("3")
                                                                        .setTypeCode(SpecCode.ANALYSIS_ID.getTypeCode().getValue())
                                                                        .setTypeName(SpecCode.ANALYSIS_ID.getTypeCode().getDesc())
                                                                        .build()
                                                        ))
                                                        .build()
                                        )).build()
                        ))
                        .setMetallographic(List.of(
                                RecordPgpMettallographic.newBuilder()
                                        .setSignAnalysis(12)
                                        .setSpecifications(List.of(
                                                RecordPgpMetSpecs.newBuilder()
                                                        .setSpecCode(SpecCode.SULPHIDES.getValue())
                                                        .setSpecTypeCode(SpecCode.SULPHIDES.getTypeCode().getValue())
                                                        .setSpecTypeName(SpecCode.SULPHIDES.getTypeCode().getDesc())
                                                        .setSpecValue("3.4")
                                                        .setMismatch(Status.MATCHED.getValue())
                                                        .setNorms(RecordPgpMetNorms.newBuilder()
                                                                .setValueMin(3.0)
                                                                .build())
                                                        .setNote("Согласно ГОСТ 3")
                                                        .setParameters(List.of())
                                                        .build()
                                        ))
                                        .build(),
                                RecordPgpMettallographic.newBuilder()
                                        .setSignAnalysis(13)
                                        .setSpecifications(List.of(
                                                RecordPgpMetSpecs.newBuilder()
                                                        .setSpecCode(SpecCode.SILICATES.getValue())
                                                        .setSpecTypeCode(SpecCode.SILICATES.getTypeCode().getValue())
                                                        .setSpecTypeName(SpecCode.SILICATES.getTypeCode().getDesc())
                                                        .setSpecValue("2.3")
                                                        .setMismatch(Status.MATCHED.getValue())
                                                        .setNorms(RecordPgpMetNorms.newBuilder()
                                                                .setValueMax(3.0)
                                                                .build())
                                                        .setNote("Согласно ГОСТ 3")
                                                        .setParameters(List.of())
                                                        .build()
                                        ))
                                        .build()
                        ))
                        .build())
                .build();
    }

}
