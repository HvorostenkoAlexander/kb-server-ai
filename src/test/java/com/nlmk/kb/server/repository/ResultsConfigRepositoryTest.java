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
        Assertions.assertEquals(4, repository.count());
        Assertions.assertTrue(repository.findByAvroName("-").isEmpty());
        {
            final var res = repository.findByAvroName("VerificationResults");
            Assertions.assertEquals(1, res.size());
            Assertions.assertTrue(res.get(0).isEnabled());
            Assertions.assertEquals("000-1.l3-apcs.db.nlmk.verification-results.0", res.get(0).getTopic());
        }
        {
            final var res = repository.findByAvroName("VerificationResultsPts");
            Assertions.assertEquals(1, res.size());
            Assertions.assertTrue(res.get(0).isEnabled());
            Assertions.assertEquals("000-1.l3-apcs.db.nlmk.verification-results-pts.0", res.get(0).getTopic());
        }
        {
            final var res = repository.findByAvroName("VerificationResultsKc1");
            Assertions.assertEquals(1, res.size());
            Assertions.assertTrue(res.get(0).isEnabled());
            Assertions.assertEquals("000-1.l3-apcs.db.nlmk.verification-results-kc1.0", res.get(0).getTopic());
        }
        {
            final var res = repository.findByAvroName("VerificationResultsKc2");
            Assertions.assertEquals(1, res.size());
            Assertions.assertTrue(res.get(0).isEnabled());
            Assertions.assertEquals("000-1.l3-apcs.db.nlmk.verification-results-kc2.0", res.get(0).getTopic());
        }
    }

}
