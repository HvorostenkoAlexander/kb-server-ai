package com.nlmk.kb.server.service.zifra.parsers;

import com.nlmk.attestation.product.api.nsi.SpMappingAttributeCcm3tDto;
import com.nlmk.kb.server.service.zifra.Catalogue;
import com.nlmk.kb.server.service.zifra.CatalogueParser;
import com.nlmk.kb.server.util.AdapterUtils;
import nlmk.l3.nsi.zifra.Data;
import nlmk.l3.nsi.zifra.pk;
import org.springframework.stereotype.Component;

@Component
public class SpMappingAttributeCcm3tParser implements CatalogueParser<SpMappingAttributeCcm3tDto> {
    @Override
    public Catalogue getCatalogue() {
        return Catalogue.SP_MAPPING_ATTRIBUTE_CCM3T;
    }

    @Override
    public SpMappingAttributeCcm3tDto parse(pk pk, Data data, Long messageId) {
        return SpMappingAttributeCcm3tDto.builder()
                .id(AdapterUtils.sequenceToString(pk.getLineId()))
                .messageId(messageId)
                .ssm3tCode(getAttrIntegerValueByName(data.getLineAttributes(), "ssm3tCode"))
                .attrId(getAttrStringValueByName(data.getLineAttributes(), "attrId"))
                .attrCode(getAttrIntegerValueByName(data.getLineAttributes(), "attrCode"))
                .ssm3tDataType(getAttrStringValueByName(data.getLineAttributes(), "ssm3tDataType"))
                .attrIsMapping(getAttrBooleanValueByName(data.getLineAttributes(), "attrIsMaping"))
                .dateBegin(getBeginDate(data.getProperties()))
                .dateEnd(getEndDate(data.getProperties()))
                .build();
    }
}
