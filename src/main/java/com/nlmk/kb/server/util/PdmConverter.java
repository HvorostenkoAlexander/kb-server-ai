package com.nlmk.kb.server.util;

import com.nlmk.kb.server.entity.PdmMessage;
import com.nlmk.kb.server.entity.pdm.PdmDictionary;
import com.nlmk.kb.server.entity.pdm.Pk;
import com.nlmk.kb.server.entity.pdm.Data;
import com.nlmk.kb.server.entity.pdm.Spec;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import nlmk.l3.pdm.SpAsapChemicalProperties;
import nlmk.l3.pdm.SpEquivalents;
import nlmk.l3.pdm.SpMicrostructure;
import org.apache.kafka.clients.consumer.ConsumerRecord;

@Slf4j
public class PdmConverter {
    private PdmConverter() {
        throw new RuntimeException("PdmConverter is utility class, only for create PdmDictionary objects.");
    }

    public static PdmMessage fromConsumerRecord(ConsumerRecord record){
        val topic = record.topic();
        PdmMessage message = new PdmMessage();
        message.setTopic(record.topic());
        message.setKey((String) record.key());//todo !!!
        message.setOffset(record.offset());
        message.setPartition(record.partition());

        switch (topic){
            case "000-1.l3-pdm.cdc.sp-microstructure.0":{// todo убрать хардкод

                val pdmDictionary = fromSpMicrostructure((SpMicrostructure) record.value());
                message.setOp(pdmDictionary.getOp());
                message.setTs(pdmDictionary.getTs());
                message.setDictionary(pdmDictionary);
                break;
            }
            case "000-1.l3-pdm.cdc.sp-asap-chemical-properties.0":{
                val pdmDictionary = fromSpAsapChemicalProperties((SpAsapChemicalProperties) record.value());
                message.setOp(pdmDictionary.getOp());
                message.setTs(pdmDictionary.getTs());
                message.setDictionary(pdmDictionary);
                break;
            }
            case "000-1.l3-pdm.cdc.sp-equivalents.0":{
                val pdmDictionary = fromSpEquivalents((SpEquivalents) record.value());
                message.setOp(pdmDictionary.getOp());
                message.setTs(pdmDictionary.getTs());
                message.setDictionary(pdmDictionary);
                break;
            }
            default:{
                log.error("Not supported type of: {}",record);
                throw new IllegalArgumentException("Not supported type of: "+record);
            }
        }
        return message;
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

    public static PdmDictionary fromSpAsapChemicalProperties(SpAsapChemicalProperties spChemicalProperties){
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

    public static PdmDictionary fromSpEquivalents(SpEquivalents equivalents){
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
