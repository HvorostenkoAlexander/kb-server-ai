package com.nlmk.kb.server.testing;

import com.nlmk.attestation.product.api.specification.SpecCode;
import com.nlmk.attestation.product.api.specification.TypeCode;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;

@Disabled("hand sender")
class SendPdmMessageTest extends SendMessageToKafka {

    // одна комбинация: тема + схема (версия схемы привязана к теме!)
    private static final String PDM_TOPIC_ASAP_MECH_PROP_DT = "000-0.l3-pdm.cdc.sp-asap-mech-properties-dt.0";
    private static final String PDM_TOPIC_PHYS_MECH_PROP_ANIS_STEEL = "000-0.l3-pdm.cdc.sp-phys-mech-prop-anis-steel-stand.1";
    private static final String PDM_TOPIC_SP_TOL_EVENNESS_DT = "000-0.l3-pdm.cdc.sp-tol-evenness-dt.0";
    private static final String PDM_TOPIC_SP_TOL_THICK_DT = "000-0.l3-pdm.cdc.sp-tol-thick-dt.0";
    private static final String PDM_TOPIC_SP_TOL_WIDTH_DT = "000-0.l3-pdm.cdc.sp-tol-width-dt.0";

    @Test
    void sendPdmSpAsapMechPropertiesDt() {
        nlmk.l3.pdm.SpAsapMechPropertiesDt value = nlmk.l3.pdm.SpAsapMechPropertiesDt.newBuilder()
                .setTs("2022-07-25T15:25:25.123+05:00")
                .setOp(nlmk.l3.pdm.opEnum.I) // I -> U -> D
                .setPk(nlmk.l3.pdm.Pk.newBuilder()
                        .setId("42")
                        .setSystemCode("16")
                        .setDirectoryId("4242")
                        .build())
                .setData(nlmk.l3.pdm.Data.newBuilder()
                        .setSpecifications(List.of(
                                nlmk.l3.pdm.Spec.newBuilder()
                                        .setSpecCode(SpecCode.STEEL_MARK.getValue())
                                        .setSpecName(SpecCode.STEEL_MARK.getDesc())
                                        .setSpecTypeCode(TypeCode.STRING.getValue()).setSpecMeasure("x")
                                        .setSpecValue("NV23S-95L").build(),
                                nlmk.l3.pdm.Spec.newBuilder()
                                        .setSpecCode(SpecCode.ADDITIONAL_REQUIREMENTS.getValue())
                                        .setSpecName(SpecCode.ADDITIONAL_REQUIREMENTS.getDesc())
                                        .setSpecTypeCode(TypeCode.STRING.getValue()).setSpecMeasure("x")
                                        .setSpecValue("ДТ 0043").build(),
                                nlmk.l3.pdm.Spec.newBuilder()
                                        .setSpecCode(SpecCode.PRODUCT_STANDARD.getValue())
                                        .setSpecName(SpecCode.PRODUCT_STANDARD.getDesc())
                                        .setSpecTypeCode(TypeCode.STRING.getValue()).setSpecMeasure("x")
                                        .setSpecValue("СТО 05757665-008-2019").build(),
                                nlmk.l3.pdm.Spec.newBuilder()
                                        .setSpecCode(SpecCode.P1750SST.getValue())
                                        .setSpecName(SpecCode.P1750SST.getDesc())
                                        .setSpecTypeCode(TypeCode.STRING.getValue()).setSpecMeasure("x")
                                        .setSpecValue("*..0.95").build()
                        ))
                        .build())
                .build();

        ProducerRecord<Object, Object> record = new ProducerRecord<>(PDM_TOPIC_ASAP_MECH_PROP_DT, randomKey(), value);

        Assertions.assertDoesNotThrow(() -> sendAvro(record));
    }

