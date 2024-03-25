package com.nlmk.kb.server.service;

import com.nlmk.kb.server.service.listener.CcmPgpKafkaService;
import com.nlmk.kb.server.service.listener.CcmPtsKafkaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(properties = {"kafka.ccm.pts.enable=false"})
class KafkaListenerInContextTest {

    @Autowired
    private CcmPgpKafkaService ccmPgpKafkaService;
    @Autowired(required = false)
    private CcmPtsKafkaService ccmPtsKafkaService;

    @Test
    void checkBean() {
        assertThat(ccmPgpKafkaService).isNotNull();
        assertThat(ccmPtsKafkaService).isNull();
    }

}
