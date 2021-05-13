package com.nlmk.kb.server.entity.pdm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PdmMessageDto {

    private Long id;
    private String topic;
    private Integer partition;
    private Long offset;
    private String key;
    private String ts;
    private String op;
    private String pk_Id;
    private String pk_systemCode;
    private String pk_directoryId;
    @Builder.Default
    private List<SpecDto> specifications = new ArrayList<>();

    public void addSpec(SpecDto spec) {
        if (spec != null)
            this.getSpecifications().add(spec);
    }
}
