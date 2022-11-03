package com.nlmk.kb.server.service.result.sending;

import com.fasterxml.jackson.databind.JsonNode;
import com.nlmk.kb.server.entity.KafkaMessageKey;
import com.nlmk.kb.server.exception.KafkaRestConfigException;
import com.nlmk.kb.server.exception.KafkaRestException;
import com.nlmk.kb.server.exception.ProductSenderException;
import lombok.extern.slf4j.Slf4j;
import nlmk.l3.apcs.VerificationResults;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class VerificationResultSenderImpl extends BaseSender implements VerificationResultSender {

    public VerificationResultSenderImpl(@Value("${service-web-client.kafka-rest.address}")
                                        String kafkaHttpProxyAddress,
                                        @Value("${service-web-client.kafka-rest.login}")
                                        String kafkaHttpProxyLogin,
                                        @Value("${service-web-client.kafka-rest.password}")
                                        String kafkaHttpProxyPassword,
                                        RestTemplateBuilder restTemplateBuilder,
                                        KafkaRestMessageAdapter kafkaRestMessageAdapter) {
        super(kafkaHttpProxyAddress, kafkaHttpProxyLogin, kafkaHttpProxyPassword, restTemplateBuilder, kafkaRestMessageAdapter);
    }

    @Override
    public void send(VerificationResults result, String topic) {

        if (result == null || StringUtils.isBlank(topic)) {
            throw new ProductSenderException("Не указан результат или топик для отправки сообщения.");
        }
        if (StringUtils.isBlank(getKafkaHttpProxyAddress())) {
            throw new KafkaRestConfigException("Не установлен адрес сервера kafka-rest. Передача данных невозможна.");
        }

        final var key = generateKey(result);
        final var batchDto = getKafkaRestMessageAdapter().adapt(result, key);
        final var address = String.format(KAFKA_REST_PROXY_TEMPLATE, getKafkaHttpProxyAddress(), topic);

        try {
            ResponseEntity<JsonNode> resp = getRestTemplate()
                    .exchange(address, HttpMethod.POST, buildHttpEntity(batchDto), JsonNode.class);

            log.info("Response from KAFKA: [{}]", resp);
        } catch (Exception e) {
            log.error("Ошибка отправки сообщения: {}", e.getMessage());
            throw new KafkaRestException(e);
        }
    }

    private KafkaMessageKey generateKey(VerificationResults result) {
        if (result.getPk() == null) {
            log.warn("result.getPk() is null");
            throw new ProductSenderException("Не удается сформировать key для сообщения в топик Kafka. RecordPk is null.");
        }

        final var key = StringUtils.joinWith("~", result.getPk().getSystemCode(), result.getPk().getId());
        final var schemaKey = "{\"type\": \"string\"}";

        return KafkaMessageKey.create(key, schemaKey);
    }

}
