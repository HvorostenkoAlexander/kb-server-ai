package com.nlmk.kb.server.repository;

import com.nlmk.kb.server.entity.CcmMessageSource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CcmMessageSourceRepositoryTest {

    @Autowired
    private CcmMessageSourceRepository repository;

    @Test
    void saveFind() {
        // given
        assertThat(repository.count()).isEqualTo(0L);

        repository.saveAll(List.of(
                CcmMessageSource.builder().requestId(1010L).primeId("11")
                        .messageSource("{\"ts\": \"2022-09-02T14:36:25.000+05:00\", \"op\": \"U\"}").build(),
                CcmMessageSource.builder().requestId(1010L).primeId("11")
                        .messageSource("{\"ts\": \"2022-09-02T14:36:25.000+05:00\", \"op\": \"I\"}").build()
        ));

        final var time = LocalDateTime.now();
        repository.saveAll(List.of(
                CcmMessageSource.builder().requestId(1020L).primeId("22").createdAt(time.minusMinutes(5L))
                        .messageSource("{\"ts\": \"2023-02-27T15:26:25.000+05:00\", \"op\": \"I\"}").build(),
                CcmMessageSource.builder().requestId(1021L).primeId("22").createdAt(time)
                        .messageSource("{\"ts\": \"2023-02-27T15:26:25.000+05:00\", \"op\": \"I\"}").build()
        ));

        repository.flush();
        assertThat(repository.count()).isEqualTo(3L);

        {
            final var found = repository.findByRequestId(1010L);
            assertThat(found).hasValueSatisfying(ccmMessageSource -> {
                assertThat(ccmMessageSource.getRequestId()).isEqualTo(1010L);
                assertThat(ccmMessageSource.getMessageSource().endsWith("I\"}")).isTrue();
            });
        }
        {
            final var found = repository.findFirstByPrimeIdOrderByCreatedAtDesc("22");
            assertThat(found).hasValueSatisfying(ccmMessageSource ->
                    assertThat(ccmMessageSource.getRequestId()).isEqualTo(1021L));
        }
    }

}
