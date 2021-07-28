package com.nlmk.kb.server.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DictionaryConfigDto {

    private Long id;
    @NotBlank
    private String topic;// имя топика;
    @NotEmpty
    private Integer[] codes;// перечень характеристик(кодов);
    @NotBlank
    private String nsiPath;// точка куда скидывать данные;
    @NotNull
    private Boolean enabled; //активность.
}
