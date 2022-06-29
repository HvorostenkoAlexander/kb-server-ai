package com.nlmk.kb.server.config.broker;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class SadimBrokerConfig {

    private final SadimConsumerProperties consumerProperties;

    private ConsumerFactory<String, String> sadimConsumerFactory() {
        Map<String, Object> configuration = new HashMap<>();

        configuration.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, consumerProperties.getKafkaServer());
        configuration.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configuration.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configuration.put(ConsumerConfig.GROUP_ID_CONFIG, consumerProperties.getKafkaGroupId());
        configuration.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        StringDeserializer keyDeserializer = new StringDeserializer();
        keyDeserializer.configure(configuration, true);

        StringDeserializer valueDeserializer = new StringDeserializer();
        valueDeserializer.configure(configuration, false);

        ErrorHandlingDeserializer<String> errorHandlingValueDeserializer
                = new ErrorHandlingDeserializer<>(valueDeserializer);

        return new DefaultKafkaConsumerFactory<>(
                configuration,
                keyDeserializer,
                errorHandlingValueDeserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> sadimKafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(sadimConsumerFactory());
        factory.setErrorHandler(((thrownException, consumerRecord) -> log.error("ERROR", thrownException)));
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);

        return factory;
    }

}
