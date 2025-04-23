package com.nlmk.kb.server.service.sender;

import com.nlmk.kb.server.api.IntegralParamsRequest;
import com.nlmk.kb.server.entity.integral.IntegralParamsResponse;
import com.nlmk.kb.server.exception.RemoteServiceSenderException;
import com.nlmk.kb.server.util.SenderUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Slf4j
@Component
public class PgpSenderImpl implements PgpSender {

    private final WebClient pgpWebClient;
    private final int webClientTimeout;
    private final String pgpIntegralParamsUrl;

    public PgpSenderImpl(@Value("${service-web-client.pgp-server.url}") String pgpUrl,
                         @Value("${service-web-client.timeout:5000}") int timeout,
                         @Qualifier("defaultWebClient") WebClient pgpWebClient
    ) {
        this.pgpWebClient = pgpWebClient;
        this.webClientTimeout = timeout;
        this.pgpIntegralParamsUrl = pgpUrl + "/nlmk-mes-cgp-public-api/v1/apcs/integral-params-stan2000";
    }

    @Override
    public IntegralParamsResponse getIntegralParams(IntegralParamsRequest integralParamsRequest) {
        if (integralParamsRequest == null
                || CollectionUtils.isEmpty(integralParamsRequest.getMetalUnitId())
                || CollectionUtils.isEmpty(integralParamsRequest.getIntegralParameters())) {
            throw new RemoteServiceSenderException("PgpSender, IntegralParamsRequest is NULL");
        }

        var metalUtilId = integralParamsRequest.getMetalUnitId().get(0);

        final var response = pgpWebClient.post()
                .uri(pgpIntegralParamsUrl)
                .accept(MediaType.APPLICATION_JSON)
                .acceptCharset(StandardCharsets.UTF_8)
                .headers(SenderUtils::addRequestId)
                .bodyValue(integralParamsRequest)
                .retrieve()
                .bodyToMono(IntegralParamsResponse.class)
                .timeout(Duration.ofMillis(webClientTimeout))
                .onErrorResume(e -> Mono.error(
                        new RemoteServiceSenderException(String.format("PgpSender, getIntegralParams, metalUnitId [%s], send error, message [%s]",
                                metalUtilId, e.getMessage()))
                ))
                .block();

        log.info("PgpSender, getIntegralParams, metalUtilId [{}], PGP response [{}]", metalUtilId, response);

        return response;
    }

}
