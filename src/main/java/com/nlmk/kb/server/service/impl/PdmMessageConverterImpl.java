package com.nlmk.kb.server.service.impl;

import com.nlmk.attestation.product.api.nsi.ChemicalEquivalentStdDto;
import com.nlmk.attestation.product.api.nsi.ChemicalStdLimitDto;
import com.nlmk.attestation.product.api.nsi.MatchRpDto;
import com.nlmk.attestation.product.api.nsi.MatchTkDto;
import com.nlmk.attestation.product.api.nsi.MicrostructureDto;
import com.nlmk.attestation.product.api.nsi.PcmDto;
import com.nlmk.attestation.product.api.nsi.ToleranceDto;
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
    public ChemicalStdLimitDto toChemicalStdLimitDto(PdmDictionary dictionary) {
        val specs = dictionary.getData().getSpecifications();

        log.debug("--- PDM DICTIONARY: {} ", dictionary);

        val chemicalStdLimitDto = ChemicalStdLimitDto.builder()
                .remote_id(dictionary.getPk().getId())
                // .ts(parseToDate(dictionary.getTs())) //todo заменить как решиться вопрос с датой в топиках на стророне НЛМК
                .prProdMark(converter.getSpecValue(specs,SpecCode.STEEL_MARK.getValue()))
                .prStandMark(converter.getSpecValue(specs,SpecCode.PRODUCT_STANDARD.getValue()))
                .c(converter.stringToLimit(converter.getSpecValue(specs,SpecCode.MASS_FRACTION_C.getValue())))
                .si(converter.stringToLimit(converter.getSpecValue(specs,SpecCode.MASS_FRACTION_SI.getValue())))
                .mn(converter.stringToLimit(converter.getSpecValue(specs,SpecCode.MASS_FRACTION_MN.getValue())))
                .s(converter.stringToLimit(converter.getSpecValue(specs,SpecCode.MASS_FRACTION_S.getValue())))
                .p(converter.stringToLimit(converter.getSpecValue(specs,SpecCode.MASS_FRACTION_P.getValue())))
                .al(converter.stringToLimit(converter.getSpecValue(specs,SpecCode.MASS_FRACTION_AL.getValue())))
                .cr(converter.stringToLimit(converter.getSpecValue(specs,SpecCode.MASS_FRACTION_CR.getValue())))
                .ni(converter.stringToLimit(converter.getSpecValue(specs,SpecCode.MASS_FRACTION_NI.getValue())))
                .cu(converter.stringToLimit(converter.getSpecValue(specs,SpecCode.MASS_FRACTION_CU.getValue())))
                .n(converter.stringToLimit(converter.getSpecValue(specs,SpecCode.MASS_FRACTION_N.getValue())))
                .ti(converter.stringToLimit(converter.getSpecValue(specs,SpecCode.MASS_FRACTION_TI.getValue())))
                .nb(converter.stringToLimit(converter.getSpecValue(specs,SpecCode.MASS_FRACTION_NB.getValue())))
                .v(converter.stringToLimit(converter.getSpecValue(specs,SpecCode.MASS_FRACTION_V.getValue())))
                .b(converter.stringToLimit(converter.getSpecValue(specs,SpecCode.MASS_FRACTION_B.getValue())))
                .mo(converter.stringToLimit(converter.getSpecValue(specs,SpecCode.MASS_FRACTION_MO.getValue())))
                .ca(converter.stringToLimit(converter.getSpecValue(specs,SpecCode.MASS_FRACTION_CA.getValue())))
                .w(converter.stringToLimit(converter.getSpecValue(specs,SpecCode.MASS_FRACTION_W.getValue())))
                .as(converter.stringToLimit(converter.getSpecValue(specs,SpecCode.MASS_FRACTION_AS.getValue())))
                .cP(converter.stringToLimit(converter.getSpecValue(specs,SpecCode.CP.getValue())))
                .sP(converter.stringToLimit(converter.getSpecValue(specs,SpecCode.SP.getValue())))
                .crNiMoCu(converter.stringToLimit(converter.getSpecValue(specs,SpecCode.CrNiCuMo.getValue())))
                .crMo(converter.stringToLimit(converter.getSpecValue(specs,SpecCode.CrMo.getValue())))
                .alTi(converter.stringToLimit(converter.getSpecValue(specs,SpecCode.AlTi.getValue())))
                .alTiVNb(converter.stringToLimit(converter.getSpecValue(specs,SpecCode.AlTiVNb.getValue())))
                .bTiVNb(converter.stringToLimit(converter.getSpecValue(specs,SpecCode.BTiVNb.getValue())))
                .vNbTi(converter.stringToLimit(converter.getSpecValue(specs,SpecCode.TiVNb.getValue())))
                .tiNb(converter.stringToLimit(converter.getSpecValue(specs,SpecCode.TiNb.getValue())))
                .ti34n15s(converter.stringToLimit(converter.getSpecValue(specs,SpecCode.Ti34N15S.getValue())))
                .prAnnotation(converter.getSpecValue(specs,SpecCode.NOTE.getValue()))
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

    @Override
    public MicrostructureDto toMicrostructureDto(PdmDictionary dictionary) {
        val specs = dictionary.getData().getSpecifications();

        val microstructureDto = MicrostructureDto.builder()
                .remote_id(dictionary.getPk().getId())
                .ts(converter.parseToDate(dictionary.getTs()))
                .tkNum(converter.getSpecValue(specs, SpecCode.TK_NUMBER_OR_VTK_VERSION_ROUTE.getValue()))
                .tkRoute(converter.getSpecValue(specs,SpecCode.ROUTE_TK.getValue()))
                .prProdMark(converter.getSpecValue(specs,SpecCode.STEEL_MARK.getValue()))
                .prStandMark(converter.getSpecValue(specs,SpecCode.PRODUCT_STANDARD.getValue()))
                .prThickUncoat(converter.stringToLimit(converter.getSpecValue(specs,SpecCode.THICKNESS_OF_ROLLED_PRODUCTS.getValue())))
                .category(converter.getSpecValue(specs,SpecCode.CATEGORY_GOST_4041.getValue()))
                .attestStand(converter.getSpecValue(specs,SpecCode.MICROCTRUCTURE_STANDART.getValue()))
                .ferriteGrain(converter.stringToLimit(converter.getSpecValue(specs,SpecCode.FERRIT_GRAIN.getValue())))
                .unevenessFerriteGrain(converter.getSpecValue(specs,SpecCode.UNEVENNESS_OF_FERRIT_GRAIN.getValue()))
                .structFreeCementite(converter.stringToLimit(converter.getSpecValue(specs,SpecCode.CEMENTITE.getValue())))
                .unmetallInclusionsOxides(converter.stringToLimit(converter.getSpecValue(specs,SpecCode.OXIDES.getValue())))
                .unmetallInclusionsSulfides(converter.stringToLimit(converter.getSpecValue(specs,SpecCode.SULPHIDES.getValue())))
                .unmetallInclusionsNitrides(converter.stringToLimit(converter.getSpecValue(specs,SpecCode.NITRIDES.getValue())))
                .unmetallInclusionsSilicates(converter.stringToLimit(converter.getSpecValue(specs,SpecCode.SILICATES.getValue())))
                .unmetallInclusionsOxidesB(converter.stringToLimit(converter.getSpecValue(specs,SpecCode.OXIDES_TYPE_B.getValue())))
                .unmetallInclusionsSulfidesA(converter.stringToLimit(converter.getSpecValue(specs,SpecCode.SULPHIDES_TYPE_A.getValue())))
                .unmetallInclusionsSilicatesC(converter.stringToLimit(converter.getSpecValue(specs,SpecCode.SILICATES_TYPE_C.getValue())))
                .unmetallInclusionsGlobOxidesD(converter.stringToLimit(converter.getSpecValue(specs,SpecCode.OXIDES_TYPE_D.getValue())))
                .unmetallInclusions(converter.stringToLimit(converter.getSpecValue(specs,SpecCode.NON_METALLIC_INCLUSIONS_ISO_4967_2013.getValue())))
                .polFerPerStruct(converter.stringToLimit(converter.getSpecValue(specs,SpecCode.BANDING_OF_FERRIE_PEARLITE_STRUCTURE.getValue())))
                .depthDecarbLayer(converter.stringToLimit(converter.getSpecValue(specs,SpecCode.DEPTH_WITHOUT_C_LAYER.getValue())))
                .perliteGrain(converter.stringToLimit(converter.getSpecValue(specs,SpecCode.PERLITE_GRAIN.getValue())))
                .prAnnotation(converter.getSpecValue(specs,SpecCode.NOTE.getValue()))
                .build();
        return microstructureDto;
    }

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

    @Override
    public MatchTkDto toMatchTkDto(PdmDictionary dictionary) {
        val specs = dictionary.getData().getSpecifications();
        log.debug("--- toMatchTkDto PDM DICTIONARY: {} ", dictionary);

        MatchTkDto matchTkDto = MatchTkDto.builder()
                .remote_id(dictionary.getPk().getId())
                //.ts()// todo после решения вопроса по передачи даты и времени заменить
                .tkNum(converter.getSpecValue(specs,SpecCode.TK_NUMBER_OR_VTK_VERSION_ROUTE.getValue()))
                .tkNumSap(converter.getSpecValue(specs,SpecCode.TK_SAP_NUMBER.getValue()))
                .prAnnotation(converter.getSpecValue(specs,SpecCode.NOTE.getValue()))
                .build();

        val format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SS");
        try {
            matchTkDto.setTs(format.parse(dictionary.getTs()));
        } catch (ParseException e) {
            log.error("Ошибка парсинга ts: {}",dictionary.getTs());
            throw new RuntimeException("Ошибка парсинга ts: "+dictionary.getTs()+"; "+e);
        }

        log.debug("--- PDM MatchTkDto: {} ", matchTkDto);

        return matchTkDto;
    }

    @Override
    public MatchRpDto toMatchRpDto(PdmDictionary dictionary) {
        val specs = dictionary.getData().getSpecifications();
        log.debug("--- toMatchRpDto PDM DICTIONARY: {} ", dictionary);

        MatchRpDto matchTkDto = MatchRpDto.builder()
                .remote_id(dictionary.getPk().getId())
                // .ts() todo после решения вопроса по передачи даты и времени заменить
                .rpNumSap(converter.getSpecValue(specs,SpecCode.RP_SAP_NUMBER.getValue()))
                .tkNum(converter.getSpecValue(specs,SpecCode.RP_NUMBER_VERSION_ROUTE.getValue()))
                .build();

        val format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SS");
        try {
            matchTkDto.setTs(format.parse(dictionary.getTs()));
        } catch (ParseException e) {
            log.error("Ошибка парсинга ts: {}",dictionary.getTs());
            throw new RuntimeException("Ошибка парсинга ts: "+dictionary.getTs()+"; "+e);
        }

        log.debug("--- PDM MatchRpDto: {} ", matchTkDto);
        return matchTkDto;
    }

    @Override
    public PcmDto toPcmDto(PdmDictionary dictionary){
        val specs = dictionary.getData().getSpecifications();
        log.debug("--- toPcmDto PDM DICTIONARY: {} ", dictionary);

        val pcmDto = PcmDto.builder()
                .remote_id(dictionary.getPk().getId())
                .ts(converter.parseToDate(dictionary.getTs()))
                .pcmNum(converter.getSpecValue(specs,SpecCode.CRACK_RESISTANCE_COEFFICIENT_FORMULA_NUMBER.getValue()))
                .pcmFormula(converter.getSpecValue(specs,SpecCode.CRACK_RESISTANCE_FORMULA.getValue()))
                .prAnnotation(converter.getSpecValue(specs,SpecCode.NOTE.getValue()))
                .build();

        log.debug("--- toPcmDto PcmDto: {} ", pcmDto);
        return pcmDto;
    }

    @Override
    public ToleranceDto toToleranceDto(PdmDictionary dictionary){
        val specs = dictionary.getData().getSpecifications();
        log.debug("--- toleranceDto PDM DICTIONARY: {} ", dictionary);

        val toleranceDto = ToleranceDto.builder()
                .remote_id(dictionary.getPk().getId())
                .ts(converter.parseToDate(dictionary.getTs()))
                .prStandMark(converter.getSpecValue(specs,SpecCode.PRODUCT_STANDARD.getValue()))
                .useStandMark(converter.getSpecValue(specs,SpecCode.PRODUCT_STANDARD_ADDITIONAL.getValue()))
                .standTolThick(converter.getSpecValue(specs,SpecCode.THICKNESS_TOLERANCE_STANDART.getValue()))
                .standTolWidth(converter.getSpecValue(specs,SpecCode.WIDTH_TOLERANCE_STANDART.getValue()))
                .standTolLength(converter.getSpecValue(specs,SpecCode.LENGTH_TOLERANCE_STANDART.getValue()))
                .standTolEvenness(converter.getSpecValue(specs,SpecCode.EVENNESS_TOLERANCE_STANDART.getValue()))
                .prAnnotation(converter.getSpecValue(specs,SpecCode.NOTE.getValue()))
                .build();

        log.debug("--- toPcmDto PcmDto: {} ", toleranceDto);
        return toleranceDto;
    }
}
