package com.nlmk.kb.server.util;

import com.nlmk.kb.server.entity.pdm.PdmMessage;
import com.nlmk.kb.server.entity.pdm.PdmDictionary;
import com.nlmk.kb.server.entity.pdm.Pk;
import com.nlmk.kb.server.entity.pdm.Data;
import com.nlmk.kb.server.entity.pdm.Spec;
import com.nlmk.kb.server.entity.pdm.SpecDto;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import nlmk.l3.pdm.SpAsapChemicalProperties;
import nlmk.l3.pdm.SpAsapMechProperties;
import nlmk.l3.pdm.SpAsapTolLinks;
import nlmk.l3.pdm.SpCeq;
import nlmk.l3.pdm.SpChemicalProperties;
import nlmk.l3.pdm.SpEquivalents;
import nlmk.l3.pdm.SpKatSteelMarkGost4041;
import nlmk.l3.pdm.SpMatchRabplanNum;
import nlmk.l3.pdm.SpMatchTkNum;
import nlmk.l3.pdm.SpMechProperties;
import nlmk.l3.pdm.SpMicrostructure;
import nlmk.l3.pdm.SpPcm;
import nlmk.l3.pdm.SpTkNum;
import nlmk.l3.pdm.SpTolEvenness;
import nlmk.l3.pdm.SpTolLength;
import nlmk.l3.pdm.SpTolThick;
import nlmk.l3.pdm.SpTolWidth;
import org.apache.kafka.clients.consumer.ConsumerRecord;


@Deprecated
//готово: создать отдельный сервис, отказаться от хардкода, применить шаблон проектирвания для ухода от повторяющегося кода
@Slf4j
public class PdmConverter {
    private PdmConverter() {
        throw new RuntimeException("PdmConverter is utility class, only for create PdmDictionary objects.");
    }

