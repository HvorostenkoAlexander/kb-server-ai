package com.nlmk.kb.server.service.ccm.kc1;

import com.nlmk.attestation.product.api.pam.*;
import com.nlmk.kb.server.service.CommonConverter;
import com.nlmk.kb.server.service.ccm.KafkaRequestAdapter;
import com.nlmk.kb.server.util.AdapterUtils;
import java.math.BigDecimal;
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
                .orderNum(recordData.getOrderNum())
                .orderPos(recordData.getOrderPos())
                .requirements(toPamRequirement(recordData))
                .chemData(toChemData(recordData.getChemData()))
                .specifications(
                        recordData.getSpecifications().stream()
                                .map(this::toPamSpecs)
                                .collect(Collectors.toUnmodifiableList())
                )
                .marking(toKcMarking(recordData.getMarking()))
                .markingAcc(toKcMarkingAcc(recordData.getMarkingAcc()))
                //TODO: нет в Avro схеме
                //.weightNet(recordData.)
                .werks(recordData.getWerks())
                .werksName(AdapterUtils.sequenceToString(recordData.getWerksName()))
                //TODO: нет в Avro схеме
                //.unitCode(recordData.)
                //.unitName(recordData.)
                .build();
    }

    private KcMarking toKcMarking(RecordMarking marking) {
        if (Objects.isNull(marking)) {
            return KcMarking.builder().build();
        }
        return KcMarking.builder()
                .heat(marking.getHeat())
                .slab(marking.getSlab())
                .strand(marking.getStrand())
                .build();
    }

    private KcMarking toKcMarkingAcc(RecordMarkingAcc markingAcc) {
        if (Objects.isNull(markingAcc)) {
            return KcMarking.builder().build();
        }
        return KcMarking.builder()
                .heat(markingAcc.getHeat())
                .slab(markingAcc.getSlab())
                .strand(markingAcc.getStrand())
                .build();
    }

    private PlanTask toPlanTask(RecordPlanTask planTask) {
        if (Objects.isNull(planTask)) {
            return PlanTask.builder().build();
        }
        return PlanTask.builder()
                .planTaskId(planTask.getPlanTaskId())
                .planTaskLineId(planTask.getPlanTaskLineId())
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
                        .probeCode(AdapterUtils.sequenceToString(a.getProbeCode()))
                        .analysisCode(AdapterUtils.sequenceToString(a.getAnalysisCode()))
                        .heat(a.getHeat())
                        .samplingPlaceName(AdapterUtils.sequenceToString(a.getSamplingPlaceName()))
                        .chemical(toChemical(a.getChemical()))
                        //TODO: нет в схеме
                        //.reason()
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
                                .chemCode(Long.valueOf(a.getChemCode()).intValue())
                                .chemName(AdapterUtils.sequenceToString(a.getChemName()))
                                .chemValue(new BigDecimal(AdapterUtils.sequenceToString(a.getChemValue()))) //расхождение спецификации со схемой - в спецификации это число
                                .build()
                ).collect(Collectors.toUnmodifiableList());
    }

    private Specs toPamSpecs(RecordDataSpecifications specifications) {
        return Specs.builder()
                .specCode(specifications.getSpecCode())
                .specName(AdapterUtils.sequenceToString(specifications.getSpecName()))
                .specTypeCode(specifications.getSpecTypeCode())
                .specValue(AdapterUtils.sequenceToString(specifications.getSpecValue()))
                .specTypeName(AdapterUtils.sequenceToString(specifications.getSpecTypeName()))
                .specTypeValue(specifications.getSpecTypeValue())
                .listValues(toListValues(specifications.getListValues()))
                .specDecryption(AdapterUtils.sequenceToString(specifications.getSpecDecryption()))
                .specFormat(AdapterUtils.sequenceToString(specifications.getSpecFormat()))
                .specMeasure(AdapterUtils.sequenceToString(specifications.getSpecMeasure()))
                .build();
    }

    private List<SpecValue> toListValues(List<RecordDataSpecificationsListValues> listValues) {
        if (Objects.isNull(listValues)) {
            return Collections.emptyList();
        }
        return listValues.stream()
                .map(a ->
                        SpecValue.builder()
                                .value(AdapterUtils.sequenceToString(a.getValue()))
                                .description(AdapterUtils.sequenceToString(a.getDescription()))
                                .build()
                ).collect(Collectors.toUnmodifiableList());
    }

    private Requirement toPamRequirement(RecordData recordData) {
        return Requirement.builder()
                .planTask(toPlanTask(recordData.getPlanTask()))
                .chemicalReq(Objects.nonNull(recordData.getRequirements().getChemicalReq()) ?
                        recordData.getRequirements().getChemicalReq().stream()
                                .map(this::toPamChemicalReq)
                                .collect(Collectors.toUnmodifiableList()) :
                        null)
                .specifications(Objects.nonNull(recordData.getRequirements().getSpecifications()) ?
                        recordData.getRequirements().getSpecifications().stream()
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
                .chemCode(converter.parseToInteger(AdapterUtils.sequenceToString(recordChemicalReg.getChemCode())))
                .chemName(AdapterUtils.sequenceToString(recordChemicalReg.getChemName()))
                .valueMin(AdapterUtils.toBigDecimal(recordChemicalReg.getValueMin()))
                .valueMax(AdapterUtils.toBigDecimal(recordChemicalReg.getValueMax()))
                .digitsQuantity(recordChemicalReg.getDigitsQuantity())
                .build();
    }

}
