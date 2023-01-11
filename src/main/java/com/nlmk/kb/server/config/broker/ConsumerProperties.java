package com.nlmk.kb.server.config.broker;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public abstract class ConsumerProperties {

    private final String kafkaServer;
    private final String kafkaGroupId;
    private final String schemaRegistryUrl;
    private final boolean sslEnabled;
    private final String truststorePassword;
    private final String keystorePassword;
    private final String truststorePath;
    private final String keystorePath;

}
