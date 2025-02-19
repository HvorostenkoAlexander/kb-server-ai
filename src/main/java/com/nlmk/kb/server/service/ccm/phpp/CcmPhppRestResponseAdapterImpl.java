package com.nlmk.kb.server.service.ccm.phpp;

import com.nlmk.attestation.product.api.AttestationDto;
import com.nlmk.attestation.product.api.Group;
import com.nlmk.attestation.product.api.RequestDto;
import com.nlmk.attestation.product.api.pam.ProductAttestationResultDto;
import com.nlmk.attestation.product.api.specification.SpecCode;
import com.nlmk.kb.server.api.ccm.phpp.CcmPhppResponse;
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
public class CcmPhppRestResponseAdapterImpl implements RestResponseAdapter<CcmPhppResponse> {

    @Override
    public CcmPhppResponse adapt(ProductAttestationResultDto attResult) {
        if (attResult == null || attResult.getResult() == null) {
            return CcmPhppResponse.builder().build();
        }

        final var product = attResult.getResult();

        if (product.getRequests() == null || product.getRequests().isEmpty()) {
            return CcmPhppResponse.builder().build();
        }

        final var request = product.getRequests().get(0);

        return CcmPhppResponse.builder()
                .ts(new Date())
                .pk(CcmPhppResponse.Pk.builder()
                        .id(product.getId() != null ? product.getId().toString() : null)
                        .systemCode(SpecCode.SYSTEM_CODE.getValue().toString())
                        .build())
                .data(CcmPhppResponse.Record.builder()
                        .primeSystemCode(product.getReferenceCode())
                        .primeId(request.getPrimeID())
                        .mismatch(CcmPhppResponse.Mismatch.builder()
                                .code(request.getStatus() != null ? request.getStatus().getValue() : null)
                                .name(request.getStatus() != null ? request.getStatus().getDesc() : null)
                                .build())
                        .attestationList(prepareAttestation(request))
                        .build())
                .build();
    }

    private List<CcmPhppResponse.Attestation> prepareAttestation(RequestDto request) {
        if (request == null
                || request.getAttestations() == null
                || request.getAttestations().isEmpty()) {
            return List.of();
        }

        final var attestations = new ArrayList<CcmPhppResponse.Attestation>();

        // объединение групп характеристик
        Arrays.stream(Group.values()).forEach(group -> {
            final var oneGroupValues = prepareAttestationValue(request.getAttestations(), group);
            if (!oneGroupValues.isEmpty()) {
                attestations.add(
                        CcmPhppResponse.Attestation.builder()
                                .groupCode(group.getCode())
                                .groupName(group.name())
                                .listValues(oneGroupValues)
                                .build()
                );
            }
        });

        return attestations;
    }

    private List<CcmPhppResponse.AttestationValue> prepareAttestationValue(List<AttestationDto> attResult, Group group) {
        return attResult.stream()
                .filter(attestation -> group.equals(attestation.getGroup()))
                .filter(attestation -> Objects.nonNull(attestation.getCode()))
                .map(attestation -> {
                    final var specCode = SpecCode.fromValue(attestation.getCode());
                    // пропускаем: format, measure
                    return CcmPhppResponse.AttestationValue.builder()
                            .code(specCode.getValue())
                            .name(specCode.getDesc())
                            .typeCode(specCode.getTypeCode())
                            .typeName(specCode.getTypeCode().getDesc())
                            .value(attestation.getValue())
                            .docId(attestation.getDocId() != null ? attestation.getDocId().getValue() : null)
                            .docName(attestation.getDocId() != null ? attestation.getDocId().getDesc() : null)
                            .normLimits(prepareNormLimit(attestation))
                            .mismatch(CcmPhppResponse.Mismatch.builder()
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

    private CcmPhppResponse.NormLimit prepareNormLimit(AttestationDto attestation) {
        return CcmPhppResponse.NormLimit.builder()
                .valueMin(attestation.getMin())
                .valueMax(attestation.getMax())
                .listAccValues(attestation.getEqual() == null ? null : List.of(
                        CcmPhppResponse.AccValue.builder().value(attestation.getEqual()).build()
                ))
                .build();
    }

    private List<CcmPhppResponse.Parameter> prepareParameters(AttestationDto attestation) {
        final var map = AdapterUtils.prepareParameters(attestation);
        if (map.isEmpty()) {
            return List.of();
        }

        return map.entrySet().stream()
                .map(p -> CcmPhppResponse.Parameter.builder()
                        .code(p.getKey().getValue())
                        .name(p.getKey().getDesc())
                        .value(p.getValue())
                        .typeCode(p.getKey().getTypeCode())
                        .typeName(p.getKey().getTypeCode().getDesc())
                        .build())
                .collect(Collectors.toList());
    }

}
