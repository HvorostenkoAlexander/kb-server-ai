package com.nlmk.kb.server.service.pdm.senders;

import com.nlmk.kb.server.service.pdm.DictionaryConfigService;
import com.nlmk.kb.server.service.NsiCommonSender;
import com.nlmk.kb.server.service.pdm.PdmDictionaryCreator;
import com.nlmk.kb.server.service.pdm.PdmDtoConverter;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
abstract class BaseCreator {

    private final String type;
    private final PdmDtoConverter pdmDtoConverter;
    private final NsiCommonSender commonSender;
    private final PdmDictionaryCreator pdmDictionaryCreator;
    private final DictionaryConfigService dictionaryConfigService;

}
