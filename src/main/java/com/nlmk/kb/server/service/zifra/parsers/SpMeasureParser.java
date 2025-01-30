package com.nlmk.kb.server.service.zifra.parsers;

import com.nlmk.attestation.product.api.nsi.SpMeasureDto;
import com.nlmk.kb.server.service.zifra.Catalogue;
import com.nlmk.kb.server.service.zifra.CatalogueParser;
import com.nlmk.kb.server.util.AdapterUtils;
import nlmk.l3.nsi.zifra.Data;
import nlmk.l3.nsi.zifra.pk;
import org.springframework.stereotype.Component;

@Component
public class SpMeasureParser implements CatalogueParser<SpMeasureDto> {
    @Override
    public Catalogue getCatalogue() {
        return Catalogue.SP_MEASURE;
    }

    @Override
    public SpMeasureDto parse(pk pk, Data data) {
        return SpMeasureDto.builder()
                .id(AdapterUtils.sequenceToString(pk.getLineId()))
                .code(getAttrStringValueByName(data.getLineAttributes(), "code"))
                .unitId(getAttrStringValueByName(data.getLineAttributes(), "unitId"))
                .dimension(getAttrStringValueByName(data.getLineAttributes(), "dimension"))
                .name(getAttrStringValueByName(data.getLineAttributes(), "name"))
                .abbr(getAttrStringValueByName(data.getLineAttributes(), "abbr"))
                .nameEng(getAttrStringValueByName(data.getLineAttributes(), "nameEng"))
                .abbrEng(getAttrStringValueByName(data.getLineAttributes(), "abbrEng"))
                .coefficient(getAttrIntegerValueByName(data.getLineAttributes(), "coefficient"))
                .description(getAttrStringValueByName(data.getLineAttributes(), "description"))
                .codeIso(getAttrStringValueByName(data.getLineAttributes(), "codeISO"))
                .dateBegin(getBeginDate(data.getProperties()))
                .dateEnd(getEndDate(data.getProperties()))
                .build();
    }
}
