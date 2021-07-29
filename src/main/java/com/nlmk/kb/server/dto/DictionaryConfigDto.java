package com.nlmk.kb.server.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DictionaryConfigDto {

    private Long id;

    @NotBlank(message = "поле не должно быть пустым")
    private String topic;// имя топика;

    @NotEmpty(message = "поле не должно быть пустым")
    private Integer[] codes;// перечень характеристик(кодов);

    @NotBlank(message = "поле не должно быть пустым")
    private String nsiPath;// точка куда скидывать данные;

    @NotNull(message = "поле не должно быть null")
    private Boolean enabled; //активность.
}
