package com.nlmk.kb.server.entity;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "mes_message", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"topic", "partition", "msg_offset"})
})
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class MesMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, name = "id")
    private Long id;

    @Column(name = "msg_offset", nullable = false)
    private Integer offset;

    @Column(name = "partition", nullable = false)
    private Integer partition;

    @Column(name = "msg_key", nullable = false)
    private String key;

    @Column(name = "topic", nullable = false)
    private String topic;

    @Column(name = "metal_unit_id")
    private UUID metalUnitId;

    @CreationTimestamp
    @Column(name = "ts_timestamp")
    private Instant ts;

    @Type(type = "jsonb")
    @Column(columnDefinition = "json", name = "data")
    private com.nlmk.attestation.product.api.pam.AttestationRequest request;

}
