package com.nlmk.kb.server.service.mes.pgp;

import com.nlmk.attestation.product.api.AdditionalProperty;
import com.nlmk.attestation.product.api.AttributeAttestationGroup;
import com.nlmk.attestation.product.api.Kceh;
import com.nlmk.attestation.product.api.Params;
import com.nlmk.attestation.product.api.Property;
import com.nlmk.attestation.product.api.pam.ChemicalSpec;
import com.nlmk.attestation.product.api.pam.DataPgp;
import com.nlmk.attestation.product.api.pam.MechanicalAnalysisData;
import com.nlmk.attestation.product.api.pam.MechanicalData;
import com.nlmk.attestation.product.api.pam.MechanicalSpec;
import com.nlmk.attestation.product.api.pam.MetallographicAnalysisData;
import com.nlmk.attestation.product.api.pam.MetallographicData;
import com.nlmk.attestation.product.api.pam.MetallographicSpec;
import com.nlmk.attestation.product.api.pam.Pk;
import com.nlmk.attestation.product.api.pam.Value;
import com.nlmk.attestation.product.api.specification.SpecCode;
import com.nlmk.kb.server.service.CommonConverter;
import com.nlmk.kb.server.service.ccm.KafkaRequestAdapter;
import com.nlmk.kb.server.service.client.NsiClient;
import com.nlmk.kb.server.util.AdapterUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nlmk.mes.cgp.asap.adapter.analysis.request.v0.AsapAnalysisRequestVer0;
import nlmk.mes.cgp.asap.adapter.analysis.request.v0.PkType;
import nlmk.mes.cgp.asap.adapter.analysis.request.v0.RecordAddProperties;
import nlmk.mes.cgp.asap.adapter.analysis.request.v0.RecordAnalyzes;
import nlmk.mes.cgp.asap.adapter.analysis.request.v0.RecordData;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;


import static com.nlmk.kb.server.config.KbConstants.TEMPLATE_INTEGER_PARSE_EXCEPTION;
import static com.nlmk.kb.server.config.KbConstants.TEMPLATE_MECHANIC;
import static com.nlmk.kb.server.config.KbConstants.TEMPLATE_METALLOGRAPHIC;

import static com.nlmk.kb.server.config.KbConstants.TEMPLATE_PROT_NUM;
import static com.nlmk.kb.server.config.KbConstants.TEMPLATE_TEST_TYPE_REQUEST_ID;
import static com.nlmk.kb.server.config.KbConstants.TEMPLATE_HNUM;

@Component
@RequiredArgsConstructor
@Slf4j
public class MesPgpKafkaRequestAdapterImpl implements KafkaRequestAdapter<AsapAnalysisRequestVer0> {




    private final CommonConverter converter;
    private final NsiClient nsiClient;

    // Список кодов, которые используются для сборки DataPgp, передаваемые в коллекции marking
    private final List<SpecCode> pgpMarkingCodes = Arrays.asList(
            SpecCode.MELTING_ID, // 93 - № плавки (nplv)
            SpecCode.HOT_ROLLED_STEEL_NUMBER, // 37 - Номер ГК партии (hnum)
            SpecCode.ROLL_PACKAGE_SHEET_NUMBER, // 463 - Номер рулона/пачки (roll)
            SpecCode.LENGTH_PRODUCT, // 592 - Длина продукции (length)
            SpecCode.THICKNESS_PRODUCTS, // 596 - Толщина проката (thickness)
            SpecCode.WIDTH_PRODUCT, // 587 - Ширина продукции (width)
            SpecCode.UNIT_WEIGHT, // 91 - Масса единицы продукции (weightNet)
            SpecCode.BUNDLE_MASS // 461 - Масса связки (bundleWeight)
    );

