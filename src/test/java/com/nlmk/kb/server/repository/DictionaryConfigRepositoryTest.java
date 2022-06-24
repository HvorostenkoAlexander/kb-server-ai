package com.nlmk.kb.server.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class DictionaryConfigRepositoryTest {

    @Autowired
    private DictionaryConfigRepository repository;

    @Test
    void init() {
        Assertions.assertEquals(0L, repository.count());
    }

}
