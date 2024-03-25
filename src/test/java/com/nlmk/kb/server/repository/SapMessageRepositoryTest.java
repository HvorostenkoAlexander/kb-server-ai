package com.nlmk.kb.server.repository;

import com.nlmk.kb.server.entity.SapMessage;
import com.nlmk.kb.server.entity.SapMessageState;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class SapMessageRepositoryTest {

    @Autowired
    private SapMessageRepository repository;

    @Test
    void init() {
        assertThat(repository.count()).isEqualTo(0L);

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

        assertThat(repository.count()).isEqualTo(5L);

        {
            var res = repository.findFirstByIdGreaterThanAndStateOrderById(-1L, SapMessageState.DONE);
            assertThat(res).isPresent();
            assertThat(res.get().getId()).isEqualTo(1L);
            assertThat(repository.existsByOrderNumAndIdGreaterThanAndState(res.get().getOrderNum(), res.get().getId(), SapMessageState.DONE)).isTrue();
        }
        {
            var res = repository.findFirstByIdGreaterThanAndStateOrderById(1L, SapMessageState.DONE);
            assertThat(res).isPresent();
            assertThat(res.get().getId()).isEqualTo(2L);
            assertThat(repository.existsByOrderNumAndIdGreaterThanAndState(res.get().getOrderNum(), res.get().getId(), SapMessageState.DONE)).isFalse();
        }
        {
            var res = repository.findFirstByIdGreaterThanAndStateOrderById(2L, SapMessageState.DONE);
            assertThat(res).isPresent();
            assertThat(res.get().getId()).isEqualTo(4L);
            assertThat(repository.existsByOrderNumAndIdGreaterThanAndState(res.get().getOrderNum(), res.get().getId(), SapMessageState.DONE)).isFalse();
        }
        {
            var res = repository.findFirstByIdGreaterThanAndStateOrderById(4L, SapMessageState.DONE);
            assertThat(res).isEmpty();
        }
    }

}
