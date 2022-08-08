package com.nlmk.kb.server.repository;

import com.nlmk.kb.server.entity.AttestationMessage;
import com.nlmk.kb.server.entity.AttestationMessageSender;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Date;
import java.util.List;

@DataJpaTest
class AttestationMessageRepositoryTest {

    @Autowired
    private AttestationMessageRepository repository;

    @Test
    void saveFind() {
        Assertions.assertEquals(0L, repository.count());
        Assertions.assertDoesNotThrow(() -> repository.saveAll(List.of(
                AttestationMessage.builder().sender(AttestationMessageSender.CCM_PTS)
                        .receiptTs(new Date(1600_000000_000L)).primeId("0001").request("{\"ts\":\"1\"}").build(),
                AttestationMessage.builder().sender(AttestationMessageSender.CCM_PTS)
                        .receiptTs(new Date(1600_100000_000L)).primeId("0001").request("{\"ts\":\"2\"}").build(),
                AttestationMessage.builder().sender(AttestationMessageSender.CCM_PTS)
                        .receiptTs(new Date(1600_200000_000L)).primeId("0002").request("{\"ts\":\"3\"}").build()
        )));
        repository.flush();
        Assertions.assertEquals(3L, repository.count());

        final var fined = repository.findFirstByPrimeIdOrderByReceiptTsDesc("0001");
        Assertions.assertTrue(fined.isPresent());
        Assertions.assertEquals(1600_100000_000L, fined.get().getReceiptTs().getTime());
        Assertions.assertEquals("{\"ts\":\"2\"}", fined.get().getRequest());

        fined.get().setAttestationTs(new Date(1600_300000_000L));
        Assertions.assertDoesNotThrow(() -> repository.save(fined.get()));
        repository.flush();

        final var updated = repository.findById(fined.get().getId());
        Assertions.assertTrue(updated.isPresent());
        Assertions.assertEquals("0001", updated.get().getPrimeId());
        Assertions.assertEquals(1600_300000_000L, fined.get().getAttestationTs().getTime());

        Assertions.assertTrue(repository.findFirstByPrimeIdOrderByReceiptTsDesc("0005").isEmpty());
    }

}
