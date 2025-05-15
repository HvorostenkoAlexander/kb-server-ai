package com.nlmk.kb.server.entity.integral;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IntegralParams {

    private UUID attrId;
    private Integer attrCode;
    private String attrValue;

    public IntegralParams(Integer attrCode) {
        this.attrCode = attrCode;
    }

}
