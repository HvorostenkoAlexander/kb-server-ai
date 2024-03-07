package com.nlmk.kb.server.service.ccm.pts;

import com.nlmk.attestation.product.api.AttestationDto;
import com.nlmk.attestation.product.api.Group;
import com.nlmk.attestation.product.api.RequestDto;
import com.nlmk.attestation.product.api.pam.ProductAttestationResultDto;
import com.nlmk.attestation.product.api.specification.SpecCode;
import com.nlmk.kb.server.api.ccm.pts.CcmPtsResponse;
import com.nlmk.kb.server.service.ccm.RestResponseAdapter;
import com.nlmk.kb.server.util.AdapterUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
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
            final var oneGroupValues = prepareAttestationValue(request.getAttestations(), group);
            if (!oneGroupValues.isEmpty()) {
                attestations.add(
                        CcmPtsResponse.Attestation.builder()
                                .groupCode(group.getCode())
                                .groupName(group.name())
                                .listValues(oneGroupValues)
                                .build()
                );
            }
        });

        return attestations;
    }

    private List<CcmPtsResponse.AttestationValue> prepareAttestationValue(List<AttestationDto> attResult, Group group) {
        return attResult.stream()
                .filter(attestation -> group.equals(attestation.getGroup()))
                .filter(attestation -> Objects.nonNull(attestation.getCode()))
                .map(attestation -> {
                    final var specCode = SpecCode.fromValue(attestation.getCode());
                    // пропускаем: format, measure
                    return CcmPtsResponse.AttestationValue.builder()
                            .code(specCode.getValue())
                            .name(specCode.getDesc())
                            .typeCode(specCode.getTypeCode())
                            .typeName(specCode.getTypeCode().getDesc())
                            .value(attestation.getValue())
                            .docId(attestation.getDocId() != null ? attestation.getDocId().getValue() : null)
                            .docName(attestation.getDocId() != null ? attestation.getDocId().getDesc() : null)
                            .normLimits(prepareNormLimit(attestation))
                            .mismatch(CcmPtsResponse.Mismatch.builder()
                                    .code(attestation.getStatus() != null ? attestation.getStatus().getValue() : null)
                                    .name(attestation.getStatus() != null ? attestation.getStatus().getDesc() : null)
                                    .build())
                            .note(AdapterUtils.detectNote(attestation))
                            .defectSuggestion(AdapterUtils.detectDefectSuggestion(attestation))
                            .parameters(prepareParameters(attestation))
                            .build();
                })
                .collect(Collectors.toList());
    }

    private CcmPtsResponse.NormLimit prepareNormLimit(AttestationDto attestation) {
        return CcmPtsResponse.NormLimit.builder()
                .valueMin(attestation.getMin())
                .valueMax(attestation.getMax())
                .listAccValues(attestation.getEqual() == null ? null : List.of(
                        CcmPtsResponse.AccValue.builder().value(attestation.getEqual()).build()
                ))
                .build();
    }

    private List<CcmPtsResponse.Parameter> prepareParameters(AttestationDto attestation) {
        final var map = AdapterUtils.prepareParameters(attestation);
        if (map.isEmpty()) {
            return List.of();
        }

        return map.entrySet().stream()
                .map(p -> CcmPtsResponse.Parameter.builder()
                        .code(p.getKey().getValue())
                        .name(p.getKey().getDesc())
                        .value(p.getValue())
                        .typeCode(p.getKey().getTypeCode())
                        .typeName(p.getKey().getTypeCode().getDesc())
                        .build())
                .collect(Collectors.toList());
    }

}
