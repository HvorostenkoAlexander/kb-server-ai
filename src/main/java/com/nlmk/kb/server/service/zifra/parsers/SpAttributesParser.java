package com.nlmk.kb.server.service.zifra.parsers;

import com.nlmk.attestation.product.api.nsi.SpAttributesDto;
import com.nlmk.kb.server.service.zifra.Catalogue;
import com.nlmk.kb.server.service.zifra.CatalogueParser;
import com.nlmk.kb.server.util.AdapterUtils;
import nlmk.l3.nsi.zifra.Data;
import nlmk.l3.nsi.zifra.pk;
import org.springframework.stereotype.Component;

@Component
public class SpAttributesParser implements CatalogueParser<SpAttributesDto> {

    @Override
    public Catalogue getCatalogue() {
        return Catalogue.SP_ATTRIBUTES;
    }

    @Override
    public SpAttributesDto parse(pk pk, Data data, Long messageId) {
        return SpAttributesDto.builder()
                .id(AdapterUtils.sequenceToString(pk.getLineId()))
                .messageId(messageId)
                .code(getAttrIntegerValueByName(data.getLineAttributes(), "code"))
                .name(getAttrStringValueByName(data.getLineAttributes(), "name"))
                .shName(getAttrStringValueByName(data.getLineAttributes(), "shName"))
                .abbr(getAttrStringValueByName(data.getLineAttributes(), "abbr"))
                .directoryId(getAttrStringValueByName(data.getLineAttributes(), "directoryId"))
                .directoryField(getAttrStringValueByName(data.getLineAttributes(), "directoryField"))
                .active(getActive(data.getLineAttributes()))
                .measureId(getAttrStringValueByName(data.getLineAttributes(), "measureId"))
                .systemMes(getAttrStringValueByName(data.getLineAttributes(), "systemMES"))
                .note(getAttrStringValueByName(data.getLineAttributes(), "note"))
                .hierarchyScopeDirectory(getAttrStringValueByName(data.getLineAttributes(), "hierarchyScopeDirectory"))
                .dataType(getAttrStringValueByName(data.getLineAttributes(), "dataType"))
                .dataTypeId(getAttrStringValueByName(data.getLineAttributes(), "dataTypeId"))
                .signPublishResource(getAttrBooleanValueByName(data.getLineAttributes(), "signPublishResource"))
                .signException(getAttrBooleanValueByName(data.getLineAttributes(), "signException"))
                .dateBegin(getBeginDate(data.getProperties()))
                .dateEnd(getEndDate(data.getProperties()))
                .build();
    }
}
