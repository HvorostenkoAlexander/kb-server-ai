package com.nlmk.kb.server.config.broker;

import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
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
public class ZifraBrokerConfig {

    private final ZifraConsumerProperties consumerProperties;

    private ConsumerFactory<Object, Object> zifraConsumerFactory() {
        Map<String, Object> configuration = new HashMap<>();

        configuration.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, consumerProperties.getKafkaServer());
        configuration.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);
        configuration.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);
        configuration.put(ConsumerConfig.GROUP_ID_CONFIG, consumerProperties.getKafkaGroupId());
        configuration.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        configuration.put("schema.registry.url", consumerProperties.getSchemaRegistryUrl());
        configuration.put("specific.avro.reader", "true");

        log.info("SSL {}", consumerProperties.isSslEnabled() ? "enabled" : "disabled");
        if (consumerProperties.isSslEnabled()) {
            configuration.put("security.protocol", "SSL");
            configuration.put("ssl.truststore.location", consumerProperties.getTruststorePath());
            configuration.put("ssl.truststore.password", consumerProperties.getTruststorePassword());
            configuration.put("ssl.keystore.password", consumerProperties.getKeystorePassword());
            configuration.put("ssl.keystore.location", consumerProperties.getKeystorePath());
            configuration.put("ssl.endpoint.identification.algorithm", "");
        }

        KafkaAvroDeserializer keyDeserializer = new KafkaAvroDeserializer();
        keyDeserializer.configure(configuration, true);

        KafkaAvroDeserializer valueDeserializer = new KafkaAvroDeserializer();
        valueDeserializer.configure(configuration, false);

        ErrorHandlingDeserializer<Object> errorHandlingKeyDeserializer
                = new ErrorHandlingDeserializer<>(keyDeserializer);

        ErrorHandlingDeserializer<Object> errorHandlingValueDeserializer
                = new ErrorHandlingDeserializer<>(valueDeserializer);

        return new DefaultKafkaConsumerFactory<>(
                configuration,
                errorHandlingKeyDeserializer,
                errorHandlingValueDeserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> zifraKafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, Object> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(zifraConsumerFactory());
        factory.setErrorHandler((thrownException, consumerRecord) -> {
            final var data = (consumerRecord == null) ? "empty" : consumerRecord.toString();
            log.error("ERROR, consumerRecord [{}]", data, thrownException);
        });
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);

        return factory;
    }

}
