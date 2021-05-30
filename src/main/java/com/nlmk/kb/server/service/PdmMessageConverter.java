package com.nlmk.kb.server.service;

import com.nlmk.attestation.product.api.nsi.ChemicalEquivalentStdDto;
import com.nlmk.attestation.product.api.nsi.ChemicalFormulaCEqTkDto;
import com.nlmk.attestation.product.api.nsi.ChemicalStdLimitDto;
import com.nlmk.attestation.product.api.nsi.EvennessTkLimitDto;
import com.nlmk.attestation.product.api.nsi.LengthTkLimitDto;
import com.nlmk.attestation.product.api.nsi.MatchRpDto;
import com.nlmk.attestation.product.api.nsi.MatchTkDto;
import com.nlmk.attestation.product.api.nsi.MicrostructureDto;
import com.nlmk.attestation.product.api.nsi.PcmDto;
import com.nlmk.attestation.product.api.nsi.PhysMechPropertiesDto;
import com.nlmk.attestation.product.api.nsi.SteelCategoryG4041Dto;
import com.nlmk.attestation.product.api.nsi.ThicknessTkLimitDto;
import com.nlmk.attestation.product.api.nsi.TkNumDto;
import com.nlmk.attestation.product.api.nsi.ToleranceDto;
import com.nlmk.attestation.product.api.nsi.WidthTkLimitDto;
import com.nlmk.kb.server.entity.pdm.PdmDictionary;

public interface PdmMessageConverter {

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

    ChemicalFormulaCEqTkDto toChemicalFormulaCEqTkDto(PdmDictionary dictionary);
}
