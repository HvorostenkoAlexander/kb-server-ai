package com.nlmk.kb.server.config;

import lombok.Getter;

@Getter
public abstract class ConsumerProperties {
    private final String kafkaServer;
    private final String kafkaGroupId;
    private final String schemaRegistryUrl;

    public ConsumerProperties(String kafkaServer,
                              String kafkaGroupId,
                              String schemaRegistryUrl) {
        this.kafkaServer = kafkaServer;
        this.kafkaGroupId = kafkaGroupId;
        this.schemaRegistryUrl = schemaRegistryUrl;
    }
}
