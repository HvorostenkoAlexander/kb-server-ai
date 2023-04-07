package com.nlmk.kb.server.service.ccm.kc2;

import com.nlmk.attestation.product.api.pam.*;
import com.nlmk.attestation.product.api.pam.Pk;
import com.nlmk.attestation.product.api.pam.PlanTask;
import com.nlmk.attestation.product.api.pam.SpecValue;
import com.nlmk.kb.server.api.ccm.kc.request.*;
import com.nlmk.kb.server.service.CommonConverter;
import com.nlmk.kb.server.service.ccm.RestRequestAdapter;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CcmKc2RestRequestAdapterImpl implements RestRequestAdapter<CcmKc2Request> {

    private final CommonConverter converter;

    @Override
    public AttestationRequest adapt(CcmKc2Request requestMessage) {
        final var dateRequest = converter.parseToDate(requestMessage.getTs());
        final var data = requestMessage.getData();

        return AttestationRequest.builder()
                .value(Value.builder()
                        .ts(dateRequest)
                        .op("I")
                        .pk(Pk.builder()
                                .systemCode(requestMessage.getPk().getSystemCode())
                                .id(requestMessage.getPk().getId())
                                .build())
                        .data(DataKc.builder()
                                .primeId(requestMessage.getPk().getId())
                                .werks(data.getWerks())
                                .werksName(data.getWerksName())
                                .kceh(data.getWorkshop())
                                .kcehName(data.getWorkshopName())
                                .orderNum(data.getOrderNum())
                                .orderPos(data.getOrderPos())
                                .unitCode(data.getUnitCode())
                                .unitName(data.getUnitName())
                                .weightNet(data.getWeightNet())
                                .marking(toMarking(data.getMarking()))
                                .markingAcc(toMarking(data.getMarkingAcc()))
                                .requirements(toRequirements(data.getRequirements()))
                                .chemData(toChemData(data.getChemData()))
                                .specifications(toSpecifications(data.getSpecifications()))
                                .build())
                        .build())
                .build();
    }

    private List<KcChemData> toChemData(List<ChemData> chemData) {
        if (Objects.isNull(chemData)) {
            return Collections.emptyList();
        }
        return chemData.stream().map(a ->
                KcChemData.builder()
                        .sampleId(a.getSampleId())
                        .probeCode(a.getProbeCode())
                        .analysisCode(a.getAnalysisCode())
                        .sampleNum(a.getSampleNum())
                        .heat(a.getHeat())
                        .samplingPlaceName(a.getSamplingPlaceName())
                        .reason(a.getReason())
                        .chemical(toChemical(a.getChemical()))
                        .build()
        ).collect(Collectors.toUnmodifiableList());
    }

    private List<KcChemical> toChemical(List<Chemical> chemical) {
        if (Objects.isNull(chemical)) {
            return Collections.emptyList();
        }
        return chemical.stream().map(a ->
                KcChemical.builder()
                        .chemCode(a.getChemCode())
                        .chemName(a.getChemName())
                        .chemValue(a.getChemValue())
                        .build()
        ).collect(Collectors.toUnmodifiableList());
    }

    private Requirement toRequirements(Requirements requirements) {
        if (Objects.isNull(requirements)) {
            return Requirement.builder().build();
        }
        return Requirement.builder()
                .planTask(toPlanTask(requirements.getPlanTask()))
                .chemicalReq(toChemicalReq(requirements.getChemicalReq()))
                .specifications(toSpecifications(requirements.getSpecifications()))
                .build();
        
    }

    private List<Specs> toSpecifications(List<Specification> specifications) {
        if (Objects.isNull(specifications)) {
            return Collections.emptyList();
        }
        return specifications.stream().map(a ->
                Specs.builder()
                        .specCode(a.getSpecCode())
                        .specName(a.getSpecName())
                        .specTypeCode(a.getSpecTypeCode().getValue())
                        .specTypeName(a.getSpecTypeName())
                        .specTypeValue(a.getSpecTypeValue().getValue())
                        .specValue(a.getSpecValue())
                        .listValues(toListValues(a.getListValues()))
                        .specDecryption(a.getSpecDecryption())
                        .specFormat(a.getSpecFormat())
                        .specMeasure(a.getSpecMeasure())
                        .build()
        ).collect(Collectors.toUnmodifiableList());
    }

    private List<SpecValue> toListValues(List<com.nlmk.kb.server.api.ccm.kc.request.SpecValue> listValues) {
        if (Objects.isNull(listValues)) {
            return Collections.emptyList();
        }
        return listValues.stream().map(a ->
                SpecValue.builder()
                        .value(a.getValue())
                        .description(a.getDescription())
                        .build()
        ).collect(Collectors.toUnmodifiableList());
    }

    private List<RequirementChemicalSpec> toChemicalReq(List<ChemicalReq> chemicalReq) {
        if (Objects.isNull(chemicalReq)) {
            return Collections.emptyList();
        }
        return chemicalReq.stream().map(a ->
            RequirementChemicalSpec.builder()
                    .chemCode(a.getChemCode())
                    .chemName(a.getChemName())
                    .valueMax(a.getValueMax())
                    .valueMin(a.getValueMin())
                    .digitsQuantity(a.getDigitsQuantity())
                    .build()
        ).collect(Collectors.toUnmodifiableList());
    }

    private PlanTask toPlanTask(com.nlmk.kb.server.api.ccm.kc.request.PlanTask planTask) {
        if (Objects.isNull(planTask)) {
            return PlanTask.builder().build();
        }
        return PlanTask.builder()
                .planTaskId(planTask.getPlanTaskId())
                .planTaskLineId(planTask.getPlanTaskLineId())
                .build();
    }

    private KcMarking toMarking(Marking marking) {
        if (Objects.isNull(marking)) {
            return KcMarking.builder().build();
        }
        return KcMarking.builder()
                .strand(marking.getStrand())
                .slab(marking.getSlab())
                .heat(marking.getHeat())
                .build();
    }

}
