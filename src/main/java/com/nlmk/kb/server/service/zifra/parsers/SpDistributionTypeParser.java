package com.nlmk.kb.server.service.zifra.parsers;

import com.nlmk.attestation.product.api.nsi.SpDistributionTypeDto;
import com.nlmk.kb.server.service.zifra.Catalogue;
import com.nlmk.kb.server.service.zifra.CatalogueParser;
import com.nlmk.kb.server.util.AdapterUtils;
import nlmk.l3.nsi.zifra.Data;
import nlmk.l3.nsi.zifra.pk;
import org.springframework.stereotype.Component;

@Component
public class SpDistributionTypeParser implements CatalogueParser<SpDistributionTypeDto> {
    @Override
    public Catalogue getCatalogue() {
        return Catalogue.SP_DISTRIBUTION_TYPE;
    }

    @Override
    public SpDistributionTypeDto parse(pk pk, Data data, Long messageId) {
        return SpDistributionTypeDto.builder()
                .id(AdapterUtils.sequenceToString(pk.getLineId()))
                .messageId(messageId)
                .code(getAttrIntegerValueByName(data.getLineAttributes(), "code"))
                .name(getAttrStringValueByName(data.getLineAttributes(), "name"))
                .dateBegin(getBeginDate(data.getProperties()))
                .dateEnd(getEndDate(data.getProperties()))
                .build();
    }

}
