package com.nlmk.kb.server.service.result.sending;

import com.fasterxml.jackson.databind.JsonNode;
import com.nlmk.kb.server.api.MessagesBatchDto;
import com.nlmk.kb.server.entity.KafkaMessageKey;
import com.nlmk.kb.server.exception.KafkaRestConfigException;
import com.nlmk.kb.server.exception.KafkaRestException;
import com.nlmk.kb.server.exception.ProductSenderException;
import lombok.extern.slf4j.Slf4j;
import nlmk.l3.apcs.RecordPk;
import nlmk.l3.apcs.VerificationResults;
import nlmk.l3.apcs.VerificationResultsPts;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ResultSenderImpl extends BaseSender implements ResultSenderPgp, ResultSenderPts {

    public ResultSenderImpl(@Value("${service-web-client.kafka-rest.address}")
                            String kafkaHttpProxyAddress,
                            @Value("${service-web-client.kafka-rest.login}")
                            String kafkaHttpProxyLogin,
                            @Value("${service-web-client.kafka-rest.password}")
                            String kafkaHttpProxyPassword,
                            RestTemplateBuilder restTemplateBuilder,
                            KafkaRestMessageAdapter kafkaRestMessageAdapter) {
        super(kafkaHttpProxyAddress, kafkaHttpProxyLogin, kafkaHttpProxyPassword, restTemplateBuilder, kafkaRestMessageAdapter);
    }

    private void checkBeforeSend(Object result, String topic) {
        if (result == null || StringUtils.isBlank(topic)) {
            throw new ProductSenderException("checkBeforeSend, empty result OR topic");
        }
        if (StringUtils.isBlank(getKafkaHttpProxyAddress())) {
            throw new KafkaRestConfigException("checkBeforeSend, kafka-rest.address is EMPTY, cancel sending");
        }
    }

    private KafkaMessageKey generateKey(RecordPk pk) {
        if (pk == null) {
            log.warn("generateKey, result.getPk() is null");
            throw new ProductSenderException("generateKey, gen key error: result.getPk() is NULL");
        }

        final var key = StringUtils.joinWith("~", pk.getSystemCode(), pk.getId());
        final var schemaKey = "{\"type\": \"string\"}";

        return KafkaMessageKey.create(key, schemaKey);
    }

    @Override
    public void send(VerificationResults result, String topic) {
        checkBeforeSend(result, topic);

        final var key = generateKey(result.getPk());
        final var batchDto = getKafkaRestMessageAdapter().adapt(result, key);

        log.info("send, VerificationResults, request to KAFKA: key [{}], value [{}]", key.getKey(), result);
        send(batchDto, topic);
    }

    @Override
    public void send(VerificationResultsPts result, String topic) {
        checkBeforeSend(result, topic);

        final var key = generateKey(result.getPk());
        final var batchDto = getKafkaRestMessageAdapter().adapt(result, key);

        log.info("send, VerificationResultsPts, request to KAFKA: key [{}], value [{}]", key.getKey(), result);
        send(batchDto, topic);
    }

    private void send(MessagesBatchDto batchDto, String topic) {
        final var address = String.format(KAFKA_REST_PROXY_TEMPLATE, getKafkaHttpProxyAddress(), topic);

        try {
            ResponseEntity<JsonNode> resp = getRestTemplate()
                    .exchange(address, HttpMethod.POST, buildHttpEntity(batchDto), JsonNode.class);

            log.info("send, response from KAFKA: [{}]", resp);
        } catch (Exception e) {
            log.error("send, sending error: [{}]", e.getMessage());
            throw new KafkaRestException(e);
        }
    }

}
