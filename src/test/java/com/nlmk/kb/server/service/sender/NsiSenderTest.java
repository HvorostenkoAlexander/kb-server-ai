package com.nlmk.kb.server.service.sender;

import com.nlmk.attestation.product.api.nsi.CEqDto;
import com.nlmk.kb.server.entity.pdm.PdmOp;
import com.nlmk.kb.server.exception.NsiSenderException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
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

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class NsiSenderTest {

    @Autowired
    private NsiSender nsiSender;
    static MockWebServer mockWebServer;

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

    @Test
    void exchange() throws Exception {
        // на примере объекта CEqDto
        final var urlDictionary = "/nsi/dict/nsd_ceq";

        {
            final var response = Assertions.assertThrows(
                    NsiSenderException.class,
                    () -> nsiSender.exchange((CEqDto) null, urlDictionary, PdmOp.I)
            );
            Assertions.assertEquals("Body is NULL", response.getMessage());
        }

        final var dto = CEqDto.builder().build();

        {
            mockWebServer.enqueue(new MockResponse()
                    .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .setResponseCode(HttpStatus.BAD_REQUEST.value())
            );
            final var response = Assertions.assertThrows(
                    NsiSenderException.class,
                    () -> nsiSender.exchange(dto, urlDictionary, PdmOp.I)
            );
            Assertions.assertEquals(String.format(
                    "exchange, send error, message [%d Bad Request from POST http://localhost:%d/nsi/dict/nsd_ceq]",
                    HttpStatus.BAD_REQUEST.value(), mockWebServer.getPort()), response.getMessage());
            mockWebServer.takeRequest();
        }
        {
            mockWebServer.enqueue(new MockResponse()
                    .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .setResponseCode(HttpStatus.OK.value())
                    .setBody("123")
            );
            final var response = Assertions.assertDoesNotThrow(
                    () -> nsiSender.exchange(dto, urlDictionary, PdmOp.I)
            );
            Assertions.assertNotNull(response);
            Assertions.assertEquals(123L, response.getBody());
            RecordedRequest request = mockWebServer.takeRequest();
            assertEquals("POST", request.getMethod());
            assertEquals(urlDictionary, request.getPath());
        }
        {
            mockWebServer.enqueue(new MockResponse()
                    .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .setResponseCode(HttpStatus.OK.value())
                    .setBody("123")
            );
            final var response = Assertions.assertDoesNotThrow(
                    () -> nsiSender.exchange(dto, urlDictionary, PdmOp.U)
            );
            Assertions.assertNotNull(response);
            Assertions.assertEquals(123L, response.getBody());
            RecordedRequest request = mockWebServer.takeRequest();
            assertEquals("PUT", request.getMethod());
            assertEquals(urlDictionary, request.getPath());
        }
        {
            mockWebServer.enqueue(new MockResponse()
                    .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .setResponseCode(HttpStatus.OK.value())
                    .setBody("123")
            );
            final var response = Assertions.assertDoesNotThrow(
                    () -> nsiSender.exchange(dto, urlDictionary, PdmOp.D)
            );
            Assertions.assertNotNull(response);
            Assertions.assertEquals(123L, response.getBody());
            RecordedRequest request = mockWebServer.takeRequest();
            assertEquals("DELETE", request.getMethod());
            assertEquals(urlDictionary, request.getPath());
        }
        {
            mockWebServer.enqueue(new MockResponse()
                    .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .setResponseCode(HttpStatus.NOT_FOUND.value())
            );
            final var response = Assertions.assertDoesNotThrow(
                    () -> nsiSender.exchange(dto, urlDictionary, PdmOp.D)
            );
            Assertions.assertNotNull(response);
            Assertions.assertEquals(0L, response.getBody());
            RecordedRequest request = mockWebServer.takeRequest();
            assertEquals("DELETE", request.getMethod());
            assertEquals(urlDictionary, request.getPath());
        }
    }

}
