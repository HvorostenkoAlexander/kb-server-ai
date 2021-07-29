package com.nlmk.kb.server.service.impl.senders;

import com.nlmk.kb.server.service.DictionaryConfigService;
import com.nlmk.kb.server.service.NsiCommonSender;
import com.nlmk.kb.server.service.PdmDictionaryCreator;
import com.nlmk.kb.server.service.PdmDtoConverter;
import lombok.Getter;

@Getter
abstract class BaseSender {
    private final String type;
    private final PdmDtoConverter pdmDtoConverter;
    private final NsiCommonSender commonSender;
    private final PdmDictionaryCreator pdmDictionaryCreator;
    private final DictionaryConfigService dictionaryConfigService;

    public BaseSender(String type,
                      PdmDtoConverter pdmDtoConverter,
                      NsiCommonSender commonSender,
                      PdmDictionaryCreator pdmDictionaryCreator,
                      DictionaryConfigService dictionaryConfigService) {
        this.type = type;
        this.pdmDtoConverter = pdmDtoConverter;
        this.commonSender = commonSender;
        this.pdmDictionaryCreator = pdmDictionaryCreator;
        this.dictionaryConfigService = dictionaryConfigService;
    }
}
