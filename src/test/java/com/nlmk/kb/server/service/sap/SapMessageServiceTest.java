package com.nlmk.kb.server.service.sap;

import com.nlmk.kb.server.entity.SapMessage;
import com.nlmk.kb.server.entity.SapMessageState;
import com.nlmk.kb.server.exception.DataNotFoundException;
import com.nlmk.kb.server.repository.SapMessageRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

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

    @Test
    void getNextSapMessageTest() {

        prepareData();

        assertEquals(5L, repository.count());

        var res = sapMessageService.getNextSapMessage(-1L);
        assertEquals(2L, res.getId());

        res = sapMessageService.getNextSapMessage(null);
        assertEquals(2L, res.getId());

        res = sapMessageService.getNextSapMessage(res.getId());
        assertEquals(4L, res.getId());

        var lastId = res.getId();
        assertThrows(DataNotFoundException.class, () -> sapMessageService.getNextSapMessage(lastId));
    }

    private void prepareData() {
        var now = new Date();

        var sapMessages = List.of(
                // id = 1
                SapMessage.builder().offset(1L).partition(1).topic("t").bucket("b").path("p").processorVersion("v").server("s").ts(now)
                        .orderNum("1").state(SapMessageState.DONE).build(),
                // id = 2
                SapMessage.builder().offset(2L).partition(1).topic("t").bucket("b").path("p").processorVersion("v").server("s").ts(now)
                        .orderNum("2").state(SapMessageState.DONE).build(),
                // id = 3
                SapMessage.builder().offset(11L).partition(1).topic("t").bucket("b").path("p").processorVersion("v").server("s").ts(now)
                        .orderNum("1").state(SapMessageState.ERROR).build(),
                // id = 4
                SapMessage.builder().offset(12L).partition(1).topic("t").bucket("b").path("p").processorVersion("v").server("s").ts(now)
                        .orderNum("1").state(SapMessageState.DONE).build(),
                // id = 5
                SapMessage.builder().offset(22L).partition(1).topic("t").bucket("b").path("p").processorVersion("v").server("s").ts(now)
                        .orderNum("2").state(SapMessageState.ERROR).build()
        );

        repository.saveAll(sapMessages);
        repository.flush();
    }
}
