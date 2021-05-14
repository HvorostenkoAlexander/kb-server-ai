package com.nlmk.kb.server.util;

import com.nlmk.attestation.product.api.nsi.LimitDto;
import com.nlmk.attestation.product.api.nsi.MicrostructureDto;
import com.nlmk.attestation.product.api.specification.SpecCode;
import com.nlmk.kb.server.entity.pdm.PdmMessage;
import com.nlmk.kb.server.entity.pdm.PdmDictionary;
import com.nlmk.kb.server.entity.pdm.PdmMessageDto;
import com.nlmk.kb.server.entity.pdm.Pk;
import com.nlmk.kb.server.entity.pdm.Data;
import com.nlmk.kb.server.entity.pdm.Spec;
import com.nlmk.kb.server.entity.pdm.SpecDto;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import nlmk.l3.pdm.SpAsapChemicalProperties;
import nlmk.l3.pdm.SpEquivalents;
import nlmk.l3.pdm.SpMicrostructure;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Slf4j
public class PdmConverter {
    private PdmConverter() {
        throw new RuntimeException("PdmConverter is utility class, only for create PdmDictionary objects.");
    }

    public static PdmMessageDto toPdmMessageDto(PdmMessage pdmMessage) {
        val pdmMessageDto = PdmMessageDto.builder()
                .id(pdmMessage.getId().longValue())
                .topic(pdmMessage.getTopic())
                .partition(pdmMessage.getPartition().intValue())
                .offset(pdmMessage.getOffset().longValue())
                .key(pdmMessage.getKey())
                .ts(pdmMessage.getTs())
                .op(pdmMessage.getOp())
                .build();

        if (pdmMessage.getDictionary() != null) {
            pdmMessageDto.setPk_Id(pdmMessage.getDictionary().getPk().getId());
            pdmMessageDto.setPk_systemCode(pdmMessage.getDictionary().getPk().getSystemCode());
            pdmMessageDto.setPk_directoryId(pdmMessage.getDictionary().getPk().getDirectoryId());

            if (pdmMessage.getDictionary().getData().getSpecifications() != null) {
                pdmMessage.getDictionary().getData().getSpecifications().forEach(
                        s -> pdmMessageDto.addSpec(toSpecDto(s))
                );
            }
        }
        return pdmMessageDto;
    }

