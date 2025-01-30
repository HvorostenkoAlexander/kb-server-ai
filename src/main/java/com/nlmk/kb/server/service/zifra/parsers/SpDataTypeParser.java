package com.nlmk.kb.server.service.zifra.parsers;

import com.nlmk.attestation.product.api.nsi.SpDataTypeDto;
import com.nlmk.kb.server.service.zifra.Catalogue;
import com.nlmk.kb.server.service.zifra.CatalogueParser;
import com.nlmk.kb.server.util.AdapterUtils;
import nlmk.l3.nsi.zifra.Data;
import nlmk.l3.nsi.zifra.pk;
import org.springframework.stereotype.Component;

@Component
public class SpDataTypeParser implements CatalogueParser<SpDataTypeDto> {
    @Override
    public Catalogue getCatalogue() {
        return Catalogue.SP_DATA_TYPE;
    }

    @Override
    public SpDataTypeDto parse(pk pk, Data data) {
        return SpDataTypeDto.builder()
                .id(AdapterUtils.sequenceToString(pk.getLineId()))
                .code(getAttrIntegerValueByName(data.getLineAttributes(), "code"))
                .abbr(getAttrStringValueByName(data.getLineAttributes(), "abbr"))
                .name(getAttrStringValueByName(data.getLineAttributes(), "name"))
                .dateBegin(getBeginDate(data.getProperties()))
                .dateEnd(getEndDate(data.getProperties()))
                .build();
    }
}
