package com.nlmk.kb.server.config.broker;

import nlmk.l3.ccm.pds.DbAttestationRequestVer1;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;

@Configuration
@ConditionalOnProperty(value = "kafka.ccm.pds.enable", matchIfMissing = true)
public class CcmPdsBrokerConfig extends BrokerConfigBase {

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, DbAttestationRequestVer1> ccmPdsKafkaListenerContainerFactory(
            CcmPdsConsumerProperties consumerProperties) {

        return getContainerFactory(consumerProperties);

    }
}