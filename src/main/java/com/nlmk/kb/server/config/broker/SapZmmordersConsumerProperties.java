package com.nlmk.kb.server.config.broker;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class SapZmmordersConsumerProperties extends ConsumerProperties {
    public SapZmmordersConsumerProperties(@Value("${kafka.sap.bootstrap-servers}") String kafkaServer,
                                          @Value("${kafka.sap.group-id}") String kafkaGroupId,
                                          @Value("${kafka.sap.schema.registry.url}") String schemaRegistryUrl,
                                          @Value("${kafka.sap.ssl-enabled}") boolean sslEnabled,
                                          @Value("${kafka.zmmorders.sslTruststorePassword}") String truststorePassword,
                                          @Value("${kafka.zmmorders.sslKeystorePassword}") String keystorePassword,
                                          @Value("${kafka.zmmorders-client.truststore-path}") String truststorePath,
                                          @Value("${kafka.zmmorders-client.keystore-path}") String keystorePath) {
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
