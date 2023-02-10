package com.nlmk.kb.server.service.ccm.kc1;

import com.nlmk.attestation.product.api.pam.*;
import com.nlmk.kb.server.service.CommonConverter;
import com.nlmk.kb.server.service.ccm.KafkaRequestAdapter;
import com.nlmk.kb.server.util.AdapterUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import nlmk.nlmk.l3.sus.kc1.DbAttestRequestVer0;
import nlmk.nlmk.l3.sus.kc1.db.attestrequest.ver0.*;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * @link <a href="https://confluence.nlmk.com/pages/viewpage.action?pageId=103213237">Спецификация КЦ-1</a>
 */
@Component
@RequiredArgsConstructor
public class CcmKc1KafkaRequestAdapterImpl implements KafkaRequestAdapter<DbAttestRequestVer0> {

    private final CommonConverter converter;

    @Override
    public AttestationRequest adapt(DbAttestRequestVer0 requestMessage) {
        Assert.notNull(requestMessage, "requestMessage is null");
        Assert.notNull(requestMessage.getTs(), "requestMessage.getTs() is null");
        Assert.notNull(requestMessage.getOp(), "requestMessage.getOp() is null");

        final var dateRequest = converter.parseToDate(AdapterUtils.sequenceToString(requestMessage.getTs()));
        Assert.notNull(dateRequest, "Не удалось получить сведения о ts в запросе на аттестацию");

        return AttestationRequest.builder()
                .value(Value.builder()
                        .ts(dateRequest)
                        .op(requestMessage.getOp().toString())
                        .pk(toPamPk(requestMessage.getPk()))
                        .data(toPamDataField(requestMessage.getPk(), requestMessage.getData()))
                        .build())
                .build();
    }

    private Pk toPamPk(PkType recordPk) {
        if (Objects.isNull(recordPk)) {
            return null;
        }

        return Pk.builder()
                .systemCode(AdapterUtils.sequenceToString(recordPk.getSystemCode()))
                .id(AdapterUtils.sequenceToString(recordPk.getId()))
                .build();
    }

    private DataField toPamDataField(PkType recordPk,
                                     RecordData recordData) {
        if (Objects.isNull(recordData)) {
            return null;
        }

        return DataKc.builder()
                .kceh(recordData.getKceh())
                .primeId(Objects.nonNull(recordPk) ? AdapterUtils.sequenceToString(recordPk.getId()) : null)
                .heat(recordData.getMarking().getHeat())
                .strand(recordData.getMarking().getStrand())
                .slab(recordData.getMarking().getSlab())
                .orderNum(recordData.getOrderNum())
                .orderPos(recordData.getOrderPos())
                .requirements(toPamRequirement(recordData.getRequirements()))
                .chemData(toChemData(recordData.getChemData()))
                .planTask(toPlanTask(recordData.getPlanTask()))
                .specifications(
                        recordData.getSpecifications().stream()
                                .map(this::toPamSpecs)
                                .collect(Collectors.toUnmodifiableList())
                )
                .build();
    }

    private PlanTask toPlanTask(RecordPlanTask planTask) {
        if (Objects.isNull(planTask)) {
            return PlanTask.builder().build();
        }
        return PlanTask.builder()
                .planTaskId(AdapterUtils.sequenceToString(planTask.getPlanTaskId()))
                .planTaskLineId(AdapterUtils.sequenceToString(planTask.getPlanTaskLineId()))
                .build();
    }

    private List<KcChemData> toChemData(List<RecordChemData> chemData) {
        if (Objects.isNull(chemData)) {
            return Collections.emptyList();
        }
        return chemData.stream()
                .map(a -> KcChemData.builder()
                        .sampleId(a.getSampleId())
                        .sampleNum(a.getSampleNum())
                        .probeCode(String.valueOf(a.getProbeCode())) // fixme
                        .analysisCode(AdapterUtils.sequenceToString(a.getAnalysisCode()))
                        .heat(a.getHeat())
                        .samplingPlaceName(AdapterUtils.sequenceToString(a.getSamplingPlaceName()))
                        .chemical(toChemical(a.getChemical()))
                        .build()
                ).collect(Collectors.toUnmodifiableList());
    }

    private List<KcChemical> toChemical(List<RecordChemical> chemical) {
        if (Objects.isNull(chemical)) {
            return Collections.emptyList();
        }
        return chemical.stream()
                .map(a ->
                        KcChemical.builder()
                                .chemCode(a.getChemCode())
                                .chemName(AdapterUtils.sequenceToString(a.getChemName()))
                                .chemValue(AdapterUtils.sequenceToString(a.getChemValue())) //расхождение спецификации со схемой - в спецификации это число
                                .build()
                ).collect(Collectors.toUnmodifiableList());
    }

    private Specs toPamSpecs(RecordDataSpecifications specifications) {
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
                .chemicalReg(Objects.nonNull(recordRequirements.getChemicalReq()) ?
                        recordRequirements.getChemicalReq().stream()
                                .map(this::toPamChemicalReq)
                                .collect(Collectors.toUnmodifiableList()) :
                        null)
                .specifications(Objects.nonNull(recordRequirements.getSpecifications()) ?
                        recordRequirements.getSpecifications().stream()
                                .map(this::toPamSpecs)
                                .collect(Collectors.toUnmodifiableList()) :
                        null)
                .build();
    }

    private Specs toPamSpecs(RecordDataRequirementsSpecifications specifications) {
        return Specs.builder()
                .specCode(specifications.getSpecCode())
                .specName(AdapterUtils.sequenceToString(specifications.getSpecName()))
                .specTypeCode(specifications.getSpecTypeCode())
                .specValue(AdapterUtils.sequenceToString(specifications.getSpecValue()))
                .specFormat(AdapterUtils.sequenceToString(specifications.getSpecFormat()))
                .specMeasure(AdapterUtils.sequenceToString(specifications.getSpecMeasure()))
                .build();
    }

    private RequirementChemicalSpec toPamChemicalReq(RecordChemicalReq recordChemicalReg) {
        return RequirementChemicalSpec.builder()
                .chemCode(AdapterUtils.sequenceToString(recordChemicalReg.getChemCode()))
                .chemName(AdapterUtils.sequenceToString(recordChemicalReg.getChemName()))
                .valueMin(recordChemicalReg.getValueMin())
                .valueMax(recordChemicalReg.getValueMax())
                .digitsQuantity(recordChemicalReg.getDigitsQuantity())
                .build();
    }

}
