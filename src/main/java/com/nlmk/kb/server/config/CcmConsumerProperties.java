package com.nlmk.kb.server.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Component
@ConfigurationProperties("kafka.ccm")
public class CcmConsumerProperties {
    private final String kafkaServer;
    private final String kafkaGroupId;//todo при работе с множдественными потребителями перейти на пользовательские настройки свойств (kafka.consumer.group-id)
    private final String topicReq;
    private final String schemaRegistryUrl;

    public CcmConsumerProperties(@Value("${kafka.ccm.bootstrap-servers}") String kafkaServer,
                                 @Value("${kafka.ccm.consumer.group-id}")String kafkaGroupId,
                                 @Value("${kafka.ccm.topicReq}") String topicReq,
                                 @Value("${kafka.schema.registry.url}") String schemaRegistryUrl) {
        this.kafkaServer = kafkaServer;
        this.kafkaGroupId = kafkaGroupId;
        this.topicReq = topicReq;
        this.schemaRegistryUrl = schemaRegistryUrl;
    }
}
