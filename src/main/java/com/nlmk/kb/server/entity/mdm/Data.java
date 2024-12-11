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
public class Data implements Serializable {

    private static final long serialVersionUID = 4709471410470870853L;

    private CharSequence catalogId;
    private CharSequence catalogCode;
    private List<CharSequence> hashtagLine = new ArrayList<>();
    private List<CharSequence> hashtagCatalog = new ArrayList<>();

    private Properties properties;
    private List<LineAttributesRecord> lineAttributesRecords = new ArrayList<>();

    public void addHashtagLine(CharSequence line) {
        if (line != null) {
            this.hashtagLine.add(line);
        }
    }

    public void addHashtagCatalog(CharSequence catalog) {
        if (catalog != null) {
            this.hashtagCatalog.add(catalog);
        }
    }

    public void addLineAttributeRecord(LineAttributesRecord lineAttributeRecord) {
        if (lineAttributeRecord != null) {
            lineAttributesRecords.add(lineAttributeRecord);
        }
    }

}
