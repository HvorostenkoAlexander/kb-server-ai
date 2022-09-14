package com.nlmk.kb.server.service.result.sending;

import com.fasterxml.jackson.databind.JsonNode;
import com.nlmk.kb.server.api.MessagesBatchDto;
import com.nlmk.kb.server.entity.KafkaMessageKey;
import com.nlmk.kb.server.exception.KafkaRestConfigException;
import com.nlmk.kb.server.exception.KafkaRestException;
import com.nlmk.kb.server.exception.AttestationResultSenderException;
import lombok.extern.slf4j.Slf4j;
import nlmk.l3.apcs.RecordPk;
import nlmk.l3.apcs.VerificationResults;
import nlmk.l3.apcs.VerificationResultsPts;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.Collections;

@Slf4j
@Service
public class ResultSenderImpl implements ResultSenderPgp, ResultSenderPts {

    private final String kafkaHttpProxyAddress;
    private final RestTemplate restTemplate;
    private final KafkaRestMessageAdapter kafkaRestMessageAdapter;

    private static final String KAFKA_REST_PROXY_TEMPLATE = "%s/topics/%s";
    private static final String CONTENT_TYPE_HEADER = "application/vnd.kafka.avro.v2+json";
    private static final String ACCEPT_HEADER = "application/vnd.kafka.v2+json";
    private static final Long DEFAULT_CONNECT_TIMEOUT = 30000L;
    private static final Long DEFAULT_READ_TIMEOUT = 30000L;

    public ResultSenderImpl(@Value("${service-web-client.kafka-rest.address}") String kafkaHttpProxyAddress,
                            @Value("${service-web-client.kafka-rest.login}") String kafkaHttpProxyLogin,
                            @Value("${service-web-client.kafka-rest.password}") String kafkaHttpProxyPassword,
                            RestTemplateBuilder restTemplateBuilder,
                            KafkaRestMessageAdapter kafkaRestMessageAdapter) {
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

    private HttpEntity<MessagesBatchDto> buildHttpEntity(MessagesBatchDto batchDto) {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.put(HttpHeaders.ACCEPT, Collections.singletonList(ACCEPT_HEADER));
        headers.put(HttpHeaders.CONTENT_TYPE, Collections.singletonList(CONTENT_TYPE_HEADER));
        return new HttpEntity<>(batchDto, headers);
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

}
