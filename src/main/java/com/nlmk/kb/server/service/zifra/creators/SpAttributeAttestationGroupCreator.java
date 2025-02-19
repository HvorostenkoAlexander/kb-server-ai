package com.nlmk.kb.server.service.zifra.creators;

import com.nlmk.kb.server.service.zifra.MdmDictionaryCreator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SpAttributeAttestationGroupCreator extends BaseMdmCreator {
    public SpAttributeAttestationGroupCreator(MdmDictionaryCreator mdmDictionaryCreator,
                                              @Value("${kafka.zifra.topic.sp-attribute-attestation-group}") String type) {
        super(mdmDictionaryCreator, type);
    }
}
