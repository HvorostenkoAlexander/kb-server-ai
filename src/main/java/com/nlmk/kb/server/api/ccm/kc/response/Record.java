package com.nlmk.kb.server.api.ccm.kc.response;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@Jacksonized
public class Record {

    @NotBlank
    private final String primeSystemCode; // Система - первоисточник запроса на аттестацию
    @NotBlank
    private final String primeId; // Идентификатор (ИД) единицы ЕМ в первоисточнике
    @NotNull
    private final Mismatch mismatch; // Результат проверки ЕМ
    @NotEmpty
    private final List<Attestation> attestationList; // Группы характеристик с результатами проверки

}
