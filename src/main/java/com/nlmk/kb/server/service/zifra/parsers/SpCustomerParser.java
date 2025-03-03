package com.nlmk.kb.server.service.zifra.parsers;

import com.nlmk.attestation.product.api.nsi.SpCustomerDto;
import com.nlmk.kb.server.service.zifra.Catalogue;
import com.nlmk.kb.server.service.zifra.CatalogueParser;
import com.nlmk.kb.server.util.AdapterUtils;
import nlmk.l3.nsi.zifra.Data;
import nlmk.l3.nsi.zifra.pk;
import org.springframework.stereotype.Component;

@Component
public class SpCustomerParser implements CatalogueParser<SpCustomerDto> {

    @Override
    public Catalogue getCatalogue() {
        return Catalogue.SP_CUSTOMER;
    }

    @Override
    public SpCustomerDto parse(pk pk, Data data, Long messageId) {

        return SpCustomerDto.builder()
                .id(AdapterUtils.sequenceToString(pk.getLineId()))
                .messageId(messageId)
                .customerId(getAttrStringValueByName(data.getLineAttributes(), "customerId"))
                .customerName(getAttrStringValueByName(data.getLineAttributes(), "customerName"))
                .shortName(getAttrStringValueByName(data.getLineAttributes(), "shName"))
                .dateBegin(getBeginDate(data.getProperties()))
                .dateEnd(getEndDate(data.getProperties()))
                .isActive(getActive(data.getLineAttributes()))
                .build();
    }

}
