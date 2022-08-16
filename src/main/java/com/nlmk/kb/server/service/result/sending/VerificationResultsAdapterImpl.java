package com.nlmk.kb.server.service.result.sending;

import com.nlmk.attestation.product.api.*;
import com.nlmk.attestation.product.api.specification.SpecCode;
import com.nlmk.attestation.product.api.specification.TypeCode;
import lombok.extern.slf4j.Slf4j;
import nlmk.l3.apcs.*;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class VerificationResultsAdapterImpl implements VerificationResultsAdapter {

    @Override
    public VerificationResults adapt(ProductDto product, boolean isNew) {
        Assert.notNull(product, "The product is null");
        Assert.notEmpty(product.getRequests(), "The product.getRequests() must contain elements.");
        Assert.notEmpty(product.getRequests().get(0).getAttestations(), "The product.getRequests().get(0).getAttestations() must contain elements.");

        EnumOp op = EnumOp.U;
        if (isNew) {
            op = EnumOp.I;
        }

        final var recordPk = RecordPk.newBuilder()
                .setId(product.getId())
                .setSystemCode(SpecCode.SYSTEM_CODE.getValue().toString())
                .build();

        List<AttestationDto> attestations = List.of();
        String primeId = null;
        long kceh = 0L;
        int mismatch = Status.WAITING_FOR_DATA.getValue();
        String ts = null;
        if (product.getRequests() != null && !product.getRequests().isEmpty()) {
            attestations = product.getRequests().get(0).getAttestations();
            primeId = product.getRequests().get(0).getPrimeID();
            if (product.getRequests().get(0).getKceh() != null) {
                kceh = product.getRequests().get(0).getKceh();
            }
            if (product.getRequests().get(0).getStatus() != null) {
                mismatch = product.getRequests().get(0).getStatus().getValue();
            }
            if (product.getRequests().get(0).getAttestationTs() != null) {
                ts = product.getRequests().get(0).getAttestationTs().toString();
            }
        }

        List<RecordCommons> commonSpec = new ArrayList<>();
        List<RecordChemical> chemicalSpec = new ArrayList<>();
        List<RecordMechanical> mechanicalSpec = new ArrayList<>();
        List<RecordMettallographic> metallographSpec = new ArrayList<>();

        if (attestations != null) {
            commonSpec = attestations.stream()
                    .filter(att -> !(Group.HIM.equals(att.getGroup())
                            || Group.MEH.equals(att.getGroup())
                            || Group.MET.equals(att.getGroup()))
                    ).map(this::toCommonRecord)
                    .collect(Collectors.toList());

            chemicalSpec = attestations.stream()
                    .filter(att -> Group.HIM.equals(att.getGroup()))
                    .map(this::toChemicalRecord)
                    .collect(Collectors.toList());

            mechanicalSpec = this.toMechanicalRecordList(attestations);

            metallographSpec = this.toMettallographicRecordList(attestations);
        }

        return VerificationResults.newBuilder()
                .setTs(ts)
                .setPk(recordPk)
                .setOp(op)
                .setData(RecordData.newBuilder()
                        .setPrimeId(primeId)
                        .setKceh(kceh)
                        .setMismatch(mismatch)
                        .setCommons(commonSpec)
                        .setChemical(chemicalSpec)
                        .setMechanical(mechanicalSpec)
                        .setMetallographic(metallographSpec)
                        .build()
                ).build();
    }

    private RecordCommons toCommonRecord(AttestationDto attestation) {
        NormSpecData norm = null;

        if (attestation.getEqual() != null) {
            norm = NormSpecData.newBuilder()
                    .setListAccValues(List.of(attestation.getEqual()))
                    .setValueMax(attestation.getMax())
                    .setValueMin(attestation.getMin())
                    .build();
        }

        return RecordCommons.newBuilder()
                .setSpecCode(attestation.getCode())
                .setSpecTypeCode(TypeCode.STRING.getValue())
                .setSpecTypeName(TypeCode.STRING.getDesc())
                .setSpecValue(attestation.getValue())
                .setMismatch(attestation.getStatus().getValue())
                .setNorms(norm)
                .setMismatch(attestation.getStatus().getValue())
                .setNote(detectNote(attestation))
                .setDefectSuggestion(detectDefectSuggestion(attestation))
                .build();
    }

    private RecordChemical toChemicalRecord(AttestationDto attestation) {
        NormChemData norm = null;

        if (attestation.getEqual() != null) {
            norm = NormChemData.newBuilder()
                    .setListAccValues(List.of(attestation.getEqual()))
                    .setValueMax(attestation.getMax())
                    .setValueMin(attestation.getMin())
                    .build();
        }

        return RecordChemical.newBuilder()
                .setSpecCode(attestation.getCode())
                .setSpecTypeCode(TypeCode.STRING.getValue())
                .setSpecTypeName(TypeCode.STRING.getDesc())
                .setSpecValue(attestation.getValue())
                .setMismatch(attestation.getStatus().getValue())
                .setNorms(norm)
                .setMismatch(attestation.getStatus().getValue())
                .setNote(detectNote(attestation))
                .setDefectSuggestion(detectDefectSuggestion(attestation))
                .build();
    }

    private List<RecordMettallographic> toMettallographicRecordList(List<AttestationDto> attestations) {
        List<AttestationDto> metallAttestation = attestations.stream()
                .filter(att -> Group.MET.equals(att.getGroup()))
                .collect(Collectors.toList());

        Map<Integer, List<AttestationDto>> signAnalysisGrouping = groupBySignAnalysis(metallAttestation);

        return signAnalysisGrouping.keySet().stream()
                .map(k -> toMettallographicRecord(k, signAnalysisGrouping.get(k)))
                .collect(Collectors.toList());
    }

    private List<RecordMechanical> toMechanicalRecordList(List<AttestationDto> attestations) {
        List<AttestationDto> mechAttestation = attestations.stream()
                .filter(att -> Group.MEH.equals(att.getGroup()))
                .collect(Collectors.toList());

        Map<Integer, List<AttestationDto>> signAnalysisGrouping = groupBySignAnalysis(mechAttestation);

        return signAnalysisGrouping.keySet().stream()
                .map(k -> toMechanicalRecord(k, signAnalysisGrouping.get(k)))
                .collect(Collectors.toList());
    }

    private RecordMettallographic toMettallographicRecord(Integer signAnalysis, List<AttestationDto> attestations) {
        List<RecordMettallographicSpecifications> metallSpecifications = attestations.stream()
                .map(this::toMetallSpecifications).collect(Collectors.toList());

        return RecordMettallographic.newBuilder()
                .setSignAnalysis(signAnalysis)
                .setSpecifications(metallSpecifications)
                .build();
    }

    private RecordMechanical toMechanicalRecord(Integer signAnalysis, List<AttestationDto> attestations) {
        List<RecordMechanicalSpecifications> mechanicalSpecifications = attestations.stream()
                .map(this::toMechanicalSpecifications).collect(Collectors.toList());

        return RecordMechanical.newBuilder()
                .setSignAnalysis(signAnalysis)
                .setSpecifications(mechanicalSpecifications)
                .build();
    }

    private Map<Integer, List<AttestationDto>> groupBySignAnalysis(List<AttestationDto> attestations) {
        Map<Integer, List<AttestationDto>> attestationGroups = new HashMap<>();

        for (AttestationDto a : attestations) {
            Integer key = null;
            if (a.getParams() != null) {
                key = a.getParams().getSignAnalysis();
            }

            attestationGroups.computeIfAbsent(key, k -> new ArrayList<>()).add(a);
        }

        return attestationGroups;
    }

    private RecordMettallographicSpecifications toMetallSpecifications(AttestationDto attestation) {
        NormMetallData norm = null;

        if (attestation.getEqual() != null) {
            norm = NormMetallData.newBuilder()
                    .setListAccValues(List.of(attestation.getEqual()))
                    .setValueMax(attestation.getMax())
                    .setValueMin(attestation.getMin())
                    .build();
        }

        return RecordMettallographicSpecifications.newBuilder()
                .setSpecCode(attestation.getCode())
                .setSpecTypeCode(TypeCode.STRING.getValue())
                .setSpecTypeName(TypeCode.STRING.getDesc())
                .setSpecValue(attestation.getValue())
                .setMismatch(attestation.getStatus().getValue())
                .setNorms(norm)
                .setMismatch(attestation.getStatus().getValue())
                .setNote(detectNote(attestation))
                .setDefectSuggestion(detectDefectSuggestion(attestation))
                .build();
    }

    private RecordMechanicalSpecifications toMechanicalSpecifications(AttestationDto attestation) {
        NormMechData norm = null;

        if (attestation.getEqual() != null) {
            norm = NormMechData.newBuilder()
                    .setListAccValues(List.of(attestation.getEqual()))
                    .setValueMax(attestation.getMax())
                    .setValueMin(attestation.getMin())
                    .build();
        }

        return RecordMechanicalSpecifications.newBuilder()
                .setSpecCode(attestation.getCode())
                .setSpecTypeCode(TypeCode.STRING.getValue())
                .setSpecTypeName(TypeCode.STRING.getDesc())
                .setSpecValue(attestation.getValue())
                .setMismatch(attestation.getStatus().getValue())
                .setNorms(norm)
                .setMismatch(attestation.getStatus().getValue())
                .setNote(detectNote(attestation))
                .setDefectSuggestion(detectDefectSuggestion(attestation))
                .build();
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
