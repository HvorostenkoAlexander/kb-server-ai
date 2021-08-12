package com.nlmk.kb.server.service;

import com.nlmk.attestation.product.api.PreAttestationParamDto;
import com.nlmk.kb.server.dto.DictionaryConfigDto;
import com.nlmk.kb.server.dto.PdmMessageDto;
import com.nlmk.kb.server.entity.PreAttestationParam;
import com.nlmk.kb.server.entity.configurator.DictionaryConfig;
import com.nlmk.kb.server.entity.pdm.PdmMessage;

public interface DtoConverter {

    public PreAttestationParamDto toPreAttestationParamDto(PreAttestationParam entity);

    public DictionaryConfig toDictionaryConfig(DictionaryConfigDto dto);

    public DictionaryConfigDto toDictionaryConfigDto(DictionaryConfig entity);

    public PdmMessageDto toPdmMessageDto(PdmMessage entity);
}
