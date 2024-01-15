package com.nlmk.kb.server.service.sap;

import com.nlmk.attestation.zmmorder.ZMMORDERS05DOP;
import com.nlmk.attestation.zorder.ZORDERS051;
import com.nlmk.kb.server.entity.SapMessage;
import com.nlmk.kb.server.entity.SapMessageState;
import com.nlmk.kb.server.exception.DataNotFoundException;
import com.nlmk.kb.server.repository.SapMessageRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import({SapMessageServiceImpl.class})
class SapMessageServiceTest {

    @Autowired
    private SapMessageRepository repository;
    @Autowired
    private SapMessageService sapMessageService;

    @MockBean
    private S3Service s3Service;

    private static final String ZORDERS_BUCKET = "zorders";
    private static final String ZMMORDERS_BUCKET = "zmmorders";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(sapMessageService, "idoczordrsBucketName", ZORDERS_BUCKET);
    }

    @AfterEach
    void tearDown() {
        repository.deleteAll();
    }

    @Test
    void getNextSapMessageTest() {
        prepareData();

        assertEquals(6L, repository.count());

        var res = sapMessageService.getNextSapMessage(-1L);
        assertEquals(2L, res.getId());
        assertEquals(ZORDERS051.class, res.getOrderClass());

        res = sapMessageService.getNextSapMessage(null);
        assertEquals(2L, res.getId());

        res = sapMessageService.getNextSapMessage(res.getId());
        assertEquals(3L, res.getId());
        assertEquals(ZMMORDERS05DOP.class, res.getOrderClass());

        res = sapMessageService.getNextSapMessage(res.getId());
        assertEquals(5L, res.getId());

        var lastId = res.getId();
        assertThrows(DataNotFoundException.class, () -> sapMessageService.getNextSapMessage(lastId));
    }

    private void prepareData() {
        var now = new Date();

        var sapMessages = List.of(
                // id = 1
                SapMessage.builder().offset(1L).partition(1).topic("t").bucket(ZORDERS_BUCKET).path("p").processorVersion("v").server("s").ts(now)
                        .orderNum("1").state(SapMessageState.DONE).build(),
                // id = 2
                SapMessage.builder().offset(2L).partition(1).topic("t").bucket(ZORDERS_BUCKET).path("p").processorVersion("v").server("s").ts(now)
                        .orderNum("2").state(SapMessageState.DONE).build(),
                // id = 3
                SapMessage.builder().offset(8L).partition(1).topic("t2").bucket(ZMMORDERS_BUCKET).path("p2").processorVersion("v").server("s").ts(now)
                        .orderNum("3").state(SapMessageState.DONE).build(),
                // id = 4
                SapMessage.builder().offset(11L).partition(1).topic("t").bucket(ZORDERS_BUCKET).path("p").processorVersion("v").server("s").ts(now)
                        .orderNum("1").state(SapMessageState.ERROR).build(),
                // id = 5
                SapMessage.builder().offset(12L).partition(1).topic("t").bucket(ZORDERS_BUCKET).path("p").processorVersion("v").server("s").ts(now)
                        .orderNum("1").state(SapMessageState.DONE).build(),
                // id = 6
                SapMessage.builder().offset(22L).partition(1).topic("t").bucket(ZORDERS_BUCKET).path("p").processorVersion("v").server("s").ts(now)
                        .orderNum("2").state(SapMessageState.ERROR).build()
        );

        repository.saveAll(sapMessages);
        repository.flush();
    }
}
