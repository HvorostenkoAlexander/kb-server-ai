package com.nlmk.kb.server.config.dev;

import com.nlmk.kb.server.config.PdmConsumerProperties;
import com.nlmk.kb.server.config.dev.deserializer.AvroDeserializer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nlmk.l3.pdm.SpMicrostructure;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
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
@Profile("dev")
public class PdmBrokerConfig {

    private final PdmConsumerProperties consumerProperties;

    @Bean
    public ConsumerFactory<String, SpMicrostructure> pdmConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, consumerProperties.getKafkaServer());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, AvroDeserializer.class);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, consumerProperties.getKafkaGroupId());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        AvroDeserializer<SpMicrostructure> deserializer = new AvroDeserializer<>(SpMicrostructure.class);

        ErrorHandlingDeserializer<SpMicrostructure> errorHandlingValueDeserializer
                = new ErrorHandlingDeserializer<>(deserializer);

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                errorHandlingValueDeserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, SpMicrostructure> kafkaListenerContainerFactoryPdm() {

        ConcurrentKafkaListenerContainerFactory<String, SpMicrostructure> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(pdmConsumerFactory());
        factory.setErrorHandler(((thrownException, data) -> {
            log.error("ERROR: " + thrownException.getMessage());
            if (data!=null) {
                log.error("ERROR RECORD: " + data.toString());
            }
        }));
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        factory.setConcurrency(1);
        return factory;
    }
}