    @Override
    public com.nlmk.attestation.product.api.pam.AttestationRequest adapt(AsapAnalysisRequestVer0 requestMessagePgp) {
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
                .systemCode(AdapterUtils.sequenceToString(recordPk.getSystemCode()))
                .id(AdapterUtils.sequenceToString(recordPk.getMetalUnitId()))
                .build();
    }

    private DataPgp toPamDataField(RecordData recordData, PkType pk) {
        if (recordData == null) {
            return null;
        }

        DataPgp.DataPgpBuilder<?, ?> builder = DataPgp.builder();

        var primeId = AdapterUtils.sequenceToString(pk.getMetalUnitId());

        builder.primeId(primeId);
        builder.kceh(Kceh.PGP.getValue());
        builder.orderNum(recordData.getOrderNum());
        builder.orderPos(recordData.getOrderPosition());

        String hnum = "";

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
        }

        for (var analyze : recordData.getAnalyzes()) {

            var attestationGroup = nsiClient.getAttributeAttestationGroup(
                    analyze.getGroupId().toString(),
                    primeId
            );

            if (attestationGroup.isPresent()) {

                var attGroup = attestationGroup.get();

                if (attGroup.getCode().equals(AttributeAttestationGroup.HIM.getCode())) {
                    buildChemicalSpec(builder, analyze, primeId);
                } else if (attGroup.getCode().equals(AttributeAttestationGroup.MET.getCode())
                        || attGroup.getCode().equals(AttributeAttestationGroup.MECH.getCode())) {

                    var testTypeRequestId = analyze.getTestTypeRequestId(); // signAnalyses
                    String protNum = null;
                    String protDate = null;
                    if (analyze.getProtNum() != null) {
                        protNum = (String) analyze.getProtNum();
                    }
                    if (analyze.getProtDate() != null) {
                        protDate = (String) analyze.getProtDate();
                    }

                    if (attGroup.getCode().equals(AttributeAttestationGroup.MET.getCode())) {
                        buildMetallographicSpec(builder, analyze, primeId, (String) testTypeRequestId, hnum, protNum, protDate);
                    } else {
                        buildMechanicalSpec(builder, analyze, primeId, (String) testTypeRequestId, hnum, protNum, protDate);
                    }

                } else {
                    buildSpec(builder, analyze, primeId);
                }

            } else {
                log.warn("Не найдена группа аттестуемых характеристик \"[{}]\", по коду [{}], пропуск",
                        analyze.getGroupId(), analyze.getGroupName());
            }

        }

