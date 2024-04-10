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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
        assertThatThrownBy(() -> pamSender.postAttestationRequest(null))
                .isInstanceOf(RemoteServiceSenderException.class);

        final var attestationRequest = AttestationRequest.builder().build();
        assertThatThrownBy(() -> pamSender.postAttestationRequest(attestationRequest))
                .isInstanceOf(RemoteServiceSenderException.class);

        attestationRequest.setValue(Value.builder().build());
        assertThatThrownBy(() -> pamSender.postAttestationRequest(attestationRequest))
                .isInstanceOf(RemoteServiceSenderException.class);
        // передачи не было
        assertThat(mockWebServer.getRequestCount()).isZero();

        attestationRequest.setValue(Value.builder().data(DataField.builder().primeId("p100").build()).build());

        {
            mockWebServer.enqueue(new MockResponse()
                    .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .setResponseCode(HttpStatus.BAD_REQUEST.value())
            );
            assertThatThrownBy(() -> pamSender.postAttestationRequest(attestationRequest))
                    .isInstanceOf(RemoteServiceSenderException.class)
                    .hasMessage(String.format("PamSender, postAttestationRequest, primeId [p100], "
                                    + "send error, message [%d Bad Request from POST http://localhost:%d/attestation]",
                            HttpStatus.BAD_REQUEST.value(), mockWebServer.getPort()));
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
            final var response = pamSender.postAttestationRequest(attestationRequest);
            assertThat(response.getResult().getId()).isEqualTo(100L);
            RecordedRequest request = mockWebServer.takeRequest();
            assertThat(request.getMethod()).isEqualTo("POST");
            assertThat(request.getPath()).isEqualTo("/attestation");
        }
        {
            mockWebServer.enqueue(new MockResponse().setSocketPolicy(SocketPolicy.NO_RESPONSE));

            assertThatThrownBy(() -> pamSender.postAttestationRequest(attestationRequest))
                    .isInstanceOf(RemoteServiceSenderException.class)
                    .hasMessage("PamSender, postAttestationRequest, primeId [p100], send error, message "
                            + "[Did not observe any item or terminal signal within 2500ms "
                            + "in 'flatMap' (and no fallback has been configured)]");
            mockWebServer.takeRequest(100, TimeUnit.MILLISECONDS); // timeout 100 < 1000
        }
    }

}
