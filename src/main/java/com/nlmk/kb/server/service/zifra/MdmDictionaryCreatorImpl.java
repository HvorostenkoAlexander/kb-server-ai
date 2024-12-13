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
                .systemCode(pk.getSystemCode().toString())
                .lineId(pk.getLineId().toString())
                .build();
    }

    private com.nlmk.kb.server.entity.mdm.Data fromData(Data mdmData) {
        com.nlmk.kb.server.entity.mdm.Data data = new com.nlmk.kb.server.entity.mdm.Data();
        data.setCatalogId(mdmData.getCatalogId().toString());
        data.setCatalogCode(mdmData.getCatalogCode().toString());
        if (mdmData.getHashtagLine() != null) {
            mdmData.getHashtagLine().forEach(
                    hashtagLine -> {
                        if (hashtagLine != null) {
                            data.addHashtagLine(hashtagLine.toString());
                        }
                    }
            );
        }
        if (mdmData.getHashtagCatalog() != null) {
            mdmData.getHashtagCatalog().forEach(
                    hashtagCatalog -> {
                        if (hashtagCatalog != null) {
                            data.addHashtagCatalog(hashtagCatalog.toString());
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
                .cron(mdmProperties.getCron())
                .dateEnd(mdmProperties.getDateEnd())
                .dateBegin(mdmProperties.getDateBegin())
                .dateChange(mdmProperties.getDateChange())
                .build();
    }

    private LineAttributesRecord fromLineAttributesRecord(lineAttributes_record lineAttributeRecord) {
        LineAttributesRecord lineAttributesRecord = new LineAttributesRecord();
        lineAttributesRecord.setAttrCode(lineAttributeRecord.getAttrCode().toString());
        lineAttributesRecord.setAttrName(lineAttributeRecord.getAttrName().toString());
        lineAttributesRecord.setAttrType(lineAttributeRecord.getAttrType().toString());
        lineAttributesRecord.setAttrValue(lineAttributeRecord.getAttrValue().toString());
        lineAttributesRecord.setAttrNameEng(lineAttributeRecord.getAttrNameEng().toString());
        if (lineAttributeRecord.getHashtagColumn() != null) {
            lineAttributeRecord.getHashtagColumn().forEach(
                    hashtagColumn -> {
                        if (hashtagColumn != null) {
                            lineAttributesRecord.addHashtagColumn(hashtagColumn.toString());
                        }
                    }
            );
        }
        return lineAttributesRecord;
    }



}
