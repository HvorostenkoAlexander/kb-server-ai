package com.nlmk.kb.server.service.pdm;

import com.nlmk.kb.server.api.DictionaryConfigDto;
import com.nlmk.kb.server.api.PdmMessageDto;
import com.nlmk.kb.server.entity.DictionaryConfig;
import com.nlmk.kb.server.entity.pdm.PdmMessage;

public interface DtoConverter {

    DictionaryConfig toDictionaryConfig(DictionaryConfigDto dto);

    DictionaryConfigDto toDictionaryConfigDto(DictionaryConfig entity);

    PdmMessageDto toPdmMessageDto(PdmMessage entity);

}
