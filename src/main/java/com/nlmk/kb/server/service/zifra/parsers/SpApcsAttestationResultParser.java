package com.nlmk.kb.server.service.zifra.parsers;

import com.nlmk.attestation.product.api.nsi.SpApcsAttestationResultDto;
import com.nlmk.kb.server.service.zifra.Catalogue;
import com.nlmk.kb.server.service.zifra.CatalogueParser;
import com.nlmk.kb.server.util.AdapterUtils;
import nlmk.l3.nsi.zifra.Data;
import nlmk.l3.nsi.zifra.pk;
import org.springframework.stereotype.Component;

@Component
public class SpApcsAttestationResultParser implements CatalogueParser<SpApcsAttestationResultDto> {

    @Override
    public Catalogue getCatalogue() {
        return Catalogue.SP_APCS_ATTESTATION_RESULT;
    }

    @Override
    public SpApcsAttestationResultDto parse(pk pk, Data data, Long messageId) {
        return SpApcsAttestationResultDto.builder()
                .id(AdapterUtils.sequenceToString(pk.getLineId()))
                .messageId(messageId)
                .code(getAttrIntegerValueByName(data.getLineAttributes(), "code"))
                .name(getAttrStringValueByName(data.getLineAttributes(), "name"))
                .signActive(getAttrBooleanValueByName(data.getLineAttributes(), "signActive"))
                .dateBegin(getBeginDate(data.getProperties()))
                .dateEnd(getEndDate(data.getProperties()))
                .build();
    }
}
