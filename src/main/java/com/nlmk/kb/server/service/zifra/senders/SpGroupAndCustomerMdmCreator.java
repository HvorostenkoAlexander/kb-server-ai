package com.nlmk.kb.server.service.zifra.senders;

import com.nlmk.kb.server.service.zifra.MdmDictionaryCreator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SpGroupAndCustomerMdmCreator extends BaseMdmCreator {
    public SpGroupAndCustomerMdmCreator(MdmDictionaryCreator mdmDictionaryCreator,
                                        @Value("${kafka.zifra.topic.sp-group-and-customer}") String type) {
        super(mdmDictionaryCreator, type);
    }
}
