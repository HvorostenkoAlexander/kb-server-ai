package com.nlmk.kb.server.service.ccm;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CcmMessageServiceTest {

    @Autowired
    private CcmMessageService service;

    @Test
    void saveFindTest() {
        service.saveSourceMessage(1020L, "1111111111",
                "{\"ts\": \"2022-09-02T14:36:25.000+05:00\", \"op\": \"U\"}", LocalDateTime.now());

        {
            var found = service.findSourceMessageByRequestId(1020L);
            assertThat(found).hasValueSatisfying(ccmMessageSource ->
                    assertThat(ccmMessageSource.getRequestId()).isEqualTo(1020L));
        }
        {
            var found = service.findSourceMessageByPrimeId("1111111111");
            assertThat(found).hasValueSatisfying(ccmMessageSource ->
                    assertThat(ccmMessageSource.getRequestId()).isEqualTo(1020L));
        }
    }

}
