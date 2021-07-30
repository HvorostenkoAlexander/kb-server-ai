package com.nlmk.kb.server.service.impl.creators;

import com.nlmk.kb.server.entity.pdm.PdmDictionary;
import com.nlmk.kb.server.entity.pdm.Spec;
import com.nlmk.kb.server.service.CommonConverter;
import com.nlmk.kb.server.service.PdmDictionaryCreator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import nlmk.l3.pdm.Data;
import nlmk.l3.pdm.Pk;
import nlmk.l3.pdm.opEnum;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PdmDictionaryCreatorImpl implements PdmDictionaryCreator {

    private final CommonConverter commonConverter;

    @Override
    public PdmDictionary createPdmDictionary(CharSequence ts,
                                             opEnum op,
                                             Pk pk,
                                             Data data) {
        val pdmDictionaryBuilder = PdmDictionary.builder()
                .op(op.name())
                .pk(
                        fromPk(pk)
                )
                .data(
                        fromData(data)
                );
        if (ts != null) {
            pdmDictionaryBuilder.ts(
                    commonConverter.parseToDate(ts.toString())
            );
        }
        return pdmDictionaryBuilder.build();
    }

    private com.nlmk.kb.server.entity.pdm.Pk fromPk(nlmk.l3.pdm.Pk pdmPk) {
        val pkBuilder = com.nlmk.kb.server.entity.pdm.Pk.builder();
        if (pdmPk.getId() != null) {
            pkBuilder.Id(pdmPk.getId().toString());
        }
        if (pdmPk.getSystemCode() != null) {
            pkBuilder.systemCode(pdmPk.getSystemCode().toString());
        }
        if (pdmPk.getDirectoryId() != null) {
            pkBuilder.directoryId(pdmPk.getDirectoryId().toString());
        }
        return pkBuilder.build();
    }

    private com.nlmk.kb.server.entity.pdm.Data fromData(nlmk.l3.pdm.Data pdmData) {
        com.nlmk.kb.server.entity.pdm.Data data = new com.nlmk.kb.server.entity.pdm.Data();
        pdmData.getSpecifications().forEach(
                s -> data.addSpec(
                        fromSpec(s)
                )
        );
        return data;
    }

    private Spec fromSpec(nlmk.l3.pdm.Spec pdmSpec) {
        val specBuilder = Spec.builder()
                .specCode(pdmSpec.getSpecCode())
                .specTypeCode(pdmSpec.getSpecTypeCode());

        if (pdmSpec.getSpecName() != null) {
            specBuilder.specName(pdmSpec.getSpecName().toString());
        }

        if (pdmSpec.getSpecMeasure() != null) {
            specBuilder.specMeasure(pdmSpec.getSpecMeasure().toString());
        }

        if (pdmSpec.getSpecValue() != null) {
            specBuilder.specValue(pdmSpec.getSpecValue().toString());
        }
        return specBuilder.build();
    }
}
