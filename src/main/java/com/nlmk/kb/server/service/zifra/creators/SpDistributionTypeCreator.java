package com.nlmk.kb.server.service.zifra.creators;

import com.nlmk.kb.server.service.zifra.MdmDictionaryCreator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SpDistributionTypeCreator extends BaseMdmCreator {
    public SpDistributionTypeCreator(MdmDictionaryCreator mdmDictionaryCreator,
                                     @Value("${kafka.zifra.topic.sp-distribution-type}") String type) {
        super(mdmDictionaryCreator, type);
    }
}
