package com.nlmk.kb.server.repository;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nlmk.attestation.product.api.Kceh;
import com.nlmk.attestation.product.api.pam.AttestationRequest;
import com.nlmk.attestation.product.api.pam.DataField;
import com.nlmk.attestation.product.api.pam.DataPgp;
import com.nlmk.attestation.product.api.pam.DataPts;
import com.nlmk.attestation.product.api.pam.Value;
import com.nlmk.kb.server.entity.CcmMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CcmMessageRepositoryTest {

    @Autowired
    private CcmMessageRepository repository;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @Test
    void saveFind() {
        // given
        assertThat(repository.count()).isEqualTo(0L);

        repository.save(CcmMessage.builder().topic("topic1").partition(0).offset(100).key("key100")
                .kbSendingTs(new Date(1600_000000_000L))
                .kbReceiptTs(new Date(1600_000000_000L)).primeId("0001")
                .request(AttestationRequest.builder().id(1L).value(Value.builder()
                        .data("incorrect DataField")
                        .build()).build()).build()
        );
        repository.flush();

        final var res1 = repository.findFirstByPrimeIdOrderByKbReceiptTsDesc("0001");
        assertThat(res1).hasValueSatisfying(ccmMessage -> {
            assertThat(ccmMessage.getKbReceiptTs().getTime()).isEqualTo(1600_000000_000L);
            assertThat(ccmMessage.getRequest().getId()).isEqualTo(1L);
            assertThat(ccmMessage.getRequest().getValue().getData()).isNotInstanceOf(DataField.class);
        });

        repository.saveAll(List.of(
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
        ));
        repository.flush();

        assertThat(repository.count()).isEqualTo(3L);

        final var res2 = repository.findFirstByPrimeIdOrderByKbReceiptTsDesc("0001");
        assertThat(res2).hasValueSatisfying(ccmMessage -> {
            assertThat(ccmMessage.getKbReceiptTs().getTime()).isEqualTo(1600_100000_000L);
            assertThat(ccmMessage.getRequest().getId()).isEqualTo(2L);
            assertThat(ccmMessage.getRequest().getValue().getData()).isInstanceOf(DataField.class);
            assertThat(ccmMessage.getRequest().getValue().getData()).isInstanceOf(DataPgp.class);
            var data2 = objectMapper.convertValue(ccmMessage.getRequest().getValue().getData(), DataPgp.class);
            assertThat(data2.getHnum()).isEqualTo(2);
        });

        final var res3 = repository.findFirstByPrimeIdOrderByKbReceiptTsDesc("0002");
        assertThat(res3).hasValueSatisfying(ccmMessage -> {
            assertThat(ccmMessage.getKbReceiptTs().getTime()).isEqualTo(1600_200000_000L);
            assertThat(ccmMessage.getRequest().getId()).isEqualTo(3L);
            assertThat(ccmMessage.getRequest().getValue().getData()).isInstanceOf(DataField.class);
            assertThat(ccmMessage.getRequest().getValue().getData()).isInstanceOf(DataPts.class);
            var data3 = objectMapper.convertValue(ccmMessage.getRequest().getValue().getData(), DataPts.class);
            assertThat(data3.getHnum()).isEqualTo(3);
        });
    }


    @Test
    void saveDelete() {
        // given
        repository.save(CcmMessage.builder().topic("topic10").partition(0).offset(200).key("key100")
                .kbSendingTs(new Date(1600_000000_000L))
                .kbReceiptTs(new Date(1600_000000_000L)).primeId("123456")
                .request(AttestationRequest.builder().id(1L).value(Value.builder().build()).build()).build()
        );
        repository.flush();

        repository.deleteOldByTopicAndPartitionAndOffset("another", 0, 200);
        repository.flush();

        // when
        final var res1 = repository.findFirstByPrimeIdOrderByKbReceiptTsDesc("123456");
        // then
        assertThat(res1).isPresent();

        // given
        repository.deleteOldByTopicAndPartitionAndOffset("topic10", 0, 200);
        repository.flush();

        // when
        final var res2 = repository.findFirstByPrimeIdOrderByKbReceiptTsDesc("123456");
        // then
        assertThat(res2).isEmpty();
    }

}
