package com.nlmk.kb.server.service.impl;

import com.nlmk.attestation.product.api.nsi.ChemicalEquivalentStdDto;
import com.nlmk.attestation.product.api.nsi.MatchRpDto;
import com.nlmk.attestation.product.api.nsi.MatchTkDto;
import com.nlmk.attestation.product.api.specification.SpecCode;
import com.nlmk.kb.server.entity.pdm.PdmDictionary;
import com.nlmk.kb.server.service.CommonConverter;
import com.nlmk.kb.server.service.PdmMessageConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;

@Slf4j
@Component
@RequiredArgsConstructor
public class PdmMessageConverterImpl implements PdmMessageConverter {

    private final CommonConverter converter;

    @Override
    public ChemicalEquivalentStdDto toChemicalEquivalentStdDto(PdmDictionary dictionary){
        val specs = dictionary.getData().getSpecifications();

        log.debug("--- toChemicalEquivalentStdDto PDM DICTIONARY: {} ", dictionary);

        ChemicalEquivalentStdDto chemicalEquivalentStdDto = ChemicalEquivalentStdDto.builder()
                .remote_id(dictionary.getPk().getId())
                .ts(converter.parseToDate(dictionary.getTs()))
                .prProdMark(converter.getSpecValue(specs, SpecCode.STEEL_MARK.getValue()))
                .prStandMark(converter.getSpecValue(specs,SpecCode.PRODUCT_STANDARD.getValue()))
                .prThickUncoat(converter.stringToLimit(
                        converter.getSpecValue(specs,SpecCode.THICKNESS_OF_ROLLED_PRODUCTS.getValue()))
                )
                .prStrengthClass(converter.getSpecValue(specs,SpecCode.STRENGTH_CLASS.getValue()))
                .ceqNum(converter.getSpecValue(specs,SpecCode.CARBON_EQUIVALENT_FORMULA_NUMBER.getValue()))
                .ceq(converter.stringToLimit(
                                converter.getSpecValue(specs,SpecCode.CARBON_EQUIVALENT.getValue()))
                )
                .pcmNum(converter.getSpecValue(specs,SpecCode.CRACK_RESISTANCE_COEFFICIENT_FORMULA_NUMBER.getValue()))
                .pcm(converter.stringToLimit(
                        converter.getSpecValue(specs,SpecCode.CRACK_RESISTANCE_COEFFICIENT.getValue()))
                )
                .prAnnotation(converter.getSpecValue(specs,SpecCode.NOTE.getValue()))
                .build();
        log.debug("--- PDM chemicalEquivalentStdDto: {} ", chemicalEquivalentStdDto);

        return chemicalEquivalentStdDto;
    }

//    @Override
//    public MatchTkDto toMatchTkDto(PdmDictionary dictionary) {
//        val specs = dictionary.getData().getSpecifications();
//        log.debug("--- toMatchTkDto PDM DICTIONARY: {} ", dictionary);
//
//        MatchTkDto matchTkDto = MatchTkDto.builder()
//                .remote_id(dictionary.getPk().getId())
//                //.ts()// todo после решения вопроса по передачи даты и времени заменить
//                .tkNum(converter.getSpecValue(specs,SpecCode.TK_NUMBER_OR_VTK_VERSION_ROUTE.getValue()))
//                .tkNumSap(converter.getSpecValue(specs,SpecCode.TK_SAP_NUMBER.getValue()))
//                .prAnnotation(converter.getSpecValue(specs,SpecCode.NOTE.getValue()))
//                .build();
//
//        val format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SS");
//        try {
//            matchTkDto.setTs(format.parse(dictionary.getTs()));
//        } catch (ParseException e) {
//            log.error("Ошибка парсинга ts: {}",dictionary.getTs());
//            throw new RuntimeException("Ошибка парсинга ts: "+dictionary.getTs()+"; "+e);
//        }
//
//        log.debug("--- PDM MatchTkDto: {} ", matchTkDto);
//
//        return matchTkDto;
//    }
//
//    @Override
//    public MatchRpDto toMatchRpDto(PdmDictionary dictionary) {
//        val specs = dictionary.getData().getSpecifications();
//        log.debug("--- toMatchRpDto PDM DICTIONARY: {} ", dictionary);
//
//        MatchRpDto matchTkDto = MatchRpDto.builder()
//                .remote_id(dictionary.getPk().getId())
//                // .ts() todo после решения вопроса по передачи даты и времени заменить
//                .rpNumSap(converter.getSpecValue(specs,SpecCode.RP_SAP_NUMBER.getValue()))
//                .tkNum(converter.getSpecValue(specs,SpecCode.RP_NUMBER_VERSION_ROUTE.getValue()))
//                .build();
//
//        val format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SS");
//        try {
//            matchTkDto.setTs(format.parse(dictionary.getTs()));
//        } catch (ParseException e) {
//            log.error("Ошибка парсинга ts: {}",dictionary.getTs());
//            throw new RuntimeException("Ошибка парсинга ts: "+dictionary.getTs()+"; "+e);
//        }
//
//        log.debug("--- PDM MatchRpDto: {} ", matchTkDto);
//        return matchTkDto;
//    }
}
