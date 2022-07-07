package com.nlmk.kb.server.entity;

import com.vladmihalcea.hibernate.type.json.JsonType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.util.Date;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ccm_message", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"topic", "partition", "msg_offset"})
})
@TypeDef(name = "json", typeClass = JsonType.class)
public class CcmMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, name = "id")
    private Long id;

    @Column(name = "topic", nullable = false)
    private String topic;

    @Column(name = "partition", nullable = false)
    private Integer partition;

    @Column(name = "msg_offset", nullable = false)
    private Integer offset;

    @Column(name = "msg_key", nullable = false)
    private String key;

    // момент приема результата Аттестации от PAM
    @Column(name = "kb_sending_ts", nullable = false)
    private Date kbSendingTs;

    // момент приема сообщения из Kafka
    @Column(name = "kb_receipt_ts", nullable = false)
    private Date kbReceiptTs;

    @Column(name = "status")
    private String status;

    @Column(name = "note")
    private String note;

    @Column(name = "prime_id")
    private String primeId;

    @Type(type = "json")
    @Column(columnDefinition = "json")
    // сохранение в едином формате, значение, которое пойдет в запросе к PAM
    private com.nlmk.attestation.product.api.pam.AttestationRequest request;

}
