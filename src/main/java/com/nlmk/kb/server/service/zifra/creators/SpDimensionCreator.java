package com.nlmk.kb.server.service.zifra.creators;

import com.nlmk.kb.server.service.zifra.MdmDictionaryCreator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SpDimensionCreator extends BaseMdmCreator {
    public SpDimensionCreator(MdmDictionaryCreator mdmDictionaryCreator,
                              @Value("${kafka.zifra.topic.sp-dimension}") String type) {
        super(mdmDictionaryCreator, type);
    }
}
