package com.nlmk.kb.server.service;

import com.nlmk.attestation.product.api.RequestSource;
import com.nlmk.attestation.product.api.pam.IntegralParam;
import com.nlmk.kb.server.api.IntegralParamsRequest;
import com.nlmk.kb.server.entity.integral.IntegralParams;
import com.nlmk.kb.server.service.ccm.KafkaRequestAdapter;
import com.nlmk.kb.server.service.integral.IntegralParamsMessageService;
import com.nlmk.kb.server.service.sender.PgpSender;
import lombok.RequiredArgsConstructor;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public abstract class CommonKafkaRequestAdapter<T> implements KafkaRequestAdapter<T> {

    private final PgpSender pgpSender;
    private final IntegralParamsMessageService integralParamsMessageService;
    protected List<IntegralParam> processIntegralParams(String ids, List<IntegralParams> integralParams, RequestSource source) {
        var integralParamsRequest = IntegralParamsRequest.builder()
                .materialIds(List.of(ids))
                .integralParameters(integralParams)
                .build();

        var responses = pgpSender.getIntegralParams(integralParamsRequest);
        if (CollectionUtils.isEmpty(responses)) {
            return Collections.emptyList();
        }

        var paramsMessage = integralParamsMessageService.upsert(responses.get(0), source);
        var response = paramsMessage.getResponse();
        if (ObjectUtils.isEmpty(response)) {
            return Collections.emptyList();
        }

        return response.getIntegralParameters().stream()
                .map(this::toIntegralPam)
                .collect(Collectors.toList());
    }

    private IntegralParam toIntegralPam(IntegralParams integralParams) {
        return IntegralParam.builder()
                .attrId(integralParams.getAttrId())
                .attrCode(integralParams.getAttrCode())
                .attrValue(integralParams.getAttrValue())
                .build();
    }

}
