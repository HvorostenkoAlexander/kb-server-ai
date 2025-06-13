package com.nlmk.kb.server.service.zifra;

import com.nlmk.kb.server.entity.mdm.LineAttributesRecord;
import com.nlmk.kb.server.entity.mdm.MdmDictionary;
import com.nlmk.kb.server.entity.mdm.Pk;
import com.nlmk.kb.server.entity.mdm.Properties;
import com.nlmk.kb.server.service.CommonConverter;
import java.util.ArrayList;
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


    public Data fromMdmData(com.nlmk.kb.server.entity.mdm.Data mdmData) {
        Data data = new Data();
        data.setCatalogId(stringToCharSeq(mdmData.getCatalogId()));
        data.setCatalogCode(stringToCharSeq(mdmData.getCatalogCode()));

        if (mdmData.getHashtagLine() != null) {
            mdmData.getHashtagLine().forEach(
                    hashtagLine -> {
                        if (hashtagLine != null) {
                            data.getHashtagLine().add(stringToCharSeq(hashtagLine));
                        }
                    }
            );
        }

        if (mdmData.getHashtagCatalog() != null) {
            mdmData.getHashtagCatalog().forEach(
                    hashtagCatalog -> {
                        if (hashtagCatalog != null) {
                            data.getHashtagCatalog().add(stringToCharSeq(hashtagCatalog));
                        }
                    }
            );
        }

        data.setProperties(fromProperties(mdmData.getProperties()));

        if (mdmData.getLineAttributesRecords() != null) {
            data.setLineAttributes(new ArrayList<>());
            mdmData.getLineAttributesRecords().forEach(
                    lineAttributesRecord ->
                            data.getLineAttributes().add(fromLineAttributesRecord(lineAttributesRecord))
            );
        }

        return data;
    }

    private properties fromProperties(Properties mdmProperties) {
        properties props = new properties();
        props.setCron(stringToCharSeq(mdmProperties.getCron()));
        props.setDateEnd(stringToCharSeq(mdmProperties.getDateEnd()));
        props.setDateBegin(stringToCharSeq(mdmProperties.getDateBegin()));
        props.setDateChange(stringToCharSeq(mdmProperties.getDateChange()));
        return props;
    }

    private lineAttributes_record fromLineAttributesRecord(LineAttributesRecord lineAttributeRecord) {
        lineAttributes_record lineAttributesRecord = new lineAttributes_record();
        lineAttributesRecord.setAttrCode(stringToCharSeq(lineAttributeRecord.getAttrCode()));
        lineAttributesRecord.setAttrName(stringToCharSeq(lineAttributeRecord.getAttrName()));
        lineAttributesRecord.setAttrType(stringToCharSeq(lineAttributeRecord.getAttrType()));
        lineAttributesRecord.setAttrValue(stringToCharSeq(lineAttributeRecord.getAttrValue()));
        lineAttributesRecord.setAttrNameEng(stringToCharSeq(lineAttributeRecord.getAttrNameEng()));

        if (lineAttributeRecord.getHashtagColumns() != null) {
            lineAttributeRecord.getHashtagColumns().forEach(
                    hashtagColumn -> {
                        if (hashtagColumn != null) {
                            lineAttributesRecord.getHashtagColumn().add(stringToCharSeq(hashtagColumn));
                        }
                    }
            );
        }

        return lineAttributesRecord;
    }

    private CharSequence stringToCharSeq(String str) {
        return str != null ? str : null;
    }

}
