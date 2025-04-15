package com.nlmk.kb.server.service.zifra.creators;

import com.nlmk.kb.server.service.zifra.MdmDictionaryCreator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SpTestTypeRequestCreator extends BaseMdmCreator {
    public SpTestTypeRequestCreator(MdmDictionaryCreator mdmDictionaryCreator,
                                    @Value("${kafka.zifra.topic.sp-test-type-request}") String type) {
        super(mdmDictionaryCreator, type);
    }
}
