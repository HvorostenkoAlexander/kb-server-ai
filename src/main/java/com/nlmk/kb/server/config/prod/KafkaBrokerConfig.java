package com.nlmk.kb.server.config.prod;

import com.nlmk.kb.server.config.SupConsumerProperties;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nlmk.l3.sup.IntegralParameters;
import nlmk.l3.sup.UnrecoverableParametersTrends;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
@Profile("prod")
public class KafkaBrokerConfig {

    private final SupConsumerProperties supConsumerProperties;

    @Bean
    public Map<String, Object> consumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, supConsumerProperties.getKafkaServer());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, supConsumerProperties.getKafkaGroupId());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        props.put("schema.registry.url", supConsumerProperties.getSchemaRegistryUrl());
        props.put("specific.avro.reader", "true");

        return props;
    }

    @Bean
    public ConsumerFactory<Object, Object> consumerFactory() {

        KafkaAvroDeserializer keyDeserializer = new KafkaAvroDeserializer();
        keyDeserializer.configure(consumerConfigs(), true);

        KafkaAvroDeserializer valueDeserializer = new KafkaAvroDeserializer();
        valueDeserializer.configure(consumerConfigs(), false);

//        ErrorHandlingDeserializer<IntegralParameters> errorHandlingDeserializer
//                = new ErrorHandlingDeserializer<>(avroDeser);

        return new DefaultKafkaConsumerFactory<>(
                consumerConfigs(),
                keyDeserializer,
                //     errorHandlingDeserializer
                valueDeserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, IntegralParameters> kafkaListenerContainerFactoryIp() {

        ConcurrentKafkaListenerContainerFactory<String, IntegralParameters> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactory());
//        factory.setErrorHandler(new SeekToCurrentErrorHandler(
//                (record, error) -> {
//                    log.error("--- ERROR: "+error.getMessage());
//                    log.error("--- ERROR RECORD: "+record.toString());
//                }, new FixedBackOff(5000L, 1))
//        );
        factory.setConcurrency(1);

        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, UnrecoverableParametersTrends> kafkaListenerContainerFactoryUp() {

        ConcurrentKafkaListenerContainerFactory<String, UnrecoverableParametersTrends> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactory());
        factory.setConcurrency(1);

        return factory;
    }
}
