package com.nlmk.kb.server.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class ResultsConfigRepositoryTest {

    @Autowired
    private ResultsConfigRepository repository;

    @Test
    void init() {
        Assertions.assertTrue(repository.findByAvroName("-").isEmpty());

        final var res = repository.findByAvroName("Передача результатов аттестации APCS. Version: [1]");
        Assertions.assertEquals(1, res.size());
        Assertions.assertTrue(res.get(0).isEnabled());
        Assertions.assertEquals("000-0.l3-apcs.db.nlmk.verification-results.0", res.get(0).getTopic());
    }

}
