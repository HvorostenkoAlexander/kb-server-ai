package com.nlmk.kb.server.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class CcmConsumerProperties extends ConsumerProperties {

    private final String topicReq;
    private boolean sslEnabled;

    public CcmConsumerProperties(@Value("${kafka.ccm.bootstrap-servers}") String kafkaServer,
                                 @Value("${kafka.ccm.consumer.group-id}") String kafkaGroupId,
                                 @Value("${kafka.ccm.topicReq}") String topicReq,
                                 @Value("${kafka.ccm.schema.registry.url}") String schemaRegistryUrl,
                                 @Value("${kafka.ccm.ssl-enabled}") boolean sslEnabled,
                                 @Value("${kafka.sslTruststorePassword}") String truststorePassword,
                                 @Value("${kafka.sslKeystorePassword}") String keystorePassword,
                                 @Value("${kafka.client.truststore-path}") String truststorePath,
                                 @Value("${kafka.client.keystore-path}") String keystorePath) {
        super(kafkaServer,
                kafkaGroupId,
                schemaRegistryUrl,
                truststorePassword,
                keystorePassword,
                truststorePath,
                keystorePath
        );

        this.topicReq = topicReq;
        this.sslEnabled = sslEnabled;
    }
}
