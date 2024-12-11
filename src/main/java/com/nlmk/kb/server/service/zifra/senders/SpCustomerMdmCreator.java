package com.nlmk.kb.server.service.zifra.senders;

import com.nlmk.kb.server.service.zifra.MdmDictionaryCreator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SpCustomerMdmCreator extends BaseMdmCreator {

    public SpCustomerMdmCreator(MdmDictionaryCreator creator,
                                @Value("${kafka.zifra.topic.sp-customer}") String type) {
        super(creator, type);
    }

}
