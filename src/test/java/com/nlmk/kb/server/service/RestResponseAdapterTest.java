package com.nlmk.kb.server.service;

import com.nlmk.attestation.product.api.*;
import com.nlmk.attestation.product.api.pam.ProductAttestationResultDto;
import com.nlmk.attestation.product.api.specification.SpecCode;
import com.nlmk.kb.server.api.ccm.pts.CcmPtsResponse;
import com.nlmk.kb.server.service.ccm.RestResponseAdapter;
import com.nlmk.kb.server.service.ccm.pts.CcmPtsRestResponseAdapterImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;

class RestResponseAdapterTest {

    private final RestResponseAdapter<CcmPtsResponse> ccmPtsAdapter = new CcmPtsRestResponseAdapterImpl();

    @Test
    void adaptCcmPts() {
        final var attResult = prepareAttestationResult();
        final var response = ccmPtsAdapter.adapt(attResult);
        response.setTs(new Date(1000000000_000L)); // для теста!
        Assertions.assertEquals(prepareCcmPtsResponse(), response);
    }

    private ProductAttestationResultDto prepareAttestationResult() {
        return ProductAttestationResultDto.builder()
                .result(ProductDto.builder()
                        .id(123L)
                        .referenceCode("33")
                        .requests(List.of(
                                RequestDto.builder()
                                        .id(123L)
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
                                                        .build()
                                        ))
                                        .build()
                        ))
                        .build())
                .build();
    }

    private CcmPtsResponse prepareCcmPtsResponse() {
        return CcmPtsResponse.builder()
                .ts(new Date(1000000000_000L))
                .pk(CcmPtsResponse.Pk.builder().id("123").systemCode("31").build())
                .data(CcmPtsResponse.Record.builder()
                        .primeSystemCode("33").primeId("54321")
                        .mismatch(CcmPtsResponse.Mismatch.builder().code(3).name("Аттестация не требуется").build())
                        .attestationList(List.of(
                                CcmPtsResponse.Attestation.builder()
                                        .groupCode(-1)
                                        .groupName(Group.HIM.name())
                                        .listValues(List.of(
                                                CcmPtsResponse.AttestationValue.builder()
                                                        .code(SpecCode.MASS_FRACTION_B.getValue())
                                                        .name(SpecCode.MASS_FRACTION_B.getDesc())
                                                        .value("10")
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
                                        .groupCode(-1)
                                        .groupName(Group.GEOM.name())
                                        .listValues(List.of(
                                                CcmPtsResponse.AttestationValue.builder()
                                                        .code(SpecCode.LENGTH.getValue())
                                                        .name(SpecCode.LENGTH.getDesc())
                                                        .value("20")
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
                                                        .build()
                                        ))
                                        .build()
                        ))
                        .build())
                .build();
    }

}
