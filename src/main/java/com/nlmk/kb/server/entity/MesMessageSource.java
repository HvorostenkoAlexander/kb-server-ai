package com.nlmk.kb.server.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "mes_message_source")
public class MesMessageSource {

    @Id
    @Column(nullable = false)
    private Long requestId;

    @Column(nullable = false)
    private String primeId;

    @Column(nullable = false)
    private String messageSource;

    @Column(nullable = false)
    private LocalDateTime createdAt;

}
