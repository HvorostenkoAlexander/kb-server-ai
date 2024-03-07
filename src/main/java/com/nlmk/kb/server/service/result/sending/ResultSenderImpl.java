package com.nlmk.kb.server.service.result.sending;

import com.nlmk.kb.server.api.MessagesBatchDto;
import com.nlmk.kb.server.entity.KafkaMessageKey;
import com.nlmk.kb.server.exception.KafkaRestConfigException;
import com.nlmk.kb.server.exception.RemoteServiceSenderException;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

import static com.nlmk.kb.server.config.KbConstants.KAFKA_REST_ACCEPT_HEADER;
import static com.nlmk.kb.server.config.KbConstants.KAFKA_REST_CONTENT_TYPE_HEADER;
import static com.nlmk.kb.server.config.KbConstants.KAFKA_REST_PROXY_TEMPLATE;

@Slf4j
@Service
public class ResultSenderImpl implements ResultSender {

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

    @Override
    public void send(SpecificRecordBase result, String topic, String key) {

        final var schemaKey = "{\"type\": \"string\"}";

        final var messageKey = KafkaMessageKey.create(key, schemaKey);

        final var batchDto = kafkaRestMessageAdapter.adapt(result, messageKey);

        log.info("send, отправка результата в KAFKA: key [{}], value [{}]", messageKey.getKey(), result);
        sending(batchDto, topic);
    }

    private void sending(MessagesBatchDto batchDto, String topic) {

        if (StringUtils.isBlank(kafkaHttpProxyAddress)) {
            throw new KafkaRestConfigException("sending, kafka-rest.address не задан");
        }

        final var response = webClient.post()
                .uri(String.format(KAFKA_REST_PROXY_TEMPLATE, kafkaHttpProxyAddress, topic))
                .acceptCharset(StandardCharsets.UTF_8)
                .header(HttpHeaders.ACCEPT, KAFKA_REST_ACCEPT_HEADER)
                .header(HttpHeaders.CONTENT_TYPE, KAFKA_REST_CONTENT_TYPE_HEADER)
                .bodyValue(batchDto)
                .retrieve()
                .bodyToMono(String.class)// vs JsonNode
                .timeout(Duration.ofMillis(webClientTimeout))
                .onErrorResume(e -> Mono.error(
                        new RemoteServiceSenderException(
                                String.format("ResultSenderImpl, sending, ошибка отправки: [%s]", e.getMessage())
                        )))
                .block();
        log.info("sending, результат отправки в KAFKA: [{}]", response);
    }

}
