package com.nlmk.kb.server.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class CcmConsumerProperties {
    private final String kafkaServer;
    private final String kafkaGroupId;
    private final String topicReq;
    private final String schemaRegistryUrl;
    private final boolean sslEnabled;
    private final String sslTruststorePassword;
    private final String sslKeystorePassword;
    private final String sslTruststorePath;
    private final String sslKeystorePath;

    public CcmConsumerProperties(@Value("${kafka.ccm.bootstrap-servers}") String kafkaServer,
                                 @Value("${kafka.ccm.consumer.group-id}")String kafkaGroupId,
                                 @Value("${kafka.ccm.topicReq}") String topicReq,
                                 @Value("${kafka.ccm.schema.registry.url}") String schemaRegistryUrl,
                                 @Value("${kafka.ccm.ssl-enabled}") boolean sslEnabled,
                                 @Value("${kafka.sslTruststorePassword}") String sslTruststorePassword,
                                 @Value("${kafka.sslKeystorePassword}") String sslKeystorePassword,
                                 @Value("${kafka.client.truststore-path}") String sslTruststorePath,
                                 @Value("${kafka.client.keystore-path}") String sslKeystorePath) {
        this.kafkaServer = kafkaServer;
        this.kafkaGroupId = kafkaGroupId;
        this.topicReq = topicReq;
        this.schemaRegistryUrl = schemaRegistryUrl;
        this.sslEnabled = sslEnabled;
        this.sslKeystorePassword = sslKeystorePassword;
        this.sslTruststorePassword = sslTruststorePassword;
        this.sslTruststorePath = sslTruststorePath;
        this.sslKeystorePath = sslKeystorePath;
    }
}
