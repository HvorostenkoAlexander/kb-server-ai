package com.nlmk.kb.server.service.sap;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nlmk.attestation.zorder.ZORDERS051;
import com.nlmk.kb.server.entity.SapMessage;
import com.nlmk.kb.server.entity.SapMessageState;
import com.nlmk.kb.server.exception.S3ClientException;
import com.nlmk.kb.server.repository.SapMessageRepository;
import com.nlmk.kb.server.service.sender.PsmSender;
import com.nlmk.s3.proxy.s3notification;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.reset;

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

    @Test
    void handleConsumerRecord() throws IOException {

        s3notification kafkaMessage = com.nlmk.s3.proxy.s3notification.newBuilder()
                .setStorageType("s3")
                .setServer("s3.xxx.com")
                .setPath("zordersExample.xml")
                .setBucket("test bucket")
                .setProcessorVersion("1.0")
                .setTs("1652872288900")
                .build();

        ConsumerRecord<String, s3notification> consumerRecord = new ConsumerRecord<>(
                "topic", 1, 0, null, kafkaMessage
        );

        final String s3zordersExample = Arrays.toString(new ClassPathResource("xml/zordersExample.xml").getInputStream().readAllBytes());
        final ZORDERS051 parsedZorder = objectMapper.readValue(new ClassPathResource("json/zordersExample.json").getFile(), ZORDERS051.class);

        // ошибка получения файла из S3 хранилища

        when(s3Service.getObjectAsString(any(), any())).thenThrow(new S3ClientException("---"));

        var res1 = sapMessageHandler.handleConsumerRecord(consumerRecord);

        assertFalse(res1);
        List<SapMessage> sapMessages1 = sapMessageRepository.findAll();
        assertEquals(1, sapMessages1.size());
        SapMessage sapMessage1 = sapMessages1.get(0);
        assertNull(sapMessage1.getOrder());
        assertEquals(SapMessageState.NEW, sapMessage1.getState());

        // файла из S3 хранилища получен, но некорректный

        reset(s3Service);

        when(s3Service.getObjectAsString(any(), any())).thenReturn(s3zordersExample);
        when(s3Service.getZorder(any())).thenThrow(new S3ClientException("---"));

        var res2 = sapMessageHandler.handleConsumerRecord(consumerRecord);

        assertTrue(res2);
        List<SapMessage> sapMessages2 = sapMessageRepository.findAll();
        assertEquals(1, sapMessages2.size());
        SapMessage sapMessage2 = sapMessages2.get(0);
        assertNotNull(sapMessage2.getOrder());
        assertEquals(SapMessageState.ERROR, sapMessage2.getState());

        verify(psmSender, never()).postZorder(any());

        // уведомления в статусах ERROR повторно не обрабатываются

        reset(s3Service);

        when(s3Service.getObjectAsString(any(), any())).thenThrow(new S3ClientException("---"));
        when(s3Service.getZorder(any())).thenThrow(new S3ClientException("---"));

        var res3 = sapMessageHandler.handleConsumerRecord(consumerRecord);

        assertTrue(res3);
        List<SapMessage> sapMessages3 = sapMessageRepository.findAll();
        assertEquals(1, sapMessages3.size());
        SapMessage sapMessage3 = sapMessages3.get(0);
        assertNotNull(sapMessage3.getOrder());
        assertEquals(SapMessageState.ERROR, sapMessage3.getState());

        verify(psmSender, never()).postZorder(any());

        // ошибка передачи заказа в psm

        sapMessageRepository.deleteAll();
        reset(s3Service);

        when(s3Service.getObjectAsString(any(), any())).thenReturn(s3zordersExample);
        when(s3Service.getZorder(any())).thenReturn(parsedZorder);
        when(psmSender.postZorder(any(ZORDERS051.class))).thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

        var res4 = sapMessageHandler.handleConsumerRecord(consumerRecord);

        assertFalse(res4);
        List<SapMessage> sapMessages4 = sapMessageRepository.findAll();
        assertEquals(1, sapMessages4.size());
        SapMessage sapMessage4 = sapMessages4.get(0);
        assertNotNull(sapMessage4.getOrder());
        assertEquals(SapMessageState.NEW, sapMessage4.getState());

        verify(psmSender, times(1)).postZorder(any(ZORDERS051.class));

        // заказ передан в psm

        reset(s3Service);
        reset(psmSender);

        when(s3Service.getObjectAsString(any(), any())).thenReturn(s3zordersExample);
        when(s3Service.getZorder(any())).thenReturn(parsedZorder);
        when(psmSender.postZorder(any(ZORDERS051.class))).thenReturn(10);

        var res5 = sapMessageHandler.handleConsumerRecord(consumerRecord);

        assertTrue(res5);
        List<SapMessage> sapMessages5 = sapMessageRepository.findAll();
        assertEquals(1, sapMessages5.size());
        SapMessage sapMessage5 = sapMessages5.get(0);
        assertNotNull(sapMessage5.getOrder());
        assertEquals(SapMessageState.DONE, sapMessage5.getState());

        final ArgumentCaptor<ZORDERS051> captor5 = ArgumentCaptor.forClass(ZORDERS051.class);
        verify(psmSender, times(1)).postZorder(captor5.capture());

        assertEquals("0040452892", captor5.getValue().getIDOC().getE1EDK01().getBELNR());

        // почистить
        sapMessageRepository.deleteAll();
    }

}
