package com.nlmk.kb.server.service.ccm.kc1;

import com.nlmk.attestation.product.api.pam.AttestationRequest;
import com.nlmk.attestation.product.api.pam.*;
import com.nlmk.kb.server.service.CommonConverter;
import com.nlmk.kb.server.service.ccm.KafkaRequestAdapter;
import com.nlmk.kb.server.util.AdapterUtils;
import lombok.RequiredArgsConstructor;
import nlmk.l3.sus.kc1.*;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @link <a href="https://confluence.nlmk.com/pages/viewpage.action?pageId=103213237">Спецификация</a>
 */
@Component
@RequiredArgsConstructor
public class CcmKc1KafkaRequestAdapterImpl implements KafkaRequestAdapter<nlmk.l3.sus.kc1.AttestationRequest> {

    private final CommonConverter converter;

    @Override
    public AttestationRequest adapt(nlmk.l3.sus.kc1.AttestationRequest requestMessageKc1) {
        Assert.notNull(requestMessageKc1, "requestMessageKc1 is null");
        Assert.notNull(requestMessageKc1.getTs(), "requestMessageKc1.getTs() is null");
        Assert.notNull(requestMessageKc1.getOp(), "requestMessageKc1.getOp() is null");

        final var dateRequest = converter.parseToDate(AdapterUtils.sequenceToString(requestMessageKc1.getTs()));
        Assert.notNull(dateRequest, "Не удалось получить сведения о ts в запросе на аттестацию");

        return AttestationRequest.builder()
                .value(Value.builder()
                        .ts(dateRequest)
                        .op(requestMessageKc1.getOp().toString())
                        .pk(toPamPk(requestMessageKc1.getPk()))
                        .data(toPamDataField(requestMessageKc1.getPk(), requestMessageKc1.getData()))
                        .build())
                .build();
    }

    private Pk toPamPk(RecordPk recordPk) {
        if (Objects.isNull(recordPk)) {
            return null;
        }

        return Pk.builder()
                .systemCode(AdapterUtils.sequenceToString(recordPk.getSystemCode()))
                .id(AdapterUtils.sequenceToString(recordPk.getId()))
                .build();
    }

    private DataField toPamDataField(RecordPk recordPk, RecordData recordData) {
        if (Objects.isNull(recordData)) {
            return null;
        }

        var markingAcc = recordData.getMarkingAcc();

        return DataKc.builder()
                .kceh(recordData.getKceh())
                .primeId(Objects.nonNull(recordPk) ? AdapterUtils.sequenceToString(recordPk.getId()) : null)
                .nplv(Objects.nonNull(markingAcc) ? markingAcc.getNplv() : null)
                .strand(Objects.nonNull(markingAcc) ? markingAcc.getStrand() : null)
                .slab(Objects.nonNull(markingAcc) ? (markingAcc.getSlab()) : null)
                .requirements(toPamRequirement(recordData.getRequirements()))
                .specifications(
                        recordData.getSpecifications().stream()
                                .map(this::toPamSpecs)
                                .collect(Collectors.toUnmodifiableList())
                )
                .build();
    }

    private Specs toPamSpecs(RecordSpecifications specifications) {
        return Specs.builder()
                .specCode(specifications.getSpecCode())
                .specName(AdapterUtils.sequenceToString(specifications.getSpecName()))
                .specTypeCode(specifications.getSpecTypeCode())
                .specValue(AdapterUtils.sequenceToString(specifications.getSpecValue()))
                .specFormat(AdapterUtils.sequenceToString(specifications.getSpecFormat()))
                .specMeasure(AdapterUtils.sequenceToString(specifications.getSpecMeasure()))
                .build();
    }

    private Requirement toPamRequirement(RecordRequirements recordRequirements) {
        return Requirement.builder()
                .chemicalReg(Objects.nonNull(recordRequirements.getChemicalReg()) ?
                        recordRequirements.getChemicalReg().stream()
                                .map(this::toPamChemicalReg)
                                .collect(Collectors.toUnmodifiableList()) :
                        null)
                .specifications(Objects.nonNull(recordRequirements.getSpecifications()) ?
                        recordRequirements.getSpecifications().stream()
                                .map(this::toPamSpecs)
                                .collect(Collectors.toUnmodifiableList()) :
                        null)
                .build();
    }

    private Specs toPamSpecs(RecordRequirementSpecifications specifications) {
        return Specs.builder()
                .specCode(specifications.getSpecCode())
                .specName(AdapterUtils.sequenceToString(specifications.getSpecName()))
                .specTypeCode(specifications.getSpecTypeCode())
                .specValue(AdapterUtils.sequenceToString(specifications.getSpecValue()))
                .specFormat(AdapterUtils.sequenceToString(specifications.getSpecFormat()))
                .specMeasure(AdapterUtils.sequenceToString(specifications.getSpecMeasure()))
                .build();
    }

    private RequirementChemicalSpec toPamChemicalReg(RecordChemicalReg recordChemicalReg) {
        return RequirementChemicalSpec.builder()
                .chemCode(AdapterUtils.sequenceToString(recordChemicalReg.getChemCode()))
                .chemName(AdapterUtils.sequenceToString(recordChemicalReg.getChemName()))
                .valueMin(recordChemicalReg.getValueMin())
                .valueMax(recordChemicalReg.getValueMax())
                .build();
    }

}
