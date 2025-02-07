package com.nlmk.kb.server.service.zifra.parsers;

import com.nlmk.attestation.product.api.nsi.SpGroupAndCustomerDto;
import com.nlmk.kb.server.service.zifra.Catalogue;
import com.nlmk.kb.server.service.zifra.CatalogueParser;
import com.nlmk.kb.server.util.AdapterUtils;
import nlmk.l3.nsi.zifra.Data;
import nlmk.l3.nsi.zifra.pk;
import org.springframework.stereotype.Component;

@Component
public class SpGroupAndCustomerParser implements CatalogueParser<SpGroupAndCustomerDto> {

    @Override
    public Catalogue getCatalogue() {
        return Catalogue.SP_GROUP_AND_CUSTOMER;
    }

    @Override
    public SpGroupAndCustomerDto parse(pk pk, Data data) {

        return SpGroupAndCustomerDto.builder()
                .id(AdapterUtils.sequenceToString(pk.getLineId()))
                .groupId(getAttrStringValueByName(data.getLineAttributes(), "groupId"))
                .customerId(getAttrStringValueByName(data.getLineAttributes(), "customerId"))
                .priority(getAttrIntegerValueByName(data.getLineAttributes(), "priority"))
                .dateBegin(getBeginDate(data.getProperties()))
                .dateEnd(getEndDate(data.getProperties()))
                .isActive(getActive(data.getLineAttributes()))
                .build();
    }

}
