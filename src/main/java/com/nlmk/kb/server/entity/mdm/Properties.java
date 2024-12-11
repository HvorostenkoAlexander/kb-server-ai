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
public class Properties implements Serializable {

    private static final long serialVersionUID = 2179928425603170973L;

    private CharSequence cron;
    private CharSequence dateEnd;
    private CharSequence dateBegin;
    private CharSequence dateChange;

}
