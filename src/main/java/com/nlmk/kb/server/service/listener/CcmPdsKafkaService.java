package com.nlmk.kb.server.service.listener;

import com.nlmk.kb.server.exception.AttestationResultException;
import com.nlmk.kb.server.exception.AttestationResultSenderException;
import com.nlmk.kb.server.exception.DateTimeParseException;
import com.nlmk.kb.server.exception.KafkaMessageProcessingException;
import com.nlmk.kb.server.exception.KafkaRestConfigException;
import com.nlmk.kb.server.exception.RemoteServiceSenderException;
import com.nlmk.kb.server.service.ccm.CcmCommonService;
import com.nlmk.kb.server.service.ccm.CcmMessageAdapter;
import com.nlmk.kb.server.service.ccm.CcmMessageService;
import com.nlmk.kb.server.service.result.sending.AttestationResultSender;
import io.micrometer.core.annotation.Timed;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import nlmk.l3.apcs.VerificationResultsPts;
import nlmk.l3.ccm.pds.AttestationRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import static com.nlmk.kb.server.config.KbConstants.LISTENER_EXC_MESSAGE_TEMPLATE;
import static com.nlmk.kb.server.config.KbConstants.MISSING_ATT_RESULT_MESSAGE_TEMPLATE;

@Slf4j
@Service
@ConditionalOnProperty(value = "kafka.ccm.pds.enable", matchIfMissing = true)
public class CcmPdsKafkaService {

    private final long sleepTime;
    private final CcmCommonService ccmCommonService;
    private final CcmMessageAdapter<AttestationRequest> ccmMessageAdapter;
    private final AttestationResultSender attestationResultSender;
    private final CcmMessageService ccmMessageService;

    public CcmPdsKafkaService(@Value("${kafka.ack.nack.sleep-time}") long sleepTime,
                              CcmCommonService ccmCommonService,
                              CcmMessageAdapter<AttestationRequest> ccmMessageAdapter,
                              AttestationResultSender attestationResultSender,
                              CcmMessageService ccmMessageService) {
        this.sleepTime = sleepTime;
        this.ccmCommonService = ccmCommonService;
        this.ccmMessageAdapter = ccmMessageAdapter;
        this.attestationResultSender = attestationResultSender;
        this.ccmMessageService = ccmMessageService;
    }

    @KafkaListener(containerFactory = "ccmPdsKafkaListenerContainerFactory", topics = {"${kafka.ccm.pds.topicReq}"})
    @Timed(value = "kafka_listener", percentiles = {0.99, 0.95})
    public void receiveMessageReq(@Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                  @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String key,
                                  @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
                                  @Header(KafkaHeaders.OFFSET) int offset,
                                  @Header(KafkaHeaders.RECEIVED_TIMESTAMP) String timestamp,
                                  @Payload AttestationRequest request,
                                  Acknowledgment ack) {

        log.info(
                "receiveMessageReq (CCM PDS): topic [{}], partition [{}], offset [{}], key [{}], timestamp [{}], request.ts [{}], request.op [{}], request.pk.id [{}]",
                topic, partition, offset, key, timestamp, request.getTs(), request.getOp(), request.getPk().getId());

        try {
            final var requestMessage = ccmMessageAdapter.adapt(request, topic, key, partition, offset);

            if (request.getOp() == nlmk.l3.ccm.pds.EnumOp.D
                    || requestMessage.getRequest().getValue() == null
                    || requestMessage.getRequest().getValue().getData() == null) {
                log.warn(
                        "receiveMessageReq (CCM PDS), аттестация не выполняется - некорректные Op или Data, partition {}, offset {}, key {}",
                        partition, offset, key);
            } else {
                // отправка запроса при наличии тела и правильной операции
                final var attResult = ccmCommonService.postAttestation(requestMessage);
                if (attResult.isEmpty() || Objects.isNull(attResult.get().getResult())) {
                    log.warn(
                            "receiveMessageReq (CCM PDS), нет результата аттестации для primeId {}, partition {}, offset {}, key {}",
                            requestMessage.getPrimeId(), partition, offset, key);
                    throw new AttestationResultException(
                            String.format(MISSING_ATT_RESULT_MESSAGE_TEMPLATE, requestMessage.getPrimeId()));
                }

                if (!CollectionUtils.isEmpty(attResult.get().getResult().getRequests())
                        && Objects.nonNull(attResult.get().getResult().getRequests().get(0).getId())) {
                    var resultRequest = attResult.get().getResult().getRequests().get(0);
                    ccmMessageService.saveSourceMessage(resultRequest.getId(), resultRequest.getPrimeID(),
                            request.toString(), LocalDateTime.now());
                }
                // отправка ответа с результатами аттестации
                attestationResultSender.send(attResult.get(), VerificationResultsPts.class);
            }
            ack.acknowledge();
        } catch (DateTimeParseException e) {
            log.warn("receiveMessageReq, DateTimeParseException", e);
            ack.acknowledge();
            throw new DateTimeParseException(MessageFormat.format(LISTENER_EXC_MESSAGE_TEMPLATE, e));
        } catch (AttestationResultException e) {
            log.warn("receiveMessageReq, AttestationResultException", e);
            ack.nack(sleepTime);
            throw new AttestationResultException(MessageFormat.format(LISTENER_EXC_MESSAGE_TEMPLATE, e));
        } catch (KafkaRestConfigException e) {
            log.warn("receiveMessageReq, KafkaRestConfigException", e);
            ack.acknowledge();
            throw new KafkaRestConfigException(MessageFormat.format(LISTENER_EXC_MESSAGE_TEMPLATE, e));
        } catch (AttestationResultSenderException e) {
            log.warn("receiveMessageReq, AttestationResultSenderException", e);
            ack.nack(sleepTime);
            throw new AttestationResultSenderException(MessageFormat.format(LISTENER_EXC_MESSAGE_TEMPLATE, e));
        } catch (RemoteServiceSenderException e) {
            log.warn("receiveMessageReq, RemoteServiceSenderException", e);
            ack.acknowledge(); // измеить на nack после реализации аттестации
            throw new RemoteServiceSenderException(MessageFormat.format(LISTENER_EXC_MESSAGE_TEMPLATE, e));
        } catch (Exception e) {
            log.warn("receiveMessageReq, Exception", e);
            ack.nack(sleepTime);
            throw new KafkaMessageProcessingException(MessageFormat.format(LISTENER_EXC_MESSAGE_TEMPLATE, e));
        }
    }
}
