package com.nlmk.kb.server.service.zifra;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nlmk.kb.server.entity.Operation;
import com.nlmk.kb.server.service.sender.NsiSender;
import nlmk.l3.nsi.zifra.Reason;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ZifraMessageHandlerTest {

    @Autowired
    private ZifraMessageHandler zifraMessageHandler;
    @Autowired
    private NsiSender nsiSender;

    private static MockWebServer mockWebServer;
    private final ObjectMapper objectMapper = new ObjectMapper();

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

    private ConsumerRecord<Object, Object> prepareConsumerRecord(String path) throws IOException {
        final var json = new String(Files.readAllBytes(Path.of(path)));
        final var value = objectMapper.readValue(json, Reason.class);
        final var key = "key~" + Instant.now().getEpochSecond();

        return new ConsumerRecord<>("topic1", 0, 0, key, value);
    }

    @Test
    void parseAndSend() throws Exception {
        final var spCustomerRecord = prepareConsumerRecord("src/test/resources/json/SpCustomerExample.json");
        assertNotNull(spCustomerRecord);
        final var spCustomer = (Reason) spCustomerRecord.value();

        final var spCustomerGroupRecord = prepareConsumerRecord("src/test/resources/json/SpCustomerGroupExample.json");
        assertNotNull(spCustomerGroupRecord);
        final var spCustomerGroup = (Reason) spCustomerGroupRecord.value();

        final var spGroupAndCustomerRecord = prepareConsumerRecord("src/test/resources/json/SpGroupAndCustomerExample.json");
        assertNotNull(spGroupAndCustomerRecord);
        final var spGroupAndCustomer = (Reason) spGroupAndCustomerRecord.value();

        {
            mockWebServer.enqueue(new MockResponse()
                    .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .setResponseCode(HttpStatus.OK.value())
                    .setBody(spCustomer.getPk().getLineId().toString())
            );

            final var response = Assertions.assertDoesNotThrow(
                    () -> zifraMessageHandler.handleConsumerRecord(spCustomerRecord)
            );
            assertTrue(response);

            RecordedRequest request = mockWebServer.takeRequest();
            assertEquals("POST", request.getMethod());
            assertEquals("/nsi/dict/mdm/sp_customer", request.getPath());
        }
        {
            mockWebServer.enqueue(new MockResponse()
                    .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .setResponseCode(HttpStatus.OK.value())
                    .setBody(spCustomerGroup.getPk().getLineId().toString())
            );

            final var response = Assertions.assertDoesNotThrow(
                    () -> zifraMessageHandler.handleConsumerRecord(spCustomerGroupRecord)
            );
            assertTrue(response);

            RecordedRequest request = mockWebServer.takeRequest();
            assertEquals("POST", request.getMethod());
            assertEquals("/nsi/dict/mdm/sp_customer_group", request.getPath());
        }
        {
            mockWebServer.enqueue(new MockResponse()
                    .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .setResponseCode(HttpStatus.OK.value())
                    .setBody(spGroupAndCustomer.getPk().getLineId().toString())
            );

            final var response = Assertions.assertDoesNotThrow(
                    () -> zifraMessageHandler.handleConsumerRecord(spGroupAndCustomerRecord)
            );
            assertTrue(response);

            RecordedRequest request = mockWebServer.takeRequest();
            assertEquals("POST", request.getMethod());
            assertEquals("/nsi/dict/mdm/sp_group_and_customer", request.getPath());
        }
    }


}
