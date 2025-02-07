package com.nlmk.kb.server.service.zifra.creators;

import com.nlmk.kb.server.service.zifra.MdmDictionaryCreator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SpPlaceCreator extends BaseMdmCreator {
    public SpPlaceCreator(MdmDictionaryCreator mdmDictionaryCreator,
                          @Value("${kafka.zifra.topic.sp-l2code-place}") String type) {
        super(mdmDictionaryCreator, type);
    }
}
