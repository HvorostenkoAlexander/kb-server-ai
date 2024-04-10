package com.nlmk.kb.server.repository;

import com.nlmk.kb.server.entity.AttestationMessage;
import com.nlmk.kb.server.entity.AttestationMessageSender;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class AttestationMessageRepositoryTest {

    @Autowired
    private AttestationMessageRepository repository;

    @Test
    void saveFind() {
        // given
        assertThat(repository.count()).isEqualTo(0L);
        repository.saveAll(List.of(
                AttestationMessage.builder().sender(AttestationMessageSender.CCM_PTS)
                        .receiptTs(new Date(1600_000000_000L)).primeId("0001").request("{\"ts\":\"1\"}").build(),
                AttestationMessage.builder().sender(AttestationMessageSender.CCM_PTS)
                        .receiptTs(new Date(1600_100000_000L)).primeId("0001").request("{\"ts\":\"2\"}").build(),
                AttestationMessage.builder().sender(AttestationMessageSender.CCM_PTS)
                        .receiptTs(new Date(1600_200000_000L)).primeId("0002").request("{\"ts\":\"3\"}").build()
        ));
        repository.flush();
        assertThat(repository.count()).isEqualTo(3L);

        // when
        // then
        final var optionalFound = repository.findFirstByPrimeIdOrderByReceiptTsDesc("0001");
        assertThat(optionalFound).isPresent();
        AttestationMessage found = optionalFound.get();
        assertThat(found.getReceiptTs().getTime()).isEqualTo(1600_100000_000L);
        assertThat(found.getRequest()).isEqualTo("{\"ts\":\"2\"}");

        found.setAttestationTs(new Date(1600_300000_000L));
        repository.save(optionalFound.get());
        repository.flush();

        final var updated = repository.findById(optionalFound.get().getId());
        assertThat(updated).isPresent();
        assertThat(updated.get().getPrimeId()).isEqualTo("0001");
        assertThat(found.getAttestationTs().getTime()).isEqualTo(1600_300000_000L);

        assertThat(repository.findFirstByPrimeIdOrderByReceiptTsDesc("0005")).isEmpty();
    }

}
