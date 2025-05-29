package com.nlmk.kb.server.service.zifra.creators;

import com.nlmk.kb.server.service.zifra.MdmDictionaryCreator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SpApcsAttestationResultCreator extends BaseMdmCreator {
    public SpApcsAttestationResultCreator(MdmDictionaryCreator mdmDictionaryCreator,
                                          @Value("${kafka.zifra.topic.sp-apcs-attestation-result}") String type) {
        super(mdmDictionaryCreator, type);
    }
}
