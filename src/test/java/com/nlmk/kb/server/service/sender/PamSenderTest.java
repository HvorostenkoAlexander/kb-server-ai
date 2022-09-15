package com.nlmk.kb.server.service.sender;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nlmk.attestation.product.api.ProductDto;
import com.nlmk.attestation.product.api.pam.AttestationRequest;
import com.nlmk.attestation.product.api.pam.DataField;
import com.nlmk.attestation.product.api.pam.ProductAttestationResultDto;
import com.nlmk.attestation.product.api.pam.Value;
import com.nlmk.kb.server.exception.RemoteServiceSenderException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import okhttp3.mockwebserver.SocketPolicy;
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
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class PamSenderTest {

    @Autowired
    private PamSender pamSender;
    @Autowired
    private ObjectMapper objectMapper;

    static MockWebServer mockWebServer;

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry dpr) {
        dpr.add("service-web-client.pam-server.url", () -> "http://localhost:" + mockWebServer.getPort());
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
    void postAttestationRequest() throws Exception {
        Assertions.assertThrows(RemoteServiceSenderException.class, () -> pamSender.postAttestationRequest(null));

        final var attestationRequest = AttestationRequest.builder().build();
        Assertions.assertThrows(RemoteServiceSenderException.class, () -> pamSender.postAttestationRequest(attestationRequest));

        attestationRequest.setValue(Value.builder().build());
        Assertions.assertThrows(RemoteServiceSenderException.class, () -> pamSender.postAttestationRequest(attestationRequest));
        // передачи не было
        assertEquals(0, mockWebServer.getRequestCount());

        attestationRequest.setValue(Value.builder().data(DataField.builder().primeId("p100").build()).build());

        {
            mockWebServer.enqueue(new MockResponse()
                    .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .setResponseCode(HttpStatus.BAD_REQUEST.value())
            );
            final var response = Assertions.assertThrows(RemoteServiceSenderException.class, () -> pamSender.postAttestationRequest(attestationRequest));
            Assertions.assertEquals(String.format(
                    "PamSender, postAttestationRequest, primeId [p100], send error, message [%d Bad Request from POST http://localhost:%d/attestation]",
                    HttpStatus.BAD_REQUEST.value(), mockWebServer.getPort()), response.getMessage());
            mockWebServer.takeRequest();
        }
        {
            mockWebServer.enqueue(new MockResponse()
                    .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .setResponseCode(HttpStatus.OK.value())
                    .setBody(objectMapper.writeValueAsString(
                            ProductAttestationResultDto.builder()
                                    .newProduct(true)
                                    .result(ProductDto.builder().id(100L).build()).build()
                    ))
            );
            final var response = Assertions.assertDoesNotThrow(() -> pamSender.postAttestationRequest(attestationRequest));
            Assertions.assertNotNull(response);
            Assertions.assertEquals(100L, response.getResult().getId());
            RecordedRequest request = mockWebServer.takeRequest();
            assertEquals("POST", request.getMethod());
            assertEquals("/attestation", request.getPath());
        }
        {
            mockWebServer.enqueue(new MockResponse().setSocketPolicy(SocketPolicy.NO_RESPONSE));

            final var res = Assertions.assertThrows(RemoteServiceSenderException.class, () ->
                    pamSender.postAttestationRequest(attestationRequest));
            mockWebServer.takeRequest(100, TimeUnit.MILLISECONDS); // timeout 100 < 1000
            Assertions.assertEquals("PamSender, postAttestationRequest, primeId [p100], send error, message [Did not observe any item or terminal signal within 1000ms in 'flatMap' (and no fallback has been configured)]", res.getMessage());
        }
    }

}
