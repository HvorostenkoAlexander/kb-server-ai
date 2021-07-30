package com.nlmk.kb.server.entity.pdm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Builder
@lombok.Data
@NoArgsConstructor
@AllArgsConstructor
public class PdmDictionary implements Serializable {

    private static final long serialVersionUID = -5120760425855505772L;

    private Date ts;
    private String op;
    private Pk pk;
    private com.nlmk.kb.server.entity.pdm.Data data;
}
