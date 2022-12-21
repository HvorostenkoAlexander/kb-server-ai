package com.nlmk.kb.server.config.broker;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;

@Configuration
public class PdmBrokerConfig extends BrokerConfigBase {

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> pdmKafkaListenerContainerFactory(PdmConsumerProperties consumerProperties) {

        return getContainerFactory(consumerProperties);

    }

}
