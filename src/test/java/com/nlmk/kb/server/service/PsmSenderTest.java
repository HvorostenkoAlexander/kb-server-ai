package com.nlmk.kb.server.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nlmk.attestation.zorder.ZORDERS051;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.client.HttpClientErrorException;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class PsmSenderTest {

    @Autowired
    private PsmSender psmSender;
    @Autowired
    private ObjectMapper objectMapper;

    static MockWebServer mockWebServer;

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry dpr) {
        dpr.add("psm.url", () -> "http://localhost:" + mockWebServer.getPort());
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

    @Test
    void postZorderTest() throws InterruptedException, IOException {

        mockWebServer.enqueue(new MockResponse()
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setResponseCode(HttpStatus.BAD_REQUEST.value())
        );

        final ZORDERS051 zorders051 = objectMapper.readValue(new ClassPathResource("json/zordersExample.json").getFile(), ZORDERS051.class);

        assertThrows(HttpClientErrorException.class, () -> psmSender.postZorder(zorders051));

        mockWebServer.takeRequest();

        mockWebServer.enqueue(new MockResponse()
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setResponseCode(HttpStatus.OK.value())
                .setBody("100")
        );

        var result = psmSender.postZorder(zorders051);

        RecordedRequest request2 = mockWebServer.takeRequest();
        assertEquals("POST", request2.getMethod());
        assertEquals("/sap/order", request2.getPath());
        assertEquals(100, result);

        String body = request2.getBody().readUtf8();
        final ZORDERS051 zorderFromBody = objectMapper.readValue(body, ZORDERS051.class);
        assertEquals("0040452892", zorderFromBody.getIDOC().getE1EDK01().getBELNR());
    }
}
