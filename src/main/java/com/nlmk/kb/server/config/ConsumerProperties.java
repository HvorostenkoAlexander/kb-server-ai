package com.nlmk.kb.server.config;

import lombok.Getter;

@Getter
public abstract class ConsumerProperties {
    private final String kafkaServer;
    private final String kafkaGroupId;
    private final String schemaRegistryUrl;
    private final String truststorePassword;
    private final String keystorePassword;
    private final String truststorePath;
    private final String keystorePath;

    public ConsumerProperties(String kafkaServer,
                              String kafkaGroupId,
                              String schemaRegistryUrl,
                              String truststorePassword,
                              String keystorePassword,
                              String truststorePath,
                              String keystorePath
    ) {
        this.kafkaServer = kafkaServer;
        this.kafkaGroupId = kafkaGroupId;
        this.schemaRegistryUrl = schemaRegistryUrl;
        this.truststorePassword = truststorePassword;
        this.keystorePassword = keystorePassword;
        this.truststorePath = truststorePath;
        this.keystorePath = keystorePath;
    }
}
