package com.nlmk.kb.server.service.ccm.pts;

import com.nlmk.attestation.product.api.AttestationDto;
import com.nlmk.attestation.product.api.Group;
import com.nlmk.attestation.product.api.RequestDto;
import com.nlmk.attestation.product.api.Status;
import com.nlmk.attestation.product.api.pam.ProductAttestationResultDto;
import com.nlmk.attestation.product.api.specification.SpecCode;
import com.nlmk.kb.server.api.ccm.pts.CcmPtsResponse;
import com.nlmk.kb.server.service.ccm.RestResponseAdapter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CcmPtsRestResponseAdapterImpl implements RestResponseAdapter<CcmPtsResponse> {

    @Override
    public CcmPtsResponse adapt(ProductAttestationResultDto attResult) {
        if (attResult == null || attResult.getResult() == null) {
            return CcmPtsResponse.builder().build();
        }

        final var product = attResult.getResult();

        if (product.getRequests() == null || product.getRequests().isEmpty()) {
            return CcmPtsResponse.builder().build();
        }

        final var request = product.getRequests().get(0);

        return CcmPtsResponse.builder()
                .ts(new Date())
                .pk(CcmPtsResponse.Pk.builder()
                        .id(product.getId() != null ? product.getId().toString() : null)
                        .systemCode(SpecCode.SYSTEM_CODE.getValue().toString())
                        .build())
                .data(CcmPtsResponse.Record.builder()
                        .primeSystemCode(product.getReferenceCode())
                        .primeId(request.getPrimeID())
                        .mismatch(CcmPtsResponse.Mismatch.builder()
                                .code(request.getStatus() != null ? request.getStatus().getValue() : null)
                                .name(request.getStatus() != null ? request.getStatus().getDesc() : null)
                                .build())
                        .attestationList(prepareAttestation(request))
                        .build())
                .build();
    }

    private List<CcmPtsResponse.Attestation> prepareAttestation(RequestDto request) {
        if (request == null
                || request.getAttestations() == null
                || request.getAttestations().isEmpty()) {
            return List.of();
        }

        final var attestations = new ArrayList<CcmPtsResponse.Attestation>();

        // объединение групп характеристик
        Arrays.stream(Group.values()).forEach(group -> {
            final var oneGroup = prepareAttestationValue(request.getAttestations(), group);
            if (!oneGroup.isEmpty()) {
                attestations.add(
                        CcmPtsResponse.Attestation.builder()
                                .groupCode(group.getCode())
                                .groupName(group.name())
                                .listValues(prepareAttestationValue(request.getAttestations(), group))
                                .build()
                );
            }
        });

        return attestations;
    }

    private List<CcmPtsResponse.AttestationValue> prepareAttestationValue(List<AttestationDto> attResult, Group group) {
        return attResult.stream()
                .filter(f -> group.equals(f.getGroup()))
                .map(attestation -> {
                    final var specCode = SpecCode.fromValue(attestation.getCode());
                    // пропускаем: format, measure
                    // пока нет данных: docId, docName
                    return CcmPtsResponse.AttestationValue.builder()
                            .code(specCode.getValue())
                            .name(specCode.getDesc())
                            .typeCode(specCode.getTypeCode())
                            .typeName(specCode.getTypeCode().getDesc())
                            .value(attestation.getValue())
                            .normLimits(prepareNormLimit(attestation))
                            .mismatch(CcmPtsResponse.Mismatch.builder()
                                    .code(attestation.getStatus() != null ? attestation.getStatus().getValue() : null)
                                    .name(attestation.getStatus() != null ? attestation.getStatus().getDesc() : null)
                                    .build())
                            .note(detectNote(attestation))
                            .defectSuggestion(detectDefectSuggestion(attestation))
                            .parameters(prepareParameters(attestation))
                            .build();
                })
                .collect(Collectors.toList());
    }

    private CcmPtsResponse.NormLimit prepareNormLimit(AttestationDto attestation) {
        // в объекте AttestationDto ждем либо Equal, либо Min и (или) Max
        return CcmPtsResponse.NormLimit.builder()
                .valueMin(attestation.getMin())
                .valueMax(attestation.getMax())
                .listAccValues(attestation.getEqual() == null ? null : List.of(
                        CcmPtsResponse.AccValue.builder().value(attestation.getEqual()).build()
                ))
                .build();
    }

    private List<CcmPtsResponse.Parameter> prepareParameters(AttestationDto attestation) {
        if (attestation == null || attestation.getParams() == null) {
            return List.of();
        }

        final var list = new ArrayList<CcmPtsResponse.Parameter>();

        if (attestation.getParams().getKnctrator() != null) {
            list.add(CcmPtsResponse.Parameter.builder()
                    .code(SpecCode.CONCENTRATOR.getValue())
                    .name(SpecCode.CONCENTRATOR.getDesc())
                    .value(attestation.getParams().getKnctrator())
                    .typeCode(SpecCode.CONCENTRATOR.getTypeCode())
                    .typeName(SpecCode.CONCENTRATOR.getTypeCode().getDesc())
                    .build());
        }
        if (attestation.getParams().getTemp() != null) {
            list.add(CcmPtsResponse.Parameter.builder()
                    .code(SpecCode.TEMPERATURE.getValue())
                    .name(SpecCode.TEMPERATURE.getDesc())
                    .value(attestation.getParams().getTemp())
                    .typeCode(SpecCode.TEMPERATURE.getTypeCode())
                    .typeName(SpecCode.TEMPERATURE.getTypeCode().getDesc())
                    .build());
        }
        if (attestation.getParams().getAnalysisId() != null) {
            list.add(CcmPtsResponse.Parameter.builder()
                    .code(SpecCode.ANALYSIS_ID.getValue())
                    .name(SpecCode.ANALYSIS_ID.getDesc())
                    .value(attestation.getParams().getAnalysisId().toString())
                    .typeCode(SpecCode.ANALYSIS_ID.getTypeCode())
                    .typeName(SpecCode.ANALYSIS_ID.getTypeCode().getDesc())
                    .build());
        }

        return list;
    }

    private String detectNote(AttestationDto attestation) {
        if (Status.NOT_MATCHED_WITH_RECOMMENDATIONS == attestation.getStatus()
                || Status.MATCHED_MANUALLY == attestation.getStatus()) {
            return null;
        }
        return attestation.getComment();
    }

    private String detectDefectSuggestion(AttestationDto attestation) {
        if (Status.NOT_MATCHED_WITH_RECOMMENDATIONS == attestation.getStatus()
                || Status.MATCHED_MANUALLY == attestation.getStatus()) {
            return attestation.getComment();
        }
        return null;
    }

}
