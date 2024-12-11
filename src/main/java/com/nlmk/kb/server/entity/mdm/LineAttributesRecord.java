package com.nlmk.kb.server.entity.mdm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Builder
@lombok.Data
@NoArgsConstructor
@AllArgsConstructor
public class LineAttributesRecord implements Serializable {

    private static final long serialVersionUID = 8442300465558762251L;

    private CharSequence attrCode;
    private CharSequence attrName;
    private CharSequence attrType;
    private CharSequence attrValue;
    private CharSequence attrNameEng;
    private List<CharSequence> hashtagColumns = new ArrayList<>();

    public void addHashtagColumn(CharSequence hashtagColumn) {
        if (hashtagColumn != null) {
            hashtagColumns.add(hashtagColumn);
        }
    }

}
