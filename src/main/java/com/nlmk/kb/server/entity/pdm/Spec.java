package com.nlmk.kb.server.entity.pdm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Spec implements Serializable {

    private static final long serialVersionUID = -7711504563815836258L;

    private int specCode;
    private String specName;
    private int specTypeCode;
    private String specValue;
    private String specMeasure;

  //  public Spec(){}
}
