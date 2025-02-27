package com.nlmk.kb.server.service.zifra.parsers;

import com.nlmk.attestation.product.api.nsi.SpCustomerGroupDto;
import com.nlmk.kb.server.service.zifra.Catalogue;
import com.nlmk.kb.server.service.zifra.CatalogueParser;
import nlmk.l3.nsi.zifra.Data;
import nlmk.l3.nsi.zifra.pk;
import com.nlmk.kb.server.util.AdapterUtils;
import org.springframework.stereotype.Component;

@Component
public class SpCustomerGroupParser implements CatalogueParser<SpCustomerGroupDto> {

    @Override
    public Catalogue getCatalogue() {
        return Catalogue.SP_CUSTOMER_GROUP;
    }

    @Override
    public SpCustomerGroupDto parse(pk pk, Data data, Long messageId) {

        return SpCustomerGroupDto.builder()
                .id(AdapterUtils.sequenceToString(pk.getLineId()))
                .messageId(messageId)
                .groupId(getAttrIntegerValueByName(data.getLineAttributes(), "groupId"))
                .name(getAttrStringValueByName(data.getLineAttributes(), "name"))
                .dateBegin(getBeginDate(data.getProperties()))
                .dateEnd(getEndDate(data.getProperties()))
                .isActive(getActive(data.getLineAttributes()))
                .build();
    }

}
