package com.nlmk.kb.server.service.result.sending;

import com.nlmk.kb.server.api.MessagesBatchDto;
import lombok.Getter;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.Collections;

@Getter
public abstract class BaseResultSender {

    private final String kafkaHttpProxyAddress;
    private final String kafkaHttpProxyLogin;
    private final String kafkaHttpProxyPassword;
    private final RestTemplate restTemplate;
    private final KafkaRestMessageAdapter kafkaRestMessageAdapter;

    protected static final String KAFKA_REST_PROXY_TEMPLATE = "%s/topics/%s";
    protected static final String CONTENT_TYPE_HEADER = "application/vnd.kafka.avro.v2+json";
    protected static final String ACCEPT_HEADER = "application/vnd.kafka.v2+json";
    protected static final Long DEFAULT_CONNECT_TIMEOUT = 30000L;
    protected static final Long DEFAULT_READ_TIMEOUT = 30000L;

    protected BaseResultSender(String kafkaHttpProxyAddress,
                               String kafkaHttpProxyLogin,
                               String kafkaHttpProxyPassword,
                               RestTemplateBuilder restTemplateBuilder,
                               KafkaRestMessageAdapter kafkaRestMessageAdapter) {
        this.kafkaHttpProxyAddress = kafkaHttpProxyAddress;
        this.kafkaHttpProxyLogin = kafkaHttpProxyLogin;
        this.kafkaHttpProxyPassword = kafkaHttpProxyPassword;
        this.kafkaRestMessageAdapter = kafkaRestMessageAdapter;

        if (StringUtils.hasText(this.kafkaHttpProxyLogin) && StringUtils.hasText(this.kafkaHttpProxyPassword)) {
            restTemplateBuilder = restTemplateBuilder
                    .basicAuthentication(this.kafkaHttpProxyLogin, this.kafkaHttpProxyPassword);
        }
        this.restTemplate = restTemplateBuilder
                .messageConverters(new MappingJackson2HttpMessageConverter())
                .setConnectTimeout(Duration.ofMillis(DEFAULT_CONNECT_TIMEOUT))
                .setReadTimeout(Duration.ofMillis(DEFAULT_READ_TIMEOUT))
                .build();
    }

    protected HttpEntity<MessagesBatchDto> buildHttpEntity(MessagesBatchDto batchDto) {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.put(HttpHeaders.ACCEPT, Collections.singletonList(ACCEPT_HEADER));
        headers.put(HttpHeaders.CONTENT_TYPE, Collections.singletonList(CONTENT_TYPE_HEADER));
        return new HttpEntity<>(batchDto, headers);
    }

}
