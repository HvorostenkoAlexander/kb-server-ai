package com.nlmk.kb.server.service.zifra.creators;

import com.nlmk.kb.server.service.zifra.MdmDictionaryCreator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SpMappingAttributeCcm3tCreator extends BaseMdmCreator {
    public SpMappingAttributeCcm3tCreator(MdmDictionaryCreator mdmDictionaryCreator,
                                          @Value("${kafka.zifra.topic.sp-mapping-attribute-ccm3t}") String type) {
        super(mdmDictionaryCreator, type);
    }
}
