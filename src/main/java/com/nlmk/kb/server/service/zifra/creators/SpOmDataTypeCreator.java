package com.nlmk.kb.server.service.zifra.creators;

import com.nlmk.kb.server.service.zifra.MdmDictionaryCreator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SpOmDataTypeCreator extends BaseMdmCreator {
    public SpOmDataTypeCreator(MdmDictionaryCreator mdmDictionaryCreator,
                               @Value("${kafka.zifra.topic.sp-om-data-type}") String type) {
        super(mdmDictionaryCreator, type);
    }
}
