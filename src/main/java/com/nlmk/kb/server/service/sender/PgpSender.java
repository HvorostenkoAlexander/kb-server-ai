package com.nlmk.kb.server.service.sender;

import com.nlmk.kb.server.api.IntegralParamsRequest;
import com.nlmk.kb.server.entity.integral.IntegralParamsResponse;

import java.util.List;

public interface PgpSender {

    /**
     * Получение интегральных параметров материала
     */
    List<IntegralParamsResponse> getIntegralParams(IntegralParamsRequest integralParamsRequest);

}
