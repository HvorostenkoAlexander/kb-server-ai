package com.nlmk.kb.server.service.zifra;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nlmk.kb.server.exception.ZifraMessageParserException;
import nlmk.l3.nsi.zifra.EnumOp;
import nlmk.l3.nsi.zifra.Reason;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
class ZifraMessageHandlerTest {

    @Autowired
    private ZifraMessageHandler zifraMessageHandler;

    @Autowired
    private MdmMessageConverter messageConverter;

    @MockBean
    private MdmMessageService messageService;

    private static MockWebServer mockWebServer;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${kafka.zifra.topic.sp-customer}")
    private String spCustomerTopic;

    @Value("${kafka.zifra.topic.sp-customer-group}")
    private String spCustomerGroupTopic;

    @Value("${kafka.zifra.topic.sp-group-and-customer}")
    private String spGroupAndCustomerTopic;


    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry dpr) {
        dpr.add("service-web-client.nsi-server.url", () -> "http://localhost:" + mockWebServer.getPort());
    }

    @BeforeAll
    static void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    private ConsumerRecord<Object, Object> prepareConsumerRecord(String topic, String path) throws IOException {
        final var json = new String(Files.readAllBytes(Path.of(path)));
        final var value = objectMapper.readValue(json, Reason.class);
        final var key = "key~" + Instant.now().getEpochSecond();

        return new ConsumerRecord<>(topic, 0, 0, key, value);
    }

    @Test
    void parseAndSend() throws Exception {
        final var spCustomerRecord = prepareConsumerRecord(spCustomerTopic,"src/test/resources/json/SpCustomerExample.json");
        assertThat(spCustomerRecord).isNotNull();
        final var spCustomer = (Reason) spCustomerRecord.value();

        final var spCustomerGroupRecord = prepareConsumerRecord(spCustomerGroupTopic,"src/test/resources/json/SpCustomerGroupExample.json");
        assertThat(spCustomerGroupRecord).isNotNull();
        final var spCustomerGroup = (Reason) spCustomerGroupRecord.value();

        final var spGroupAndCustomerRecord = prepareConsumerRecord(spGroupAndCustomerTopic,"src/test/resources/json/SpGroupAndCustomerExample.json");
        assertThat(spGroupAndCustomerRecord).isNotNull();
        final var spGroupAndCustomer = (Reason) spGroupAndCustomerRecord.value();

        {
            mockWebServer.enqueue(new MockResponse()
                    .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .setResponseCode(HttpStatus.OK.value())
                    .setBody(spCustomer.getPk().getLineId().toString())
            );

            when(messageService.save(any())).thenReturn(Optional.of(messageConverter.fromConsumerRecord(spCustomerRecord)));
            final var response = zifraMessageHandler.handleConsumerRecord(spCustomerRecord);
            assertThat(response).isTrue();

            RecordedRequest request = mockWebServer.takeRequest();
            assertThat(request.getMethod()).isEqualTo("POST");
            assertThat(request.getPath()).isEqualTo("/nsi/dict/mdm/sp_customer");
        }
        {
            mockWebServer.enqueue(new MockResponse()
                    .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .setResponseCode(HttpStatus.OK.value())
                    .setBody(spCustomerGroup.getPk().getLineId().toString())
            );

            when(messageService.save(any())).thenReturn(Optional.of(messageConverter.fromConsumerRecord(spCustomerGroupRecord)));
            final var response = zifraMessageHandler.handleConsumerRecord(spCustomerGroupRecord);
            assertThat(response).isTrue();

            RecordedRequest request = mockWebServer.takeRequest();
            assertThat(request.getMethod()).isEqualTo("POST");
            assertThat(request.getPath()).isEqualTo("/nsi/dict/mdm/sp_customer_group");
        }
        {
            mockWebServer.enqueue(new MockResponse()
                    .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .setResponseCode(HttpStatus.OK.value())
                    .setBody(spGroupAndCustomer.getPk().getLineId().toString())
            );

            when(messageService.save(any())).thenReturn(Optional.of(messageConverter.fromConsumerRecord(spGroupAndCustomerRecord)));
            final var response = zifraMessageHandler.handleConsumerRecord(spGroupAndCustomerRecord);
            assertThat(response).isTrue();

            RecordedRequest request = mockWebServer.takeRequest();
            assertThat(request.getMethod()).isEqualTo("POST");
            assertThat(request.getPath()).isEqualTo("/nsi/dict/mdm/sp_group_and_customer");
        }
        {
            // обновление
            spCustomer.setOp(EnumOp.U);
            mockWebServer.enqueue(new MockResponse()
                    .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .setResponseCode(HttpStatus.OK.value())
            );

            when(messageService.save(any())).thenReturn(Optional.of(messageConverter.fromConsumerRecord(spCustomerRecord)));
            final var response = zifraMessageHandler.handleConsumerRecord(spCustomerRecord);
            assertThat(response).isTrue();

            RecordedRequest request = mockWebServer.takeRequest();
            assertThat(request.getMethod()).isEqualTo("PUT");
            assertThat(request.getPath()).isEqualTo("/nsi/dict/mdm/sp_customer");
        }
        {
            // удаление
            spCustomer.setOp(EnumOp.D);
            mockWebServer.enqueue(new MockResponse()
                    .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .setResponseCode(HttpStatus.NOT_FOUND.value())
            );

            when(messageService.save(any())).thenReturn(Optional.of(messageConverter.fromConsumerRecord(spCustomerRecord)));
            final var response = zifraMessageHandler.handleConsumerRecord(spCustomerRecord);
            assertThat(response).isTrue();

            RecordedRequest request = mockWebServer.takeRequest();
            assertThat(request.getMethod()).isEqualTo("DELETE");
            assertThat(request.getPath()).isEqualTo("/nsi/dict/mdm/sp_customer");
        }
        {
            // ошибка обработки: сломаем значение
            spGroupAndCustomer.getData().getLineAttributes().forEach(a -> {
                if (a.getAttrNameEng().equals("priority")) {
                    a.setAttrValue("2A");
                }
            });
            when(messageService.save(any())).thenReturn(Optional.of(messageConverter.fromConsumerRecord(spCustomerRecord)));
            final var modifyRecord = new ConsumerRecord<Object, Object>(spGroupAndCustomerTopic, 0, 0, "key", spGroupAndCustomer);

            assertThatThrownBy(() -> zifraMessageHandler.handleConsumerRecord(modifyRecord))
                    .isInstanceOf(ZifraMessageParserException.class)
                    .hasMessage("Ошибка преобразования строки \"2A\" в целое число");
        }
    }

}
