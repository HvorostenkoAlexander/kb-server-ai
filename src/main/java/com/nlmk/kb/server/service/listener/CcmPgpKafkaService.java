package com.nlmk.kb.server.service.listener;

import com.nlmk.kb.server.api.IntegralParamsRequest;
import com.nlmk.kb.server.entity.CcmMessage;
import com.nlmk.kb.server.entity.integral.IntegralParams;
import com.nlmk.kb.server.exception.AttestationResultException;
import com.nlmk.kb.server.exception.AttestationResultSenderException;
import com.nlmk.kb.server.exception.DateTimeParseException;
import com.nlmk.kb.server.exception.KafkaMessageProcessingException;
import com.nlmk.kb.server.exception.KafkaRestConfigException;
import com.nlmk.kb.server.exception.RemoteServiceSenderException;
import com.nlmk.kb.server.service.ccm.CcmCommonService;
import com.nlmk.kb.server.service.ccm.CcmMessageAdapter;
import com.nlmk.kb.server.service.ccm.CcmMessageService;
import com.nlmk.kb.server.service.integral.IntegralParamsMessageService;
import com.nlmk.kb.server.service.result.sending.AttestationResultSender;
import com.nlmk.kb.server.service.sender.PgpSender;
import io.micrometer.core.annotation.Timed;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import nlmk.l3.apcs.VerificationResults;
import nlmk.l3.ccm.pgp.AttestationRequest;
import nlmk.l3.ccm.pgp.EnumOp;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import static com.nlmk.kb.server.config.KbConstants.LISTENER_EXC_MESSAGE_TEMPLATE;
import static com.nlmk.kb.server.config.KbConstants.MISSING_ATT_RESULT_MESSAGE_TEMPLATE;
import static com.nlmk.kb.server.config.KbConstants.TEMPLATE_PRIME_ID;

@Profile("!test")
@Slf4j
@Service
public class CcmPgpKafkaService {

    // В будущем заменить value значениями из SpecCode product-api
    private static final List<IntegralParams> INTEGRAL_PARAMS_ATTRS = List.of(
            new IntegralParams(9617), new IntegralParams(9615),
            new IntegralParams(9653), new IntegralParams(9654),
            new IntegralParams(18), new IntegralParams(9666),
            new IntegralParams(9767), new IntegralParams(17),
            new IntegralParams(15), new IntegralParams(16));

    private final long sleepTime;
    private final CcmCommonService ccmCommonService;
    private final CcmMessageAdapter<AttestationRequest> ccmMessageAdapter;
    private final AttestationResultSender attestationResultSender;
    private final CcmMessageService ccmMessageService;
    private final PgpSender pgpSender;
    private final IntegralParamsMessageService integralParamsMessageService;

    public CcmPgpKafkaService(@Value("${kafka.ack.nack.sleep-time}") long sleepTime,
                              CcmCommonService ccmCommonService,
                              CcmMessageAdapter<AttestationRequest> ccmMessageAdapter,
                              AttestationResultSender attestationResultSender,
                              CcmMessageService ccmMessageService,
                              PgpSender pgpSender,
                              IntegralParamsMessageService integralParamsMessageService) {
        this.sleepTime = sleepTime;
        this.ccmCommonService = ccmCommonService;
        this.ccmMessageAdapter = ccmMessageAdapter;
        this.attestationResultSender = attestationResultSender;
        this.ccmMessageService = ccmMessageService;
        this.pgpSender = pgpSender;
        this.integralParamsMessageService = integralParamsMessageService;
    }

    @KafkaListener(containerFactory = "ccmPgpKafkaListenerContainerFactory",
            topics = {"${kafka.ccm.pgp.topicReq}"}
    )
    @Timed(value = "kafka_listener", percentiles = {0.99, 0.95})
    public void receiveMessageReq(@Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                  @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String key,
                                  @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
                                  @Header(KafkaHeaders.OFFSET) int offset,
                                  @Header(KafkaHeaders.RECEIVED_TIMESTAMP) String timestamp,
                                  @Payload AttestationRequest request,
                                  Acknowledgment ack) {

        log.info("receiveMessageReq (CCM PGP): topic [{}], partition [{}], offset [{}], key [{}], timestamp [{}], request.ts [{}], request.op [{}], request.pk.id [{}]",
                topic, partition, offset, key, timestamp, request.getTs(), request.getOp(), request.getPk().getId());

        try {
            final var requestMessage = ccmMessageAdapter.adapt(request, topic, key, partition, offset);

            if (request.getOp() == EnumOp.D
                    || requestMessage.getRequest().getValue() == null
                    || requestMessage.getRequest().getValue().getData() == null) {
                log.warn("receiveMessageReq (CCM PGP), аттестация не выполняется - некорректные Op или Data, partition {}, offset {}, key {}", partition, offset, key);
            } else {
                processIntegralParams(requestMessage);
                // отправка запроса при наличии тела и правильной операции
                final var attResult = ccmCommonService.postAttestation(requestMessage);
                if (attResult.isEmpty() || Objects.isNull(attResult.get().getResult())) {
                    log.warn("receiveMessageReq (CCM PGP), нет результата аттестации для primeId {}, partition {}, offset {}, key {}",
                            requestMessage.getPrimeId(), partition, offset, key);
                    throw new AttestationResultException(String.format(MISSING_ATT_RESULT_MESSAGE_TEMPLATE, TEMPLATE_PRIME_ID, requestMessage.getPrimeId()));
                }

                if (!CollectionUtils.isEmpty(attResult.get().getResult().getRequests())
                        && Objects.nonNull(attResult.get().getResult().getRequests().get(0).getId())) {
                    var resultRequest = attResult.get().getResult().getRequests().get(0);
                    ccmMessageService.saveSourceMessage(resultRequest.getId(), resultRequest.getPrimeID(), request.toString(), LocalDateTime.now());
                }
                // отправка ответа с результатами аттестации
                attestationResultSender.send(attResult.get(), VerificationResults.class);
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

    private void processIntegralParams(CcmMessage ccmMessage) {
        var integralParamsRequest = IntegralParamsRequest.builder()
                .materialIds(List.of(ccmMessage.getPrimeId()))
                .integralParameters(INTEGRAL_PARAMS_ATTRS)
                .build();

        var response = pgpSender.getIntegralParams(integralParamsRequest);

        if (!CollectionUtils.isEmpty(response)) {
            integralParamsMessageService.save(response.get(0));
        }
    }

}
