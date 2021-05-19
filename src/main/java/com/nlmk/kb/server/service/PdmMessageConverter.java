package com.nlmk.kb.server.service;

import com.nlmk.attestation.product.api.nsi.ChemicalEquivalentStdDto;
import com.nlmk.attestation.product.api.nsi.ChemicalStdLimitDto;
import com.nlmk.attestation.product.api.nsi.KatSteelMarkGost4041Dto;
import com.nlmk.attestation.product.api.nsi.MatchRpDto;
import com.nlmk.attestation.product.api.nsi.MatchTkDto;
import com.nlmk.attestation.product.api.nsi.MatchRpDto;
import com.nlmk.attestation.product.api.nsi.MatchTkDto;
import com.nlmk.attestation.product.api.nsi.MicrostructureDto;
import com.nlmk.attestation.product.api.nsi.PcmDto;
import com.nlmk.attestation.product.api.nsi.ThicknessTkLimitDto;
import com.nlmk.attestation.product.api.nsi.ToleranceDto;
import com.nlmk.attestation.product.api.nsi.WidthTkLimitDto;
import com.nlmk.kb.server.entity.pdm.PdmDictionary;

public interface PdmMessageConverter {

    public ChemicalEquivalentStdDto toChemicalEquivalentStdDto(PdmDictionary dictionary);

    PcmDto toPcmDto(PdmDictionary dictionary);

    ToleranceDto toToleranceDto(PdmDictionary dictionary);

    MicrostructureDto toMicrostructureDto(PdmDictionary dictionary);

    MatchTkDto toMatchTkDto(PdmDictionary dictionary);

    MatchRpDto toMatchRpDto(PdmDictionary dictionary);

    ChemicalStdLimitDto toChemicalStdLimitDto(PdmDictionary dictionary);

    KatSteelMarkGost4041Dto toKatSteel4041Dto(PdmDictionary dictionary);

    ThicknessTkLimitDto toThicknessTkLimitDto(PdmDictionary dictionary);

    WidthTkLimitDto toWidthTkLimitDto(PdmDictionary dictionary);
}
