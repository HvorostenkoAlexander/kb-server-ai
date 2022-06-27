package com.nlmk.kb.server.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class CcmPtsConsumerProperties extends ConsumerProperties {

    private final String topicReq;
    private final boolean sslEnabled;

    public CcmPtsConsumerProperties(@Value("${kafka.ccm.pts.bootstrap-servers}") String kafkaServer,
                                    @Value("${kafka.ccm.pts.consumer.group-id}") String kafkaGroupId,
                                    @Value("${kafka.ccm.pts.topicReq}") String topicReq,
                                    @Value("${kafka.ccm.pts.schema.registry.url}") String schemaRegistryUrl,
                                    @Value("${kafka.ccm.pts.ssl-enabled}") boolean sslEnabled,
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
