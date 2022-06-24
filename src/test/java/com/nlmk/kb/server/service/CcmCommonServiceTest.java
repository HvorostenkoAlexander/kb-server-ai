package com.nlmk.kb.server.service;

import com.nlmk.kb.server.entity.CcmAttestationRequestMessage;
import com.nlmk.kb.server.entity.pam.AttestationRequest;
import com.nlmk.kb.server.service.ccm.CcmCommonServiceImpl;
import com.nlmk.kb.server.service.ccm.CcmMessageService;
import com.nlmk.kb.server.service.ccm.CcmPamClientSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Date;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class CcmCommonServiceTest {

    @Mock
    private CcmPamClientSender ccmPamSender;

    @Mock
    private CcmMessageService ccmMessageService;

    @InjectMocks
    private CcmCommonServiceImpl ccmCommonService;

    private List<CcmAttestationRequestMessage> ccmMessages;

    @Captor
    ArgumentCaptor<AttestationRequest> captorRequest = ArgumentCaptor.forClass(AttestationRequest.class);

    @BeforeEach
    void setUp() {
        ccmMessages = List.of(
                CcmAttestationRequestMessage.builder()
                        .id(1L)
                        .primeId("12345")
                        .kbReceiptTs(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()))
                        .request(AttestationRequest.builder()
                                .id(11L)
                                .build())
                        .build(),
                CcmAttestationRequestMessage.builder()
                        .id(2L)
                        .primeId("12345")
                        .kbReceiptTs(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().plusMillis(123455)))
                        .request(AttestationRequest.builder()
                                .id(22L)
                                .build())
                        .build()
        );

    }

    @Test
    void rePostAttestationTestOk() {

        given(ccmMessageService.findByPrimeId(any(String.class))).willReturn(ccmMessages);
        given(ccmPamSender.postAttestationRequest(any())).willReturn(45L);

        Long resultId = ccmCommonService.rePostAttestation("12345");

        then(ccmMessageService).should(times(1)).findByPrimeId(any(String.class));
        then(ccmMessageService).should(times(1)).update(any());
        then(ccmPamSender).should(times(1)).postAttestationRequest(any());
        then(ccmPamSender).should().postAttestationRequest(captorRequest.capture());

        assertNotNull(resultId);
        assertEquals(ccmMessages.get(1).getRequest().getId(), captorRequest.getValue().getId());
    }

    @Test
    void rePostAttestationTestNullBad() {
        IllegalArgumentException iae = assertThrows(IllegalArgumentException.class,
                () -> ccmCommonService.rePostAttestation(null)
        );

        then(ccmMessageService).should(times(0)).findByPrimeId(any(String.class));
        then(ccmMessageService).should(times(0)).update(any());
        then(ccmPamSender).should(times(0)).postAttestationRequest(any());

        assertNotNull(iae);
        assertEquals("Невозможно осуществить повторную отправку. primeId is null.", iae.getMessage());
    }

    @Test
    void rePostAttestationEmptyListBad() {
        given(ccmMessageService.findByPrimeId(any(String.class))).willReturn(List.of());

        IllegalArgumentException iae = assertThrows(IllegalArgumentException.class,
                () -> ccmCommonService.rePostAttestation("12345")
        );

        then(ccmMessageService).should(times(1)).findByPrimeId(any(String.class));
        then(ccmMessageService).should(times(0)).update(any());
        then(ccmPamSender).should(times(0)).postAttestationRequest(any());

        assertNotNull(iae);
    }

}
