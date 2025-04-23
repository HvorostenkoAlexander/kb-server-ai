package com.nlmk.kb.server.api;

import com.nlmk.kb.server.entity.integral.IntegralParams;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IntegralParamsRequest {

    private List<String> metalUnitId;
    private List<IntegralParams> integralParameters;

}
