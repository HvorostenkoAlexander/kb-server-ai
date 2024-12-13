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

    private String attrCode;
    private String attrName;
    private String attrType;
    private String attrValue;
    private String attrNameEng;
    private List<String> hashtagColumns = new ArrayList<>();

    public void addHashtagColumn(String hashtagColumn) {
        if (hashtagColumn != null) {
            hashtagColumns.add(hashtagColumn);
        }
    }

}
