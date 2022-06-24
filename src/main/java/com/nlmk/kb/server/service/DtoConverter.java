package com.nlmk.kb.server.service;

import com.nlmk.kb.server.dto.DictionaryConfigDto;
import com.nlmk.kb.server.dto.PdmMessageDto;
import com.nlmk.kb.server.entity.DictionaryConfig;
import com.nlmk.kb.server.entity.pdm.PdmMessage;

public interface DtoConverter {

    DictionaryConfig toDictionaryConfig(DictionaryConfigDto dto);

    DictionaryConfigDto toDictionaryConfigDto(DictionaryConfig entity);

    PdmMessageDto toPdmMessageDto(PdmMessage entity);

}
