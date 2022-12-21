package com.nlmk.kb.server.config.broker;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class CcmKc2ConsumerProperties extends ConsumerProperties {

    private final String topicReq;
    private final boolean sslEnabled;

    public CcmKc2ConsumerProperties(@Value("${kafka.ccm.kc2.bootstrap-servers}") String kafkaServer,
                                    @Value("${kafka.ccm.kc2.consumer.group-id}") String kafkaGroupId,
                                    @Value("${kafka.ccm.kc2.topicReq}") String topicReq,
                                    @Value("${kafka.ccm.kc2.schema.registry.url}") String schemaRegistryUrl,
                                    @Value("${kafka.ccm.kc2.ssl-enabled}") boolean sslEnabled,
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
