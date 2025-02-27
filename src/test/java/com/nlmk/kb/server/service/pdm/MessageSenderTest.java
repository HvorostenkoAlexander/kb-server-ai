package com.nlmk.kb.server.service.pdm;

import com.nlmk.kb.server.entity.DictionaryConfig;
import com.nlmk.kb.server.entity.pdm.Data;
import com.nlmk.kb.server.entity.pdm.PdmDictionary;
import com.nlmk.kb.server.entity.pdm.PdmMessage;
import com.nlmk.kb.server.entity.pdm.Pk;
import com.nlmk.kb.server.repository.DictionaryConfigRepository;
import com.nlmk.kb.server.service.CommonConverterImpl;
import com.nlmk.kb.server.service.pdm.senders.AsapChemicalPropSender;
import com.nlmk.kb.server.service.pdm.senders.AsapMechPropertiesDtSender;
import com.nlmk.kb.server.service.pdm.senders.AsapMechPropertiesSender;
import com.nlmk.kb.server.service.pdm.senders.ChemicalPropertiesSender;
import com.nlmk.kb.server.service.pdm.senders.EquivalentsSender;
import com.nlmk.kb.server.service.pdm.senders.KatSteelGost4041Sender;
import com.nlmk.kb.server.service.pdm.senders.MacrostructureSender;
import com.nlmk.kb.server.service.pdm.senders.MatchRpNumSender;
import com.nlmk.kb.server.service.pdm.senders.MatchTkNumSender;
import com.nlmk.kb.server.service.pdm.senders.MechPropertiesSender;
import com.nlmk.kb.server.service.pdm.senders.MicrostructureSender;
import com.nlmk.kb.server.service.pdm.senders.MinNumberSampChemSender;
import com.nlmk.kb.server.service.pdm.senders.PdmMessageSender;
import com.nlmk.kb.server.service.pdm.senders.PhysMechPropAnisSteelSender;
import com.nlmk.kb.server.service.pdm.senders.RegisterEquivalentsSender;
import com.nlmk.kb.server.service.pdm.senders.SchemeStrippingSlabSender;
import com.nlmk.kb.server.service.pdm.senders.SpChemicalPropertiesNotesSender;
import com.nlmk.kb.server.service.pdm.senders.SpCodingSlabSender;
import com.nlmk.kb.server.service.pdm.senders.TkNumSender;
import com.nlmk.kb.server.service.pdm.senders.TolEvennessDtSender;
import com.nlmk.kb.server.service.pdm.senders.TolEvennessSender;
import com.nlmk.kb.server.service.pdm.senders.TolLengthSender;
import com.nlmk.kb.server.service.pdm.senders.TolShapeSlabSender;
import com.nlmk.kb.server.service.pdm.senders.TolThickDtSender;
import com.nlmk.kb.server.service.pdm.senders.TolThickSender;
import com.nlmk.kb.server.service.pdm.senders.TolWidthDtSender;
import com.nlmk.kb.server.service.pdm.senders.TolWidthSender;
import com.nlmk.kb.server.service.pdm.senders.ToleranceSender;
import com.nlmk.kb.server.service.pdm.senders.TypeSampleMacrostructureSender;
import com.nlmk.kb.server.service.sender.NsiSender;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DataJpaTest
@Import({
        PdmDtoConverterImpl.class,
        CommonConverterImpl.class,
        PdmDictionaryCreatorImpl.class,
        DictionaryConfigServiceImpl.class,
        DtoConverterImpl.class,
        AsapChemicalPropSender.class,
        AsapMechPropertiesDtSender.class,
        AsapMechPropertiesSender.class,
        ChemicalPropertiesSender.class,
        EquivalentsSender.class,
        KatSteelGost4041Sender.class,
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
        SpCodingSlabSender.class,
        TkNumSender.class,
        ToleranceSender.class,
        TolEvennessDtSender.class,
        TolEvennessSender.class,
        TolLengthSender.class,
        TolShapeSlabSender.class,
        TolThickDtSender.class,
        TolThickSender.class,
        TolWidthDtSender.class,
        TolWidthSender.class,
        TypeSampleMacrostructureSender.class,
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
        assertThat(messageSenders).hasSize(28);

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

        when(nsiSender.sendBodyReturnLong(any(), any(), any(), any())).thenReturn(321L);
        messageSenders.forEach(sender -> {
            final var response = sender.send(message);
            assertThat(response).isEqualTo(321L);
        });

        when(nsiSender.sendBodyReturnLong(any(), any(), any(), any())).thenReturn(null);
        messageSenders.forEach(sender -> {
            final var response = sender.send(message);
            assertThat(response).isNull();
        });
    }

}
