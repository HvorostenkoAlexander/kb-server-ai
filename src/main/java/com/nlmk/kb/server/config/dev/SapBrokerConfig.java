package com.nlmk.kb.server.config.dev;

import com.nlmk.kb.server.config.SapConsumerProperties;
import com.nlmk.kb.server.config.dev.deserializer.AvroDeserializer;
import com.nlmk.s3.proxy.s3notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
public class SapBrokerConfig {

    private final SapConsumerProperties consumerProperties;

    @Bean
    public ConsumerFactory<String, s3notification> sapConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, consumerProperties.getKafkaServer());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, consumerProperties.getKafkaGroupId());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        props.put("schema.registry.url", consumerProperties.getSchemaRegistryUrl());
        props.put("specific.avro.reader", "true");

        AvroDeserializer<s3notification> valueDeserializer =
                new AvroDeserializer<>(s3notification.class);

        ErrorHandlingDeserializer<s3notification> errorHandlingValueDeserializer
                = new ErrorHandlingDeserializer<>(valueDeserializer);

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                errorHandlingValueDeserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, s3notification> kafkaListenerSap() {

        ConcurrentKafkaListenerContainerFactory<String, s3notification> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(sapConsumerFactory());
        factory.setErrorHandler(((thrownException, data) -> {
            log.error("ERROR: " + thrownException.getClass() + "; " + thrownException.getMessage());
            if (data != null) {
                log.error("ERROR RECORD: " + data.toString());
            }
        }));
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        return factory;
    }
}