    public static PdmMessage fromConsumerRecord(ConsumerRecord record) {
        val topic = record.topic();
        PdmMessage message = new PdmMessage();
        message.setTopic(record.topic());
        message.setKey((String) record.key());
        message.setOffset(record.offset());
        message.setPartition(record.partition());

        // избавиться от лишнего кода!!!, убрать хардкод
        switch (topic) {
            case "000-1.l3-pdm.cdc.sp-microstructure.0": {
                val pdmDictionary = fromSpMicrostructure((SpMicrostructure) record.value());
                message.setOp(pdmDictionary.getOp());
                message.setTs(pdmDictionary.getTs());
                message.setDictionary(pdmDictionary);
                break;
            }
            case "000-1.l3-pdm.cdc.sp-asap-chemical-properties.0": {
                val pdmDictionary = fromSpAsapChemicalProperties((SpAsapChemicalProperties) record.value());
                message.setOp(pdmDictionary.getOp());
                message.setTs(pdmDictionary.getTs());
                message.setDictionary(pdmDictionary);
                break;
            }
            case "000-1.l3-pdm.cdc.sp-equivalents.0": {
                val pdmDictionary = fromSpEquivalents((SpEquivalents) record.value());
                message.setOp(pdmDictionary.getOp());
                message.setTs(pdmDictionary.getTs());
                message.setDictionary(pdmDictionary);
                break;
            }
            case "000-1.l3-pdm.cdc.sp-match-tk-num.0": {
                val pdmDictionary = fromSpMatchTkNum((SpMatchTkNum) record.value());
                message.setOp(pdmDictionary.getOp());
                message.setTs(pdmDictionary.getTs());
                message.setDictionary(pdmDictionary);
                break;
            }
            case "000-1.l3-pdm.cdc.sp-match-rabplan-num.0": {
                val pdmDictionary = fromSpMatchRabplanNum((SpMatchRabplanNum) record.value());
                message.setOp(pdmDictionary.getOp());
                message.setTs(pdmDictionary.getTs());
                message.setDictionary(pdmDictionary);
                break;
            }
            case "000-1.l3-pdm.cdc.sp-pcm.0": {
                val pdmDictionary = fromSpPcm((SpPcm) record.value());
                message.setOp(pdmDictionary.getOp());
                message.setTs(pdmDictionary.getTs());
                message.setDictionary(pdmDictionary);
                break;
            }
            case "000-1.l3-pdm.cdc.sp-asap-tol-links.0": {
                val pdmDictionary = fromSpAsapTolLinks((SpAsapTolLinks) record.value());
                message.setOp(pdmDictionary.getOp());
                message.setTs(pdmDictionary.getTs());
                message.setDictionary(pdmDictionary);
                break;
            }
            case "000-1.l3-pdm.cdc.sp-kat-steel-mark-gost4041.0": {
                val pdmDictionary = fromKatSteel4041((SpKatSteelMarkGost4041) record.value());
                message.setOp(pdmDictionary.getOp());
                message.setTs(pdmDictionary.getTs());
                message.setDictionary(pdmDictionary);
                break;
            }
            case "000-1.l3-pdm.cdc.sp-tol-thick.0": {
                val pdmDictionary = fromSpTolThick((SpTolThick) record.value());
                message.setOp(pdmDictionary.getOp());
                message.setTs(pdmDictionary.getTs());
                message.setDictionary(pdmDictionary);
                break;
            }
            case "000-1.l3-pdm.cdc.sp-tol-width.0": {
                val pdmDictionary = fromSpTolWidth((SpTolWidth) record.value());
                message.setOp(pdmDictionary.getOp());
                message.setTs(pdmDictionary.getTs());
                message.setDictionary(pdmDictionary);
                break;
            }
            case "000-1.l3-pdm.cdc.sp-tol-length.0": {
                val pdmDictionary = fromSpTolLength((SpTolLength) record.value());
                message.setOp(pdmDictionary.getOp());
                message.setTs(pdmDictionary.getTs());
                message.setDictionary(pdmDictionary);
                break;
            }
            case "000-1.l3-pdm.cdc.asap-mech-properties.0": {
                val pdmDictionary = fromSpAsapMechProperties((SpAsapMechProperties) record.value());
                message.setOp(pdmDictionary.getOp());
                message.setTs(pdmDictionary.getTs());
                message.setDictionary(pdmDictionary);
                break;
            }
            case "000-1.l3-pdm.cdc.sp-tol-evenness.0": {
                val pdmDictionary = fromSpTolEvenness((SpTolEvenness) record.value());
                message.setOp(pdmDictionary.getOp());
                message.setTs(pdmDictionary.getTs());
                message.setDictionary(pdmDictionary);
                break;
            }
            case "000-1.l3-pdm.cdc.sp-tk-num.0": {
                val pdmDictionary = fromSpTkNum((SpTkNum) record.value());
                message.setOp(pdmDictionary.getOp());
                message.setTs(pdmDictionary.getTs());
                message.setDictionary(pdmDictionary);
                break;
            }
            case "000-1.l3-pdm.cdc.sp-ceq.0": {
                val pdmDictionary = fromSpCeq((SpCeq) record.value());
                message.setOp(pdmDictionary.getOp());
                message.setTs(pdmDictionary.getTs());
                message.setDictionary(pdmDictionary);
                break;
            }
            case "000-1.l3-pdm.cdc.sp-mech-properties.0": {
                val pdmDictionary = fromSpMechProperties((SpMechProperties) record.value());
                message.setOp(pdmDictionary.getOp());
                message.setTs(pdmDictionary.getTs());
                message.setDictionary(pdmDictionary);
                break;
            }
            case "000-1.l3-pdm.cdc.sp-chemical-properties.0": {
                val pdmDictionary = fromSpChemicalProperties((SpChemicalProperties) record.value());
                message.setOp(pdmDictionary.getOp());
                message.setTs(pdmDictionary.getTs());
                message.setDictionary(pdmDictionary);
                break;
            }
            default: {
                log.error("Not supported type of: {}", record);
                throw new IllegalArgumentException("Not supported type of: " + record);
            }
        }
        return message;
    }

    private static PdmDictionary fromSpChemicalProperties(SpChemicalProperties value) {
        val pdmDictionaryBuilder = PdmDictionary.builder()
                .op(value.getOp().name())
                .pk(
                        fromPk(value.getPk())
                )
                .data(
                        fromData(value.getData())
                );

        if (value.getTs() != null) {
            pdmDictionaryBuilder.ts(value.getTs().toString());
        }
        return pdmDictionaryBuilder.build();
    }

    private static PdmDictionary fromSpMechProperties(SpMechProperties value) {
        val pdmDictionaryBuilder = PdmDictionary.builder()
                .op(value.getOp().name())
                .pk(
                        fromPk(value.getPk())
                )
                .data(
                        fromData(value.getData())
                );

        if (value.getTs() != null) {
            pdmDictionaryBuilder.ts(value.getTs().toString());
        }
        return pdmDictionaryBuilder.build();
    }

