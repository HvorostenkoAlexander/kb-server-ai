package com.nlmk.kb.server.service.ccm.kc;

import com.nlmk.attestation.product.api.AttestationDto;
import com.nlmk.attestation.product.api.Group;
import com.nlmk.attestation.product.api.RequestDto;
import com.nlmk.attestation.product.api.specification.SpecCode;
import com.nlmk.kb.server.api.ccm.kc.response.AccValue;
import com.nlmk.kb.server.api.ccm.kc.response.Attestation;
import com.nlmk.kb.server.api.ccm.kc.response.AttestationValue;
import com.nlmk.kb.server.api.ccm.kc.response.Mismatch;
import com.nlmk.kb.server.api.ccm.kc.response.NormLimit;
import com.nlmk.kb.server.api.ccm.kc.response.Parameter;
import com.nlmk.kb.server.util.AdapterUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class CcmKcRestResponseAdapter {

    protected List<Attestation> prepareAttestation(RequestDto request) {
        if (request == null
                || request.getAttestations() == null
                || request.getAttestations().isEmpty()) {
            return List.of();
        }

        final var attestations = new ArrayList<Attestation>();

        // объединение групп характеристик
        Arrays.stream(Group.values()).forEach(group -> {
            final var oneGroupValues = prepareAttestationValue(request.getAttestations(), group);
            if (!oneGroupValues.isEmpty()) {
                attestations.add(
                        Attestation.builder()
                                .groupCode(group.getCode())
                                .groupName(group.name())
                                .listValues(oneGroupValues)
                                .build()
                );
            }
        });

        return attestations;

    }

    private List<AttestationValue> prepareAttestationValue(List<AttestationDto> attestations, Group group) {
        return attestations.stream()
                .filter(attestation -> group.equals(attestation.getGroup()))
                .filter(attestation -> Objects.nonNull(attestation.getCode()))
                .map(attestation -> {
                    final var specCode = SpecCode.fromValue(attestation.getCode());
                    // пропускаем: format, measure
                    return AttestationValue.builder()
                            .code(specCode.getValue())
                            .name(specCode.getDesc())
                            .typeCode(specCode.getTypeCode())
                            .typeName(specCode.getTypeCode().getDesc())
                            .value(attestation.getValue())
                            .normLimits(prepareNormLimit(attestation))
                            .mismatch(Mismatch.builder()
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

    private NormLimit prepareNormLimit(AttestationDto attestation) {
        return NormLimit.builder()
                .valueMin(attestation.getMin())
                .valueMax(attestation.getMax())
                .listAccValues(attestation.getEqual() == null ? null : List.of(
                        AccValue.builder().value(attestation.getEqual()).build()
                ))
                .build();
    }

    private List<Parameter> prepareParameters(AttestationDto attestation) {
        final var map = AdapterUtils.prepareParameters(attestation);
        if (map.isEmpty()) {
            return List.of();
        }

        return map.entrySet().stream()
                .map(p -> Parameter.builder()
                        .code(p.getKey().getValue())
                        .name(p.getKey().getDesc())
                        .value(p.getValue())
                        .typeCode(p.getKey().getTypeCode())
                        .typeName(p.getKey().getTypeCode().getDesc())
                        .build())
                .collect(Collectors.toList());
    }

}