    @Test
    void sendPdmSpPhysMechPropAnisSteelStand() {
        nlmk.l3.pdm.SpPhysMechPropAnisSteelStand value = nlmk.l3.pdm.SpPhysMechPropAnisSteelStand.newBuilder()
                .setTs("2022-07-25T15:35:35.321+05:00")
                .setOp(nlmk.l3.pdm.opEnum.I) // I -> U -> D
                .setPk(nlmk.l3.pdm.Pk.newBuilder()
                        .setId("44")
                        .setSystemCode("16")
                        .setDirectoryId("4444")
                        .build())
                .setData(nlmk.l3.pdm.Data.newBuilder()
                        .setSpecifications(List.of(
                                nlmk.l3.pdm.Spec.newBuilder()
                                        .setSpecCode(SpecCode.STEEL_MARK.getValue())
                                        .setSpecName(SpecCode.STEEL_MARK.getDesc())
                                        .setSpecTypeCode(TypeCode.STRING.getValue()).setSpecMeasure("x")
                                        .setSpecValue("Ст3сп").build(),
                                nlmk.l3.pdm.Spec.newBuilder()
                                        .setSpecCode(SpecCode.PRODUCT_STANDARD.getValue())
                                        .setSpecName(SpecCode.PRODUCT_STANDARD.getDesc())
                                        .setSpecTypeCode(TypeCode.STRING.getValue()).setSpecMeasure("x")
                                        .setSpecValue("ГОСТ 21427.4-78").build(),
                                nlmk.l3.pdm.Spec.newBuilder()
                                        .setSpecCode(SpecCode.THICKNESS_PRODUCTS.getValue())
                                        .setSpecName(SpecCode.THICKNESS_PRODUCTS.getDesc())
                                        .setSpecTypeCode(TypeCode.NUMBER.getValue()).setSpecMeasure("x")
                                        .setSpecValue("2.65").build(),
                                nlmk.l3.pdm.Spec.newBuilder()
                                        .setSpecCode(SpecCode.P15400.getValue())
                                        .setSpecName(SpecCode.P15400.getDesc())
                                        .setSpecTypeCode(TypeCode.STRING.getValue()).setSpecMeasure("x")
                                        .setSpecValue("*..23.0").build(),
                                nlmk.l3.pdm.Spec.newBuilder()
                                        .setSpecCode(SpecCode.PLASTICITY_NUMBER_OF_BENDS.getValue())
                                        .setSpecName(SpecCode.PLASTICITY_NUMBER_OF_BENDS.getDesc())
                                        .setSpecTypeCode(TypeCode.STRING.getValue()).setSpecMeasure("x")
                                        .setSpecValue("2..*").build()
                        ))
                        .build())
                .build();

        ProducerRecord<Object, Object> record = new ProducerRecord<>(PDM_TOPIC_PHYS_MECH_PROP_ANIS_STEEL, randomKey(), value);

        Assertions.assertDoesNotThrow(() -> sendAvro(record));
    }

    @Test
    void sendPdmSpTolEvennessDt() {
        nlmk.l3.pdm.SpTolEvennessDt value = nlmk.l3.pdm.SpTolEvennessDt.newBuilder()
                .setTs("2022-07-25T15:35:35.321+05:00")
                .setOp(nlmk.l3.pdm.opEnum.I) // I -> U -> D
                .setPk(nlmk.l3.pdm.Pk.newBuilder()
                        .setId("41")
                        .setSystemCode("14")
                        .setDirectoryId("4141")
                        .build())
                .setData(nlmk.l3.pdm.Data.newBuilder()
                        .setSpecifications(List.of(
                                nlmk.l3.pdm.Spec.newBuilder()
                                        .setSpecCode(SpecCode.PRODUCT_STANDARD.getValue())
                                        .setSpecName(SpecCode.PRODUCT_STANDARD.getDesc())
                                        .setSpecTypeCode(TypeCode.STRING.getValue()).setSpecMeasure("x")
                                        .setSpecValue("IS 3024:2015").build(),
                                nlmk.l3.pdm.Spec.newBuilder()
                                        .setSpecCode(SpecCode.ADDITIONAL_REQUIREMENTS.getValue())
                                        .setSpecName(SpecCode.ADDITIONAL_REQUIREMENTS.getDesc())
                                        .setSpecTypeCode(TypeCode.STRING.getValue()).setSpecMeasure("x")
                                        .setSpecValue("ДТ 37.06").build(),
                                nlmk.l3.pdm.Spec.newBuilder()
                                        .setSpecCode(SpecCode.THICKNESS_OF_ROLLED_PRODUCTS.getValue())
                                        .setSpecName(SpecCode.THICKNESS_OF_ROLLED_PRODUCTS.getDesc())
                                        .setSpecTypeCode(TypeCode.STRING.getValue()).setSpecMeasure("x")
                                        .setSpecValue("7.06").build(),
                                nlmk.l3.pdm.Spec.newBuilder()
                                        .setSpecCode(SpecCode.WHIDTH_PRODUCT.getValue())
                                        .setSpecName(SpecCode.WHIDTH_PRODUCT.getDesc())
                                        .setSpecTypeCode(TypeCode.STRING.getValue()).setSpecMeasure("x")
                                        .setSpecValue("(150..*").build(),
                                nlmk.l3.pdm.Spec.newBuilder()
                                        .setSpecCode(SpecCode.EVENNESS_TOLERANCE.getValue())
                                        .setSpecName(SpecCode.EVENNESS_TOLERANCE.getDesc())
                                        .setSpecTypeCode(TypeCode.STRING.getValue()).setSpecMeasure("x")
                                        .setSpecValue("3.0").build(),
                                nlmk.l3.pdm.Spec.newBuilder()
                                        .setSpecCode(SpecCode.EVENNESS_TOLERANCE_PERCENT.getValue())
                                        .setSpecName(SpecCode.EVENNESS_TOLERANCE_PERCENT.getDesc())
                                        .setSpecTypeCode(TypeCode.NUMBER.getValue()).setSpecMeasure("x")
                                        .setSpecValue("1.5").build(),
                                nlmk.l3.pdm.Spec.newBuilder()
                                        .setSpecCode(SpecCode.NOTE.getValue())
                                        .setSpecName(SpecCode.NOTE.getDesc())
                                        .setSpecTypeCode(TypeCode.STRING.getValue()).setSpecMeasure("x")
                                        .setSpecValue("TEST").build()
                        ))
                        .build())
                .build();

        ProducerRecord<Object, Object> record = new ProducerRecord<>(PDM_TOPIC_SP_TOL_EVENNESS_DT, randomKey(), value);

        Assertions.assertDoesNotThrow(() -> sendAvro(record));
    }

