package com.nlmk.kb.server.service.zifra.creators;

import com.nlmk.kb.server.service.zifra.MdmDictionaryCreator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SpPlaceTypeCreator extends BaseMdmCreator {
    public SpPlaceTypeCreator(MdmDictionaryCreator mdmDictionaryCreator,
                              @Value("${kafka.zifra.topic.sp-place-type}") String type) {
        super(mdmDictionaryCreator, type);
    }
}
