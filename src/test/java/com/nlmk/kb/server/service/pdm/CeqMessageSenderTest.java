package com.nlmk.kb.server.service.pdm;

import com.nlmk.kb.server.entity.DictionaryConfig;
import com.nlmk.kb.server.entity.pdm.Data;
import com.nlmk.kb.server.entity.pdm.PdmDictionary;
import com.nlmk.kb.server.entity.pdm.PdmMessage;
import com.nlmk.kb.server.entity.pdm.Pk;
import com.nlmk.kb.server.repository.DictionaryConfigRepository;
import com.nlmk.kb.server.service.CommonConverterImpl;
import com.nlmk.kb.server.service.pdm.senders.CeqMessageSender;
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
        CeqMessageSender.class,
        PdmDtoConverterImpl.class,
        CommonConverterImpl.class,
        PdmDictionaryCreatorImpl.class,
        DictionaryConfigServiceImpl.class,
        DtoConverterImpl.class
})
class CeqMessageSenderTest {

    @Autowired
    private DictionaryConfigRepository configRepository;
    @Autowired
    private CeqMessageSender ceqMessageSender;
    @MockBean
    NsiSender nsiSender;

    @Test
    void send() {
        final var message = PdmMessage.builder()
                .topic("topic-ceq")
                .dictionary(PdmDictionary.builder()
                        .pk(Pk.builder().Id("1").systemCode("2").build())
                        .data(Data.builder()
                                .specifications(List.of())
                                .build())
                        .build())
                .build();

        configRepository.save(DictionaryConfig.builder()
                .topic("topic-ceq")
                .nsiPath("/nsi/dict/nsd_ceq")
                .enabled(true)
                .build());
        {
            Mockito.when(nsiSender.sendBodyReturnLong(Mockito.any(), Mockito.any(), Mockito.any()))
                    .thenReturn(321L);

            final var response = Assertions.assertDoesNotThrow(() -> ceqMessageSender.send(message));
            Assertions.assertEquals(321L, response);
        }
        {
            Mockito.when(nsiSender.sendBodyReturnLong(Mockito.any(), Mockito.any(), Mockito.any()))
                    .thenReturn(null);

            final var response = Assertions.assertDoesNotThrow(() -> ceqMessageSender.send(message));
            Assertions.assertNull(response);
        }
    }

}
