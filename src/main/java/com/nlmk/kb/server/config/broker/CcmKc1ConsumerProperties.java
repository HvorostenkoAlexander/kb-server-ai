package com.nlmk.kb.server.config.broker;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class CcmKc1ConsumerProperties extends ConsumerProperties {

    public CcmKc1ConsumerProperties(@Value("${kafka.ccm.kc1.bootstrap-servers}") String kafkaServer,
                                    @Value("${kafka.ccm.kc1.consumer.group-id}") String kafkaGroupId,
                                    @Value("${kafka.ccm.kc1.schema.registry.url}") String schemaRegistryUrl,
                                    @Value("${kafka.ccm.kc1.ssl-enabled}") boolean sslEnabled,
                                    @Value("${kafka.sslTruststorePassword}") String truststorePassword,
                                    @Value("${kafka.sslKeystorePassword}") String keystorePassword,
                                    @Value("${kafka.client.truststore-path}") String truststorePath,
                                    @Value("${kafka.client.keystore-path}") String keystorePath) {
        super(kafkaServer,
                kafkaGroupId,
                schemaRegistryUrl,
                sslEnabled,
                truststorePassword,
                keystorePassword,
                truststorePath,
                keystorePath
        );
    }

}
