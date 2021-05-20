package com.nlmk.kb.server.util;

import com.nlmk.attestation.product.api.nsi.ChemicalStdLimitDto;
import com.nlmk.attestation.product.api.nsi.LengthTkLimitDto;
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
import nlmk.l3.pdm.SpAsapTolLinks;
import nlmk.l3.pdm.SpEquivalents;
import nlmk.l3.pdm.SpKatSteelMarkGost4041;
import nlmk.l3.pdm.SpMatchRabplanNum;
import nlmk.l3.pdm.SpMatchTkNum;
import nlmk.l3.pdm.SpMicrostructure;
import nlmk.l3.pdm.SpPcm;
import nlmk.l3.pdm.SpTolLength;
import nlmk.l3.pdm.SpTolThick;
import nlmk.l3.pdm.SpTolWidth;
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

        //todo избавиться от лишнего кода!!!,перенести в PdmComminConverter? убрать хардкод
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
            default: {
                log.error("Not supported type of: {}", record);
                throw new IllegalArgumentException("Not supported type of: " + record);
            }
        }
        return message;
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

    public static ChemicalStdLimitDto toChemicalStdLimitDto(PdmDictionary dictionary){
        val specs = dictionary.getData().getSpecifications();

        log.debug("--- PDM DICTIONARY: {} ", dictionary);

        val chemicalStdLimitDto = ChemicalStdLimitDto.builder()
                .remote_id(dictionary.getPk().getId())
               // .ts(parseToDate(dictionary.getTs())) //todo заменить как решиться вопрос с датой в топиках на стророне НЛМК
                .prProdMark(getSpecValue(specs,SpecCode.STEEL_MARK.getValue()))
                .prStandMark(getSpecValue(specs,SpecCode.PRODUCT_STANDARD.getValue()))
                .c(stringToLimit(getSpecValue(specs,SpecCode.MASS_FRACTION_C.getValue())))
                .si(stringToLimit(getSpecValue(specs,SpecCode.MASS_FRACTION_SI.getValue())))
                .mn(stringToLimit(getSpecValue(specs,SpecCode.MASS_FRACTION_MN.getValue())))
                .s(stringToLimit(getSpecValue(specs,SpecCode.MASS_FRACTION_S.getValue())))
                .p(stringToLimit(getSpecValue(specs,SpecCode.MASS_FRACTION_P.getValue())))
                .al(stringToLimit(getSpecValue(specs,SpecCode.MASS_FRACTION_AL.getValue())))
                .cr(stringToLimit(getSpecValue(specs,SpecCode.MASS_FRACTION_CR.getValue())))
                .ni(stringToLimit(getSpecValue(specs,SpecCode.MASS_FRACTION_NI.getValue())))
                .cu(stringToLimit(getSpecValue(specs,SpecCode.MASS_FRACTION_CU.getValue())))
                .n(stringToLimit(getSpecValue(specs,SpecCode.MASS_FRACTION_N.getValue())))
                .ti(stringToLimit(getSpecValue(specs,SpecCode.MASS_FRACTION_TI.getValue())))
                .nb(stringToLimit(getSpecValue(specs,SpecCode.MASS_FRACTION_NB.getValue())))
                .v(stringToLimit(getSpecValue(specs,SpecCode.MASS_FRACTION_V.getValue())))
                .b(stringToLimit(getSpecValue(specs,SpecCode.MASS_FRACTION_B.getValue())))
                .mo(stringToLimit(getSpecValue(specs,SpecCode.MASS_FRACTION_MO.getValue())))
                .ca(stringToLimit(getSpecValue(specs,SpecCode.MASS_FRACTION_CA.getValue())))
                .w(stringToLimit(getSpecValue(specs,SpecCode.MASS_FRACTION_W.getValue())))
                .as(stringToLimit(getSpecValue(specs,SpecCode.MASS_FRACTION_AS.getValue())))
                .cP(stringToLimit(getSpecValue(specs,SpecCode.CP.getValue())))
                .sP(stringToLimit(getSpecValue(specs,SpecCode.SP.getValue())))
                .crNiMoCu(stringToLimit(getSpecValue(specs,SpecCode.CrNiCuMo.getValue())))
                .crMo(stringToLimit(getSpecValue(specs,SpecCode.CrMo.getValue())))
                .alTi(stringToLimit(getSpecValue(specs,SpecCode.AlTi.getValue())))
                .alTiVNb(stringToLimit(getSpecValue(specs,SpecCode.AlTiVNb.getValue())))
                .bTiVNb(stringToLimit(getSpecValue(specs,SpecCode.BTiVNb.getValue())))
                .vNbTi(stringToLimit(getSpecValue(specs,SpecCode.TiVNb.getValue())))
                .tiNb(stringToLimit(getSpecValue(specs,SpecCode.TiNb.getValue())))
                .ti34n15s(stringToLimit(getSpecValue(specs,SpecCode.Ti34N15S.getValue())))
                .prAnnotation(getSpecValue(specs,SpecCode.NOTE.getValue()))
                .build();

        val format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SS");
        try {
            chemicalStdLimitDto.setTs(format.parse(dictionary.getTs()));
        } catch (ParseException e) {
            log.error("Ошибка парсинга ts: {}",dictionary.getTs());
            throw new RuntimeException("Ошибка парсинга ts: "+dictionary.getTs()+"; "+e);
        }

        log.debug("--- PDM chemicalStdLimitDto: {} ", chemicalStdLimitDto);

        return chemicalStdLimitDto;
    }

    public static MicrostructureDto toMicrostructureDto(PdmDictionary dictionary) {
        val specs = dictionary.getData().getSpecifications();

        val microstructureDto = MicrostructureDto.builder()
                .remote_id(dictionary.getPk().getId())
                .ts(parseToDate(dictionary.getTs()))
                .tkNum(getSpecValue(specs, SpecCode.TK_NUMBER_OR_VTK_VERSION_ROUTE.getValue()))
                .tkRoute(getSpecValue(specs,SpecCode.ROUTE_TK.getValue()))
                .prProdMark(getSpecValue(specs,SpecCode.STEEL_MARK.getValue()))
                .prStandMark(getSpecValue(specs,SpecCode.PRODUCT_STANDARD.getValue()))
                .prThickUncoat(stringToLimit(getSpecValue(specs,SpecCode.THICKNESS_OF_ROLLED_PRODUCTS.getValue())))
                .category(getSpecValue(specs,SpecCode.CATEGORY_GOST_4041.getValue()))
                .attestStand(getSpecValue(specs,SpecCode.MICROCTRUCTURE_STANDART.getValue()))
                .ferriteGrain(stringToLimit(getSpecValue(specs,SpecCode.FERRIT_GRAIN.getValue())))
                .unevenessFerriteGrain(getSpecValue(specs,SpecCode.UNEVENNESS_OF_FERRIT_GRAIN.getValue()))
                .structFreeCementite(stringToLimit(getSpecValue(specs,SpecCode.CEMENTITE.getValue())))
                .unmetallInclusionsOxides(stringToLimit(getSpecValue(specs,SpecCode.OXIDES.getValue())))
                .unmetallInclusionsSulfides(stringToLimit(getSpecValue(specs,SpecCode.SULPHIDES.getValue())))
                .unmetallInclusionsNitrides(stringToLimit(getSpecValue(specs,SpecCode.NITRIDES.getValue())))
                .unmetallInclusionsSilicates(stringToLimit(getSpecValue(specs,SpecCode.SILICATES.getValue())))
                .unmetallInclusionsOxidesB(stringToLimit(getSpecValue(specs,SpecCode.OXIDES_TYPE_B.getValue())))
                .unmetallInclusionsSulfidesA(stringToLimit(getSpecValue(specs,SpecCode.SULPHIDES_TYPE_A.getValue())))
                .unmetallInclusionsSilicatesC(stringToLimit(getSpecValue(specs,SpecCode.SILICATES_TYPE_C.getValue())))
                .unmetallInclusionsGlobOxidesD(stringToLimit(getSpecValue(specs,SpecCode.OXIDES_TYPE_D.getValue())))
                .unmetallInclusions(stringToLimit(getSpecValue(specs,SpecCode.NON_METALLIC_INCLUSIONS_ISO_4967_2013.getValue())))
                .polFerPerStruct(stringToLimit(getSpecValue(specs,SpecCode.BANDING_OF_FERRIE_PEARLITE_STRUCTURE.getValue())))
                .depthDecarbLayer(stringToLimit(getSpecValue(specs,SpecCode.DEPTH_WITHOUT_C_LAYER.getValue())))
                .perliteGrain(stringToLimit(getSpecValue(specs,SpecCode.PERLITE_GRAIN.getValue())))
                .prAnnotation(getSpecValue(specs,SpecCode.NOTE.getValue()))
                .build();
        return microstructureDto;
    }

    private static Date parseToDate(String stringDate){
        val format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        try {
            return format.parse(stringDate);
        } catch (ParseException e) {
            log.error("Ошибка парсинга ts: {}",stringDate);
            throw new RuntimeException("Ошибка парсинга ts: "+stringDate+"; "+e);
        }
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
