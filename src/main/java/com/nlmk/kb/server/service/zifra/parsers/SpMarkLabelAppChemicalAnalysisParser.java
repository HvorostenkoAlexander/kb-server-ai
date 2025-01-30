package com.nlmk.kb.server.service.zifra.parsers;

import com.nlmk.attestation.product.api.nsi.SpMarkLabelAppChemicalAnalysisDto;
import com.nlmk.kb.server.service.zifra.Catalogue;
import com.nlmk.kb.server.service.zifra.CatalogueParser;
import com.nlmk.kb.server.util.AdapterUtils;
import nlmk.l3.nsi.zifra.Data;
import nlmk.l3.nsi.zifra.pk;
import org.springframework.stereotype.Component;

@Component
public class SpMarkLabelAppChemicalAnalysisParser implements CatalogueParser<SpMarkLabelAppChemicalAnalysisDto> {
    @Override
    public Catalogue getCatalogue() {
        return Catalogue.SP_MARK_LABEL_APP_CHEMICAL_ANALYSIS;
    }

    @Override
    public SpMarkLabelAppChemicalAnalysisDto parse(pk pk, Data data) {
        return SpMarkLabelAppChemicalAnalysisDto.builder()
                .id(AdapterUtils.sequenceToString(pk.getLineId()))
                .name(getAttrStringValueByName(data.getLineAttributes(), "name"))
                .shName(getAttrStringValueByName(data.getLineAttributes(), "shName"))
                .signActive(getAttrBooleanValueByName(data.getLineAttributes(), "signActive"))
                .dateBegin(getBeginDate(data.getProperties()))
                .dateEnd(getEndDate(data.getProperties()))
                .build();
    }
}