    private static PdmDictionary fromSpCeq(SpCeq value) {
        val pdmDictionaryBuilder = PdmDictionary.builder()
                .op(value.getOp().name())
                .pk(
                        fromPk(value.getPk())
                )
                .data(
                        fromData(value.getData())
                );

        if (value.getTs() != null) {
            pdmDictionaryBuilder.ts(value.getTs().toString());
        }
        return pdmDictionaryBuilder.build();
    }

    private static PdmDictionary fromSpTkNum(SpTkNum value) {
        val pdmDictionaryBuilder = PdmDictionary.builder()
                .op(value.getOp().name())
                .pk(
                        fromPk(value.getPk())
                )
                .data(
                        fromData(value.getData())
                );

        if (value.getTs() != null) {
            pdmDictionaryBuilder.ts(value.getTs().toString());
        }
        return pdmDictionaryBuilder.build();
    }

    private static PdmDictionary fromSpTolEvenness(SpTolEvenness value) {
        val pdmDictionaryBuilder = PdmDictionary.builder()
                .op(value.getOp().name())
                .pk(
                        fromPk(value.getPk())
                )
                .data(
                        fromData(value.getData())
                );

        if (value.getTs() != null) {
            pdmDictionaryBuilder.ts(value.getTs().toString());
        }
        return pdmDictionaryBuilder.build();
    }

    private static PdmDictionary fromSpAsapMechProperties(SpAsapMechProperties value) {
        val pdmDictionaryBuilder = PdmDictionary.builder()
                .op(value.getOp().name())
                .pk(
                        fromPk(value.getPk())
                )
                .data(
                        fromData(value.getData())
                );

        if (value.getTs() != null) {
            pdmDictionaryBuilder.ts(value.getTs().toString());
        }
        return pdmDictionaryBuilder.build();
    }

    private static PdmDictionary fromSpTolLength(SpTolLength value) {
        val pdmDictionaryBuilder = PdmDictionary.builder()
                .op(value.getOp().name())
                .pk(
                        fromPk(value.getPk())
                )
                .data(
                        fromData(value.getData())
                );

        if (value.getTs() != null) {
            pdmDictionaryBuilder.ts(value.getTs().toString());
        }
        return pdmDictionaryBuilder.build();
    }

    private static PdmDictionary fromSpTolWidth(SpTolWidth value) {
        val pdmDictionaryBuilder = PdmDictionary.builder()
                .op(value.getOp().name())
                .pk(
                        fromPk(value.getPk())
                )
                .data(
                        fromData(value.getData())
                );

        if (value.getTs() != null) {
            pdmDictionaryBuilder.ts(value.getTs().toString());
        }
        return pdmDictionaryBuilder.build();
    }

    private static PdmDictionary fromSpTolThick(SpTolThick value) {
        val pdmDictionaryBuilder = PdmDictionary.builder()
                .op(value.getOp().name())
                .pk(
                        fromPk(value.getPk())
                )
                .data(
                        fromData(value.getData())
                );

        if (value.getTs() != null) {
            pdmDictionaryBuilder.ts(value.getTs().toString());
        }
        return pdmDictionaryBuilder.build();
    }

    private static PdmDictionary fromKatSteel4041(SpKatSteelMarkGost4041 value) {
        val pdmDictionaryBuilder = PdmDictionary.builder()
                .op(value.getOp().name())
                .pk(
                        fromPk(value.getPk())
                )
                .data(
                        fromData(value.getData())
                );

        if (value.getTs() != null) {
            pdmDictionaryBuilder.ts(value.getTs().toString());
        }
        return pdmDictionaryBuilder.build();
    }

    private static PdmDictionary fromSpAsapTolLinks(SpAsapTolLinks value) {
        val pdmDictionaryBuilder = PdmDictionary.builder()
                .op(value.getOp().name())
                .pk(
                        fromPk(value.getPk())
                )
                .data(
                        fromData(value.getData())
                );

        if (value.getTs() != null) {
            pdmDictionaryBuilder.ts(value.getTs().toString());
        }
        return pdmDictionaryBuilder.build();
    }

    private static PdmDictionary fromSpPcm(SpPcm value) {
        val pdmDictionaryBuilder = PdmDictionary.builder()
                .op(value.getOp().name())
                .pk(
                        fromPk(value.getPk())
                )
                .data(
                        fromData(value.getData())
                );

        if (value.getTs() != null) {
            pdmDictionaryBuilder.ts(value.getTs().toString());
        }
        return pdmDictionaryBuilder.build();
    }

