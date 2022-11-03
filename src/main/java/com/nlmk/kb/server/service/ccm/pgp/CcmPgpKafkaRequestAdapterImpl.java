package com.nlmk.kb.server.service.ccm.pgp;

import com.nlmk.attestation.product.api.pam.*;
import com.nlmk.attestation.product.api.pam.AttestationRequest;
import com.nlmk.kb.server.service.CommonConverter;
import com.nlmk.kb.server.util.AdapterUtils;
import com.nlmk.kb.server.service.ccm.KafkaRequestAdapter;
import lombok.RequiredArgsConstructor;
import nlmk.l3.ccm.pgp.*;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CcmPgpKafkaRequestAdapterImpl implements KafkaRequestAdapter<nlmk.l3.ccm.pgp.AttestationRequest> {

    private final CommonConverter converter;

    @Override
    public AttestationRequest adapt(nlmk.l3.ccm.pgp.AttestationRequest requestMessagePgp) {
        Assert.notNull(requestMessagePgp, "requestMessagePgp is null");
        Assert.notNull(requestMessagePgp.getTs(), "requestMessagePgp.getTs() is null");
        Assert.notNull(requestMessagePgp.getOp(), "requestMessagePgp.getOp() is null");

        final var dateRequest = converter.parseToDate(AdapterUtils.sequenceToString(requestMessagePgp.getTs()));
        Assert.notNull(dateRequest, "Не удалось получить сведения о ts в запросе на аттестацию");

        return AttestationRequest.builder()
                .value(Value.builder()
                        .ts(dateRequest)
                        .op(requestMessagePgp.getOp().toString())
                        .pk(toPamPk(requestMessagePgp.getPk()))
                        .data(toPamDataField(requestMessagePgp.getData()))
                        .build())
                .build();
    }

    private Pk toPamPk(RecordPk recordPk) {
        if (recordPk == null) {
            return null;
        }

        return Pk.builder()
                .systemCode(AdapterUtils.sequenceToString(recordPk.getSystemCode()))
                .id(AdapterUtils.sequenceToString(recordPk.getId()))
                .build();
    }

    private DataField toPamDataField(RecordData recordData) {
        if (recordData == null) {
            return null;
        }

        // с версии 1.27.0 данные поля orderReq не используются, получение требований заказа через SAP
        return DataField.builder()
                .primeId(recordData.getPrimeId().toString())
                .nplv(recordData.getNplv())
                .hnum(recordData.getHnum())
                .roll(AdapterUtils.sequenceToString(recordData.getRoll()))
                .length(AdapterUtils.parseFloat(recordData.getLength()))
                .thickness(AdapterUtils.parseFloat(recordData.getThickness()))
                .width(AdapterUtils.parseFloat(recordData.getWidth()))
                .weightNet(AdapterUtils.parseFloat(recordData.getWeightNet()))
                .bundleWeight(AdapterUtils.parseFloat(recordData.getBundleWeight()))
                .kceh(recordData.getKceh())
                .orderNum(recordData.getOrderNum())
                .orderPos(recordData.getOrderPos())
                .orderReq(List.of())
                .specifications(
                        recordData.getSpecifications().stream()
                                .map(this::toPamSpecs)
                                .collect(Collectors.toList())
                ).chemical(
                        recordData.getChemical() == null
                                ? null
                                : recordData.getChemical().stream()
                                .map(this::toPamChemicalSpec)
                                .collect(Collectors.toList())
                ).mechanical(
                        recordData.getMechanical() == null
                                ? null
                                : recordData.getMechanical().stream()
                                .map(this::toPamMechanicalSpec)
                                .collect(Collectors.toList())
                ).metallographic(
                        recordData.getMetallographic() == null
                                ? null
                                : recordData.getMetallographic().stream()
                                .map(this::toPamMetallographicSpec)
                                .collect(Collectors.toList())
                ).build();
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

    private ChemicalSpec toPamChemicalSpec(RecordChemical recordChemical) {
        return ChemicalSpec.builder()
                .chemCode(recordChemical.getChemCode())
                .chemName(AdapterUtils.sequenceToString(recordChemical.getChemName()))
                .chemValue(AdapterUtils.sequenceToString(recordChemical.getChemValue()))
                .chemFormat(AdapterUtils.sequenceToString(recordChemical.getChemFormat()))
                .build();
    }

    private MechanicalSpec toPamMechanicalSpec(RecordMechanical mechanical) {
        return MechanicalSpec.builder()
                .hnum(mechanical.getHnum())
                .protNum(mechanical.getProtNum())
                .sampleNum(mechanical.getSampleNum())
                .signAnalysis(mechanical.getSignAnalysis())
                .protDate(AdapterUtils.sequenceToString(mechanical.getProtDate()))
                .mechData(
                        mechanical.getMechData() == null
                                ? null
                                : mechanical.getMechData().stream()
                                .map(this::toPamMechanicalData)
                                .collect(Collectors.toList())
                ).build();
    }

    private MechanicalData toPamMechanicalData(RecordMechData data) {
        return MechanicalData.builder()
                .mechAnalysisId(data.getMechAnalysisId())
                .mechAnalysisData(
                        data.getMechAnalysisData() == null
                                ? null
                                : data.getMechAnalysisData().stream()
                                .map(this::toPamMechanicalAnalysisData)
                                .collect(Collectors.toList())
                ).build();
    }

    private MechanicalAnalysisData toPamMechanicalAnalysisData(RecordMechAnalysisData analysis) {
        return MechanicalAnalysisData.builder()
                .mechCode(analysis.getMechCode())
                .mechName(AdapterUtils.sequenceToString(analysis.getMechName()))
                .mechTypeCode(analysis.getMechTypeCode())
                .mechFormat(AdapterUtils.sequenceToString(analysis.getMechFormat()))
                .mechValue(AdapterUtils.sequenceToString(analysis.getMechValue()))
                .mechMeasure(AdapterUtils.sequenceToString(analysis.getMechMeasure()))
                .build();
    }

    private MetallographicSpec toPamMetallographicSpec(RecordMetallographic metallographic) {
        return MetallographicSpec.builder()
                .hnum(metallographic.getHnum())
                .protNum(metallographic.getProtNum())
                .protDate(AdapterUtils.sequenceToString(metallographic.getProtDate()))
                .signAnalysis(metallographic.getSignAnalysis())
                .metgrapData(
                        metallographic.getMetgrapData() == null
                                ? null
                                : metallographic.getMetgrapData().stream()
                                .map(this::toPamMetallographicData)
                                .collect(Collectors.toList())
                ).build();
    }

    private MetallographicData toPamMetallographicData(RecordMetgrapData data) {
        return MetallographicData.builder()
                .metgrapAnalysisId(data.getMetgrapAnalysisId())
                .metgrapAnalysisData(data.getMetgrapAnalysisData() == null
                        ? null
                        : data.getMetgrapAnalysisData().stream()
                        .map(this::toPamMetallographicAnalysisData)
                        .collect(Collectors.toList()))
                .build();
    }

    private MetallographicAnalysisData toPamMetallographicAnalysisData(RecordMetgrapAnalysisData analysis) {
        return MetallographicAnalysisData.builder()
                .metgrapCode(analysis.getMetgrapCode())
                .metgrapName(AdapterUtils.sequenceToString(analysis.getMetgrapName()))
                .metgrapFormat(AdapterUtils.sequenceToString(analysis.getMetgrapFormat()))
                .metgrapValue(AdapterUtils.sequenceToString(analysis.getMetgrapValue()))
                .metgrapTypeCode(analysis.getMetgrapTypeCode())
                .metgrapMeasure(AdapterUtils.sequenceToString(analysis.getMetgrapMeasure()))
                .build();
    }

}
