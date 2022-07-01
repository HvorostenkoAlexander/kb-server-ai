package com.nlmk.kb.server.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

/**
 * Сообщение с запросом на Аттестацию Единицы Продукции<br>
 * <code>primeId</code> идентификатор Единицы Металла (она же Единица Продукции)<br>
 * <code>primeId</code> уникальный на стороне источнике сообщения, но хранятся все сообщения т.е. есть история.
 */
@Data
@Builder
@Entity
@Table(name = "attestation_message")
@NoArgsConstructor
@AllArgsConstructor
public class AttestationMessage {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sender", nullable = false)
    @Enumerated(EnumType.STRING)
    private AttestationMessageSender sender;

    // момент приема сообщения
    @Column(name = "receipt_ts", nullable = false)
    private Date receiptTs;

    // идентификатор Единицы Металла (ЕМ) на стане
    @Column(name = "prime_id", nullable = false)
    private String primeId;

    // запрос на аттестацию, в едином формате (com.nlmk.attestation.product.api.pam.AttestationRequest)
    @Column(name = "request", nullable = false)
    private String request;
    @Transient
    private com.nlmk.attestation.product.api.pam.AttestationRequest requestObject;

    // момент завершения запроса на Аттестацию
    @Column(name = "attestation_ts")
    private Date attestationTs;

}
