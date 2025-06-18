package com.nlmk.kb.server.service.integral;

import com.nlmk.attestation.product.api.RequestSource;
import com.nlmk.kb.server.entity.integral.IntegralParamsMessage;
import com.nlmk.kb.server.entity.integral.IntegralParamsResponse;

public interface IntegralParamsMessageService {

    /**
     * Сохранение/обновление интегральных параметров
     */
    IntegralParamsMessage upsert(IntegralParamsResponse integralParamsResponse, RequestSource source);

}