    @Test
    void sendPdmSpTolThickDt() {
        nlmk.l3.pdm.SpTolThickDt value = nlmk.l3.pdm.SpTolThickDt.newBuilder()
                .setTs("2022-07-25T15:35:35.321+05:00")
                .setOp(nlmk.l3.pdm.opEnum.I) // I -> U -> D
                .setPk(nlmk.l3.pdm.Pk.newBuilder()
                        .setId("41")
                        .setSystemCode("14")
                        .setDirectoryId("4141")
                        .build())
                .setData(nlmk.l3.pdm.Data.newBuilder()
                        .setSpecifications(List.of(
                                nlmk.l3.pdm.Spec.newBuilder()
                                        .setSpecCode(SpecCode.ADDITIONAL_REQUIREMENTS.getValue())
                                        .setSpecName(SpecCode.ADDITIONAL_REQUIREMENTS.getDesc())
                                        .setSpecTypeCode(TypeCode.STRING.getValue()).setSpecMeasure("x")
                                        .setSpecValue("ДТ 157.00").build(),
                                nlmk.l3.pdm.Spec.newBuilder()
                                        .setSpecCode(SpecCode.THICKNESS_PRODUCTS.getValue())
                                        .setSpecName(SpecCode.THICKNESS_PRODUCTS.getDesc())
                                        .setSpecTypeCode(TypeCode.NUMBER.getValue()).setSpecMeasure("x")
                                        .setSpecValue("2.65").build(),
                                nlmk.l3.pdm.Spec.newBuilder()
                                        .setSpecCode(SpecCode.WHIDTH_PRODUCT.getValue())
                                        .setSpecName(SpecCode.WHIDTH_PRODUCT.getDesc())
                                        .setSpecTypeCode(TypeCode.STRING.getValue()).setSpecMeasure("x")
                                        .setSpecValue("(1000..1020]").build(),
                                nlmk.l3.pdm.Spec.newBuilder()
                                        .setSpecCode(SpecCode.MANUFACTURING_PRECISION_BY_THICKNESS.getValue())
                                        .setSpecName(SpecCode.MANUFACTURING_PRECISION_BY_THICKNESS.getDesc())
                                        .setSpecTypeCode(TypeCode.STRING.getValue()).setSpecMeasure("x")
                                        .setSpecValue("1").build(),
                                nlmk.l3.pdm.Spec.newBuilder()
                                        .setSpecCode(SpecCode.THICKNESS_TOLERANCE_MIN.getValue())
                                        .setSpecName(SpecCode.THICKNESS_TOLERANCE_MIN.getDesc())
                                        .setSpecTypeCode(TypeCode.STRING.getValue()).setSpecMeasure("x")
                                        .setSpecValue("1.1").build(),
                                nlmk.l3.pdm.Spec.newBuilder()
                                        .setSpecCode(SpecCode.THICKNESS_TOLERANCE_MAX.getValue())
                                        .setSpecName(SpecCode.THICKNESS_TOLERANCE_MAX.getDesc())
                                        .setSpecTypeCode(TypeCode.STRING.getValue()).setSpecMeasure("x")
                                        .setSpecValue("1.1").build(),
                                nlmk.l3.pdm.Spec.newBuilder()
                                        .setSpecCode(SpecCode.LONG_THICK_DIFF.getValue())
                                        .setSpecName(SpecCode.LONG_THICK_DIFF.getDesc())
                                        .setSpecTypeCode(TypeCode.STRING.getValue()).setSpecMeasure("x")
                                        .setSpecValue("*..0.016").build(),
                                nlmk.l3.pdm.Spec.newBuilder()
                                        .setSpecCode(SpecCode.UNEVEN_GAUGE.getValue())
                                        .setSpecName(SpecCode.UNEVEN_GAUGE.getDesc())
                                        .setSpecTypeCode(TypeCode.STRING.getValue()).setSpecMeasure("x")
                                        .setSpecValue("*..0.015").build(),
                                nlmk.l3.pdm.Spec.newBuilder()
                                        .setSpecCode(SpecCode.NOTE.getValue())
                                        .setSpecName(SpecCode.NOTE.getDesc())
                                        .setSpecTypeCode(TypeCode.STRING.getValue()).setSpecMeasure("x")
                                        .setSpecValue("TEST").build()
                        ))
                        .build())
                .build();

        ProducerRecord<Object, Object> record = new ProducerRecord<>(PDM_TOPIC_SP_TOL_THICK_DT, randomKey(), value);

        Assertions.assertDoesNotThrow(() -> sendAvro(record));
    }

