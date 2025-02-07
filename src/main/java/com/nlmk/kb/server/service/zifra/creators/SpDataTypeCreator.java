package com.nlmk.kb.server.service.zifra.creators;

import com.nlmk.kb.server.service.zifra.MdmDictionaryCreator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SpDataTypeCreator extends BaseMdmCreator {
    public SpDataTypeCreator(MdmDictionaryCreator mdmDictionaryCreator,
                             @Value("${kafka.zifra.topic.sp-data-type}") String type) {
        super(mdmDictionaryCreator, type);
    }
}
