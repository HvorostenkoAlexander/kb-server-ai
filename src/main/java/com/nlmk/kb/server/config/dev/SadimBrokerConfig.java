package com.nlmk.kb.server.config.dev;

import com.nlmk.kb.server.config.SadimConsumerProperties;
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
public class SadimBrokerConfig {

    private final SadimConsumerProperties consumerProperties;

    @Bean
    public Map<String, Object> sadimConsumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, consumerProperties.getKafkaServer());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, consumerProperties.getKafkaGroupId());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        return props;
    }

    @Bean
    public ConsumerFactory<String, String> sadimConsumerFactory() {
        StringDeserializer keyDeserializer = new StringDeserializer();
        keyDeserializer.configure(sadimConsumerConfigs(), true);

        StringDeserializer valueDeserializer = new StringDeserializer();
        valueDeserializer.configure(sadimConsumerConfigs(), false);

        ErrorHandlingDeserializer<String> errorHandlingValueDeserializer
                = new ErrorHandlingDeserializer<String>(valueDeserializer);

        return new DefaultKafkaConsumerFactory<>(sadimConsumerConfigs(),
                keyDeserializer,
                errorHandlingValueDeserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerSadim() {

        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(sadimConsumerFactory());
        factory.setErrorHandler(((thrownException, data) -> {
            log.error("ERROR: " + thrownException.getMessage());
            if (data != null) {
                log.error("ERROR RECORD: " + data.toString());
            }
        }));
        //factory.setConcurrency(1);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);

        return factory;
    }
}