    @Test
    void sendPdmSpTolWidthDt() {
        nlmk.l3.pdm.SpTolWidthDt value = nlmk.l3.pdm.SpTolWidthDt.newBuilder()
                .setTs("2022-07-25T15:35:35.321+05:00")
                .setOp(nlmk.l3.pdm.opEnum.I) // I -> U -> D
                .setPk(nlmk.l3.pdm.Pk.newBuilder()
                        .setId("41")
                        .setSystemCode("14")
                        .setDirectoryId("4141")
                        .build())
                .setData(nlmk.l3.pdm.Data.newBuilder()
                        .setSpecifications(List.of(
                                nlmk.l3.pdm.Spec.newBuilder()
                                        .setSpecCode(SpecCode.PRODUCT_STANDARD.getValue())
                                        .setSpecName(SpecCode.PRODUCT_STANDARD.getDesc())
                                        .setSpecTypeCode(TypeCode.STRING.getValue()).setSpecMeasure("x")
                                        .setSpecValue("IS 3024:2015").build(),
                                nlmk.l3.pdm.Spec.newBuilder()
                                        .setSpecCode(SpecCode.STEEL_MARK.getValue())
                                        .setSpecName(SpecCode.STEEL_MARK.getDesc())
                                        .setSpecTypeCode(TypeCode.STRING.getValue()).setSpecMeasure("x")
                                        .setSpecValue("Ст3сп").build(),
                                nlmk.l3.pdm.Spec.newBuilder()
                                        .setSpecCode(SpecCode.ADDITIONAL_REQUIREMENTS.getValue())
                                        .setSpecName(SpecCode.ADDITIONAL_REQUIREMENTS.getDesc())
                                        .setSpecTypeCode(TypeCode.STRING.getValue()).setSpecMeasure("x")
                                        .setSpecValue("ДТ 157.00").build(),
                                nlmk.l3.pdm.Spec.newBuilder()
                                        .setSpecCode(SpecCode.WHIDTH_PRODUCT.getValue())
                                        .setSpecName(SpecCode.WHIDTH_PRODUCT.getDesc())
                                        .setSpecTypeCode(TypeCode.STRING.getValue()).setSpecMeasure("x")
                                        .setSpecValue("(1000..1020]").build(),
                                nlmk.l3.pdm.Spec.newBuilder()
                                        .setSpecCode(SpecCode.THICKNESS_PRODUCTS.getValue())
                                        .setSpecName(SpecCode.THICKNESS_PRODUCTS.getDesc())
                                        .setSpecTypeCode(TypeCode.STRING.getValue()).setSpecMeasure("x")
                                        .setSpecValue("2.65").build(),
                                nlmk.l3.pdm.Spec.newBuilder()
                                        .setSpecCode(SpecCode.FORM_SAP.getValue())
                                        .setSpecName(SpecCode.FORM_SAP.getDesc())
                                        .setSpecTypeCode(TypeCode.STRING.getValue()).setSpecMeasure("x")
                                        .setSpecValue("2").build(),
                                nlmk.l3.pdm.Spec.newBuilder()
                                        .setSpecCode(SpecCode.MANUFACTURING_PRECISION_BY_WIDTH.getValue())
                                        .setSpecName(SpecCode.MANUFACTURING_PRECISION_BY_WIDTH.getDesc())
                                        .setSpecTypeCode(TypeCode.STRING.getValue()).setSpecMeasure("x")
                                        .setSpecValue("3").build(),
                                nlmk.l3.pdm.Spec.newBuilder()
                                        .setSpecCode(SpecCode.LENGTH_PRODUCT.getValue())
                                        .setSpecName(SpecCode.LENGTH_PRODUCT.getDesc())
                                        .setSpecTypeCode(TypeCode.NUMBER.getValue()).setSpecMeasure("x")
                                        .setSpecValue("100.").build(),
                                nlmk.l3.pdm.Spec.newBuilder()
                                        .setSpecCode(SpecCode.EDGE_CHARACTER.getValue())
                                        .setSpecName(SpecCode.EDGE_CHARACTER.getDesc())
                                        .setSpecTypeCode(TypeCode.STRING.getValue()).setSpecMeasure("x")
                                        .setSpecValue("1").build(),
                                nlmk.l3.pdm.Spec.newBuilder()
                                        .setSpecCode(SpecCode.WIDTH_TOLERANCE_MIN.getValue())
                                        .setSpecName(SpecCode.WIDTH_TOLERANCE_MIN.getDesc())
                                        .setSpecTypeCode(TypeCode.NUMBER.getValue()).setSpecMeasure("x")
                                        .setSpecValue("3.5").build(),
                                nlmk.l3.pdm.Spec.newBuilder()
                                        .setSpecCode(SpecCode.WIDTH_TOLERANCE_MAX.getValue())
                                        .setSpecName(SpecCode.WIDTH_TOLERANCE_MAX.getDesc())
                                        .setSpecTypeCode(TypeCode.NUMBER.getValue()).setSpecMeasure("x")
                                        .setSpecValue("3.6").build(),
                                nlmk.l3.pdm.Spec.newBuilder()
                                        .setSpecCode(SpecCode.WHIDTH_TOLERANCE_PERCENT.getValue())
                                        .setSpecName(SpecCode.WHIDTH_TOLERANCE_PERCENT.getDesc())
                                        .setSpecTypeCode(TypeCode.NUMBER.getValue()).setSpecMeasure("x")
                                        .setSpecValue("6").build(),
                                nlmk.l3.pdm.Spec.newBuilder()
                                        .setSpecCode(SpecCode.SICKLE_SHAPE.getValue())
                                        .setSpecName(SpecCode.SICKLE_SHAPE.getDesc())
                                        .setSpecTypeCode(TypeCode.STRING.getValue()).setSpecMeasure("x")
                                        .setSpecValue("*..0.9").build(),
                                nlmk.l3.pdm.Spec.newBuilder()
                                        .setSpecCode(SpecCode.BURR.getValue())
                                        .setSpecName(SpecCode.BURR.getDesc())
                                        .setSpecTypeCode(TypeCode.STRING.getValue()).setSpecMeasure("x")
                                        .setSpecValue("*..0.025").build(),
                                nlmk.l3.pdm.Spec.newBuilder()
                                        .setSpecCode(SpecCode.NOTE.getValue())
                                        .setSpecName(SpecCode.NOTE.getDesc())
                                        .setSpecTypeCode(TypeCode.STRING.getValue()).setSpecMeasure("x")
                                        .setSpecValue("TEST").build()
                        ))
                        .build())
                .build();

        ProducerRecord<Object, Object> record = new ProducerRecord<>(PDM_TOPIC_SP_TOL_WIDTH_DT, randomKey(), value);

        Assertions.assertDoesNotThrow(() -> sendAvro(record));
    }

}
