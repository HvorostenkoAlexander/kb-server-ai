package com.nlmk.kb.server.entity.mdm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pk implements Serializable {

    private static final long serialVersionUID = -9100655361415006750L;

    @SuppressWarnings("checkstyle:membername")
    private String systemCode;
    private String lineId;
}
