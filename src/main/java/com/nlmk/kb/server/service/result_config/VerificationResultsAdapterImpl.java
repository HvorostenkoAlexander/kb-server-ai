package com.nlmk.kb.server.service.result_config;

import com.nlmk.attestation.product.api.AttestationDto;
import com.nlmk.attestation.product.api.Group;
import com.nlmk.attestation.product.api.ProductDto;
import com.nlmk.attestation.product.api.specification.SpecCode;
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

        List<AttestationDto> attectaions = List.of();
        String primeId = null;
        long kceh = 0L;
        int mismatch = 2;
        String ts = null;
        if (product.getRequests() != null && !product.getRequests().isEmpty()) {
            attectaions = product.getRequests().get(0).getAttestations();
            primeId = product.getRequests().get(0).getPrimeID();
            kceh = product.getRequests().get(0).getKceh();
            mismatch = product.getRequests().get(0).getStatus().getValue();
            if (product.getRequests().get(0).getAttestationTs() != null) {
                ts = product.getRequests().get(0).getAttestationTs().toString();
            }
        }

        List<RecordCommons> commonSpec = new ArrayList<>();
        List<RecordChemical> chemicalSpec = new ArrayList<>();
        List<RecordMechanical> mechanicalSpec = new ArrayList<>();
        List<RecordMettallographic> metallographSpec = new ArrayList<>();

        if (attectaions != null) {
            commonSpec = attectaions.stream()
                    .filter(attestationDto ->
                            !(attestationDto.getGroup().equals(Group.HIM) ||
                                    attestationDto.getGroup().equals(Group.MEH) ||
                                    attestationDto.getGroup().equals(Group.MET))
                    ).map(this::toRecordSpecifications).collect(Collectors.toList());

            chemicalSpec = attectaions.stream()
                    .filter(attestationDto -> attestationDto.getGroup().equals(Group.HIM))
                    .map(this::toChemicalSpecifications).collect(Collectors.toList());

            mechanicalSpec = this.toMechanicalSpecList(attectaions);

            metallographSpec = this.toMetallographSpecList(attectaions);
        }

        RecordData recordData = RecordData.newBuilder()
                .setPrimeId(primeId)
                .setKceh(kceh)
                .setMismatch(mismatch)
                .setCommons(commonSpec)
                .setChemical(chemicalSpec)
                .setMechanical(mechanicalSpec)
                .setMetallographic(metallographSpec)
                .build();

        return VerificationResults.newBuilder()
                .setTs(ts)
                .setPk(recordPk)
                .setOp(op)
                .setData(recordData)
                .build();
    }

    private RecordCommons toRecordSpecifications(AttestationDto attestation) {
        NormSpecData norm = null;

        if (attestation.getEqual() != null) {
            norm = NormSpecData.newBuilder()
                    .setListAccValues(List.of(attestation.getEqual()))
                    .setValueMax(attestation.getMax())
                    .setValueMin(attestation.getMin())
                    .build();
        }

        String note = null;
        String defectSuggestion = null;

        if (attestation.getStatus().getValue() == 4 || attestation.getStatus().getValue() == 5) {
            defectSuggestion = attestation.getComment();
        } else {
            note = attestation.getComment();
        }

        return RecordCommons.newBuilder()
                .setSpecCode(attestation.getCode())
                .setSpecName(null)
                .setSpecTypeCode(1)
                .setSpecTypeName("string")
                .setSpecValue(attestation.getValue())
                .setSpecFormat(null)
                .setSpecMeasure(null)
                .setMismatch(attestation.getStatus().getValue())
                .setNote(attestation.getComment())
                .setNorms(norm)
                .setMismatch(attestation.getStatus().getValue())
                .setNote(note)
                .setDefectSuggestion(defectSuggestion)
                .build();
    }

    private RecordChemical toChemicalSpecifications(AttestationDto attestation) {
        NormChemData norm = null;

        if (attestation.getEqual() != null) {
            norm = NormChemData.newBuilder()
                    .setListAccValues(List.of(attestation.getEqual()))
                    .setValueMax(attestation.getMax())
                    .setValueMin(attestation.getMin())
                    .build();
        }

        String note = null;
        String defectSuggestion = null;

        if (attestation.getStatus().getValue() == 4 || attestation.getStatus().getValue() == 5) {
            defectSuggestion = attestation.getComment();
        } else {
            note = attestation.getComment();
        }

        return RecordChemical.newBuilder()
                .setSpecCode(attestation.getCode())
                .setSpecName(null)
                .setSpecTypeCode(1)
                .setSpecTypeName("string")
                .setSpecValue(attestation.getValue())
                .setSpecFormat(null)
                .setSpecMeasure(null)
                .setMismatch(attestation.getStatus().getValue())
                .setNote(attestation.getComment())
                .setNorms(norm)
                .setMismatch(attestation.getStatus().getValue())
                .setNote(note)
                .setDefectSuggestion(defectSuggestion)
                .build();
    }

    private List<RecordMettallographic> toMetallographSpecList(List<AttestationDto> attestations) {
        List<AttestationDto> metallAttestation = attestations.stream()
                .filter(attestation -> attestation.getGroup().equals(Group.MET))
                .collect(Collectors.toList());

        Map<String, List<AttestationDto>> metallAttestationByFormationListId = groupByFormationListId(metallAttestation);

        return metallAttestationByFormationListId.keySet().stream()
                .map(k -> {
                    List<AttestationDto> attestationList = metallAttestationByFormationListId.get(k);
                    return toRecordMetall(attestationList);
                }).collect(Collectors.toList());
    }

    private Map<String, List<AttestationDto>> groupByFormationListId(List<AttestationDto> attestations) {
        Map<String, List<AttestationDto>> attestationGroups = new HashMap<>();

        for (AttestationDto a : attestations) {
            String key = null;
            if (a.getParams() != null) {
                key = a.getParams().getFormationListId();
            }

            List<AttestationDto> groupList = attestationGroups.get(key);
            if (groupList == null) {
                groupList = new ArrayList<>();
                attestationGroups.put(key, groupList);
            }
            groupList.add(a);
        }
        return attestationGroups;
    }

    private Map<Integer, List<AttestationDto>> groupBySignAnalysys(List<AttestationDto> attestations) {
        Map<Integer, List<AttestationDto>> attestationGroups = new HashMap<>();

        for (AttestationDto a : attestations) {
            Integer key = null;
            if (a.getParams() != null) {
                key = a.getParams().getSignAnalysis();
            }

            List<AttestationDto> groupList = attestationGroups.get(key);
            if (groupList == null) {
                groupList = new ArrayList<>();
                attestationGroups.put(key, groupList);
            }
            groupList.add(a);
        }
        return attestationGroups;
    }

    private List<RecordMechanical> toMechanicalSpecList(List<AttestationDto> attestations) {
        List<AttestationDto> mechAttestation = attestations.stream()
                .filter(attestation -> attestation.getGroup().equals(Group.MEH))
                .collect(Collectors.toList());

        Map<Integer, List<AttestationDto>> mechAttestationBySignAnalysis = groupBySignAnalysys(mechAttestation);

        return mechAttestationBySignAnalysis.keySet().stream()
                .map(k -> {
                    List<AttestationDto> attestationList = mechAttestationBySignAnalysis.get(k);
                    return toRecordMechanical(k, attestationList);
                }).collect(Collectors.toList());
    }

    private RecordMettallographic toRecordMetall(List<AttestationDto> attestations) {
        List<RecordMettallographicSpecifications> metallSpecifications = attestations.stream()
                .map(this::toMetallSpecifications).collect(Collectors.toList());

        Integer signAnalysis = null;
        if (attestations.get(0).getParams() != null) {
            signAnalysis = attestations.get(0).getParams().getSignAnalysis();
        }

        return RecordMettallographic.newBuilder()
                .setSignAnalysis(signAnalysis)
                .setSpecifications(metallSpecifications)
                .build();
    }

    private RecordMechanical toRecordMechanical(Integer signAnalysis, List<AttestationDto> attestations) {
        List<RecordMechanicalSpecifications> mechanicalSpecifications = attestations.stream()
                .map(this::toMechanicalSpecifications).collect(Collectors.toList());

        return RecordMechanical.newBuilder()
                .setSignAnalysis(signAnalysis)
                .setSpecifications(mechanicalSpecifications)
                .build();
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

        String note = null;
        String defectSuggestion = null;

        if (attestation.getStatus().getValue() == 4 || attestation.getStatus().getValue() == 5) {
            defectSuggestion = attestation.getComment();
        } else {
            note = attestation.getComment();
        }

        return RecordMettallographicSpecifications.newBuilder()
                .setSpecCode(attestation.getCode())
                .setSpecName(null)
                .setSpecTypeCode(1)
                .setSpecTypeName("string")
                .setSpecValue(attestation.getValue())
                .setSpecFormat(null)
                .setSpecMeasure(null)
                .setMismatch(attestation.getStatus().getValue())
                .setNote(attestation.getComment())
                .setNorms(norm)
                .setMismatch(attestation.getStatus().getValue())
                .setNote(note)
                .setDefectSuggestion(defectSuggestion)
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

        String note = null;
        String defectSuggestion = null;

        if (attestation.getStatus().getValue() == 4 || attestation.getStatus().getValue() == 5) {
            defectSuggestion = attestation.getComment();
        } else {
            note = attestation.getComment();
        }
        return RecordMechanicalSpecifications.newBuilder()
                .setSpecCode(attestation.getCode())
                .setSpecName(null)
                .setSpecTypeCode(1)
                .setSpecTypeName("string")
                .setSpecValue(attestation.getValue())
                .setSpecFormat(null)
                .setSpecMeasure(null)
                .setMismatch(attestation.getStatus().getValue())
                .setNote(attestation.getComment())
                .setNorms(norm)
                .setMismatch(attestation.getStatus().getValue())
                .setNote(note)
                .setDefectSuggestion(defectSuggestion)
                .build();
    }

}
