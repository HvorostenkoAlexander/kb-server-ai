package com.nlmk.kb.server.entity;

import com.nlmk.kb.server.entity.pam.AttestationRequest;
import com.vladmihalcea.hibernate.type.json.JsonType;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.util.Date;

@Data
@Entity
@Table(name = "ccm_message", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"topic", "partition", "msg_offset"})
})
@ToString(callSuper = true)
@NoArgsConstructor
@TypeDef(name = "json", typeClass = JsonType.class)
public class CcmAttestationRequestMessage extends BaseKafkaMessage {

    @Column(name = "prime_id")
    private String primeId;

    @Type(type = "json")
    @Column(columnDefinition = "json")
    private AttestationRequest request;

    @Builder
    public CcmAttestationRequestMessage(Long id,
                                        String topic,
                                        Integer partition,
                                        Integer offset,
                                        String key,
                                        Date kbSendingTs,
                                        Date kbReceiptTs,
                                        String status,
                                        String note,
                                        String primeId,
                                        AttestationRequest request) {
        super(id, topic, partition, offset, key, kbSendingTs, kbReceiptTs, status, note);
        this.primeId = primeId;
        this.request = request;
    }

    public static class CcmAttestationRequestMessageBuilder extends BaseKafkaMessageBuilder {
        CcmAttestationRequestMessageBuilder() {
            super();
        }
    }
}
