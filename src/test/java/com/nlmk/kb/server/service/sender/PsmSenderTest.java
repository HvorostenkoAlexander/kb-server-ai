package com.nlmk.kb.server.service.sender;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nlmk.attestation.product.api.SadimMessageDto;
import com.nlmk.attestation.zorder.ZORDERS051;
import com.nlmk.kb.server.exception.RemoteServiceSenderException;
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

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PsmSenderTest {

    @Autowired
    private PsmSender psmSender;
    @Autowired
    private ObjectMapper objectMapper;

    static MockWebServer mockWebServer;

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry dpr) {
        dpr.add("service-web-client.psm-server.url", () -> "http://localhost:" + mockWebServer.getPort());
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

        final var zorders051 = objectMapper.readValue(new ClassPathResource("json/zordersExample.json").getFile(), ZORDERS051.class);

        assertThrows(RemoteServiceSenderException.class, () -> psmSender.postZorder(zorders051));
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

    @Test
    void postSadimMessage() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setResponseCode(HttpStatus.BAD_REQUEST.value())
        );
        final var dto = SadimMessageDto.builder().partition(0).offset(1L).key("key")
                .param(SadimMessageDto.ParamDto.builder().primeId("pi100").build())
                .build();

        final var response1 = assertThrows(RemoteServiceSenderException.class, () -> psmSender.postSadimMessage(dto));
        assertEquals("PsmSender, postSadimMessage, primeId [pi100], send error, message [PSM return code [400]]", response1.getMessage());
        mockWebServer.takeRequest();

        mockWebServer.enqueue(new MockResponse()
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setResponseCode(HttpStatus.CREATED.value())
        );

        assertDoesNotThrow(() -> psmSender.postSadimMessage(dto));
        RecordedRequest request2 = mockWebServer.takeRequest();
        assertEquals("POST", request2.getMethod());
        assertEquals("/sadim", request2.getPath());

        mockWebServer.enqueue(new MockResponse()
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setResponseCode(HttpStatus.UNAUTHORIZED.value())
        );
        final var response3 = assertThrows(RemoteServiceSenderException.class, () -> psmSender.postSadimMessage(dto));
        assertEquals("PsmSender, postSadimMessage, primeId [pi100], send error, message [PSM return code [401]]", response3.getMessage());
        mockWebServer.takeRequest();

        mockWebServer.enqueue(new MockResponse()
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setResponseCode(HttpStatus.FORBIDDEN.value())
        );
        final var response4 = assertThrows(RemoteServiceSenderException.class, () -> psmSender.postSadimMessage(dto));
        assertEquals("PsmSender, postSadimMessage, primeId [pi100], send error, message [PSM return code [403]]", response4.getMessage());
        mockWebServer.takeRequest();
    }

}
