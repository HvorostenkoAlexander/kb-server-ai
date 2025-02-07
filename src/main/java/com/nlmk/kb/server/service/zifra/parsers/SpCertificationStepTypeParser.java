package com.nlmk.kb.server.service.zifra.parsers;

import com.nlmk.attestation.product.api.nsi.SpCertificationStepTypeDto;
import com.nlmk.kb.server.service.zifra.Catalogue;
import com.nlmk.kb.server.service.zifra.CatalogueParser;
import com.nlmk.kb.server.util.AdapterUtils;
import nlmk.l3.nsi.zifra.Data;
import nlmk.l3.nsi.zifra.pk;
import org.springframework.stereotype.Component;

@Component
public class SpCertificationStepTypeParser implements CatalogueParser<SpCertificationStepTypeDto> {
    @Override
    public Catalogue getCatalogue() {
        return Catalogue.SP_CERTIFICATION_STEP_TYPE;
    }

    @Override
    public SpCertificationStepTypeDto parse(pk pk, Data data) {
        return SpCertificationStepTypeDto.builder()
                .id(AdapterUtils.sequenceToString(pk.getLineId()))
                .code(getAttrIntegerValueByName(data.getLineAttributes(), "code"))
                .name(getAttrStringValueByName(data.getLineAttributes(), "name"))
                .shName(getAttrStringValueByName(data.getLineAttributes(), "shName"))
                .dateBegin(getBeginDate(data.getProperties()))
                .dateEnd(getEndDate(data.getProperties()))
                .build();
    }
}
