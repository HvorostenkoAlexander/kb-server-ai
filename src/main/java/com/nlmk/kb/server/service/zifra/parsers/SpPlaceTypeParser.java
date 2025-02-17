package com.nlmk.kb.server.service.zifra.parsers;

import com.nlmk.attestation.product.api.nsi.SpPlaceTypeDto;
import com.nlmk.kb.server.service.zifra.Catalogue;
import com.nlmk.kb.server.service.zifra.CatalogueParser;
import com.nlmk.kb.server.util.AdapterUtils;
import nlmk.l3.nsi.zifra.Data;
import nlmk.l3.nsi.zifra.pk;
import org.springframework.stereotype.Component;

@Component
public class SpPlaceTypeParser implements CatalogueParser<SpPlaceTypeDto> {

    @Override
    public Catalogue getCatalogue() {
        return Catalogue.SP_PLACE_TYPE;
    }

    @Override
    public SpPlaceTypeDto parse(pk pk, Data data) {
        return SpPlaceTypeDto.builder()
                .id(AdapterUtils.sequenceToString(pk.getLineId()))
                .code(getAttrIntegerValueByName(data.getLineAttributes(), "code"))
                .shName(getAttrStringValueByName(data.getLineAttributes(), "shName"))
                .name(getAttrStringValueByName(data.getLineAttributes(), "name"))
                .active(getAttrBooleanValueByName(data.getLineAttributes(), "active"))
                .dateBegin(getBeginDate(data.getProperties()))
                .dateEnd(getEndDate(data.getProperties()))
                .build();
    }
}
