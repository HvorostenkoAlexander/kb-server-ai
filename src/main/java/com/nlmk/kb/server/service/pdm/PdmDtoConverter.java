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

    AsapMechPropertiesDtDto toAsapMechPropertiesDtDto(PdmDictionary dictionary);

    PhysMechPropAnisSteelStandDto toPhysMechPropAnisSteelStandDto(PdmDictionary dictionary);

}
