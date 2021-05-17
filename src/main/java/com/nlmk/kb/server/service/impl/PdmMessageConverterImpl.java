package com.nlmk.kb.server.service.impl;

import com.nlmk.attestation.product.api.nsi.ChemicalEquivalentStdDto;
import com.nlmk.attestation.product.api.specification.SpecCode;
import com.nlmk.kb.server.entity.pdm.PdmDictionary;
import com.nlmk.kb.server.entity.pdm.Spec;
import com.nlmk.kb.server.service.CommonConverter;
import com.nlmk.kb.server.service.PdmMessageConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PdmMessageConverterImpl implements PdmMessageConverter {

    private final CommonConverter converter;

    @Override
    public ChemicalEquivalentStdDto toChemicalEquivalentStdDto(PdmDictionary dictionary){
        val specs = dictionary.getData().getSpecifications();

        log.info("--- toChemicalEquivalentStdDto PDM DICTIONARY: {} ", dictionary);

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
        log.info("--- PDM chemicalEquivalentStdDto: {} ", chemicalEquivalentStdDto);

        return chemicalEquivalentStdDto;
    }
}
