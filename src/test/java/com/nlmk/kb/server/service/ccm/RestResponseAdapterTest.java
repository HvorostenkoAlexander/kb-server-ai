package com.nlmk.kb.server.service.ccm;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nlmk.attestation.product.api.*;
import com.nlmk.attestation.product.api.pam.ProductAttestationResultDto;
import com.nlmk.attestation.product.api.specification.SpecCode;
import com.nlmk.kb.server.api.ccm.pts.CcmPtsResponse;
import com.nlmk.kb.server.service.ccm.pts.CcmPtsRestResponseAdapterImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;

class RestResponseAdapterTest {

    private final RestResponseAdapter<CcmPtsResponse> ccmPtsAdapter = new CcmPtsRestResponseAdapterImpl();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void adaptCcmPts() throws Exception {
        final var attResult = prepareAttestationResult();
        final var response = ccmPtsAdapter.adapt(attResult);
        response.setTs(new Date(1000000000_000L)); // для теста!
        final var responseJson = objectMapper.writeValueAsString(response);
        final var expected = expectedCcmPtsResponse();

        // однообразие JSON представления
        Assertions.assertEquals(objectMapper.writeValueAsString(expected), responseJson);

        // формат дата время
        final var tree = objectMapper.readTree(responseJson);
        final var ts = tree.findPath("ts");
        Assertions.assertNotNull(ts);
        Assertions.assertEquals("2001-09-09T01:46:40.000Z", ts.asText());
    }

    private ProductAttestationResultDto prepareAttestationResult() {
        return ProductAttestationResultDto.builder()
                .result(ProductDto.builder()
                        .id(1L)
                        .referenceCode("33")
                        .requests(List.of(
                                RequestDto.builder()
                                        .id(2L)
                                        .primeID("54321")
                                        .status(Status.NO_NEED_ATTESTATION)
                                        .attestations(List.of(
                                                AttestationDto.builder()
                                                        .code(SpecCode.LENGTH.getValue())
                                                        .value("20").group(Group.GEOM)
                                                        .equal("25").status(Status.NOT_MATCHED)
                                                        .comment("Согласно заказа")
                                                        .build(),
                                                AttestationDto.builder()
                                                        .code(SpecCode.MASS_FRACTION_B.getValue())
                                                        .value("10").group(Group.HIM)
                                                        .min(9.0).max(18.0).status(Status.NO_NEED_ATTESTATION)
                                                        .comment("Согласно ГОСТ 1")
                                                        .build(),
                                                AttestationDto.builder()
                                                        .code(SpecCode.WIDTH.getValue())
                                                        .value("100").group(Group.GEOM)
                                                        .min(null).max(108.0).status(Status.MATCHED_MANUALLY)
                                                        .comment("Согласно ГОСТ 2")
                                                        .build(),
                                                AttestationDto.builder()
                                                        .code(SpecCode.IMPACT_WORK_1.getValue())
                                                        .value("100").group(Group.MEH)
                                                        .min(90.0).max(110.0).status(Status.MATCHED)
                                                        .comment("Согласно ГОСТ 3")
                                                        .params(Params.builder()
                                                                .analysisId(10)
                                                                .knctrator("V").temp("30").analysisId(40)
                                                                .build())
                                                        .build()
                                        ))
                                        .build()
                        ))
                        .build())
                .build();
    }

