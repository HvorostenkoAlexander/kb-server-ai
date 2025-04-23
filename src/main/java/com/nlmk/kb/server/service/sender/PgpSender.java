package com.nlmk.kb.server.service.sender;

import com.nlmk.kb.server.api.IntegralParamsRequest;
import com.nlmk.kb.server.entity.integral.IntegralParamsResponse;

public interface PgpSender {

    /**
     * Получение интегральных параметров материала
     */
    IntegralParamsResponse getIntegralParams(IntegralParamsRequest integralParamsRequest);

}
