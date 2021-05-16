package com.nlmk.kb.server.service;

import com.nlmk.kb.server.entity.pdm.PdmMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.PartitionOffset;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaPdmService {

    private final PdmMessageService messageService;
    private final NsiClientService nsiClientService;

    @KafkaListener(containerFactory = "kafkaListenerContainerFactoryPdm",
            topicPartitions = {
                    @TopicPartition(topic = "${kafka.pdm.topic.asap-chemical-properties}",
                            partitionOffsets = @PartitionOffset(partition = "0", initialOffset = "0")),
                    @TopicPartition(topic = "${kafka.pdm.topic.equivalents}",
                            partitionOffsets = @PartitionOffset(partition = "0", initialOffset = "0")),
                    @TopicPartition(topic = "${kafka.pdm.topic.microstructure}",
                            partitionOffsets = @PartitionOffset(partition = "0", initialOffset = "0")),
                    @TopicPartition(topic = "${kafka.pdm.topic.match-tk-num}",
                            partitionOffsets = @PartitionOffset(partition = "0", initialOffset = "0")),
                    @TopicPartition(topic = "${kafka.pdm.topic.match-rabplan-num}",
                            partitionOffsets = @PartitionOffset(partition = "0", initialOffset = "0")),
                    @TopicPartition(topic = "${kafka.pdm.topic.pcm}",
                            partitionOffsets = @PartitionOffset(partition = "0", initialOffset = "0")),
                    @TopicPartition(topic = "${kafka.pdm.topic.asap-tol-links}",
                            partitionOffsets = @PartitionOffset(partition = "0", initialOffset = "0"))
            }
    )
    public void receiveMessageReq(@Payload ConsumerRecord request) {

        //todo вынести в interceptor
        MDC.put("KAFKA_ID", UUID.randomUUID().toString());

        try {
            log.info("--- PDM consumer record: topic: {}; partition: {}; offset: {}, key: {}",
                request.topic(),
                request.partition(),
                request.offset(),
                request.key()
            );

            Optional<PdmMessage> savedMessage = messageService.save(request);

            if (savedMessage.isPresent()) {
                val message = savedMessage.get();
                ResponseEntity<Long> response = nsiClientService.sendPdmDictionary(message);

                if (response.getStatusCode() == HttpStatus.ACCEPTED) {
                    message.setPosted(true);
                    messageService.save(message);
                }
            }
        } finally {
            MDC.remove("KAFKA_ID");
        }
    }
}
