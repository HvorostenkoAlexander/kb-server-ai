package com.nlmk.kb.server.service.zifra;

import com.nlmk.kb.server.entity.mdm.LineAttributesRecord;
import com.nlmk.kb.server.entity.mdm.MdmDictionary;
import com.nlmk.kb.server.entity.mdm.Pk;
import com.nlmk.kb.server.entity.mdm.Properties;
import com.nlmk.kb.server.service.CommonConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nlmk.l3.nsi.zifra.Data;
import nlmk.l3.nsi.zifra.EnumOp;
import nlmk.l3.nsi.zifra.lineAttributes_record;
import nlmk.l3.nsi.zifra.pk;
import nlmk.l3.nsi.zifra.properties;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MdmDictionaryCreatorImpl implements MdmDictionaryCreator {

    private final CommonConverter commonConverter;

    @Override
    public MdmDictionary createMdmDictionary(CharSequence ts, EnumOp op, pk pk, Data data) {
        MdmDictionary mdmDictionary = new MdmDictionary();
        mdmDictionary.setOp(op.name());
        mdmDictionary.setPk(fromPk(pk));
        mdmDictionary.setData(fromData(data));
        if (ts != null) {
            mdmDictionary.setTs(
                    commonConverter.parseToDate(ts.toString())
            );
        }
        return mdmDictionary;
    }

    private Pk fromPk(pk pk) {
        return Pk.builder()
                .systemCode(stringFromCharSeq(pk.getSystemCode()))
                .lineId(stringFromCharSeq(pk.getLineId()))
                .build();
    }

    private com.nlmk.kb.server.entity.mdm.Data fromData(Data mdmData) {
        com.nlmk.kb.server.entity.mdm.Data data = new com.nlmk.kb.server.entity.mdm.Data();
        data.setCatalogId(stringFromCharSeq(mdmData.getCatalogId()));
        data.setCatalogCode(stringFromCharSeq(mdmData.getCatalogCode()));
        if (mdmData.getHashtagLine() != null) {
            mdmData.getHashtagLine().forEach(
                    hashtagLine -> {
                        if (hashtagLine != null) {
                            data.addHashtagLine(stringFromCharSeq(hashtagLine));
                        }
                    }
            );
        }
        if (mdmData.getHashtagCatalog() != null) {
            mdmData.getHashtagCatalog().forEach(
                    hashtagCatalog -> {
                        if (hashtagCatalog != null) {
                            data.addHashtagCatalog(stringFromCharSeq(hashtagCatalog));
                        }
                    }
            );
        }
        data.setProperties(fromProperties(mdmData.getProperties()));
        mdmData.getLineAttributes().forEach(
                lineAttributesRecord ->
                        data.addLineAttributeRecord(fromLineAttributesRecord(lineAttributesRecord))
        );
        return data;
    }

    private Properties fromProperties(properties mdmProperties) {
        return Properties.builder()
                .cron(stringFromCharSeq(mdmProperties.getCron()))
                .dateEnd(stringFromCharSeq(mdmProperties.getDateEnd()))
                .dateBegin(stringFromCharSeq(mdmProperties.getDateBegin()))
                .dateChange(stringFromCharSeq(mdmProperties.getDateChange()))
                .build();
    }

    private LineAttributesRecord fromLineAttributesRecord(lineAttributes_record lineAttributeRecord) {
        LineAttributesRecord lineAttributesRecord = new LineAttributesRecord();
        lineAttributesRecord.setAttrCode(stringFromCharSeq(lineAttributeRecord.getAttrCode()));
        lineAttributesRecord.setAttrName(stringFromCharSeq(lineAttributeRecord.getAttrName()));
        lineAttributesRecord.setAttrType(stringFromCharSeq(lineAttributeRecord.getAttrType()));
        lineAttributesRecord.setAttrValue(stringFromCharSeq(lineAttributeRecord.getAttrValue()));
        lineAttributesRecord.setAttrNameEng(stringFromCharSeq(lineAttributeRecord.getAttrNameEng()));
        if (lineAttributeRecord.getHashtagColumn() != null) {
            lineAttributeRecord.getHashtagColumn().forEach(
                    hashtagColumn -> {
                        if (hashtagColumn != null) {
                            lineAttributesRecord.addHashtagColumn(stringFromCharSeq(hashtagColumn));
                        }
                    }
            );
        }
        return lineAttributesRecord;
    }

    private String stringFromCharSeq(CharSequence charSequence) {
        if (charSequence != null) {
            return charSequence.toString();
        }
        return null;
    }

}
