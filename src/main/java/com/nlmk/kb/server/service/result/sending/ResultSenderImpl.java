package com.nlmk.kb.server.service.result.sending;

import com.nlmk.kb.server.api.MessagesBatchDto;
import com.nlmk.kb.server.entity.KafkaMessageKey;
import com.nlmk.kb.server.exception.KafkaRestConfigException;
import com.nlmk.kb.server.exception.KafkaRestException;
import com.nlmk.kb.server.exception.AttestationResultSenderException;
import com.nlmk.kb.server.service.result.sending.pgp.ResultSenderPgp;
import com.nlmk.kb.server.service.result.sending.pts.ResultSenderPts;
import lombok.extern.slf4j.Slf4j;
import nlmk.l3.apcs.RecordPk;
import nlmk.l3.apcs.VerificationResults;
import nlmk.l3.apcs.VerificationResultsPts;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

import static com.nlmk.kb.server.config.KbConstants.*;

@Slf4j
@Service
public class ResultSenderImpl implements ResultSenderPgp, ResultSenderPts {

    private final String kafkaHttpProxyAddress;
    private final KafkaRestMessageAdapter kafkaRestMessageAdapter;
    private final WebClient webClient;
    private final int webClientTimeout;

    public ResultSenderImpl(@Value("${service-web-client.kafka-rest.address}") String kafkaHttpProxyAddress,
                            @Value("${service-web-client.timeout:5000}") int timeout,
                            @Qualifier("basicAuthWebClient") WebClient webClient,
                            KafkaRestMessageAdapter kafkaRestMessageAdapter) {
        this.webClient = webClient;
        this.webClientTimeout = timeout;
        this.kafkaHttpProxyAddress = kafkaHttpProxyAddress;
        this.kafkaRestMessageAdapter = kafkaRestMessageAdapter;
    }

    private void checkBeforeSend(Object result, String topic) {
        if (result == null || StringUtils.isBlank(topic)) {
            throw new AttestationResultSenderException("checkBeforeSend, empty result OR topic");
        }
        if (StringUtils.isBlank(kafkaHttpProxyAddress)) {
            throw new KafkaRestConfigException("checkBeforeSend, kafka-rest.address is EMPTY, cancel sending");
        }
    }

    private KafkaMessageKey generateKey(RecordPk pk) {
        if (pk == null) {
            log.warn("generateKey, result.getPk() is null");
            throw new AttestationResultSenderException("generateKey, gen key error: result.getPk() is NULL");
        }

        final var key = StringUtils.joinWith("~", pk.getSystemCode(), pk.getId());
        final var schemaKey = "{\"type\": \"string\"}";

        return KafkaMessageKey.create(key, schemaKey);
    }

    @Override
    public void send(VerificationResults result, String topic) {
        checkBeforeSend(result, topic);

        final var key = generateKey(result.getPk());
        final var batchDto = kafkaRestMessageAdapter.adapt(result, key);

        log.info("send, VerificationResults, request to KAFKA: key [{}], value [{}]", key.getKey(), result);
        sending(batchDto, topic);
    }

    @Override
    public void send(VerificationResultsPts result, String topic) {
        checkBeforeSend(result, topic);

        final var key = generateKey(result.getPk());
        final var batchDto = kafkaRestMessageAdapter.adapt(result, key);

        log.info("send, VerificationResultsPts, request to KAFKA: key [{}], value [{}]", key.getKey(), result);
        sending(batchDto, topic);
    }

    private void sending(MessagesBatchDto batchDto, String topic) {
        final var response = webClient.post()
                .uri(String.format(KAFKA_REST_PROXY_TEMPLATE, kafkaHttpProxyAddress, topic))
                .acceptCharset(StandardCharsets.UTF_8)
                .header(HttpHeaders.ACCEPT, KAFKA_REST_ACCEPT_HEADER)
                .header(HttpHeaders.CONTENT_TYPE, KAFKA_REST_CONTENT_TYPE_HEADER)
                .bodyValue(batchDto)
                .retrieve()
                .bodyToMono(String.class)// vs JsonNode
                .timeout(Duration.ofMillis(webClientTimeout))
                .onErrorResume(e -> {
                    log.error("sending, sending error: [{}]", e.getMessage());
                    return Mono.error(new KafkaRestException(e));
                })
                .block();
        log.info("sending, response from KAFKA: [{}]", response);
    }

}
