package com.nlmk.kb.server.service.ccm.pgp;

import com.nlmk.attestation.product.api.pam.*;
import com.nlmk.attestation.product.api.pam.AttestationRequest;
import com.nlmk.kb.server.service.CommonConverter;
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
    public com.nlmk.attestation.product.api.pam.AttestationRequest adapt(nlmk.l3.ccm.pgp.AttestationRequest requestMessagePgp) {
        Assert.notNull(requestMessagePgp, "requestMessagePgp is null");
        Assert.notNull(requestMessagePgp.getTs(), "requestMessagePgp.getTs() is null");
        Assert.notNull(requestMessagePgp.getOp(), "requestMessagePgp.getOp() is null");

        final var dateRequest = converter.parseToDate(sequenceToString(requestMessagePgp.getTs()));
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

    private static Pk toPamPk(RecordPk recordPk) {
        if (recordPk == null) {
            return null;
        }

        return Pk.builder()
                .systemCode(sequenceToString(recordPk.getSystemCode()))
                .id(sequenceToString(recordPk.getId()))
                .build();
    }

    private static DataField toPamDataField(RecordData recordData) {
        if (recordData == null) {
            return null;
        }

        // с версии 1.27.0 данные поля orderReq не используются, получение требований заказа через SAP
        return DataField.builder()
                .primeId(recordData.getPrimeId().toString())
                .nplv(recordData.getNplv())
                .hnum(recordData.getHnum())
                .roll(sequenceToString(recordData.getRoll()))
                .length(parseFloat(recordData.getLength()))
                .thickness(parseFloat(recordData.getThickness()))
                .width(parseFloat(recordData.getWidth()))
                .weightNet(parseFloat(recordData.getWeightNet()))
                .bundleWeight(parseFloat(recordData.getBundleWeight()))
                .kceh(recordData.getKceh())
                .orderNum(recordData.getOrderNum())
                .orderPos(recordData.getOrderPos())
                .orderReq(List.of())
                .specifications(
                        recordData.getSpecifications().stream()
                                .map(CcmPgpKafkaRequestAdapterImpl::toPamSpecs)
                                .collect(Collectors.toList())
                ).chemical(
                        recordData.getChemical() == null
                                ? null
                                : recordData.getChemical().stream()
                                .map(CcmPgpKafkaRequestAdapterImpl::toPamChemicalSpec)
                                .collect(Collectors.toList())
                ).mechanical(
                        recordData.getMechanical() == null
                                ? null
                                : recordData.getMechanical().stream()
                                .map(CcmPgpKafkaRequestAdapterImpl::toPamMechanicalSpec)
                                .collect(Collectors.toList())
                ).metallographic(
                        recordData.getMetallographic() == null
                                ? null
                                : recordData.getMetallographic().stream()
                                .map(CcmPgpKafkaRequestAdapterImpl::toPamMetallographicSpec)
                                .collect(Collectors.toList())
                ).build();
    }

    private static Specs toPamSpecs(RecordSpecifications specifications) {
        return Specs.builder()
                .specCode(specifications.getSpecCode())
                .specName(sequenceToString(specifications.getSpecName()))
                .specTypeCode(specifications.getSpecTypeCode())
                .specValue(sequenceToString(specifications.getSpecValue()))
                .specFormat(sequenceToString(specifications.getSpecFormat()))
                .specMeasure(sequenceToString(specifications.getSpecMeasure()))
                .build();
    }

    private static ChemicalSpec toPamChemicalSpec(RecordChemical recordChemical) {
        return ChemicalSpec.builder()
                .chemCode(recordChemical.getChemCode())
                .chemName(sequenceToString(recordChemical.getChemName()))
                .chemValue(sequenceToString(recordChemical.getChemValue()))
                .chemFormat(sequenceToString(recordChemical.getChemFormat()))
                .build();
    }

    private static MechanicalSpec toPamMechanicalSpec(RecordMechanical mechanical) {
        return MechanicalSpec.builder()
                .hnum(mechanical.getHnum())
                .protNum(mechanical.getProtNum())
                .sampleNum(mechanical.getSampleNum())
                .signAnalysis(mechanical.getSignAnalysis())
                .protDate(sequenceToString(mechanical.getProtDate()))
                .mechData(
                        mechanical.getMechData() == null
                                ? null
                                : mechanical.getMechData().stream()
                                .map(CcmPgpKafkaRequestAdapterImpl::toPamMechanicalData)
                                .collect(Collectors.toList())
                ).build();
    }

    private static MechanicalData toPamMechanicalData(RecordMechData data) {
        return MechanicalData.builder()
                .mechAnalysisId(data.getMechAnalysisId())
                .mechAnalysisData(
                        data.getMechAnalysisData() == null
                                ? null
                                : data.getMechAnalysisData().stream()
                                .map(CcmPgpKafkaRequestAdapterImpl::toPamMechanicalAnalysisData)
                                .collect(Collectors.toList())
                ).build();
    }

    private static MechanicalAnalysisData toPamMechanicalAnalysisData(RecordMechAnalysisData analysis) {
        return MechanicalAnalysisData.builder()
                .mechCode(analysis.getMechCode())
                .mechName(sequenceToString(analysis.getMechName()))
                .mechTypeCode(analysis.getMechTypeCode())
                .mechFormat(sequenceToString(analysis.getMechFormat()))
                .mechValue(sequenceToString(analysis.getMechValue()))
                .mechMeasure(sequenceToString(analysis.getMechMeasure()))
                .build();
    }

    private static MetallographicSpec toPamMetallographicSpec(RecordMetallographic metallographic) {
        return MetallographicSpec.builder()
                .hnum(metallographic.getHnum())
                .protNum(metallographic.getProtNum())
                .protDate(sequenceToString(metallographic.getProtDate()))
                .signAnalysis(metallographic.getSignAnalysis())
                .metgrapData(
                        metallographic.getMetgrapData() == null
                                ? null
                                : metallographic.getMetgrapData().stream()
                                .map(CcmPgpKafkaRequestAdapterImpl::toPamMetallographicData)
                                .collect(Collectors.toList())
                ).build();
    }

    private static MetallographicData toPamMetallographicData(RecordMetgrapData data) {
        return MetallographicData.builder()
                .metgrapAnalysisId(data.getMetgrapAnalysisId())
                .metgrapAnalysisData(data.getMetgrapAnalysisData() == null
                        ? null
                        : data.getMetgrapAnalysisData().stream()
                        .map(CcmPgpKafkaRequestAdapterImpl::toPamMetallographicAnalysisData)
                        .collect(Collectors.toList()))
                .build();
    }

    private static MetallographicAnalysisData toPamMetallographicAnalysisData(RecordMetgrapAnalysisData analysis) {
        return MetallographicAnalysisData.builder()
                .metgrapCode(analysis.getMetgrapCode())
                .metgrapName(sequenceToString(analysis.getMetgrapName()))
                .metgrapFormat(sequenceToString(analysis.getMetgrapFormat()))
                .metgrapValue(sequenceToString(analysis.getMetgrapValue()))
                .metgrapTypeCode(analysis.getMetgrapTypeCode())
                .metgrapMeasure(sequenceToString(analysis.getMetgrapMeasure()))
                .build();
    }

    private static Double parseFloat(Float f) {
        if (f == null) {
            return null;
        }
        return Double.parseDouble(Float.toString(f));
    }

    private static String sequenceToString(CharSequence sequence) {
        if (sequence == null) {
            return null;
        }
        return sequence.toString();
    }

}
