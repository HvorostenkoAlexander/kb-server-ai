package com.nlmk.kb.server.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class IntegralParamsMessageRepositoryTest {

    @Autowired
    private IntegralParamsMessageRepository repository;

    @Test
    void init() {
        assertThat(repository.count()).isEqualTo(0L);
    }

}