        return builder.build();
    }

    private void buildChemicalSpec(DataPgp.DataPgpBuilder<?, ?> builder, RecordAnalyzes recordAnalyzes, String primeId) {
        List<ChemicalSpec> chemicalSpecs = new ArrayList<>();
        for (var qIndicator: recordAnalyzes.getQualityIndicators()) {

            var attributesDto = nsiClient.getAttributes((String) qIndicator.getAttrId(), primeId);

            if (attributesDto.isPresent()) {

                var attributes = attributesDto.get();
                var paramsBuilder = Params.builder();
                var propertyBuilder = Property.builder();

                if (qIndicator.getMeasure() != null) {
                    propertyBuilder.measureId((String) qIndicator.getMeasure().getMeasureId());
                    propertyBuilder.measureName((String) qIndicator.getMeasure().getMeasureName());
                }
                propertyBuilder.relation(qIndicator.getRelation().toString());
                propertyBuilder.dataTypePhysical((String) qIndicator.getDataTypePhysical());

                paramsBuilder.property(propertyBuilder.build());

                if (!qIndicator.getAddProperties().isEmpty()) {
                    var additionalProperties = buildAndParseAdditionalProperties(qIndicator.getAddProperties(), primeId);
                    paramsBuilder.additionalProperties(additionalProperties);
                }

                chemicalSpecs.add(ChemicalSpec.builder()
                        .chemCode(attributes.getCode())
                        .chemName(attributes.getName())
                        .chemValue((String) qIndicator.getValue())
                        .chemValue((String) qIndicator.getDataTypePhysical())
                        .params(paramsBuilder.build())
                        .build());

            } else {
                log.warn("Не найдены аттрибуты в справочнике NSI для химии, код: [{}], наименование \"[{}]\", пропущено",
                        qIndicator.getAttrId(), qIndicator.getAttrName());
            }
        }
        builder.chemical(chemicalSpecs);
    }

    private void buildMechanicalSpec(DataPgp.DataPgpBuilder<?, ?> builder,
                                     RecordAnalyzes recordAnalyzes,
                                     String primeId,
                                     String testTypeRequestId,
                                     String hnum,
                                     String protNum,
                                     String protDate) {
        List<MechanicalSpec> mechanicalSpecs = new ArrayList<>();
        for (var qIndicator : recordAnalyzes.getQualityIndicators()) {

            var attributesDto = nsiClient.getAttributes((String) qIndicator.getAttrId(), primeId);

            if (attributesDto.isPresent()) {

                var attributes = attributesDto.get();
                var paramsBuilder = Params.builder();
                var propertyBuilder = Property.builder();
                var mechanicSpecBuilder = MechanicalSpec.builder();

                if (qIndicator.getMeasure() != null) {
                    propertyBuilder.measureId((String) qIndicator.getMeasure().getMeasureId());
                    propertyBuilder.measureName((String) qIndicator.getMeasure().getMeasureName());
                }
                propertyBuilder.relation(qIndicator.getRelation().toString());
                propertyBuilder.dataTypePhysical((String) qIndicator.getDataTypePhysical());

                if (!qIndicator.getAddProperties().isEmpty()) {
                    var additionalProperties = buildAndParseAdditionalProperties(qIndicator.getAddProperties(), primeId);
                    paramsBuilder.additionalProperties(additionalProperties);
                }

                // testTypeRequestId используется как signAnalysis в случае металлографии
                parseIntegerFromString(testTypeRequestId, TEMPLATE_TEST_TYPE_REQUEST_ID, TEMPLATE_MECHANIC)
                        .ifPresent(mechanicSpecBuilder::signAnalysis);
                if (!hnum.isEmpty()) {
                    parseIntegerFromString(hnum, TEMPLATE_HNUM, TEMPLATE_MECHANIC)
                            .ifPresent(mechanicSpecBuilder::hnum);
                }
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
                                                .mechCode(attributes.getCode())
                                                .mechName(attributes.getName())
                                                .mechValue((String) qIndicator.getValue())
                                                // TODO measure - что добавлять?
                                                .mechValue((String) qIndicator.getMeasure().getMeasureId())
                                                .build()
                                )
                        )
                        .build();

                mechanicalData.add(metData);
                mechanicSpecBuilder.mechData(mechanicalData);

                mechanicalSpecs.add(mechanicSpecBuilder.build());

            } else {
                log.warn("Не найдены аттрибуты в справочнике NSI для механики, код: [{}], наименование \"[{}]\", пропущено",
                        qIndicator.getAttrId(), qIndicator.getAttrName());
            }

        }
        builder.mechanical(mechanicalSpecs);
    }

    private void buildMetallographicSpec(DataPgp.DataPgpBuilder<?, ?> builder,
                                         RecordAnalyzes recordAnalyzes,
                                         String primeId,
                                         String testTypeRequestId,
                                         String hnum,
                                         String protNum,
                                         String protDate) {
        List<MetallographicSpec> metallographicSpecs = new ArrayList<>();
        for (var qIndicator : recordAnalyzes.getQualityIndicators()) {

            var attributesDto = nsiClient.getAttributes((String) qIndicator.getAttrId(), primeId);

            if (attributesDto.isPresent()) {

                var attributes = attributesDto.get();
                var paramsBuilder = Params.builder();
                var propertyBuilder = Property.builder();
                var metallographicSpecBuilder = MetallographicSpec.builder();

                if (qIndicator.getMeasure() != null) {
                    propertyBuilder.measureId((String) qIndicator.getMeasure().getMeasureId());
                    propertyBuilder.measureName((String) qIndicator.getMeasure().getMeasureName());
                }
                propertyBuilder.relation(qIndicator.getRelation().toString());
                propertyBuilder.dataTypePhysical((String) qIndicator.getDataTypePhysical());

                if (!qIndicator.getAddProperties().isEmpty()) {
                    var additionalProperties = buildAndParseAdditionalProperties(qIndicator.getAddProperties(), primeId);
                    paramsBuilder.additionalProperties(additionalProperties);
                }

                // testTypeRequestId используется как signAnalysis в случае металлографии
                parseIntegerFromString(testTypeRequestId, TEMPLATE_TEST_TYPE_REQUEST_ID, TEMPLATE_METALLOGRAPHIC)
                        .ifPresent(metallographicSpecBuilder::signAnalysis);
                if (!hnum.isEmpty()) {
                    parseIntegerFromString(hnum, TEMPLATE_HNUM, TEMPLATE_METALLOGRAPHIC)
                            .ifPresent(metallographicSpecBuilder::hnum);
                }
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
                                                .metgrapCode(attributes.getCode())
                                                .metgrapName(attributes.getName())
                                                .metgrapValue((String) qIndicator.getValue())
                                                // TODO measure - что добавлять?
                                                .metgrapMeasure((String) qIndicator.getMeasure().getMeasureId())
                                                .build()
                                )
                        )
                        .build();
                metallographicData.add(metData);
                metallographicSpecBuilder.metgrapData(metallographicData);

                metallographicSpecs.add(metallographicSpecBuilder.build());

            } else {
                log.warn("Не найдены аттрибуты в справочнике NSI для металлографии, код: [{}], наименование \"[{}]\", пропущено",
                        qIndicator.getAttrId(), qIndicator.getAttrName());
            }
        }
        builder.metallographic(metallographicSpecs);
    }

    private void buildSpec(DataPgp.DataPgpBuilder<?, ?> builder, RecordAnalyzes recordAnalyzes, String primeId) {
    }

    private List<AdditionalProperty> buildAndParseAdditionalProperties(List<RecordAddProperties> properties, String primeId) {
        List<AdditionalProperty> additionalProperties = new ArrayList<>();

        for (var prop : properties) {
            var attributesDto = nsiClient.getAttributes((String) prop.getAttrId(), primeId);
            if (attributesDto.isPresent()) {
                var attributes = attributesDto.get();
                var addPropertyBuilder = AdditionalProperty.builder();

                addPropertyBuilder.propCode(attributes.getCode());
                addPropertyBuilder.value((String) prop.getValue());
                if (prop.getMeasure() != null) {
                    addPropertyBuilder.measureId((String) prop.getMeasure().getMeasureId());
                    addPropertyBuilder.measureName((String) prop.getMeasure().getMeasureName());
                }
                addPropertyBuilder.relation(prop.getRelation().toString());
                addPropertyBuilder.dataTypePhysical((String) prop.getDataTypePhysical());

                additionalProperties.add(addPropertyBuilder.build());

            } else {
                log.warn("addProperty с кодом [{}], \"[{}]\" не найдена в справочнике, пропуск",
                        prop.getAttrId(), prop.getAttrName());
            }
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
                case HOT_ROLLED_STEEL_NUMBER: {
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
                case THICKNESS_PRODUCTS: {
                    builder.thickness(new BigDecimal(value));
                    break;
                }
                case WIDTH_PRODUCT: {
                    builder.width(new BigDecimal(value));
                    break;
                }
                case UNIT_WEIGHT: {
                    builder.weightNet(new BigDecimal(value));
                    break;
                }
                case BUNDLE_MASS: {
                    builder.bundleWeight(new BigDecimal(value));
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

}
