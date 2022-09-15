package com.nlmk.kb.server.service.result.sending;

import com.fasterxml.jackson.databind.JsonNode;
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
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Collections;

import static com.nlmk.kb.server.config.KbConstants.*;

@Slf4j
@Service
public class ResultSenderImpl implements ResultSenderPgp, ResultSenderPts {

    private final String kafkaHttpProxyAddress;
    private final KafkaRestMessageAdapter kafkaRestMessageAdapter;
    private final WebClient webClient;
    private final int webClientTimeout;

    private final RestTemplate restTemplate;
    private static final Long DEFAULT_CONNECT_TIMEOUT = 30000L;
    private static final Long DEFAULT_READ_TIMEOUT = 30000L;

    public ResultSenderImpl(@Value("${service-web-client.kafka-rest.address}") String kafkaHttpProxyAddress,
                            @Value("${service-web-client.kafka-rest.login}") String kafkaHttpProxyLogin,
                            @Value("${service-web-client.kafka-rest.password}") String kafkaHttpProxyPassword,
                            @Value("${service-web-client.timeout:5000}") int timeout,
                            @Qualifier("basicAuthWebClient") WebClient webClient,
                            KafkaRestMessageAdapter kafkaRestMessageAdapter,
                            RestTemplateBuilder restTemplateBuilder) {
        this.webClient = webClient;
        this.webClientTimeout = timeout;
        this.kafkaHttpProxyAddress = kafkaHttpProxyAddress;
        this.kafkaRestMessageAdapter = kafkaRestMessageAdapter;

        if (org.springframework.util.StringUtils.hasText(kafkaHttpProxyLogin)
                && org.springframework.util.StringUtils.hasText(kafkaHttpProxyPassword)) {
            restTemplateBuilder = restTemplateBuilder.basicAuthentication(kafkaHttpProxyLogin, kafkaHttpProxyPassword);
        }

        restTemplate = restTemplateBuilder
                .messageConverters(new MappingJackson2HttpMessageConverter())
                .setConnectTimeout(Duration.ofMillis(DEFAULT_CONNECT_TIMEOUT))
                .setReadTimeout(Duration.ofMillis(DEFAULT_READ_TIMEOUT))
                .build();
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

    private void sendingRestTemplate(MessagesBatchDto batchDto, String topic) {
        try {
            ResponseEntity<JsonNode> resp = restTemplate.exchange(
                    String.format(KAFKA_REST_PROXY_TEMPLATE, kafkaHttpProxyAddress, topic),
                    HttpMethod.POST,
                    buildHttpEntity(batchDto),
                    JsonNode.class
            );

            log.info("sending, response from KAFKA: [{}]", resp);
        } catch (Exception e) {
            log.error("sending, sending error: [{}]", e.getMessage());
            throw new KafkaRestException(e);
        }
    }

    private HttpEntity<MessagesBatchDto> buildHttpEntity(MessagesBatchDto batchDto) {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.put(HttpHeaders.ACCEPT, Collections.singletonList(KAFKA_REST_ACCEPT_HEADER));
        headers.put(HttpHeaders.CONTENT_TYPE, Collections.singletonList(KAFKA_REST_CONTENT_TYPE_HEADER));
        return new HttpEntity<>(batchDto, headers);
    }

}
