package com.nlmk.kb.server.service.phpp;

import com.nlmk.attestation.product.api.Kceh;
import com.nlmk.attestation.product.api.pam.AttestationRequest;
import com.nlmk.attestation.product.api.pam.DataPhpp;
import com.nlmk.attestation.product.api.pam.PhppChemical;
import com.nlmk.attestation.product.api.pam.Pk;
import com.nlmk.attestation.product.api.pam.Specs;
import com.nlmk.attestation.product.api.pam.Value;
import com.nlmk.attestation.product.api.specification.SpecCode;
import com.nlmk.attestation.product.api.specification.TypeCode;
import com.nlmk.kb.server.api.ccm.SpecTypeValue;
import com.nlmk.kb.server.api.ccm.phpp.CcmPhppRequest;
import com.nlmk.kb.server.service.CommonConverter;
import com.nlmk.kb.server.service.CommonConverterImpl;
import com.nlmk.kb.server.service.ccm.RestRequestAdapter;
import com.nlmk.kb.server.service.ccm.phpp.CcmPhppRestRequestAdapterImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.validation.Validation;
import javax.validation.Validator;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class RestRequestAdapterTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    private final CommonConverter commonConverter = new CommonConverterImpl();
    private RestRequestAdapter<CcmPhppRequest> ccmPhppAdapter;
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

    @BeforeEach
    void initAdapter() {
        ccmPhppAdapter = new CcmPhppRestRequestAdapterImpl(commonConverter);
    }

    @Test
    void adaptCcmPhppWithStrip() {
        final var request = prepareRequestWithStrip();
        assertThat(validator.validate(request)).isEmpty();
        final var attestationRequest1 = ccmPhppAdapter.adapt(prepareRequestWithStrip());
        final var attestationRequest2 = prepareAttestationRequestWithStrip();
        assertThat(attestationRequest1).isEqualTo(attestationRequest2);
    }

    private CcmPhppRequest prepareRequestWithStrip() {
        return CcmPhppRequest.builder()
                .ts(sdf.format(new Date(1000000000_000L))) // для теста!
                .pk(CcmPhppRequest.Pk.builder().systemCode("11").id("0001020210329001515440422").build())
                .data(CcmPhppRequest.Record.builder()
                        .heat(1)
                        .hnum(25217)
                        .roll(1)
                        .length(BigDecimal.valueOf(3000.0))
                        .thickness(BigDecimal.valueOf(30.0))
                        .width(BigDecimal.valueOf(300.0))
                        .weightNet(BigDecimal.valueOf(140.0))
                        .workshopNum(Kceh.PHPP.getValue())
                        .specifications(List.of(
                                CcmPhppRequest.Specification.builder()
                                        .specCode(SpecCode.STEEL_MARK.getValue())
                                        .specName(SpecCode.STEEL_MARK.getDesc())
                                        .specTypeCode(TypeCode.STRING.getValue())
                                        .specTypeName(TypeCode.STRING.getDesc())
                                        .specTypeValue(SpecTypeValue.SIMPLE) // !
                                        .specValue("Ст3сп")
                                        .listValues(List.of()).build()
                        ))
                        .chemical(List.of(
                                CcmPhppRequest.Chemical.builder()
                                        .chemCode(SpecCode.MASS_FRACTION_B.getValue())
                                        .chemName(SpecCode.MASS_FRACTION_B.getDesc())
                                        .chemValue("13.4")
                                        .build()
                        ))
                        // TODO
                        // Добавить тестовые данные
                        .testData(List.of())
                        .build()
                )
                .build();
    }

    private AttestationRequest prepareAttestationRequestWithStrip() {
        return AttestationRequest.builder()
                .value(Value.builder()
                        .ts(new Date(1000000000_000L)) // для теста!
                        .op("I")
                        .pk(Pk.builder().systemCode("11").id("0001020210329001515440422").build())
                        .data(DataPhpp.builder()
                                .primeId("0001020210329001515440422")
                                .heat(1)
                                .hnum(25217).roll(1)
                                .length(BigDecimal.valueOf(3000.0)).thickness(BigDecimal.valueOf(30.0)).width(BigDecimal.valueOf(300.0))
                                .weightNet(BigDecimal.valueOf(140.0))
                                .kceh(Kceh.PHPP.getValue())
                                .specifications(List.of(
                                        Specs.builder()
                                                .specCode(SpecCode.STEEL_MARK.getValue())
                                                .specName(SpecCode.STEEL_MARK.getDesc())
                                                .specTypeCode(TypeCode.STRING.getValue())
                                                .specTypeName(TypeCode.STRING.getDesc())
                                                .specTypeValue(SpecTypeValue.SIMPLE.getValue())
                                                .specValue("Ст3сп")
                                                .listValues(List.of())
                                                .build()
                                ))
                                .chemical(List.of(
                                        PhppChemical.builder()
                                                .chemCode(SpecCode.MASS_FRACTION_B.getValue())
                                                .chemName(SpecCode.MASS_FRACTION_B.getDesc())
                                                .chemValue("13.4")
                                                .build()
                                ))
                                .testData(List.of())
                                .build()
                        )
                        .build())
                .build();
    }

}
