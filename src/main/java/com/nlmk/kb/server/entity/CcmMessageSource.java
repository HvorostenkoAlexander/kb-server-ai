package com.nlmk.kb.server.entity;

import java.time.LocalDateTime;
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

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ccm_message_source")
public class CcmMessageSource {

    @Id
    @Column(nullable = false)
    private Long requestId;

    @Column(nullable = false)
    private String messageSource;

    @Column(nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

}
