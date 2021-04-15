package com.nlmk.kb.server.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Component
@ConfigurationProperties("kafka.sup")
public class SupConsumerProperties {

    private final String kafkaServer;
    private final String kafkaGroupId;//todo при работе с множдественными потребителями перейти на пользовательские настройки свойств (kafka.consumer.group-id)
    private final String topicIP;

    public SupConsumerProperties(@Value("${kafka.sup.bootstrap-servers}") String kafkaServer,
                                 @Value("${kafka.sup..consumer.group-id}") String kafkaGroupId,
                                 @Value("${kafka.sup.topicIp}") String topicIP) {
        this.kafkaServer = kafkaServer;
        this.kafkaGroupId = kafkaGroupId;
        this.topicIP = topicIP;
    }
}
