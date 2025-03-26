package com.nlmk.kb.server.service.zifra.parsers;

import com.nlmk.attestation.product.api.nsi.SpDimensionDto;
import com.nlmk.kb.server.service.zifra.Catalogue;
import com.nlmk.kb.server.service.zifra.CatalogueParser;
import com.nlmk.kb.server.util.AdapterUtils;
import nlmk.l3.nsi.zifra.Data;
import nlmk.l3.nsi.zifra.pk;
import org.springframework.stereotype.Component;

@Component
public class SpDimensionParser implements CatalogueParser<SpDimensionDto> {

    @Override
    public Catalogue getCatalogue() {
        return Catalogue.SP_DIMENSION;
    }

    @Override
    public SpDimensionDto parse(pk pk, Data data, Long messageId) {
        return SpDimensionDto.builder()
                .id(AdapterUtils.sequenceToString(pk.getLineId()))
                .messageId(messageId)
                .name(getAttrStringValueByName(data.getLineAttributes(), "name"))
                .nameEng(getAttrStringValueByName(data.getLineAttributes(), "nameEng"))
                .baseUnit(getAttrStringValueByName(data.getLineAttributes(), "baseUnit"))
                .description(getAttrStringValueByName(data.getLineAttributes(), "description"))
                .dateBegin(getBeginDate(data.getProperties()))
                .dateEnd(getEndDate(data.getProperties()))
                .build();
    }
}
