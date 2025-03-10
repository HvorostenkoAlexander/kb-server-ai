package com.nlmk.kb.server.service.zifra.parsers;

import com.nlmk.attestation.product.api.nsi.SpOmDataTypeDto;
import com.nlmk.kb.server.service.zifra.Catalogue;
import com.nlmk.kb.server.service.zifra.CatalogueParser;
import com.nlmk.kb.server.util.AdapterUtils;
import nlmk.l3.nsi.zifra.Data;
import nlmk.l3.nsi.zifra.pk;
import org.springframework.stereotype.Component;

@Component
public class SpOmDataTypeParser implements CatalogueParser<SpOmDataTypeDto> {

    @Override
    public Catalogue getCatalogue() {
        return Catalogue.SP_OM_DATA_TYPE;
    }

    @Override
    public SpOmDataTypeDto parse(pk pk, Data data, Long messageId) {
        return SpOmDataTypeDto.builder()
                .id(AdapterUtils.sequenceToString(pk.getLineId()))
                .messageId(messageId)
                .dataTypePhysicalName(getAttrStringValueByName(data.getLineAttributes(), "dataTypePhysicalName"))
                .dataTypePhysicalId(getAttrStringValueByName(data.getLineAttributes(), "dataTypePhysicalId"))
                .dataTypeLogicalName(getAttrStringValueByName(data.getLineAttributes(), "dataTypeLogicalName"))
                .dataTypeLogicalId(getAttrStringValueByName(data.getLineAttributes(), "dataTypeLogicalId"))
                .dateBegin(getBeginDate(data.getProperties()))
                .dateEnd(getEndDate(data.getProperties()))
                .build();
    }
}
