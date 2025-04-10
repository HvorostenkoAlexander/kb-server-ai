package com.nlmk.kb.server.entity.integral;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IntegralParamsResponse {

    private String metalUnitId;
    private List<IntegralParams> integralParameters;

}
