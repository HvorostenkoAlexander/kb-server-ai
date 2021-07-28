package com.nlmk.kb.server.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpecDto {
    private int specCode;
    private String specName;
    private int specTypeCode;
    private String specValue;
    private String specMeasure;
}
