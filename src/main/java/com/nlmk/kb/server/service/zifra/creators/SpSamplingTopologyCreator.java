package com.nlmk.kb.server.service.zifra.creators;

import com.nlmk.kb.server.service.zifra.MdmDictionaryCreator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SpSamplingTopologyCreator extends BaseMdmCreator {
    public SpSamplingTopologyCreator(MdmDictionaryCreator mdmDictionaryCreator,
                                     @Value("${kafka.zifra.topic.sp-sampling-topology}") String type) {
        super(mdmDictionaryCreator, type);
    }
}
