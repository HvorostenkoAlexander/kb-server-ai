package com.nlmk.kb.server.config.broker;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class CcmPgpConsumerProperties extends ConsumerProperties {

    public CcmPgpConsumerProperties(@Value("${kafka.ccm.pgp.bootstrap-servers}") String kafkaServer,
                                    @Value("${kafka.ccm.pgp.consumer.group-id}") String kafkaGroupId,
                                    @Value("${kafka.ccm.pgp.schema.registry.url}") String schemaRegistryUrl,
                                    @Value("${kafka.ccm.pgp.ssl-enabled}") boolean sslEnabled,
                                    @Value("${kafka.sslTruststorePassword002}") String truststorePassword,
                                    @Value("${kafka.sslKeystorePassword}") String keystorePassword,
                                    @Value("${kafka.client.truststore-path-002}") String truststorePath,
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
