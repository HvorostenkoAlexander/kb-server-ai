package com.nlmk.kb.server.service.zifra.parsers;

import com.nlmk.attestation.product.api.nsi.SpPlaceDto;
import com.nlmk.kb.server.service.zifra.Catalogue;
import com.nlmk.kb.server.service.zifra.CatalogueParser;
import com.nlmk.kb.server.util.AdapterUtils;
import nlmk.l3.nsi.zifra.Data;
import nlmk.l3.nsi.zifra.pk;
import org.springframework.stereotype.Component;

@Component
public class SpPlaceParser implements CatalogueParser<SpPlaceDto> {
    @Override
    public Catalogue getCatalogue() {
        return Catalogue.SP_PLACE;
    }

    @Override
    public SpPlaceDto parse(pk pk, Data data, Long messageId) {
        return SpPlaceDto.builder()
                .id(AdapterUtils.sequenceToString(pk.getLineId()))
                .messageId(messageId)
                .shName(getAttrStringValueByName(data.getLineAttributes(), "shName"))
                .name(getAttrStringValueByName(data.getLineAttributes(), "name"))
                .placeType(getAttrStringValueByName(data.getLineAttributes(), "placeType"))
                .placeCode(getAttrStringValueByName(data.getLineAttributes(), "placeCode"))
                .code(getAttrIntegerValueByName(data.getLineAttributes(), "code"))
                .parent(getAttrStringValueByName(data.getLineAttributes(), "parent"))
                .sapPmId(getAttrStringValueByName(data.getLineAttributes(), "sapPmId"))
                .sapPpId(getAttrStringValueByName(data.getLineAttributes(), "sapPpId"))
                .sapMmId(getAttrStringValueByName(data.getLineAttributes(), "sapMmId"))
                .sapHcmId(getAttrStringValueByName(data.getLineAttributes(), "sapHcmId"))
                .unitIdSpep2(getAttrIntegerValueByName(data.getLineAttributes(), "unitIdSPEP2"))
                .oneCId(getAttrStringValueByName(data.getLineAttributes(), "1cId"))
                .fict(getAttrBooleanValueByName(data.getLineAttributes(), "fict"))
                .active(getActive(data.getLineAttributes()))
                .dateBegin(getBeginDate(data.getProperties()))
                .dateEnd(getEndDate(data.getProperties()))
                .build();
    }
}
