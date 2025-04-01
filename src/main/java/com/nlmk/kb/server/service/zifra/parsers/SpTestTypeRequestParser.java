package com.nlmk.kb.server.service.zifra.parsers;

import com.nlmk.attestation.product.api.nsi.SpTestTypeRequestDto;
import com.nlmk.kb.server.service.zifra.Catalogue;
import com.nlmk.kb.server.service.zifra.CatalogueParser;
import com.nlmk.kb.server.util.AdapterUtils;
import nlmk.l3.nsi.zifra.Data;
import nlmk.l3.nsi.zifra.pk;
import org.springframework.stereotype.Component;

@Component
public class SpTestTypeRequestParser implements CatalogueParser<SpTestTypeRequestDto> {

    @Override
    public Catalogue getCatalogue() {
        return Catalogue.SP_TEST_TYPE_REQUEST;
    }

    @Override
    public SpTestTypeRequestDto parse(pk pk, Data data, Long messageId) {
        return SpTestTypeRequestDto.builder()
                .id(AdapterUtils.sequenceToString(pk.getLineId()))
                .messageId(messageId)
                .code(getAttrIntegerValueByName(data.getLineAttributes(), "code"))
                .requestType(getAttrStringValueByName(data.getLineAttributes(), "requestType"))
                .signActive(getAttrBooleanValueByName(data.getLineAttributes(), "signActive"))
                .dateBegin(getBeginDate(data.getProperties()))
                .dateEnd(getEndDate(data.getProperties()))
                .build();
    }

}
