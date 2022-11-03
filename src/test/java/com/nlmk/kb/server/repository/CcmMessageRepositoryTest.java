package com.nlmk.kb.server.repository;

import com.nlmk.attestation.product.api.pam.AttestationRequest;
import com.nlmk.attestation.product.api.pam.Value;
import com.nlmk.kb.server.entity.CcmMessage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Date;
import java.util.List;

@DataJpaTest
class CcmMessageRepositoryTest {

    @Autowired
    private CcmMessageRepository repository;

    @Test
    void saveFind() {
        Assertions.assertEquals(0L, repository.count());
        Assertions.assertDoesNotThrow(() -> repository.saveAll(List.of(
                CcmMessage.builder().topic("topic1").partition(0).offset(100).key("key100")
                        .kbSendingTs(new Date(1600_000000_000L))
                        .kbReceiptTs(new Date(1600_000000_000L)).primeId("0001")
                        .request(AttestationRequest.builder().id(1L).value(Value.builder().build()).build()).build(),
                CcmMessage.builder().topic("topic1").partition(0).offset(101).key("key101")
                        .kbSendingTs(new Date(1600_100000_000L))
                        .kbReceiptTs(new Date(1600_100000_000L)).primeId("0001")
                        .request(AttestationRequest.builder().id(2L).value(Value.builder().build()).build()).build(),
                CcmMessage.builder().topic("topic1").partition(0).offset(102).key("key102")
                        .kbSendingTs(new Date(1600_200000_000L))
                        .kbReceiptTs(new Date(1600_200000_000L)).primeId("0002")
                        .request(AttestationRequest.builder().id(3L).value(Value.builder().build()).build()).build()
        )));
        repository.flush();
        Assertions.assertEquals(3L, repository.count());

        final var fined = repository.findFirstByPrimeIdOrderByKbReceiptTsDesc("0001");
        Assertions.assertTrue(fined.isPresent());
        Assertions.assertEquals(1600_100000_000L, fined.get().getKbReceiptTs().getTime());
        Assertions.assertEquals(2L, fined.get().getRequest().getId());
    }

}
