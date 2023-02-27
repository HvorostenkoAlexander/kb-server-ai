package com.nlmk.kb.server.service.ccm;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CcmMessageServiceTest {

    @Autowired
    private CcmMessageService service;

    @Test
    void saveFindTest() {
        Assertions.assertDoesNotThrow(() -> service.saveSourceMessage(1020L, "1111111111", "{\"ts\": \"2022-09-02T14:36:25.000+05:00\", \"op\": \"U\"}"));

        {
            var found = service.findSourceMessageByRequestId(1020L);
            Assertions.assertTrue(found.isPresent());
            Assertions.assertEquals(1020L, found.get().getRequestId());
        }
        {
            var found = service.findSourceMessageByPrimeId("1111111111");
            Assertions.assertTrue(found.isPresent());
            Assertions.assertEquals(1020L, found.get().getRequestId());
        }
    }

}
