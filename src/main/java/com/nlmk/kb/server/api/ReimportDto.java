package com.nlmk.kb.server.api;


import java.time.Instant;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

@Getter
@Builder
@Jacksonized
public class ReimportDto {
    private final ReimportType name;
    private final ReimportState state;
    private final Instant lastImportDate;
    private final Long lastImportedId;
    private final Instant lastImportedTs;
    private final String errorMessage;
}
