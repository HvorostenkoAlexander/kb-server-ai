package com.nlmk.kb.server.entity.integral;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import java.time.Instant;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "integral_params_message")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class IntegralParamsMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    private String primeId;
    private UUID metalUnitId;

    @Type(type = "jsonb")
    @Column(nullable = false)
    private IntegralParamsResponse response;

    @CreationTimestamp
    @Column(nullable = false)
    private Instant createdAt;
}
