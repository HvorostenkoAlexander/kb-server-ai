package com.nlmk.kb.server.service.ccm;

import com.nlmk.attestation.product.api.ProductDto;
import com.nlmk.attestation.product.api.pam.AttestationRequest;
import com.nlmk.attestation.product.api.pam.ProductAttestationResultDto;
import com.nlmk.kb.server.entity.AttestationMessage;
import com.nlmk.kb.server.entity.AttestationMessageSender;
import com.nlmk.kb.server.entity.CcmMessage;
import com.nlmk.kb.server.service.AttestationMessageService;
import com.nlmk.kb.server.service.sender.PamSender;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class CcmCommonServiceTest {

    @Mock
    private PamSender pamSender;
    @Mock
    private CcmMessageService ccmMessageService;
    @Mock
    private AttestationMessageService attMessageService;
    @InjectMocks
    private CcmCommonServiceImpl ccmCommonService;

    @Captor
    ArgumentCaptor<AttestationRequest> captorRequest = ArgumentCaptor.forClass(AttestationRequest.class);

    @Test
    void rePostCcmAttestationTestOk() {
        given(ccmMessageService.findLastMessage(any(String.class))).willReturn(Optional.of(
                CcmMessage.builder()
                        .id(2L)
                        .primeId("12345")
                        .kbReceiptTs(new Date(1600000000_000L + 123455L))
                        .request(AttestationRequest.builder()
                                .id(22L)
                                .build())
                        .build()));
        given(pamSender.postAttestationRequest(any()))
                .willReturn(ProductAttestationResultDto.builder()
                        .result(ProductDto.builder().id(45L).build())
                        .build());

        final var result = ccmCommonService.rePostAttestation("12345");

        then(ccmMessageService).should(times(1)).findLastMessage(any(String.class));
        then(ccmMessageService).should(times(1)).update(any());
        then(attMessageService).should(times(1)).findLastAttestationMessage(any(String.class));
        then(attMessageService).should(times(0)).updateAttestationMessage(any());

        then(pamSender).should(times(1)).postAttestationRequest(any());
        then(pamSender).should().postAttestationRequest(captorRequest.capture());

        assertThat(result).isNotNull();
        assertThat(captorRequest.getValue().getId()).isEqualTo(22L);
    }

    @Test
    void rePostRestAttestationTestOk() {
        given(ccmMessageService.findLastMessage(any(String.class))).willReturn(Optional.empty());
        given(pamSender.postAttestationRequest(any()))
                .willReturn(ProductAttestationResultDto.builder()
                        .result(ProductDto.builder().id(50L).build())
                        .build());
        given(attMessageService.findLastAttestationMessage(any(String.class)))
                .willReturn(Optional.of(AttestationMessage.builder()
                        .id(100L).sender(AttestationMessageSender.CCM_PTS)
                        .request("{}").primeId("12345")
                        .receiptTs(new Date(1600000000_000L)).build()));

        final var result = ccmCommonService.rePostAttestation("12345");

        then(ccmMessageService).should(times(1)).findLastMessage(any(String.class));
        then(ccmMessageService).should(times(0)).update(any());
        then(attMessageService).should(times(1)).findLastAttestationMessage(any(String.class));
        then(attMessageService).should(times(1)).updateAttestationMessage(any());

        assertThat(result).isNotNull();
    }

    @Test
    void rePostCcmMessage() {
        // есть сообщения в двух таблицах одновременно, выбор последнего
        given(ccmMessageService.findLastMessage(any(String.class))).willReturn(Optional.of(
                CcmMessage.builder()
                        .id(2L)
                        .primeId("12345")
                        .kbReceiptTs(new Date(1600000000_000L + 123455L))
                        .request(AttestationRequest.builder()
                                .id(22L)
                                .build())
                        .build()));
        given(pamSender.postAttestationRequest(any()))
                .willReturn(ProductAttestationResultDto.builder()
                        .result(ProductDto.builder().id(50L).build())
                        .build());
        given(attMessageService.findLastAttestationMessage(any(String.class)))
                .willReturn(Optional.of(AttestationMessage.builder()
                        .id(100L).sender(AttestationMessageSender.CCM_PTS)
                        .request("{}").primeId("12345")
                        .receiptTs(new Date(1600000000_000L)).build()));

        final var result = ccmCommonService.rePostAttestation("12345");

        then(ccmMessageService).should(times(1)).findLastMessage(any(String.class));
        then(ccmMessageService).should(times(1)).update(any());
        then(attMessageService).should(times(1)).findLastAttestationMessage(any(String.class));
        then(attMessageService).should(times(0)).updateAttestationMessage(any());

        then(pamSender).should(times(1)).postAttestationRequest(any());
        then(pamSender).should().postAttestationRequest(captorRequest.capture());

        // выбрано сообщение CcmMessage
        assertThat(result).isNotNull();
        assertThat(captorRequest.getValue().getId()).isEqualTo(22L);
    }

    @Test
    void rePostAttestationMessage() {
        // есть сообщения в двух таблицах одновременно, выбор последнего
        given(ccmMessageService.findLastMessage(any(String.class))).willReturn(Optional.of(
                CcmMessage.builder()
                        .id(2L)
                        .primeId("12345")
                        .kbReceiptTs(new Date(1600000000_000L))
                        .request(AttestationRequest.builder()
                                .id(22L)
                                .build())
                        .build()));
        given(pamSender.postAttestationRequest(any()))
                .willReturn(ProductAttestationResultDto.builder()
                        .result(ProductDto.builder().id(50L).build())
                        .build());
        given(attMessageService.findLastAttestationMessage(any(String.class)))
                .willReturn(Optional.of(AttestationMessage.builder()
                        .id(100L).sender(AttestationMessageSender.CCM_PTS)
                        .request("{}").primeId("12345")
                        .receiptTs(new Date(1600000000_000L + 123455L)).build()));
        given(attMessageService.getAttestationRequestFromMessage(any()))
                .willReturn(AttestationRequest.builder().id(33L).build());

        final var result = ccmCommonService.rePostAttestation("12345");

        then(ccmMessageService).should(times(1)).findLastMessage(any(String.class));
        then(ccmMessageService).should(times(0)).update(any());
        then(attMessageService).should(times(1)).findLastAttestationMessage(any(String.class));
        then(attMessageService).should(times(1)).updateAttestationMessage(any());

        then(pamSender).should(times(1)).postAttestationRequest(any());
        then(pamSender).should().postAttestationRequest(captorRequest.capture());

        // выбрано сообщение AttestationMessage
        assertThat(result).isNotNull();
        assertThat(captorRequest.getValue().getId()).isEqualTo(33L);
    }

    @Test
    void rePostAttestationTestNullBad() {
        assertThatThrownBy(() -> ccmCommonService.rePostAttestation(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Невозможно осуществить повторную отправку. primeId is null.");

        then(ccmMessageService).should(never()).findLastMessage(any(String.class));
        then(ccmMessageService).should(never()).update(any());
        then(pamSender).should(never()).postAttestationRequest(any());
    }

    @Test
    void rePostAttestationEmptyListBad() {
        given(attMessageService.findLastAttestationMessage(any(String.class))).willReturn(Optional.empty());
        given(ccmMessageService.findLastMessage(any(String.class))).willReturn(Optional.empty());

        assertThatThrownBy(() -> ccmCommonService.rePostAttestation("12345"))
                .isInstanceOf(IllegalArgumentException.class);

        then(ccmMessageService).should(times(1)).findLastMessage(any(String.class));
        then(ccmMessageService).should(never()).update(any());
        then(attMessageService).should(times(1)).findLastAttestationMessage(any(String.class));
        then(attMessageService).should(never()).updateAttestationMessage(any());
        then(pamSender).should(never()).postAttestationRequest(any());
    }

}
