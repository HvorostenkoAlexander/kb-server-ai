package com.nlmk.kb.server.service.zifra.creators;

import com.nlmk.kb.server.service.zifra.MdmDictionaryCreator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SpAttributesCreator extends BaseMdmCreator {
    public SpAttributesCreator(MdmDictionaryCreator mdmDictionaryCreator,
                               @Value("${kafka.zifra.topic.sp-attributes}") String type) {
        super(mdmDictionaryCreator, type);
    }
}
