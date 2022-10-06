package com.nlmk.kb.server.service.sap;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nlmk.attestation.product.api.specification.SapOrderPosCode;
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
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
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

    @Test
    void garbageCleaningTest() throws IOException, JAXBException {
        S3Service localService = new S3ServiceImpl(null);

        s3notification kafkaMessage = s3notification.newBuilder()
                .setStorageType("s3")
                .setServer("s3.xxx.com")
                .setPath("zordersTestVariant.xml")
                .setBucket("test bucket")
                .setProcessorVersion("1.0")
                .setTs("1652872288900")
                .build();

        ConsumerRecord<String, s3notification> consumerRecord = new ConsumerRecord<>(
                "topic", 1, 0, null, kafkaMessage
        );

        final String xmlZOrder = new String(Files.readAllBytes(Path.of("src/test/resources/xml/zordersTestVariant.xml")));
        ZORDERS051 zorder = localService.getZorder(xmlZOrder);

        when(s3Service.getObjectAsString(any(), any())).thenReturn(xmlZOrder);
        when(s3Service.getZorder(any())).thenReturn(zorder);
        when(psmSender.postZorder(any(ZORDERS051.class))).thenReturn(100);

        var res = sapMessageHandler.handleConsumerRecord(consumerRecord);
        assertTrue(res);

        ArgumentCaptor<ZORDERS051> zorderCaptor = ArgumentCaptor.forClass(ZORDERS051.class);
        verify(psmSender, times(1)).postZorder(zorderCaptor.capture());

        final ZORDERS051 zorderSended = zorderCaptor.getValue();
        assertEquals("40434341", zorderSended.getIDOC().getE1EDK01().getBELNR());

        zorderSended.getIDOC().getE1CUCFG().forEach(
                e1cucfg -> e1cucfg.getE1CUVAL().forEach(
                        e1cuval -> {
                            // все коды, которые есть в заказе
                            SapOrderPosCode code = SapOrderPosCode.valueOf(e1cuval.getCHARC());
                            switch (code) {
                                case STNDRT_PROD:
                                case STNDRT_MARKA:
                                case STNDRT_SORT:
                                    assertEquals("ГОСТ 14918-2020", e1cuval.getVALUE());
                                    break;
                                case CEH_PROD:
                                    assertEquals("11", e1cuval.getVALUE());
                                    break;
                                case MARKA:
                                    assertEquals("02", e1cuval.getVALUE());
                                    break;
                                case VID_POSTAVKI:
                                    assertEquals("РЛН", e1cuval.getVALUE());
                                    break;
                                case KROM:
                                    assertEquals("НО", e1cuval.getVALUE());
                                    break;
                                case TPRK:
                                    assertEquals("ТУ 0027", e1cuval.getVALUE());
                                    break;
                                case TEXK:
                                case RABPL:
                                case GROT:
                                case ROUTE_TK:
                                    assertNull(e1cuval.getVALUE(),
                                            MessageFormat.format("not NULL value in code {0}", code));
                                    break;
                                case SHOT_MIN:
                                    assertEquals("1250.0", e1cuval.getVALUE());
                                    break;
                                case SHOT_MAX:
                                    assertEquals("1234567.8", e1cuval.getVALUE());
                                    break;
                                case DLIN_MIN:
                                    assertEquals("6000", e1cuval.getVALUE());
                                    break;
                                case DLIN_MAX:
                                    assertEquals("3000.0", e1cuval.getVALUE());
                                    break;
                                case VN_DIAM_RL:
                                    assertEquals("600", e1cuval.getVALUE());
                                    break;
                            }
                        }
                )
        );

        reset(s3Service);
        reset(psmSender);
        // почистить
        sapMessageRepository.deleteAll();
    }

}
