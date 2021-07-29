package com.nlmk.kb.server.dto;

import com.nlmk.kb.server.entity.pdm.PdmDictionary;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
