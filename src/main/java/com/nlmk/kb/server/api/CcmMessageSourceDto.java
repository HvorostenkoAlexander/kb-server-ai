package com.nlmk.kb.server.api;

import com.fasterxml.jackson.databind.JsonNode;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CcmMessageSourceDto {

    private Long requestId;
    private String primeId;
    private JsonNode messageSource;
    private LocalDateTime createdAt;

}
