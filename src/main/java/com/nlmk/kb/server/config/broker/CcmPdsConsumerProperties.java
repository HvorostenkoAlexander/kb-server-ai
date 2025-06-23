package com.nlmk.kb.server.config.broker;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Getter
@Component
@ConditionalOnProperty(value = "kafka.ccm.pds.enable", matchIfMissing = true)
public class CcmPdsConsumerProperties extends ConsumerProperties {

    public CcmPdsConsumerProperties(@Value("${kafka.ccm.pds.bootstrap-servers}") String kafkaServer,
                                    @Value("${kafka.ccm.pds.consumer.group-id}") String kafkaGroupId,
                                    @Value("${kafka.ccm.pds.schema.registry.url}") String schemaRegistryUrl,
                                    @Value("${kafka.ccm.pds.ssl-enabled}") boolean sslEnabled,
                                    @Value("${kafka.sslTruststorePassword}") String truststorePassword,
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