    private static PdmDictionary fromSpMatchRabplanNum(SpMatchRabplanNum value) {
        val pdmDictionaryBuilder = PdmDictionary.builder()
                .op(value.getOp().name())
                .pk(
                        fromPk(value.getPk())
                )
                .data(
                        fromData(value.getData())
                );

        if (value.getTs() != null) {
            pdmDictionaryBuilder.ts(value.getTs().toString());
        }
        return pdmDictionaryBuilder.build();
    }

    private static PdmDictionary fromSpMatchTkNum(SpMatchTkNum value) {
        val pdmDictionaryBuilder = PdmDictionary.builder()
                .op(value.getOp().name())
                .pk(
                        fromPk(value.getPk())
                )
                .data(
                        fromData(value.getData())
                );

        if (value.getTs() != null) {
            pdmDictionaryBuilder.ts(value.getTs().toString());
        }
        return pdmDictionaryBuilder.build();
    }

    public static PdmDictionary fromSpMicrostructure(SpMicrostructure micro) {
        val pdmDictionaryBuilder = PdmDictionary.builder()
                .op(micro.getOp().name())
                .pk(
                        fromPk(micro.getPk())
                )
                .data(
                        fromData(micro.getData())
                );

        if (micro.getTs() != null) {
            pdmDictionaryBuilder.ts(micro.getTs().toString());
        }
        return pdmDictionaryBuilder.build();
    }

    public static PdmDictionary fromSpAsapChemicalProperties(SpAsapChemicalProperties spChemicalProperties) {
        val pdmDictionaryBuilder = PdmDictionary.builder()
                .op(spChemicalProperties.getOp().name())
                .pk(
                        fromPk(spChemicalProperties.getPk())
                )
                .data(
                        fromData(spChemicalProperties.getData())
                );

        if (spChemicalProperties.getTs() != null) {
            pdmDictionaryBuilder.ts(spChemicalProperties.getTs().toString());
        }
        return pdmDictionaryBuilder.build();
    }

    public static PdmDictionary fromSpEquivalents(SpEquivalents equivalents) {
        val pdmDictionaryBuilder = PdmDictionary.builder()
                .op(equivalents.getOp().name())
                .pk(
                        fromPk(equivalents.getPk())
                )
                .data(
                        fromData(equivalents.getData())
                );

        if (equivalents.getTs() != null) {
            pdmDictionaryBuilder.ts(equivalents.getTs().toString());
        }
        return pdmDictionaryBuilder.build();
    }

    private static com.nlmk.kb.server.entity.pdm.Pk fromPk(nlmk.l3.pdm.Pk pdmPk) {

        val pkBuilder = Pk.builder();
        if (pdmPk.getId() != null) {
            pkBuilder.Id(pdmPk.getId().toString());
        }
        if (pdmPk.getSystemCode() != null) {
            pkBuilder.systemCode(pdmPk.getSystemCode().toString());
        }
        if (pdmPk.getDirectoryId() != null) {
            pkBuilder.directoryId(pdmPk.getDirectoryId().toString());
        }
        return pkBuilder.build();
    }

    private static SpecDto toSpecDto(com.nlmk.kb.server.entity.pdm.Spec spec) {
        val specDto = SpecDto.builder()
                .specCode(spec.getSpecCode())
                .specName(spec.getSpecName())
                .specTypeCode(spec.getSpecTypeCode())
                .build();
        if (spec.getSpecMeasure() != null) {
            specDto.setSpecMeasure(spec.getSpecMeasure());
        }
        if (spec.getSpecValue() != null) {
            specDto.setSpecValue(spec.getSpecValue());
        }
        return specDto;
    }

    private static Data fromData(nlmk.l3.pdm.Data pdmData) {
        Data data = new Data();
        pdmData.getSpecifications().forEach(
                s -> data.addSpec(
                        fromSpec(s)
                )
        );
        return data;
    }

    private static Spec fromSpec(nlmk.l3.pdm.Spec pdmSpec) {
        val specBuilder = Spec.builder()
                .specCode(pdmSpec.getSpecCode())
                .specTypeCode(pdmSpec.getSpecTypeCode());

        if (pdmSpec.getSpecName() != null) {
            specBuilder.specName(pdmSpec.getSpecName().toString());
        }

        if (pdmSpec.getSpecMeasure() != null) {
            specBuilder.specMeasure(pdmSpec.getSpecMeasure().toString());
        }

        if (pdmSpec.getSpecValue() != null) {
            specBuilder.specValue(pdmSpec.getSpecValue().toString());
        }
        return specBuilder.build();
    }
}
