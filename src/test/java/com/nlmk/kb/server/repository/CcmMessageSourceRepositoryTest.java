package com.nlmk.kb.server.repository;

import com.nlmk.kb.server.entity.CcmMessageSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;

@DataJpaTest
class CcmMessageSourceRepositoryTest {

    @Autowired
    private CcmMessageSourceRepository repository;

    @Test
    void saveFind() {
        Assertions.assertEquals(0L, repository.count());

        Assertions.assertDoesNotThrow(() -> repository.save(
                CcmMessageSource.builder().requestId(1010L).primeId("11")
                        .messageSource("{\"ts\": \"2022-09-02T14:36:25.000+05:00\", \"op\": \"U\"}").build()));
        Assertions.assertDoesNotThrow(() -> repository.save(
                CcmMessageSource.builder().requestId(1010L).primeId("11")
                        .messageSource("{\"ts\": \"2022-09-02T14:36:25.000+05:00\", \"op\": \"I\"}").build()));

        final var time = LocalDateTime.now();
        Assertions.assertDoesNotThrow(() -> repository.save(
                CcmMessageSource.builder().requestId(1020L).primeId("22").createdAt(time.minusMinutes(5L))
                        .messageSource("{\"ts\": \"2023-02-27T15:26:25.000+05:00\", \"op\": \"I\"}").build()));
        Assertions.assertDoesNotThrow(() -> repository.save(
                CcmMessageSource.builder().requestId(1021L).primeId("22").createdAt(time)
                        .messageSource("{\"ts\": \"2023-02-27T15:26:25.000+05:00\", \"op\": \"I\"}").build()));

        repository.flush();
        Assertions.assertEquals(3L, repository.count());

        {
            final var found = repository.findByRequestId(1010L);
            Assertions.assertTrue(found.isPresent());
            Assertions.assertEquals(1010L, found.get().getRequestId());
            Assertions.assertTrue(found.get().getMessageSource().endsWith("I\"}"));
        }
        {
            final var found = repository.findFirstByPrimeIdOrderByCreatedAtDesc("22");
            Assertions.assertTrue(found.isPresent());
            Assertions.assertEquals(1020L, found.get().getRequestId());
        }
    }

}
