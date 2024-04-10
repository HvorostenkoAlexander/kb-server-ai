package com.nlmk.kb.server.service.sender;

import com.nlmk.attestation.product.api.nsi.TolWidthDtDto;
import com.nlmk.kb.server.entity.Operation;
import com.nlmk.kb.server.exception.RemoteServiceSenderException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterAll;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
    void sendBodyReturnLong() throws Exception {
        // на примере объекта TolWidthDtDto
        final var urlDictionary = "/nsi/dict/width";

        {
            assertThatThrownBy(() -> nsiSender.sendBodyReturnLong((TolWidthDtDto) null, urlDictionary, Operation.I))
                    .isInstanceOf(RemoteServiceSenderException.class)
                    .hasMessage("NsiSender, exchange, пустое тело");
        }

        final var dto = TolWidthDtDto.builder().build();

        {
            mockWebServer.enqueue(new MockResponse()
                    .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .setResponseCode(HttpStatus.BAD_REQUEST.value())
            );
            assertThatThrownBy(() -> nsiSender.sendBodyReturnLong(dto, urlDictionary, Operation.I))
                    .isInstanceOf(RemoteServiceSenderException.class)
                    .hasMessage(String.format("NsiSender, exchange, ошибка при отправке "
                                    + "[%d Bad Request from POST http://localhost:%d/nsi/dict/width]",
                            HttpStatus.BAD_REQUEST.value(), mockWebServer.getPort()));
            mockWebServer.takeRequest();
        }
        {
            mockWebServer.enqueue(new MockResponse()
                    .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .setResponseCode(HttpStatus.OK.value())
                    .setBody("123")
            );
            final var response = nsiSender.sendBodyReturnLong(dto, urlDictionary, Operation.I);
            assertThat(response).isEqualTo(123L);
            RecordedRequest request = mockWebServer.takeRequest();
            assertThat(request.getMethod()).isEqualTo("POST");
            assertThat(request.getPath()).isEqualTo(urlDictionary);
        }
        {
            mockWebServer.enqueue(new MockResponse()
                    .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .setResponseCode(HttpStatus.OK.value())
                    .setBody("123")
            );
            final var response = nsiSender.sendBodyReturnLong(dto, urlDictionary, Operation.U);
            assertThat(response).isEqualTo(123L);
            RecordedRequest request = mockWebServer.takeRequest();
            assertThat(request.getMethod()).isEqualTo("PUT");
            assertThat(request.getPath()).isEqualTo(urlDictionary);
        }
        {
            mockWebServer.enqueue(new MockResponse()
                    .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .setResponseCode(HttpStatus.OK.value())
                    .setBody("123")
            );
            final var response = nsiSender.sendBodyReturnLong(dto, urlDictionary, Operation.D);
            assertThat(response).isEqualTo(123L);
            RecordedRequest request = mockWebServer.takeRequest();
            assertThat(request.getMethod()).isEqualTo("DELETE");
            assertThat(request.getPath()).isEqualTo(urlDictionary);
        }
        {
            mockWebServer.enqueue(new MockResponse()
                    .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .setResponseCode(HttpStatus.NOT_FOUND.value())
            );
            final var response = nsiSender.sendBodyReturnLong(dto, urlDictionary, Operation.D);
            assertThat(response).isNull();
            RecordedRequest request = mockWebServer.takeRequest();
            assertThat(request.getMethod()).isEqualTo("DELETE");
            assertThat(request.getPath()).isEqualTo(urlDictionary);
        }
    }

}
