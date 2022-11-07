package com.nlmk.kb.server.repository;

import com.nlmk.kb.server.entity.CcmMessageSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class CcmMessageSourceRepositoryTest {

    @Autowired
    private CcmMessageSourceRepository repository;

    @Test
    void saveFind() {
        Assertions.assertEquals(0L, repository.count());
        Assertions.assertDoesNotThrow(() -> repository.save(
                CcmMessageSource.builder().requestId(1010L).messageSource("Test requestA 1010").build()));
        Assertions.assertDoesNotThrow(() -> repository.save(
                CcmMessageSource.builder().requestId(1010L).messageSource("Test requestA 1010 2").build()));
        repository.flush();
        Assertions.assertEquals(1L, repository.count());

        final var found = repository.findByRequestId(1010L);
        Assertions.assertFalse(found.isEmpty());
        Assertions.assertEquals(1010L, found.get().getRequestId());
        Assertions.assertTrue(found.get().getMessageSource().endsWith("1010 2"));
    }

}
