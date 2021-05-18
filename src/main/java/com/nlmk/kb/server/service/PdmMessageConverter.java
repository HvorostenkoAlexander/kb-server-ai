package com.nlmk.kb.server.service;

import com.nlmk.attestation.product.api.nsi.ChemicalEquivalentStdDto;
import com.nlmk.attestation.product.api.nsi.MatchRpDto;
import com.nlmk.attestation.product.api.nsi.MatchTkDto;
import com.nlmk.kb.server.entity.pdm.PdmDictionary;

public interface PdmMessageConverter {

    public ChemicalEquivalentStdDto toChemicalEquivalentStdDto(PdmDictionary dictionary);

    MatchTkDto toMatchTkDto(PdmDictionary dictionary);

    MatchRpDto toMatchRpDto(PdmDictionary dictionary);
}
