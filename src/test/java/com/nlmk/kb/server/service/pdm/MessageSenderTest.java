package com.nlmk.kb.server.service.pdm;

import com.nlmk.kb.server.entity.DictionaryConfig;
import com.nlmk.kb.server.entity.pdm.Data;
import com.nlmk.kb.server.entity.pdm.PdmDictionary;
import com.nlmk.kb.server.entity.pdm.PdmMessage;
import com.nlmk.kb.server.entity.pdm.Pk;
import com.nlmk.kb.server.repository.DictionaryConfigRepository;
import com.nlmk.kb.server.service.CommonConverterImpl;
import com.nlmk.kb.server.service.pdm.senders.*;
import com.nlmk.kb.server.service.sender.NsiSender;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import java.util.List;

@DataJpaTest
@Import({
        PdmDtoConverterImpl.class,
        CommonConverterImpl.class,
        PdmDictionaryCreatorImpl.class,
        DictionaryConfigServiceImpl.class,
        DtoConverterImpl.class,
        //
        AsapChemicalPropSender.class,
        AsapMechPropertiesDtSender.class,
        AsapMechPropertiesSender.class,
        ChemicalPropertiesSender.class,
        EquivalentsSender.class,
        KatSteel4041Sender.class,
        MacrostructureSender.class,
        MatchRpNumSender.class,
        MatchTkNumSender.class,
        MechPropertiesSender.class,
        MicrostructureSender.class,
        MinNumberSampChemSender.class,
        PhysMechPropAnisSteelSender.class,
        RegisterEquivalentsSender.class,
        SchemeStrippingSlabSender.class,
        SpChemicalPropertiesNotesSender.class,
        TkNumSender.class,
        ToleranceSender.class,
        TolEvennessDtSender.class,
        TolEvennessSender.class,
        TolLengthSender.class,
        TolShapeSlabSender.class,
        TolThickDtSender.class,
        TolThickSender.class,
        TolWidthDtSender.class,
        TolWidthSender.class
})
class MessageSenderTest {

    @Autowired
    private DictionaryConfigRepository configRepository;
    @Autowired
    private List<PdmMessageSender> messageSenders;

    @MockBean
    NsiSender nsiSender;

    @Test
    void send() {
        Assertions.assertEquals(26, messageSenders.size());

        final var message = PdmMessage.builder()
                .topic("topic-for-all")
                .dictionary(PdmDictionary.builder()
                        .pk(Pk.builder().Id("1").systemCode("2").build())
                        .data(Data.builder()
                                .specifications(List.of())
                                .build())
                        .build())
                .build();

        configRepository.save(DictionaryConfig.builder()
                .topic("topic-for-all")
                .nsiPath("/nsi/dict/target")
                .enabled(true)
                .build());

        messageSenders.forEach(sender -> {
            Mockito.when(nsiSender.sendBodyReturnLong(Mockito.any(), Mockito.any(), Mockito.any()))
                    .thenReturn(321L);

            final var response = Assertions.assertDoesNotThrow(() -> sender.send(message));
            Assertions.assertEquals(321L, response);
        });
        messageSenders.forEach(sender -> {
            Mockito.when(nsiSender.sendBodyReturnLong(Mockito.any(), Mockito.any(), Mockito.any()))
                    .thenReturn(null);

            final var response = Assertions.assertDoesNotThrow(() -> sender.send(message));
            Assertions.assertNull(response);
        });
    }

}
