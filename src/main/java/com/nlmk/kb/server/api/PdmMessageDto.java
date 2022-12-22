package com.nlmk.kb.server.api;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
public class PdmMessageDto {

    private Long id;
    private String topic;
    private Integer partition;
    private Long offset;
    private String key;
    private String ts;
    private String op;
    private boolean isPosted;
    private String dictionary;
    private String kbReceiptTs;
    private String note;

}