    private CcmPtsResponse expectedCcmPtsResponse() {
        return CcmPtsResponse.builder()
                .ts(new Date(1000000000_000L))
                .pk(CcmPtsResponse.Pk.builder().id("1").systemCode("31").build())
                .data(CcmPtsResponse.Record.builder()
                        .primeSystemCode("33").primeId("54321")
                        .mismatch(CcmPtsResponse.Mismatch.builder().code(3).name("Аттестация не требуется").build())
                        .attestationList(List.of(
                                // в порядке элементов внутри Group ENUM
                                CcmPtsResponse.Attestation.builder()
                                        .groupCode(Group.HIM.getCode())
                                        .groupName(Group.HIM.name())
                                        .listValues(List.of(
                                                CcmPtsResponse.AttestationValue.builder()
                                                        .code(SpecCode.MASS_FRACTION_B.getValue())
                                                        .name(SpecCode.MASS_FRACTION_B.getDesc())
                                                        .typeCode(SpecCode.MASS_FRACTION_B.getTypeCode())
                                                        .typeName(SpecCode.MASS_FRACTION_B.getTypeCode().getDesc())
                                                        .value("10")
                                                        .docId(0).docName("Заказ")
                                                        .normLimits(CcmPtsResponse.NormLimit.builder()
                                                                .valueMin(9.0).valueMax(18.0)
                                                                .build())
                                                        .mismatch(CcmPtsResponse.Mismatch.builder()
                                                                .code(Status.NO_NEED_ATTESTATION.getValue())
                                                                .name(Status.NO_NEED_ATTESTATION.getDesc())
                                                                .build())
                                                        .note("Согласно ГОСТ 1")
                                                        .parameters(List.of())
                                                        .build()
                                        ))
                                        .build(),
                                CcmPtsResponse.Attestation.builder()
                                        .groupCode(Group.MEH.getCode())
                                        .groupName(Group.MEH.name())
                                        .listValues(List.of(
                                                CcmPtsResponse.AttestationValue.builder()
                                                        .code(SpecCode.IMPACT_WORK_1.getValue())
                                                        .name(SpecCode.IMPACT_WORK_1.getDesc())
                                                        .typeCode(SpecCode.IMPACT_WORK_1.getTypeCode())
                                                        .typeName(SpecCode.IMPACT_WORK_1.getTypeCode().getDesc())
                                                        .value("100")
                                                        .docId(0).docName("Заказ")
                                                        .normLimits(CcmPtsResponse.NormLimit.builder()
                                                                .valueMin(90.0).valueMax(110.0)
                                                                .build())
                                                        .mismatch(CcmPtsResponse.Mismatch.builder()
                                                                .code(Status.MATCHED.getValue())
                                                                .name(Status.MATCHED.getDesc())
                                                                .build())
                                                        .note("Согласно ГОСТ 3")
                                                        // порядок элементов определяет AdapterUtils.prepareParameters()
                                                        .parameters(List.of(
                                                                CcmPtsResponse.Parameter.builder()
                                                                        .code(SpecCode.TEMPERATURE.getValue())
                                                                        .name(SpecCode.TEMPERATURE.getDesc())
                                                                        .value("30")
                                                                        .typeCode(SpecCode.TEMPERATURE.getTypeCode())
                                                                        .typeName(SpecCode.TEMPERATURE.getTypeCode().getDesc())
                                                                        .build(),
                                                                CcmPtsResponse.Parameter.builder()
                                                                        .code(SpecCode.CONCENTRATOR.getValue())
                                                                        .name(SpecCode.CONCENTRATOR.getDesc())
                                                                        .value("V")
                                                                        .typeCode(SpecCode.CONCENTRATOR.getTypeCode())
                                                                        .typeName(SpecCode.CONCENTRATOR.getTypeCode().getDesc())
                                                                        .build(),
                                                                CcmPtsResponse.Parameter.builder()
                                                                        .code(SpecCode.ANALYSIS_ID.getValue())
                                                                        .name(SpecCode.ANALYSIS_ID.getDesc())
                                                                        .value("40")
                                                                        .typeCode(SpecCode.ANALYSIS_ID.getTypeCode())
                                                                        .typeName(SpecCode.ANALYSIS_ID.getTypeCode().getDesc())
                                                                        .build()
                                                        ))
                                                        .build()
                                        ))
                                        .build(),
                                CcmPtsResponse.Attestation.builder()
                                        .groupCode(Group.GEOM.getCode())
                                        .groupName(Group.GEOM.name())
                                        .listValues(List.of(
                                                CcmPtsResponse.AttestationValue.builder()
                                                        .code(SpecCode.LENGTH.getValue())
                                                        .name(SpecCode.LENGTH.getDesc())
                                                        .typeCode(SpecCode.LENGTH.getTypeCode())
                                                        .typeName(SpecCode.LENGTH.getTypeCode().getDesc())
                                                        .value("20")
                                                        .docId(0).docName("Заказ")
                                                        .normLimits(CcmPtsResponse.NormLimit.builder()
                                                                .listAccValues(List.of(
                                                                        CcmPtsResponse.AccValue.builder().value("25").build()
                                                                ))
                                                                .build())
                                                        .mismatch(CcmPtsResponse.Mismatch.builder()
                                                                .code(Status.NOT_MATCHED.getValue())
                                                                .name(Status.NOT_MATCHED.getDesc())
                                                                .build())
                                                        .note("Согласно заказа")
                                                        .parameters(List.of())
                                                        .build(),
                                                CcmPtsResponse.AttestationValue.builder()
                                                        .code(SpecCode.WIDTH.getValue())
                                                        .name(SpecCode.WIDTH.getDesc())
                                                        .typeCode(SpecCode.WIDTH.getTypeCode())
                                                        .typeName(SpecCode.WIDTH.getTypeCode().getDesc())
                                                        .value("100")
                                                        .docId(0).docName("Заказ")
                                                        .normLimits(CcmPtsResponse.NormLimit.builder()
                                                                .valueMax(108.0)
                                                                .build())
                                                        .mismatch(CcmPtsResponse.Mismatch.builder()
                                                                .code(Status.MATCHED_MANUALLY.getValue())
                                                                .name(Status.MATCHED_MANUALLY.getDesc())
                                                                .build())
                                                        .note(null)
                                                        .defectSuggestion("Согласно ГОСТ 2")
                                                        .parameters(List.of())
                                                        .build()
                                        ))
                                        .build()
                        ))
                        .build())
                .build();
    }

}
