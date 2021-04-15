package com.nlmk.kb.server.config;

import com.nlmk.kb.server.config.deserializer.AvroDeserializer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nlmk.l3.sup.IntegralParameters;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@EnableKafka
@Configuration
@RequiredArgsConstructor
public class KafkaBrokerConfig {
    //todo в application-prod.properties указать правильные десериализаторы для ключа и значения

    private final SupConsumerProperties supConsumerProperties;

    @Bean
    public Map<String,Object> consumerConfigs(){
        Map<String,Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, supConsumerProperties.getKafkaServer());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, AvroDeserializer.class);
        props.put(ConsumerConfig.GROUP_ID_CONFIG,supConsumerProperties.getKafkaGroupId());
        props.put (ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,"earliest");

        return props;
    }

    @Bean
    public ConsumerFactory<String, IntegralParameters> consumerFactory(){

        return new DefaultKafkaConsumerFactory<>(
                consumerConfigs(),
                new StringDeserializer(),
                new AvroDeserializer<>(IntegralParameters.class)
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String,IntegralParameters> kafkaListenerContainerFactory(){

        ConcurrentKafkaListenerContainerFactory<String,IntegralParameters> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactory());
        factory.setConcurrency(1); // todo устанавливается по количеству partitions в топике https://howtoprogram.xyz/2016/09/25/spring-kafka-multi-threaded-message-consumption/

        return factory;
    }
}
