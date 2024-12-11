package com.nlmk.kb.server.service.zifra.senders;

import com.nlmk.kb.server.service.zifra.MdmDictionaryCreator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SpCustomerGroupMdmCreator extends BaseMdmCreator {
    public SpCustomerGroupMdmCreator(MdmDictionaryCreator mdmDictionaryCreator,
                                     @Value("${kafka.zifra.topic.sp-customer-group}") String type) {
        super(mdmDictionaryCreator, type);
    }
}
