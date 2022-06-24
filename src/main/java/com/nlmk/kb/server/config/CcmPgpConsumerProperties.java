package com.nlmk.kb.server.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class CcmPgpConsumerProperties extends ConsumerProperties {

    private final String topicReq;
    private final boolean sslEnabled;

    public CcmPgpConsumerProperties(@Value("${kafka.ccm.pgp.bootstrap-servers}") String kafkaServer,
                                    @Value("${kafka.ccm.pgp.consumer.group-id}") String kafkaGroupId,
                                    @Value("${kafka.ccm.pgp.topicReq}") String topicReq,
                                    @Value("${kafka.ccm.pgp.schema.registry.url}") String schemaRegistryUrl,
                                    @Value("${kafka.ccm.pgp.ssl-enabled}") boolean sslEnabled,
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
