package com.nlmk.kb.server.service.sap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nlmk.attestation.zmmorder.ZMMORDERS05DOP;
import com.nlmk.attestation.zorder.ZORDERS051;
import com.nlmk.kb.server.entity.SapMessage;
import com.nlmk.kb.server.entity.SapMessageState;
import com.nlmk.kb.server.exception.S3ClientException;
import com.nlmk.kb.server.repository.SapMessageRepository;
import com.nlmk.kb.server.service.sender.PsmSender;
import com.nlmk.s3.proxy.s3notification;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.util.DateUtil.now;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class SapMessageHandlerTest {

    @Autowired
    private SapMessageHandler sapMessageHandler;
    @Autowired
    private SapMessageRepository sapMessageRepository;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private S3Service s3Service;
    @MockBean
    private PsmSender psmSender;

    private static final String ZORDERS_BUCKET_NAME = "test bucket";
    private static final String ZMMORDERS_BUCKET_NAME = "another test bucket";

    private static final s3notification ZORDERS_NOTIFICATION = com.nlmk.s3.proxy.s3notification.newBuilder()
            .setStorageType("s3").setServer("s3.xxx.com").setPath("zordersExample.xml")
            .setBucket(ZORDERS_BUCKET_NAME)
            .setProcessorVersion("1.0")
            .setTs("1652872288900")
            .build();

    private static final s3notification ZMMORDERS_NOTIFICATION = com.nlmk.s3.proxy.s3notification.newBuilder()
            .setStorageType("s3").setServer("s3.xxx.com").setPath("zmmordersExample.xml")
            .setBucket(ZMMORDERS_BUCKET_NAME)
            .setProcessorVersion("1.0")
            .setTs("1652872288900")
            .build();

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(sapMessageHandler, "idoczordrsBucketName", ZORDERS_BUCKET_NAME);
    }

    @AfterEach
    void tearDown() {
        sapMessageRepository.deleteAll();
    }

    @Test
    void handleConsumerRecordForZorderShouldReturnFalseAndSaveNullOrderWithStatusNewIfEncounteredS3ClientExceptionDuringBucketAccess() {
        // given
        when(s3Service.getObjectAsStringFromBucket(any(), any())).thenThrow(new S3ClientException("---"));

        // when
        var res = sapMessageHandler.handleConsumerRecord(createConsumerRecord(ZORDERS_NOTIFICATION));

        // then
        assertThat(res).isFalse();
        List<SapMessage> sapMessages = sapMessageRepository.findAll();
        assertThat(sapMessages).hasSize(1);
        SapMessage sapMessage = sapMessages.get(0);
        assertThat(sapMessage.getOrder()).isNull();
        assertThat(sapMessage.getState()).isEqualTo(SapMessageState.NEW);
    }

    @Test
    void handleConsumerRecordForZorderShouldReturnTrueIfAndSaveNonNullOrderWithStatusErrorIfEncounteredS3ClientExceptionDuringUnmarshalling() throws IOException {
        // given
        final String s3zordersExample = Arrays.toString(new ClassPathResource("xml/zordersExample.xml").getInputStream().readAllBytes());
        when(s3Service.getObjectAsStringFromBucket(any(), any())).thenReturn(s3zordersExample);
        // файла из S3 хранилища получен, но некорректный
        when(s3Service.unmarshalZorder(any())).thenThrow(new S3ClientException("---"));

        // when
        var res = sapMessageHandler.handleConsumerRecord(createConsumerRecord(ZORDERS_NOTIFICATION));

        // then
        assertThat(res).isTrue();
        List<SapMessage> sapMessages = sapMessageRepository.findAll();
        assertThat(sapMessages).hasSize(1);
        SapMessage sapMessage = sapMessages.get(0);
        assertThat(sapMessage.getOrder()).isNotNull();
        assertThat(sapMessage.getState()).isEqualTo(SapMessageState.ERROR);

        verify(psmSender, never()).postZorder(any());
    }

    @ParameterizedTest
    @MethodSource("nonRetryableSapMessageStatuses")
    void handleConsumerRecordShouldNotProcessNotificationThatWasPreviouslyProcessedAndSavedInNonRetryableState(SapMessageState state) throws JsonProcessingException {
        // given
        sapMessageRepository.save(SapMessage.builder()
                .topic("topic").partition(1).offset(0L).key(null)
                .bucket(ZORDERS_BUCKET_NAME).path("path")
                .processorVersion("processorVersion").server("server")
                .order("mock order. contents are unimportant for this test").orderNum("orderNum")
                .ts(now())
                .state(state)
                .build());

        // when
        var res = sapMessageHandler.handleConsumerRecord(createConsumerRecord(ZORDERS_NOTIFICATION));

        // then
        assertThat(res).isTrue();
        List<SapMessage> sapMessages = sapMessageRepository.findAll();
        assertThat(sapMessages).hasSize(1);
        SapMessage sapMessage = sapMessages.get(0);
        assertThat(sapMessage.getOrder()).isNotNull();
        assertThat(sapMessage.getState()).isEqualTo(state);

        verify(psmSender, never()).postZorder(any());
    }

    @Test
    void handleConsumerRecordForZorderCanTryToProcessNotificationAgain() throws IOException {
        // given
        sapMessageRepository.save(SapMessage.builder()
                .topic("topic").partition(1).offset(0L).key(null)
                .bucket(ZORDERS_BUCKET_NAME).path("path")
                .processorVersion("processorVersion").server("server")
                .order("mock order. contents are unimportant for this test").orderNum("orderNum")
                .ts(now())
                .state(SapMessageState.NEW)
                .build());
        final ZORDERS051 parsedZorder = objectMapper.readValue(new ClassPathResource("json/zordersExample.json").getFile(), ZORDERS051.class);
        when(s3Service.unmarshalZorder(any())).thenReturn(parsedZorder);
        when(psmSender.postZorder(any(ZORDERS051.class))).thenReturn(10);

        // when
        var res = sapMessageHandler.handleConsumerRecord(createConsumerRecord(ZORDERS_NOTIFICATION));

        // then
        assertThat(res).isTrue();
        List<SapMessage> sapMessages = sapMessageRepository.findAll();
        assertThat(sapMessages).hasSize(1);
        SapMessage sapMessage = sapMessages.get(0);
        assertThat(sapMessage.getOrder()).isNotNull();
        assertThat(sapMessage.getState()).isEqualTo(SapMessageState.DONE);
        final ArgumentCaptor<ZORDERS051> captor = ArgumentCaptor.forClass(ZORDERS051.class);
        verify(psmSender, times(1)).postZorder(captor.capture());
        assertThat(captor.getValue().getIDOC().getE1EDK01().getBELNR()).isEqualTo("0040452892");
    }

    @Test
    void canHandleConsumerRecordForZorder() throws IOException {
        // given
        final String s3zordersExample = Arrays.toString(new ClassPathResource("xml/zordersExample.xml").getInputStream().readAllBytes());
        final ZORDERS051 parsedZorder = objectMapper.readValue(new ClassPathResource("json/zordersExample.json").getFile(), ZORDERS051.class);
        when(s3Service.getObjectAsStringFromBucket(any(), any())).thenReturn(s3zordersExample);
        when(s3Service.unmarshalZorder(any())).thenReturn(parsedZorder);
        when(psmSender.postZorder(any(ZORDERS051.class))).thenReturn(10);

        // when
        var res = sapMessageHandler.handleConsumerRecord(createConsumerRecord(ZORDERS_NOTIFICATION));

        // then
        assertThat(res).isTrue();
        List<SapMessage> sapMessages = sapMessageRepository.findAll();
        assertThat(sapMessages).hasSize(1);
        SapMessage sapMessage = sapMessages.get(0);
        assertThat(sapMessage.getOrder()).isNotNull();
        assertThat(sapMessage.getState()).isEqualTo(SapMessageState.DONE);

        final ArgumentCaptor<ZORDERS051> captor = ArgumentCaptor.forClass(ZORDERS051.class);
        verify(psmSender, times(1)).postZorder(captor.capture());
        assertThat(captor.getValue().getIDOC().getE1EDK01().getBELNR()).isEqualTo("0040452892");
    }

    @Test
    void canHandleConsumerRecordForZmmorder() throws IOException {
        // given
        final String s3zmmordersExample = Arrays.toString(new ClassPathResource("xml/zmmordersExample.xml").getInputStream().readAllBytes());
        final ZMMORDERS05DOP parsedZmmorder = objectMapper.readValue(new ClassPathResource("json/zmmordersExample.json").getFile(), ZMMORDERS05DOP.class);
        when(s3Service.getObjectAsStringFromBucket(any(), any())).thenReturn(s3zmmordersExample);
        when(s3Service.unmarshalZmmorder(any())).thenReturn(parsedZmmorder);
        when(psmSender.postZmmorder(any(ZMMORDERS05DOP.class))).thenReturn(10);

        // when
        var res = sapMessageHandler.handleConsumerRecord(createConsumerRecord(ZMMORDERS_NOTIFICATION));

        // then
        assertThat(res).isTrue();
        List<SapMessage> sapMessages = sapMessageRepository.findAll();
        assertThat(sapMessages).hasSize(1);
        SapMessage sapMessage = sapMessages.get(0);
        assertThat(sapMessage.getOrder()).isNotNull();
        assertThat(sapMessage.getState()).isEqualTo(SapMessageState.DONE);

        final ArgumentCaptor<ZMMORDERS05DOP> captor = ArgumentCaptor.forClass(ZMMORDERS05DOP.class);
        verify(psmSender, times(1)).postZmmorder(captor.capture());
        assertThat(captor.getValue().getIDOC().getE1EDK01().getBELNR()).isEqualTo("4500745477");
    }

    private static Stream<Arguments> nonRetryableSapMessageStatuses() {
        // уведомления в статусах ERROR и DONE повторно не обрабатываются
        return Stream.of(Arguments.of(SapMessageState.ERROR), Arguments.of(SapMessageState.DONE));
    }

    private static ConsumerRecord<String, s3notification> createConsumerRecord(s3notification notification) {
        return new ConsumerRecord<>("topic", 1, 0, null, notification);
    }
}
