package com.nlmk.kb.server.service;

import com.nlmk.kb.server.repository.AttestationMessageRepository;
import com.nlmk.kb.server.repository.CcmMessageSourceRepository;
import com.nlmk.kb.server.service.sender.PamSender;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.io.IOException;

@SpringBootTest
public class AttestationMessageServiceTest {

    @Autowired
    private AttestationMessageRepository attestationMessageRepository;
    @Autowired
    private CcmMessageSourceRepository ccmMessageSourceRepository;
    @Autowired
    private PamSender pamSender;
    @Autowired
    private AttestationMessageService attestationMessageService;

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
    void ccmPtsRequestProcessing() {
        Assertions.assertThrows(NullPointerException.class, () -> attestationMessageService.ccmPtsRequestProcessing(null));
        // todo
    }

}
