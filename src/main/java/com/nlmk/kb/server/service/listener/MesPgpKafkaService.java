package com.nlmk.kb.server.service.listener;

import com.nlmk.kb.server.exception.AttestationResultException;
import com.nlmk.kb.server.exception.AttestationResultSenderException;
import com.nlmk.kb.server.exception.DateTimeParseException;
import com.nlmk.kb.server.exception.KafkaMessageProcessingException;
import com.nlmk.kb.server.exception.KafkaRestConfigException;
import com.nlmk.kb.server.exception.RemoteServiceSenderException;
import com.nlmk.kb.server.service.mes.MesCommonService;
import com.nlmk.kb.server.service.mes.MesMessageAdapter;
import com.nlmk.kb.server.service.mes.MesMessageService;
import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import nlmk.mes.cgp.asap.adapter.analysis.request.v0.AsapAnalysisRequestVer0;
import nlmk.mes.cgp.asap.adapter.analysis.request.v0.EnumOp;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.Objects;

import static com.nlmk.kb.server.config.KbConstants.LISTENER_EXC_MESSAGE_TEMPLATE;
import static com.nlmk.kb.server.config.KbConstants.MISSING_ATT_RESULT_MESSAGE_TEMPLATE;
import static com.nlmk.kb.server.config.KbConstants.TEMPLATE_METAL_UNIT_ID;

@Slf4j
@Service
public class MesPgpKafkaService {

    private final long sleepTime;
    private final MesCommonService mesCommonService;
    private final MesMessageAdapter<AsapAnalysisRequestVer0> mesMessageAdapter;
    private final MesMessageService mesMessageService;

    public MesPgpKafkaService(@Value("${kafka.ack.nack.sleep-time}") long sleepTime,
                                MesCommonService mesCommonService,
                                MesMessageAdapter<AsapAnalysisRequestVer0> mesMessageAdapter,
                                MesMessageService mesMessageService) {
        this.sleepTime = sleepTime;
        this.mesCommonService = mesCommonService;
        this.mesMessageAdapter = mesMessageAdapter;
        this.mesMessageService = mesMessageService;
    }

    @KafkaListener(containerFactory = "mesPgpKafkaListenerContainerFactory",
            topics = {"${kafka.mes.pgp.topicReq}"}
    )
    @Timed(value = "kafka_listener", percentiles = {0.99, 0.95})
    public void receiveMessageReq(@Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                  @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String key,
                                  @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
                                  @Header(KafkaHeaders.OFFSET) int offset,
                                  @Header(KafkaHeaders.RECEIVED_TIMESTAMP) String timestamp,
                                  @Payload AsapAnalysisRequestVer0 request,
                                  Acknowledgment ack) {

        log.info("receiveMessageReq (MES PGP): topic [{}], partition [{}], offset [{}], key [{}], timestamp [{}], request.ts [{}], request.op [{}], request.pk.metalUnitId [{}]",
                topic, partition, offset, key, timestamp, request.getTs(), request.getOp(), request.getPk().getMetalUnitId());

        try {

            final var requestMessage = mesMessageAdapter.adapt(request, topic, key, partition, offset);

            if (request.getOp() == EnumOp.D
                    || requestMessage.getRequest().getValue() == null
                    || requestMessage.getRequest().getValue().getData() == null) {
                log.warn("receiveMessageReq (MES PGP), аттестация не выполняется - некорректные Op или Data, partition {}, offset {}, key {}", partition, offset, key);
            } else {
                // отправка запроса при наличии тела и правильной операции
                final var attResult = mesCommonService.postAttestation(requestMessage);
                if (attResult.isEmpty() || Objects.isNull(attResult.get().getResult())) {
                    log.warn("receiveMessageReq (MES PGP), нет результата аттестации для metalUnitId {}, partition {}, offset {}, key {}",
                            requestMessage.getMetalUnitId(), partition, offset, key);
                    throw new AttestationResultException(String.format(MISSING_ATT_RESULT_MESSAGE_TEMPLATE, TEMPLATE_METAL_UNIT_ID, requestMessage.getMetalUnitId()));
                }

                if (!CollectionUtils.isEmpty(attResult.get().getResult().getRequests())
                        && Objects.nonNull(attResult.get().getResult().getRequests().get(0).getId())) {
                    var resultRequest = attResult.get().getResult().getRequests().get(0);
                    mesMessageService.saveSourceMessage(resultRequest.getId(), resultRequest.getPrimeID(), request.toString(), LocalDateTime.now());
                }
                // отправка ответа с результатами аттестации
                // TODO реализовать отправку результатов
                // attestationResultSender.send(attResult.get(), VerificationResults.class);
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
            ack.nack(sleepTime);
            throw new RemoteServiceSenderException(MessageFormat.format(LISTENER_EXC_MESSAGE_TEMPLATE, e));
        } catch (Exception e) {
            log.warn("receiveMessageReq, Exception", e);
            ack.nack(sleepTime);
            throw new KafkaMessageProcessingException(MessageFormat.format(LISTENER_EXC_MESSAGE_TEMPLATE, e));
        }

    }

}
