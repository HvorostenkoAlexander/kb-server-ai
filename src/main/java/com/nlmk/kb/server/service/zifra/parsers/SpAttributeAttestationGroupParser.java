package com.nlmk.kb.server.service.zifra.parsers;

import com.nlmk.attestation.product.api.nsi.SpAttributeAttestationGroupDto;
import com.nlmk.kb.server.service.zifra.Catalogue;
import com.nlmk.kb.server.service.zifra.CatalogueParser;
import com.nlmk.kb.server.util.AdapterUtils;
import nlmk.l3.nsi.zifra.Data;
import nlmk.l3.nsi.zifra.pk;
import org.springframework.stereotype.Component;

@Component
public class SpAttributeAttestationGroupParser implements CatalogueParser<SpAttributeAttestationGroupDto> {
    @Override
    public Catalogue getCatalogue() {
        return Catalogue.SP_ATTRIBUTE_ATTESTATION_GROUP;
    }

    @Override
    public SpAttributeAttestationGroupDto parse(pk pk, Data data, Long messageId) {
        return SpAttributeAttestationGroupDto.builder()
                .id(AdapterUtils.sequenceToString(pk.getLineId()))
                .messageId(messageId)
                .code(getAttrIntegerValueByName(data.getLineAttributes(), "code"))
                .name(getAttrStringValueByName(data.getLineAttributes(), "name"))
                .shName(getAttrStringValueByName(data.getLineAttributes(), "shName"))
                .dateBegin(getBeginDate(data.getProperties()))
                .dateEnd(getEndDate(data.getProperties()))
                .build();
    }
}
