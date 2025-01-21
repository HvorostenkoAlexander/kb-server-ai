package com.nlmk.kb.server.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ResultsConfigRepositoryTest {

    @Autowired
    private ResultsConfigRepository repository;

    @Test
    void init() {
        assertThat(repository.count()).isEqualTo(5L);
        assertThat(repository.findByAvroName("-")).isEmpty();
        {
            final var res = repository.findByAvroName("VerificationResults");
            assertThat(res).hasSize(1);
            assertThat(res.get(0).isEnabled()).isTrue();
            assertThat(res.get(0).getTopic()).isEqualTo("000-1.l3-apcs.db.nlmk.verification-results.0");
        }
        {
            final var res = repository.findByAvroName("VerificationResultsPts");
            assertThat(res).hasSize(1);
            assertThat(res.get(0).isEnabled()).isTrue();
            assertThat(res.get(0).getTopic()).isEqualTo("000-1.l3-apcs.db.nlmk.verification-results-pts.0");
        }
        {
            final var res = repository.findByAvroName("VerificationResultsKc1");
            assertThat(res).hasSize(1);
            assertThat(res.get(0).isEnabled()).isTrue();
            assertThat(res.get(0).getTopic()).isEqualTo("000-1.l3-apcs.db.nlmk.verification-results-kc1.0");
        }
        {
            final var res = repository.findByAvroName("VerificationResultsKc2");
            assertThat(res).hasSize(1);
            assertThat(res.get(0).isEnabled()).isTrue();
            assertThat(res.get(0).getTopic()).isEqualTo("000-1.l3-apcs.db.nlmk.verification-results-kc2.0");
        }
        {
            final var res = repository.findByAvroName("VerificationResultsPhpp");
            assertThat(res).hasSize(1);
            assertThat(res.get(0).isEnabled()).isTrue();
            assertThat(res.get(0).getTopic()).isEqualTo("000-1.l3-apcs.db.verification-results-phpp.0");
        }
    }

}
