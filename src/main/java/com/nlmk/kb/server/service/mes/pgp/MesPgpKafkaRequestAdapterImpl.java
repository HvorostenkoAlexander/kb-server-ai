package com.nlmk.kb.server.service.mes.pgp;

import com.nlmk.attestation.product.api.AdditionalProperty;
import com.nlmk.attestation.product.api.AttributeAttestationGroup;
import com.nlmk.attestation.product.api.Kceh;
import com.nlmk.attestation.product.api.Params;
import com.nlmk.attestation.product.api.QualityIndicator;
import com.nlmk.attestation.product.api.RequestSource;
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
import com.nlmk.attestation.product.api.specification.SpecCode;
import com.nlmk.kb.server.entity.integral.IntegralParams;
import com.nlmk.kb.server.service.CommonConverter;
import com.nlmk.kb.server.service.CommonKafkaRequestAdapter;
import com.nlmk.kb.server.service.client.NsiClient;
import com.nlmk.kb.server.service.integral.IntegralParamsMessageService;
import com.nlmk.kb.server.service.sender.PgpSender;
import com.nlmk.kb.server.util.AdapterUtils;
import lombok.extern.slf4j.Slf4j;
import nlmk.mes.cgp.asap.adapter.analysis.request.v2.AsapAnalysisRequestVer2;
import nlmk.mes.cgp.asap.adapter.analysis.request.v2.PkType;
import nlmk.mes.cgp.asap.adapter.analysis.request.v2.RecordAddProperties;
import nlmk.mes.cgp.asap.adapter.analysis.request.v2.RecordAnalyzes;
import nlmk.mes.cgp.asap.adapter.analysis.request.v2.RecordData;
import nlmk.mes.cgp.asap.adapter.analysis.request.v2.RecordMarking;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;


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
import static com.nlmk.kb.server.config.KbConstants.TEMPLATE_INTEGER_PARSE_EXCEPTION;
import static com.nlmk.kb.server.config.KbConstants.TEMPLATE_MECHANIC;
import static com.nlmk.kb.server.config.KbConstants.TEMPLATE_METALLOGRAPHIC;

import static com.nlmk.kb.server.config.KbConstants.TEMPLATE_PROT_NUM;
import static com.nlmk.kb.server.config.KbConstants.TEMPLATE_TEST_TYPE_REQUEST_ID;
import static com.nlmk.kb.server.config.KbConstants.TEMPLATE_HNUM;

@Component
@Slf4j
public class MesPgpKafkaRequestAdapterImpl extends CommonKafkaRequestAdapter<AsapAnalysisRequestVer2> {

    private final CommonConverter converter;
    private final NsiClient nsiClient;

