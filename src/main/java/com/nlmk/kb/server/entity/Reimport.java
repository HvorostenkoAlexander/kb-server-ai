package com.nlmk.kb.server.entity;


import com.nlmk.kb.server.api.ReimportState;
import com.nlmk.kb.server.api.ReimportType;
import java.time.Instant;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "reimport")
public class Reimport {

    @Id
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ReimportType name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReimportState state;

    private Instant lastImportDate;
    private Long lastImportedId;
    private Instant lastImportedTs;
    private String errorMessage;

}