package com.nlmk.kb.server.service.pdm;

import com.nlmk.attestation.product.api.nsi.*;
import com.nlmk.kb.server.entity.pdm.PdmDictionary;

/**
 * Методы создания заданных DTO из PdmDictionary
 */
public interface PdmDtoConverter {

    ChemicalEquivalentStdDto toChemicalEquivalentStdDto(PdmDictionary dictionary);

    PcmDto toPcmDto(PdmDictionary dictionary);

    ToleranceDto toToleranceDto(PdmDictionary dictionary);

    MicrostructureDto toMicrostructureDto(PdmDictionary dictionary);

    MatchTkDto toMatchTkDto(PdmDictionary dictionary);

    MatchRpDto toMatchRpDto(PdmDictionary dictionary);

    /**
     * <a href="https://confluence.nlmk.com/pages/viewpage.action?pageId=120034208">Химический состав по стандартам</a>
     */
    ChemicalStdLimitDto toChemicalStdLimitDto(PdmDictionary dictionary);

    SteelCategoryG4041Dto toKatSteel4041Dto(PdmDictionary dictionary);

    ThicknessTkLimitDto toThicknessTkLimitDto(PdmDictionary dictionary);

    WidthTkLimitDto toWidthTkLimitDto(PdmDictionary dictionary);

    LengthTkLimitDto toLengthTkLimitDto(PdmDictionary dictionary);

    PhysMechPropertiesDto toPhysMechPropertiesDto(PdmDictionary dictionary);

    EvennessTkLimitDto toEvennessTkLimitDto(PdmDictionary dictionary);

    TkNumDto toTkNumDto(PdmDictionary dictionary);

    CEqDto toCEqDto(PdmDictionary dictionary);

    MechanicalTkDto toMechanicalTkDto(PdmDictionary dictionary);

    ChemicalTkLimitDto toChemicalTkLimitDto(PdmDictionary dictionary);

    /**
     * <a href="https://confluence.nlmk.com/pages/viewpage.action?pageId=120034811">Физико-механические свойства по ДТ для ЦТС</a>
     */
    AsapMechPropertiesDtDto toAsapMechPropertiesDtDto(PdmDictionary dictionary);

    /**
     * <a href="https://confluence.nlmk.com/pages/viewpage.action?pageId=120625772">Физико-механические свойства проката анизотропной стали по стандартам для ЦТС</a>
     */
    PhysMechPropAnisSteelStandDto toPhysMechPropAnisSteelStandDto(PdmDictionary dictionary);

    TolEvennessDtDto toTolEvennessDtDto(PdmDictionary dictionary);

    TolThickDtDto toTolThickDtDto(PdmDictionary dictionary);

    TolWidthDtDto toTolWidthDtDto(PdmDictionary dictionary);

    /**
     * <a href="https://confluence.nlmk.com/pages/viewpage.action?pageId=120629816">Расширение химического состава по примечаниям(NSD_chemical_properties_notes)</a>
     */
    SpChemicalPropertiesNotesDto toSpChemicalPropertiesNotesDto(PdmDictionary dictionary);

    /**
     * <a href="https://confluence.nlmk.com/pages/viewpage.action?pageId=116498692">Допуски по форме слябов(NSD_tol_shape_slab)</a>
     */
    TolShapeSlabDto toTolShapeSlabDto(PdmDictionary dictionary);

    /**
     * <a href="https://confluence.nlmk.com/pages/viewpage.action?pageId=144109644">Реестр эквивалентов(NSD_register_equivalents )</a>
     */
    RegisterEquivalentsDto toRegisterEquivalentsDto(PdmDictionary dictionary);

    /**
     * <a href="https://confluence.nlmk.com/pages/viewpage.action?pageId=147134079">Минимальное количество проб для химанализа( NSD_min_number_samp_chem )</a>
     */
    MinNumberSampChemDto toMinNumberSampChemDto(PdmDictionary dictionary);

    /**
     * <a href="https://confluence.nlmk.com/pages/viewpage.action?pageId=118453626">Схемы зачистки слябов( NSD_scheme_stripping_slab )</a>
     */
    SchemeStrippingSlabDto toSchemeStrippingSlabDto(PdmDictionary dictionary);

    /**
     * <a href="https://confluence.nlmk.com/pages/viewpage.action?pageId=118428454">Макроструктура( NSD_macrosructure )</a>
     */
    MacrostructureDto toMacrostructureDto(PdmDictionary dictionary);

}