    public MesPgpKafkaRequestAdapterImpl(
            CommonConverter converter,
            NsiClient client,
            PgpSender sender,
            IntegralParamsMessageService integralParamsMessageService
    ) {
        super(sender, integralParamsMessageService);
        this.converter = converter;
        this.nsiClient = client;
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

    // Список кодов, которые используются для сборки DataPgp, передаваемые в коллекции marking
    private final List<SpecCode> pgpMarkingCodes = Arrays.asList(
            SpecCode.STEEL_MARK, // 3 - Марка (prProdMark)
            SpecCode.MELTING_ID, // 93 - № плавки (nplv)
            SpecCode.HNUM, // 5750 - Номер ГК партии (hnum)
            SpecCode.ROLL_PACKAGE_SHEET_NUMBER, // 463 - Номер рулона/пачки (roll)
            SpecCode.THICKNESS_OF_ROLLED_PRODUCTS, // 416 - Толщина проката (thickness)
            SpecCode.WIDTH_PRODUCT, // 587 - Ширина продукции (width)
            SpecCode.WEIGHT_NET, // 1203 - Масса единицы продукции (weightNet)
            SpecCode.SMENA_NUMBER // 1045 - Номер прокатной смены (smenaNumber)
    );

    @Override
    public com.nlmk.attestation.product.api.pam.AttestationRequest adapt(AsapAnalysisRequestVer2 requestMessagePgp) {
        Assert.notNull(requestMessagePgp, "requestMessagePgp is null");
        Assert.notNull(requestMessagePgp.getTs(), "requestMessagePgp.getTs() is null");
        Assert.notNull(requestMessagePgp.getOp(), "requestMessagePgp.getOp() is null");

        final var dateRequest = converter.parseToDate(AdapterUtils.sequenceToString(requestMessagePgp.getTs()));
        Assert.notNull(dateRequest, "Не удалось получить сведения о ts в запросе на аттестацию");

        return com.nlmk.attestation.product.api.pam.AttestationRequest.builder()
                .value(Value.builder()
                        .ts(dateRequest)
                        .op(requestMessagePgp.getOp().toString())
                        .pk(toPamPk(requestMessagePgp.getPk()))
                        .data(toPamDataField(requestMessagePgp.getData(), requestMessagePgp.getPk()))
                        .build())
                .build();
    }

    private Pk toPamPk(PkType recordPk) {
        if (recordPk == null) {
            return null;
        }

        return Pk.builder()
                .id(AdapterUtils.sequenceToString(recordPk.getMetalUnitId()))
                .systemCode("MES")
                .build();
    }

    private DataPgp toPamDataField(RecordData recordData, PkType pk) {
        if (recordData == null) {
            return null;
        }

        DataPgp.DataPgpBuilder<?, ?> builder = DataPgp.builder();
        builder.requestSource(RequestSource.MES);

        var primeId = AdapterUtils.sequenceToString(pk.getMetalUnitId());

        if (primeId != null) {
            builder.metalUnitId(UUID.fromString(primeId));
        }
        builder.kceh(Kceh.PGP.getValue());
        builder.orderNum(recordData.getOrderNum());
        builder.orderPos(recordData.getOrderPosition());

        String hnum = "";

        var specs = new ArrayList<Specs>();
        var chemical = new ArrayList<ChemicalSpec>();
        var metallographic = new ArrayList<MetallographicSpec>();
        var mechanical = new ArrayList<MechanicalSpec>();

        // Разбор маркировки
        for (var mark : recordData.getMarking()) {
            for (var specCode : pgpMarkingCodes) {
                if (specCode.getValue().equals(mark.getAttrCode())) {
                    buildDataPgpMarking(builder, specCode, String.valueOf(mark.getValue()));
                    if (specCode.equals(SpecCode.HOT_ROLLED_STEEL_NUMBER)) {
                        hnum = String.valueOf(mark.getValue());
                    }
                }
            }
            specs.add(buildSpecFromMarking(mark));
        }

        for (var analyze : recordData.getAnalyzes()) {

            var attestationGroup = nsiClient.getAttributeAttestationGroup(
                    analyze.getGroupId().toString(),
                    primeId
            );

            if (attestationGroup.isPresent()) {

                var attGroup = attestationGroup.get();

                if (attGroup.getCode().equals(AttributeAttestationGroup.HIM.getCode())) {
                    log.info("Обработка группы химии");
                    buildChemicalSpec(chemical, analyze, primeId);
                } else if (attGroup.getCode().equals(AttributeAttestationGroup.MET.getCode())
                        || attGroup.getCode().equals(AttributeAttestationGroup.MECH.getCode())) {

                    var testTypeRequestId = analyze.getTestTypeRequestId(); // signAnalyses
                    String protNum = null;
                    String protDate = null;
                    if (analyze.getProtNum() != null) {
                        protNum = analyze.getProtNum().toString();
                    }
                    if (analyze.getProtDate() != null) {
                        protDate = analyze.getProtDate().toString();
                    }

                    if (attGroup.getCode().equals(AttributeAttestationGroup.MET.getCode())) {
                        buildMetallographicSpec(metallographic, analyze, primeId, testTypeRequestId.toString(), hnum, protNum, protDate);
                    } else {
                        buildMechanicalSpec(mechanical, analyze, primeId, testTypeRequestId.toString(), hnum, protNum, protDate);
                    }

                } else {
                    buildSpec(analyze, specs, primeId);
                }

            } else {
                log.warn("Не найдена группа аттестуемых характеристик \"[{}]\", по коду [{}], пропуск",
                        analyze.getGroupId(), analyze.getGroupName());
            }

        }

        builder.chemical(chemical);
        builder.metallographic(metallographic);
        builder.mechanical(mechanical);
        builder.specifications(specs);

        return builder.build();
    }

    private Specs buildSpecFromMarking(RecordMarking marking) {
        var builder = Specs.builder();
        builder.specCode(marking.getAttrCode());
        builder.specValue(marking.getValue().toString());
        builder.specFormat(marking.getDataTypePhysical().toString());
        builder.specTypeCode(getIntegerValueOfDataType(marking.getDataTypePhysical().toString()));
        builder.specName(marking.getAttrName().toString());
        builder.fromMesMarking(true);
        return builder.build();
    }

    private void buildChemicalSpec(ArrayList<ChemicalSpec> chemicalSpecs, RecordAnalyzes recordAnalyzes, String primeId) {
        for (var qIndicator: recordAnalyzes.getQualityIndicators()) {

            var paramsBuilder = Params.builder();
            var indicatorBuilder = QualityIndicator.builder();

            if (qIndicator.getMeasure() != null) {
                indicatorBuilder.measureId(qIndicator.getMeasure().getMeasureId().toString());
                indicatorBuilder.measureName(qIndicator.getMeasure().getMeasureName().toString());
            }
            indicatorBuilder.comparison(qIndicator.getComparison().toString());
            indicatorBuilder.dataTypePhysical(qIndicator.getDataTypePhysical().toString());

            paramsBuilder.property(indicatorBuilder.build());

            if (qIndicator.getAddProperties() != null) {
                if (!qIndicator.getAddProperties().isEmpty()) {
                    var additionalProperties = buildAndParseAdditionalProperties(qIndicator.getAddProperties(), primeId);
                    paramsBuilder.additionalProperties(additionalProperties);
                }
            }

            chemicalSpecs.add(ChemicalSpec.builder()
                    .chemCode(qIndicator.getAttrCode())
                    .chemName(qIndicator.getAttrName().toString())
                    .chemValue(qIndicator.getValue().toString())
                    .chemFormat(qIndicator.getDataTypePhysical().toString())
                    .params(paramsBuilder.build())
                    .build());

        }
    }

    private void buildMechanicalSpec(ArrayList<MechanicalSpec> mechanicalSpecs,
                                     RecordAnalyzes recordAnalyzes,
                                     String primeId,
                                     String testTypeRequestId,
                                     String hnum,
                                     String protNum,
                                     String protDate) {
        for (var qIndicator : recordAnalyzes.getQualityIndicators()) {

            var paramsBuilder = Params.builder();
            var indicatorBuilder = QualityIndicator.builder();
            var mechanicSpecBuilder = MechanicalSpec.builder();

            if (qIndicator.getMeasure() != null) {
                indicatorBuilder.measureId(qIndicator.getMeasure().getMeasureId().toString());
                indicatorBuilder.measureName(qIndicator.getMeasure().getMeasureName().toString());
            }
            indicatorBuilder.comparison(qIndicator.getComparison().toString());
            indicatorBuilder.dataTypePhysical(qIndicator.getDataTypePhysical().toString());

            paramsBuilder.property(indicatorBuilder.build());

            if (qIndicator.getAddProperties() != null) {
                if (!qIndicator.getAddProperties().isEmpty()) {
                    var additionalProperties = buildAndParseAdditionalProperties(qIndicator.getAddProperties(), primeId);
                    paramsBuilder.additionalProperties(additionalProperties);
                }
            }

            // testTypeRequestId используется как signAnalysis в случае механики
            parseIntegerFromString(testTypeRequestId, TEMPLATE_TEST_TYPE_REQUEST_ID, TEMPLATE_MECHANIC)
                    .ifPresent(mechanicSpecBuilder::signAnalysis);
            parseIntegerFromString(hnum, TEMPLATE_HNUM, TEMPLATE_MECHANIC)
                    .ifPresent(mechanicSpecBuilder::hnum);
            parseIntegerFromString(protNum, TEMPLATE_PROT_NUM, TEMPLATE_MECHANIC)
                    .ifPresent(mechanicSpecBuilder::protNum);

            mechanicSpecBuilder.protDate(protDate);
            // TODO sampleNum - откуда брать?

            List<MechanicalData> mechanicalData = new ArrayList<>();

            // TODO mechAnalysisId - откуда брать?
            var metData = MechanicalData.builder()
                    //.mechAnalysisId()
                    .mechAnalysisData(
                            Collections.singletonList(
                                    MechanicalAnalysisData.builder()
                                            .mechCode(qIndicator.getAttrCode())
                                            .mechName(qIndicator.getAttrName().toString())
                                            .mechValue(qIndicator.getValue().toString())
                                            .mechFormat(qIndicator.getDataTypePhysical().toString())
                                            .mechTypeCode(getIntegerValueOfDataType(qIndicator.getDataTypePhysical().toString()))
                                            // TODO measure - что добавлять?
                                            .mechMeasure(qIndicator.getMeasure() == null
                                                       ? null : qIndicator.getMeasure().getMeasureId().toString())
                                            .build()
                            )
                    )
                    .build();

            mechanicalData.add(metData);
            mechanicSpecBuilder.mechData(mechanicalData);
            mechanicSpecBuilder.params(paramsBuilder.build());

            mechanicalSpecs.add(mechanicSpecBuilder.build());

        }
    }

    private void buildMetallographicSpec(ArrayList<MetallographicSpec> metallographicSpecs,
                                         RecordAnalyzes recordAnalyzes,
                                         String primeId,
                                         String testTypeRequestId,
                                         String hnum,
                                         String protNum,
                                         String protDate) {
        for (var qIndicator : recordAnalyzes.getQualityIndicators()) {

            var paramsBuilder = Params.builder();
            var indicatorBuilder = QualityIndicator.builder();
            var metallographicSpecBuilder = MetallographicSpec.builder();

            if (qIndicator.getMeasure() != null) {
                indicatorBuilder.measureId(qIndicator.getMeasure().getMeasureId().toString());
                indicatorBuilder.measureName(qIndicator.getMeasure().getMeasureName().toString());
            }
            indicatorBuilder.comparison(qIndicator.getComparison().toString());
            indicatorBuilder.dataTypePhysical(qIndicator.getDataTypePhysical().toString());

            paramsBuilder.property(indicatorBuilder.build());

            if (qIndicator.getAddProperties() != null) {
                if (!qIndicator.getAddProperties().isEmpty()) {
                    var additionalProperties = buildAndParseAdditionalProperties(qIndicator.getAddProperties(), primeId);
                    paramsBuilder.additionalProperties(additionalProperties);
                }
            }

            // testTypeRequestId используется как signAnalysis в случае металлографии
            parseIntegerFromString(testTypeRequestId, TEMPLATE_TEST_TYPE_REQUEST_ID, TEMPLATE_METALLOGRAPHIC)
                    .ifPresent(metallographicSpecBuilder::signAnalysis);
            parseIntegerFromString(hnum, TEMPLATE_HNUM, TEMPLATE_METALLOGRAPHIC)
                    .ifPresent(metallographicSpecBuilder::hnum);
            parseIntegerFromString(protNum, TEMPLATE_PROT_NUM, TEMPLATE_METALLOGRAPHIC)
                    .ifPresent(metallographicSpecBuilder::protNum);

            metallographicSpecBuilder.protDate(protDate);

            List<MetallographicData> metallographicData = new ArrayList<>();

            // TODO metgrapAnalysisId - откуда брать?
            var metData = MetallographicData.builder()
                    //.metgrapAnalysisId()
                    .metgrapAnalysisData(
                            Collections.singletonList(
                                    MetallographicAnalysisData.builder()
                                            .metgrapCode(qIndicator.getAttrCode())
                                            .metgrapName(qIndicator.getAttrName().toString())
                                            .metgrapValue(qIndicator.getValue().toString())
                                            .metgrapTypeCode(getIntegerValueOfDataType(qIndicator.getDataTypePhysical().toString()))
                                            // TODO measure - что добавлять?
                                            .metgrapMeasure(qIndicator.getMeasure() == null
                                                            ? null : qIndicator.getMeasure().getMeasureId().toString())
                                            .build()
                            )
                    )
                    .build();
            metallographicData.add(metData);
            metallographicSpecBuilder.metgrapData(metallographicData);
            metallographicSpecBuilder.params(paramsBuilder.build());

            metallographicSpecs.add(metallographicSpecBuilder.build());

        }
    }

    private void buildSpec(RecordAnalyzes recordAnalyzes,
                           ArrayList<Specs> specs,
                           String primeId) {

        for (var qIndicator : recordAnalyzes.getQualityIndicators()) {

            var paramsBuilder = Params.builder();
            var indicatorBuilder = QualityIndicator.builder();
            var specsBuilder = Specs.builder();

            if (qIndicator.getMeasure() != null) {
                indicatorBuilder.measureId(qIndicator.getMeasure().getMeasureId().toString());
                indicatorBuilder.measureName(qIndicator.getMeasure().getMeasureName().toString());
            }
            indicatorBuilder.comparison(qIndicator.getComparison().toString());
            indicatorBuilder.dataTypePhysical(qIndicator.getDataTypePhysical().toString());

            paramsBuilder.property(indicatorBuilder.build());

            if (qIndicator.getAddProperties() != null) {
                if (!qIndicator.getAddProperties().isEmpty()) {
                    var additionalProperties = buildAndParseAdditionalProperties(qIndicator.getAddProperties(), primeId);
                    paramsBuilder.additionalProperties(additionalProperties);
                }
            }

            specsBuilder.specCode(qIndicator.getAttrCode());
            specsBuilder.specName(qIndicator.getAttrName().toString());
            specsBuilder.specValue(qIndicator.getValue().toString());
            specsBuilder.specTypeCode(getIntegerValueOfDataType(qIndicator.getDataTypePhysical().toString()));
            specsBuilder.specFormat(qIndicator.getDataTypePhysical().toString());
            specsBuilder.params(paramsBuilder.build());

            specs.add(specsBuilder.build());

        }
    }

    private List<AdditionalProperty> buildAndParseAdditionalProperties(List<RecordAddProperties> properties, String primeId) {
        List<AdditionalProperty> additionalProperties = new ArrayList<>();

        for (var prop : properties) {
            var addPropertyBuilder = AdditionalProperty.builder();

            addPropertyBuilder.propCode(prop.getAttrCode());
            addPropertyBuilder.value(prop.getValue().toString());
            if (prop.getMeasure() != null) {
                addPropertyBuilder.measureId(prop.getMeasure().getMeasureId().toString());
                addPropertyBuilder.measureName(prop.getMeasure().getMeasureName().toString());
            }
            addPropertyBuilder.comparison(prop.getComparison().toString());
            addPropertyBuilder.dataTypePhysical(prop.getDataTypePhysical().toString());

            additionalProperties.add(addPropertyBuilder.build());
        }

        return additionalProperties;
    }

    private void buildDataPgpMarking(DataPgp.DataPgpBuilder<?, ?> builder, SpecCode specCode, String value) {
        try {
            switch (specCode) {
                case MELTING_ID: {
                    builder.nplv(Integer.valueOf(value));
                    break;
                }
                case HNUM: {
                    builder.hnum(Integer.valueOf(value));
                    break;
                }
                case ROLL_PACKAGE_SHEET_NUMBER: {
                    builder.roll(value);
                    break;
                }
                case LENGTH_PRODUCT: {
                    builder.length(new BigDecimal(value));
                    break;
                }
                case THICKNESS_OF_ROLLED_PRODUCTS: {
                    builder.thickness(new BigDecimal(value));
                    break;
                }
                case WIDTH_PRODUCT: {
                    builder.width(new BigDecimal(value));
                    break;
                }
                case WEIGHT_NET: {
                    builder.weightNet(new BigDecimal(value));
                    break;
                }
                case SMENA_NUMBER: {
                    builder.smenaNumber(Long.valueOf(value));
                    break;
                }
                default: {
                    break;
                }
            }
        } catch (NumberFormatException e) {
            log.error("Ошибка парсинга значения [{}] для кода: [{}], [{}]", value, specCode.getValue(), specCode.getDesc());
        }
    }

    private Optional<Integer> parseIntegerFromString(String str, String attributeTemplate, String groupTemplate) {
        try {
            return Optional.of(Integer.valueOf(str));
        } catch (NumberFormatException e) {
            log.warn(TEMPLATE_INTEGER_PARSE_EXCEPTION, attributeTemplate, str, groupTemplate);
        }
        return Optional.empty();
    }

    private Integer getIntegerValueOfDataType(String dataType) {
        if (Objects.equals(dataType, "int") || Objects.equals(dataType, "long")) {
            return 2;
        }
        return 1;
    }

}
