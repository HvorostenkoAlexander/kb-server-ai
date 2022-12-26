package com.nlmk.kb.server.repository;

import com.nlmk.kb.server.entity.SapMessage;
import com.nlmk.kb.server.entity.SapMessageState;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class SapMessageRepositoryTest {

    @Autowired
    private SapMessageRepository repository;

    @Test
    void init() {

        assertEquals(0L, repository.count());

        var now = new Date();

        var sapMessages = List.of(
                // id = 1
                SapMessage.builder().offset(1L).partition(1).topic("t").bucket("b").path("p").processorVersion("v").server("s").ts(now)
                        .orderNum("1").state(SapMessageState.DONE)
                        .build(),
                // id = 2
                SapMessage.builder().offset(2L).partition(1).topic("t").bucket("b").path("p").processorVersion("v").server("s").ts(now)
                        .orderNum("2").state(SapMessageState.DONE)
                        .build(),
                // id = 3
                SapMessage.builder().offset(11L).partition(1).topic("t").bucket("b").path("p").processorVersion("v").server("s").ts(now)
                        .orderNum("1").state(SapMessageState.ERROR)
                        .build(),
                // id = 4
                SapMessage.builder().offset(12L).partition(1).topic("t").bucket("b").path("p").processorVersion("v").server("s").ts(now)
                        .orderNum("1").state(SapMessageState.DONE)
                        .build(),
                // id = 5
                SapMessage.builder().offset(22L).partition(1).topic("t").bucket("b").path("p").processorVersion("v").server("s").ts(now)
                        .orderNum("2").state(SapMessageState.ERROR)
                        .build()
        );

        repository.saveAll(sapMessages);
        repository.flush();

        assertEquals(5L, repository.count());

        {
            var res = repository.findFirstByIdGreaterThanAndStateOrderById(-1L, SapMessageState.DONE);
            assertTrue(res.isPresent());
            assertEquals(1L, res.get().getId());
            assertTrue(repository.existsByOrderNumAndIdGreaterThanAndState(res.get().getOrderNum(), res.get().getId(), SapMessageState.DONE));
        }
        {
            var res = repository.findFirstByIdGreaterThanAndStateOrderById(1L, SapMessageState.DONE);
            assertTrue(res.isPresent());
            assertEquals(2L, res.get().getId());
            assertFalse(repository.existsByOrderNumAndIdGreaterThanAndState(res.get().getOrderNum(), res.get().getId(), SapMessageState.DONE));
        }
        {
            var res = repository.findFirstByIdGreaterThanAndStateOrderById(2L, SapMessageState.DONE);
            assertTrue(res.isPresent());
            assertEquals(4L, res.get().getId());
            assertFalse(repository.existsByOrderNumAndIdGreaterThanAndState(res.get().getOrderNum(), res.get().getId(), SapMessageState.DONE));
        }
        {
            var res = repository.findFirstByIdGreaterThanAndStateOrderById(4L, SapMessageState.DONE);
            assertFalse(res.isPresent());
        }
    }

}
