package com.nlmk.kb.server.service.zifra.parsers;

import com.nlmk.attestation.product.api.nsi.SpSamplingTopologyDto;
import com.nlmk.kb.server.service.zifra.Catalogue;
import com.nlmk.kb.server.service.zifra.CatalogueParser;
import com.nlmk.kb.server.util.AdapterUtils;
import nlmk.l3.nsi.zifra.Data;
import nlmk.l3.nsi.zifra.pk;
import org.springframework.stereotype.Component;

@Component
public class SpSamplingTopologyParser implements CatalogueParser<SpSamplingTopologyDto> {
    @Override
    public Catalogue getCatalogue() {
        return Catalogue.SP_SAMPLING_TOPOLOGY;
    }

    @Override
    public SpSamplingTopologyDto parse(pk pk, Data data, Long messageId) {
        return SpSamplingTopologyDto.builder()
                .id(AdapterUtils.sequenceToString(pk.getLineId()))
                .messageId(messageId)
                .code(getAttrIntegerValueByName(data.getLineAttributes(), "code"))
                .name(getAttrStringValueByName(data.getLineAttributes(), "name"))
                .hierarchyScopeDirectory(getAttrStringValueByName(data.getLineAttributes(), "hierarchyScopeDirectory"))
                .dateBegin(getBeginDate(data.getProperties()))
                .dateEnd(getEndDate(data.getProperties()))
                .build();
    }
}
