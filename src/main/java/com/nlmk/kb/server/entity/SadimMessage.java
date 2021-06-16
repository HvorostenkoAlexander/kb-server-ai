package com.nlmk.kb.server.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.time.LocalDateTime;

@Data
@Builder
@Entity
@Table(name = "sadim_message", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"partition","msg_offset"})
})
@NoArgsConstructor
@AllArgsConstructor
public class SadimMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, name = "id")
    private Long id;

    @Column(nullable = false)
    private Integer partition;

    @Column(name = "msg_offset",
            nullable = false)
    private Long offset;

    @Column(name = "msg_key",
            nullable = false)
    private String key;

    @Column(nullable = false)
    private LocalDateTime ts;

    @Column(name = "status")
    private String status;

    @Column(name="note")
    private String note;

    @OneToOne(optional = false, cascade = CascadeType.ALL)
    @JoinColumn(unique = false)
    private PreAttestationParam param;
}
