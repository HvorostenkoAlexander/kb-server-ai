package com.nlmk.kb.server.repository;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nlmk.attestation.product.api.Kceh;
import com.nlmk.attestation.product.api.pam.*;
import com.nlmk.kb.server.entity.CcmMessage;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class CcmMessageRepositoryTest {

    @Autowired
    private CcmMessageRepository repository;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @Test
    void saveFind() {
        assertEquals(0L, repository.count());

        assertDoesNotThrow(() -> repository.saveAll(List.of(
                CcmMessage.builder().topic("topic1").partition(0).offset(100).key("key100")
                        .kbSendingTs(new Date(1600_000000_000L))
                        .kbReceiptTs(new Date(1600_000000_000L)).primeId("0001")
                        .request(AttestationRequest.builder().id(1L).value(Value.builder()
                                .data("incorrect DataField")
                                .build()).build()).build()
        )));
        repository.flush();

        final var res1 = repository.findFirstByPrimeIdOrderByKbReceiptTsDesc("0001");
        assertTrue(res1.isPresent());
        assertEquals(1600_000000_000L, res1.get().getKbReceiptTs().getTime());
        assertEquals(1L, res1.get().getRequest().getId());
        assertFalse(res1.get().getRequest().getValue().getData() instanceof DataField);

        assertDoesNotThrow(() -> repository.saveAll(List.of(
                CcmMessage.builder().topic("topic1").partition(0).offset(101).key("key101")
                        .kbSendingTs(new Date(1600_100000_000L))
                        .kbReceiptTs(new Date(1600_100000_000L)).primeId("0001")
                        .request(AttestationRequest.builder().id(2L).value(Value.builder()
                                        .data(DataPgp.builder().kceh(Kceh.PGP.getValue()).hnum(2).build())
                                .build()).build()).build(),
                CcmMessage.builder().topic("topic1").partition(0).offset(102).key("key102")
                        .kbSendingTs(new Date(1600_200000_000L))
                        .kbReceiptTs(new Date(1600_200000_000L)).primeId("0002")
                        .request(AttestationRequest.builder().id(3L).value(Value.builder()
                                .data(DataPts.builder().kceh(Kceh.PTS.getValue()).hnum(3).build())
                                .build()).build()).build()
        )));
        repository.flush();

        assertEquals(3L, repository.count());

        final var res2 = repository.findFirstByPrimeIdOrderByKbReceiptTsDesc("0001");
        assertTrue(res2.isPresent());
        assertEquals(1600_100000_000L, res2.get().getKbReceiptTs().getTime());
        assertEquals(2L, res2.get().getRequest().getId());
        assertTrue(res2.get().getRequest().getValue().getData() instanceof DataField);
        assertTrue(res2.get().getRequest().getValue().getData() instanceof DataPgp);

        var data2 = objectMapper.convertValue(res2.get().getRequest().getValue().getData(), DataPgp.class);
        assertEquals(2, data2.getHnum());


        final var res3 = repository.findFirstByPrimeIdOrderByKbReceiptTsDesc("0002");
        assertTrue(res3.isPresent());
        assertEquals(1600_200000_000L, res3.get().getKbReceiptTs().getTime());
        assertEquals(3L, res3.get().getRequest().getId());
        assertTrue(res3.get().getRequest().getValue().getData() instanceof DataField);
        assertTrue(res3.get().getRequest().getValue().getData() instanceof DataPts);

        var data3 = objectMapper.convertValue(res3.get().getRequest().getValue().getData(), DataPts.class);
        assertEquals(3, data3.getHnum());

    }


    @Test
    void saveDelete() {
        Assertions.assertDoesNotThrow(() -> repository.save(
                CcmMessage.builder().topic("topic10").partition(0).offset(200).key("key100")
                        .kbSendingTs(new Date(1600_000000_000L))
                        .kbReceiptTs(new Date(1600_000000_000L)).primeId("123456")
                        .request(AttestationRequest.builder().id(1L).value(Value.builder().data(
                                DataPgp.builder()
                                        .primeId("123456")
                                        .width(900.0)
                                        .build()
                        ).build()).build()).build()));
        repository.flush();

        repository.deleteByTopicAndPartitionAndOffset("another", 0, 200);
        repository.flush();

        final var res1 = repository.findFirstByPrimeIdOrderByKbReceiptTsDesc("123456");
        assertTrue(res1.isPresent());

        repository.deleteByTopicAndPartitionAndOffset("topic10", 0, 200);
        repository.flush();

        final var res2 = repository.findFirstByPrimeIdOrderByKbReceiptTsDesc("123456");
        assertFalse(res2.isPresent());
    }

}