    public static PdmMessage fromConsumerRecord(ConsumerRecord record) {
        val topic = record.topic();
        PdmMessage message = new PdmMessage();
        message.setTopic(record.topic());
        message.setKey((String) record.key());
        message.setOffset(record.offset());
        message.setPartition(record.partition());

        switch (topic) {
            case "000-1.l3-pdm.cdc.sp-microstructure.0": {// todo убрать хардкод

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
            default: {
                log.error("Not supported type of: {}", record);
                throw new IllegalArgumentException("Not supported type of: " + record);
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

    public static MicrostructureDto toMicrostructureDto(PdmDictionary dictionary) {
        val format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        Date ts=null;
        try {
            ts = format.parse(dictionary.getTs());
        } catch (ParseException e) {
            log.error("Ошибка парсинга ts: {}",dictionary.getTs());
            throw new RuntimeException("Ошибка парсинга ts: "+dictionary.getTs()+"; "+e);
        }


        val specs = dictionary.getData().getSpecifications();

        MicrostructureDto microstructureDto = MicrostructureDto.builder()
                .remote_id(dictionary.getPk().getId())
                .ts(ts)
                .tkNum(getSpecValue(specs, SpecCode.TK_NUMBER_OR_VTK_VERSION_ROUTE.getValue()))
                .tkRoute(getSpecValue(specs,415))
                .prProdMark(getSpecValue(specs,SpecCode.STEEL_MARK.getValue()))
                .prStandMark(getSpecValue(specs,SpecCode.PRODUCT_STANDARD.getValue()))
                .prThickUncoat(stringToLimit(getSpecValue(specs,416)))
                .category(getSpecValue(specs,417))
                .attestStand(getSpecValue(specs,418))
                .ferriteGrain(stringToLimit(getSpecValue(specs,419)))
                .unevenessFerriteGrain(getSpecValue(specs,420))
                .structFreeCementite(stringToLimit(getSpecValue(specs,421)))
                .unmetallInclusionsOxides(stringToLimit(getSpecValue(specs,SpecCode.OXIDES.getValue())))
                .unmetallInclusionsSulfides(stringToLimit(getSpecValue(specs,SpecCode.SULPHIDES.getValue())))
                .unmetallInclusionsNitrides(stringToLimit(getSpecValue(specs,SpecCode.NITRIDES.getValue())))
                .unmetallInclusionsSilicates(stringToLimit(getSpecValue(specs,SpecCode.SILICATES.getValue())))
                .unmetallInclusionsOxidesB(stringToLimit(getSpecValue(specs,422)))
                .unmetallInclusionsSulfidesA(stringToLimit(getSpecValue(specs,423)))
                .unmetallInclusionsSilicatesC(stringToLimit(getSpecValue(specs,424)))
                .unmetallInclusionsGlobOxidesD(stringToLimit(getSpecValue(specs,449)))
                .unmetallInclusions(stringToLimit(getSpecValue(specs,425)))
                .polFerPerStruct(stringToLimit(getSpecValue(specs,426)))
                .depthDecarbLayer(stringToLimit(getSpecValue(specs,348)))
                .perliteGrain(stringToLimit(getSpecValue(specs,427)))
                .prAnnotation(getSpecValue(specs,138))
                .build();
        return microstructureDto;
    }

    private static String getSpecValue(List<com.nlmk.kb.server.entity.pdm.Spec> specs, int code) {
        if (specs == null) {
            return null;
        }

        val spec = specs.stream().filter((s) -> s.getSpecCode() == code).findFirst();
        if (spec.isPresent()){
            return spec.get().getSpecValue();
        } else {
            return null;
        }
    }

    public static LimitDto stringToLimit(String value) {
        if (value == null || value.isEmpty() || value.isBlank()) {
            return null;
        }

        // чистка от мусора (только положительные числа) .. todo
        final var test = value.replaceAll("[^0-9,.*()\\[\\]]", "");
        // минимальный вариант - одиночное дробное значение, типа '1.0'
        if (test.length() < 3) {
            return null;
        }

        // Преобразование строкового представления в объект LimitDto по его правилам.
        //   Варианты значений:
        //           одиночные: '0.42'
        //            диапазон: '*..0.07' '0.030..0.050' '0.015..*'
        // уточненный диапазон: '(20..*' '[0.017..*' '[0.031..0.052)'
        // Для преобразования в Double нужен разделитель '.'
        // Разделитель '..' для диапазона чисел, в единственном числе.

        final var range = test.split("\\.\\.", 2);
        final var left = range[0].replaceAll(",", ".");
        final var leftDigit = left.replaceAll("[()\\[\\]]", "");

        // одиночное значение

        if (range.length == 1) {
            return LimitDto.builder()
                    .singleValue(Double.valueOf(leftDigit))
                    .range(false)
                    .build();
        }

        // диапазон

        final var right = range[1].replaceAll(",", ".");
        final var rightDigit = right.replaceAll("[()\\[\\]]", "");
        var builder = LimitDto.builder().range(true);

        if (left.contains("*")) {
            // диапазон открытый слева
            builder.closedRange(false).openedLeft(true)
                    .rightValue(Double.valueOf(rightDigit));
        } else if (right.contains("*")) {
            // диапазон открытый справа
            builder.closedRange(false).openedRight(true)
                    .leftValue(Double.valueOf(leftDigit));
        } else {
            builder.leftValue(Double.valueOf(leftDigit))
                    .rightValue(Double.valueOf(rightDigit));
        }
        // уточнение диапазона, по-умолчанию нестрогое неравенство, поиск строго
        if (left.contains("(")) {
            builder.strictLeft(true);
        }
        if (right.contains(")")) {
            builder.strictRight(true);
        }

        return builder.build();
    }
}
