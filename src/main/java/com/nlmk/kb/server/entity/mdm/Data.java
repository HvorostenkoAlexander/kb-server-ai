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

    private String catalogId;
    private String catalogCode;
    private List<String> hashtagLine = new ArrayList<>();
    private List<String> hashtagCatalog = new ArrayList<>();

    private Properties properties;
    private List<LineAttributesRecord> lineAttributesRecords = new ArrayList<>();

    public void addHashtagLine(String line) {
        if (line != null) {
            this.hashtagLine.add(line);
        }
    }

    public void addHashtagCatalog(String catalog) {
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
