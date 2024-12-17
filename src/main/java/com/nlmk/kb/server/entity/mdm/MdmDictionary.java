package com.nlmk.kb.server.entity.mdm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Builder
@lombok.Data
@NoArgsConstructor
@AllArgsConstructor
public class MdmDictionary implements Serializable {

    private static final long serialVersionUID = -1950167116125833229L;

    private Date ts;
    private String op;
    private Pk pk;
    private com.nlmk.kb.server.entity.mdm.Data data;

}
