package com.nlmk.kb.server.service.ccm.pgp;

import com.nlmk.attestation.product.api.RequestSource;
import com.nlmk.attestation.product.api.pam.AttestationRequest;
import com.nlmk.attestation.product.api.pam.ChemicalSpec;
import com.nlmk.attestation.product.api.pam.DataPgp;
import com.nlmk.attestation.product.api.pam.MechanicalAnalysisData;
import com.nlmk.attestation.product.api.pam.MechanicalData;
import com.nlmk.attestation.product.api.pam.MechanicalSpec;
import com.nlmk.attestation.product.api.pam.MetallographicAnalysisData;
import com.nlmk.attestation.product.api.pam.MetallographicData;
import com.nlmk.attestation.product.api.pam.MetallographicSpec;
import com.nlmk.attestation.product.api.pam.Pk;
import com.nlmk.attestation.product.api.pam.Specs;
import com.nlmk.attestation.product.api.pam.Value;
import com.nlmk.kb.server.entity.integral.IntegralParams;
import com.nlmk.kb.server.service.CommonConverter;
import com.nlmk.kb.server.service.CommonKafkaRequestAdapter;
import com.nlmk.kb.server.service.integral.IntegralParamsMessageService;
import com.nlmk.kb.server.service.sender.PgpSender;
import com.nlmk.kb.server.util.AdapterUtils;
import java.util.List;
import java.util.stream.Collectors;
import nlmk.l3.ccm.pgp.RecordChemical;
import nlmk.l3.ccm.pgp.RecordData;
import nlmk.l3.ccm.pgp.RecordMechAnalysisData;
import nlmk.l3.ccm.pgp.RecordMechData;
import nlmk.l3.ccm.pgp.RecordMechanical;
import nlmk.l3.ccm.pgp.RecordMetallographic;
import nlmk.l3.ccm.pgp.RecordMetgrapAnalysisData;
import nlmk.l3.ccm.pgp.RecordMetgrapData;
import nlmk.l3.ccm.pgp.RecordPk;
import nlmk.l3.ccm.pgp.RecordSpecifications;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import static com.nlmk.attestation.product.api.specification.SpecCode.COIL_TEMPERATURE_MAX;
import static com.nlmk.attestation.product.api.specification.SpecCode.COIL_TEMPERATURE_MIN;
import static com.nlmk.attestation.product.api.specification.SpecCode.END_ROLLING_TEMPERATURE_MAX;
import static com.nlmk.attestation.product.api.specification.SpecCode.END_ROLLING_TEMPERATURE_MIN;
import static com.nlmk.attestation.product.api.specification.SpecCode.PERCENTAGE_STRIP_LENGTH_TOLERANCE;
import static com.nlmk.attestation.product.api.specification.SpecCode.PERCENTAGE_STRIP_LENGTH_TOLERANCE_12;
import static com.nlmk.attestation.product.api.specification.SpecCode.PERCENTAGE_STRIP_LENGTH_TOLERANCE_23;
import static com.nlmk.attestation.product.api.specification.SpecCode.PERCENTAGE_STRIP_LENGTH_TOLERANCE_FULL;
import static com.nlmk.attestation.product.api.specification.SpecCode.PROFILE;
import static com.nlmk.attestation.product.api.specification.SpecCode.WEDGE;

@Component
public class CcmPgpKafkaRequestAdapterImpl extends CommonKafkaRequestAdapter<nlmk.l3.ccm.pgp.AttestationRequest> {

    private final CommonConverter converter;

    public CcmPgpKafkaRequestAdapterImpl(
            CommonConverter converter,
            PgpSender sender,
            IntegralParamsMessageService integralParamsMessageService
    ) {
        super(sender, integralParamsMessageService);
        this.converter = converter;
    }

    private static final List<IntegralParams> INTEGRAL_PARAMS_ATTRS = List.of(
            new IntegralParams(END_ROLLING_TEMPERATURE_MIN.getValue()),
            new IntegralParams(END_ROLLING_TEMPERATURE_MAX.getValue()),
            new IntegralParams(COIL_TEMPERATURE_MIN.getValue()),
            new IntegralParams(COIL_TEMPERATURE_MAX.getValue()),
            new IntegralParams(PERCENTAGE_STRIP_LENGTH_TOLERANCE.getValue()),
            new IntegralParams(PROFILE.getValue()),
            new IntegralParams(WEDGE.getValue()),
            new IntegralParams(PERCENTAGE_STRIP_LENGTH_TOLERANCE_FULL.getValue()),
            new IntegralParams(PERCENTAGE_STRIP_LENGTH_TOLERANCE_12.getValue()),
            new IntegralParams(PERCENTAGE_STRIP_LENGTH_TOLERANCE_23.getValue()));

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

    private DataPgp toPamDataField(RecordData recordData) {
        if (recordData == null) {
            return null;
        }

        return DataPgp.builder()
                .primeId(recordData.getPrimeId().toString())
                .nplv(recordData.getNplv())
                .hnum(recordData.getHnum())
                .roll(AdapterUtils.sequenceToString(recordData.getRoll()))
                .length(AdapterUtils.toBigDecimal(recordData.getLength()))
                .thickness(AdapterUtils.toBigDecimal(recordData.getThickness()))
                .width(AdapterUtils.toBigDecimal(recordData.getWidth()))
                .weightNet(AdapterUtils.toBigDecimal(recordData.getWeightNet()))
                .bundleWeight(AdapterUtils.toBigDecimal(recordData.getBundleWeight()))
                .kceh(recordData.getKceh())
                .orderNum(recordData.getOrderNum())
                .orderPos(recordData.getOrderPos())
                .attestationPoint(recordData.getAttestationPoint())
                .cutTaskNum(recordData.getCutTaskNum())
                .cutTaskDate(AdapterUtils.sequenceToString(recordData.getCutTaskDate()))
                .cutTaskStrNum(recordData.getCutTaskStrNum())
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
                ).integralParameters(processIntegralParams(recordData.getPrimeId().toString(),
                        INTEGRAL_PARAMS_ATTRS, RequestSource.CCM))
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
