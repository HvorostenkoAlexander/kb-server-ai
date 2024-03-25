package com.nlmk.kb.server.service.sender;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nlmk.attestation.product.api.SadimMessageDto;
import com.nlmk.attestation.zmmorder.ZMMORDERS05DOP;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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

    private static final SadimMessageDto SADIM_MESSAGE_DTO = SadimMessageDto.builder().partition(0).offset(1L).key("key")
            .param(SadimMessageDto.ParamDto.builder().primeId("pi100").build())
            .build();

    @Test
    void postZorderToPsmShouldThrowRemoteServiceSenderExceptionIfResponseCodeIsBadRequest() throws IOException, InterruptedException {
        // given
        mockWebServer.enqueue(new MockResponse()
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setResponseCode(HttpStatus.BAD_REQUEST.value())
        );
        final var zorders051 = objectMapper.readValue(new ClassPathResource("json/zordersExample.json").getFile(), ZORDERS051.class);
        // when
        // then
        assertThatThrownBy(() -> psmSender.postZorder(zorders051)).isInstanceOf(RemoteServiceSenderException.class);
        mockWebServer.takeRequest();
    }

    @Test
    void canPostZorderToPsm() throws InterruptedException, IOException {
        // given
        mockWebServer.enqueue(new MockResponse()
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setResponseCode(HttpStatus.OK.value())
                .setBody("100")
        );
        final var zorders051 = objectMapper.readValue(new ClassPathResource("json/zordersExample.json").getFile(), ZORDERS051.class);

        // when
        var result = psmSender.postZorder(zorders051);

        // then
        RecordedRequest request = mockWebServer.takeRequest();
        assertThat(request.getMethod()).isEqualTo("POST");
        assertThat(request.getPath()).isEqualTo("/sap/zorder");
        assertThat(result).isEqualTo(100);

        String body = request.getBody().readUtf8();
        final ZORDERS051 zorderFromBody = objectMapper.readValue(body, ZORDERS051.class);
        assertThat(zorderFromBody.getIDOC().getE1EDK01().getBELNR()).isEqualTo("0040452892");
    }

    @Test
    void postZmmorderToPsmShouldThrowRemoteServiceSenderExceptionIfResponseCodeIsBadRequest() throws IOException, InterruptedException {
        // given
        mockWebServer.enqueue(new MockResponse()
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setResponseCode(HttpStatus.BAD_REQUEST.value())
        );
        final var zmmorder = objectMapper.readValue(new ClassPathResource("json/zmmordersExample.json").getFile(), ZMMORDERS05DOP.class);
        // when
        // then
        assertThatThrownBy(() -> psmSender.postZmmorder(zmmorder)).isInstanceOf(RemoteServiceSenderException.class);
        mockWebServer.takeRequest();
    }

    @Test
    void canPostZmmorderToPsm() throws InterruptedException, IOException {
        // given
        mockWebServer.enqueue(new MockResponse()
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setResponseCode(HttpStatus.OK.value())
                .setBody("20")
        );
        final var zmmorder = objectMapper.readValue(new ClassPathResource("json/zmmordersExample.json").getFile(), ZMMORDERS05DOP.class);

        // when
        var result = psmSender.postZmmorder(zmmorder);

        // then
        RecordedRequest request = mockWebServer.takeRequest();
        assertThat(request.getMethod()).isEqualTo("POST");
        assertThat(request.getPath()).isEqualTo("/sap/zmmorder");
        assertThat(result).isEqualTo(20);

        String body = request.getBody().readUtf8();
        final ZMMORDERS05DOP zmmorderFromBody = objectMapper.readValue(body, ZMMORDERS05DOP.class);
        assertThat(zmmorderFromBody.getIDOC().getE1EDK01().getBELNR()).isEqualTo("4500745477");
    }

    @Test
    void postSadimMessageShouldThrowRemoteServiceSenderExceptionIfResponseCodeIsBadRequest() throws InterruptedException {
        // given
        mockWebServer.enqueue(new MockResponse()
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setResponseCode(HttpStatus.BAD_REQUEST.value())
        );
        // when
        // then
        assertThatThrownBy(() -> psmSender.postSadimMessage(SADIM_MESSAGE_DTO))
                .isInstanceOf(RemoteServiceSenderException.class)
                .hasMessage("PsmSender, postSadimMessage, primeId [pi100], send error, message [PSM return code [400]]");
        mockWebServer.takeRequest();
    }

    @Test
    void postSadimMessageShouldThrowRemoteServiceSenderExceptionIfResponseCodeIsUnauthorized() throws InterruptedException {
        // given
        mockWebServer.enqueue(new MockResponse()
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setResponseCode(HttpStatus.UNAUTHORIZED.value())
        );
        // when
        // then
        assertThatThrownBy(() -> psmSender.postSadimMessage(SADIM_MESSAGE_DTO))
                .isInstanceOf(RemoteServiceSenderException.class)
                .hasMessage("PsmSender, postSadimMessage, primeId [pi100], send error, message [PSM return code [401]]");
        mockWebServer.takeRequest();
    }

    @Test
    void postSadimMessageShouldThrowRemoteServiceSenderExceptionIfResponseCodeIsForbidden() throws InterruptedException {
        // given
        mockWebServer.enqueue(new MockResponse()
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setResponseCode(HttpStatus.FORBIDDEN.value())
        );
        // when
        // then
        assertThatThrownBy(() -> psmSender.postSadimMessage(SADIM_MESSAGE_DTO))
                .isInstanceOf(RemoteServiceSenderException.class)
                .hasMessage("PsmSender, postSadimMessage, primeId [pi100], send error, message [PSM return code [403]]");
        mockWebServer.takeRequest();
    }

    @Test
    void canPostSadimMessage() throws Exception {
        // given
        mockWebServer.enqueue(new MockResponse()
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setResponseCode(HttpStatus.CREATED.value())
        );

        // when
        psmSender.postSadimMessage(SADIM_MESSAGE_DTO);

        // then
        RecordedRequest request = mockWebServer.takeRequest();
        assertThat(request.getMethod()).isEqualTo("POST");
        assertThat(request.getPath()).isEqualTo("/sadim");
    }
}
