package com.nlmk.kb.server.config.broker;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Getter
@Component
@ConditionalOnProperty("kafka.sadim.enabled")
public class SadimConsumerProperties {

    private final String kafkaServer;
    private final String kafkaGroupId;

    public SadimConsumerProperties(@Value("${kafka.sadim.bootstrap-servers}") String kafkaServer,
                                   @Value("${kafka.sadim.group-id}") String kafkaGroupId) {
        this.kafkaServer = kafkaServer;
        this.kafkaGroupId = kafkaGroupId;
    }

}
