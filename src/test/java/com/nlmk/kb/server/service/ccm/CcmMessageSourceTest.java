package com.nlmk.kb.server.service.ccm;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
class CcmMessageSourceTest {

    @Autowired
    private CcmMessageSourceService service;

    @Test
    void saveFindTest() {
        service.save(1020L, "Test 1020");
        var found = service.findByRequestId(1020L);
        Assertions.assertFalse(found.isEmpty());
        Assertions.assertEquals(1020L, found.stream().findFirst().get().getRequestId());
    }

}
