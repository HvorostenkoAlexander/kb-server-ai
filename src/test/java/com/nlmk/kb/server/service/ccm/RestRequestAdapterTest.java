package com.nlmk.kb.server.service.ccm;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nlmk.attestation.product.api.pam.*;
import com.nlmk.attestation.product.api.specification.SpecCode;
import com.nlmk.attestation.product.api.specification.TypeCode;
import com.nlmk.kb.server.api.ccm.pts.CcmPtsRequest;
import com.nlmk.kb.server.service.CommonConverter;
import com.nlmk.kb.server.service.CommonConverterImpl;
import com.nlmk.kb.server.service.ccm.pts.CcmPtsRestRequestAdapterImpl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.validation.Validation;
import javax.validation.Validator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class RestRequestAdapterTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    private final CommonConverter commonConverter = new CommonConverterImpl();
    private RestRequestAdapter<CcmPtsRequest> ccmPtsAdapter;
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

    @BeforeEach
    void initAdapter() {
        ccmPtsAdapter = new CcmPtsRestRequestAdapterImpl(commonConverter);
    }

    @Test
    void adaptCcmPts() {
        final var request = prepareRequestWithMandatoryData();
        Assertions.assertTrue(validator.validate(request).isEmpty());
        final var attestationRequest = Assertions.assertDoesNotThrow(() -> ccmPtsAdapter.adapt(prepareRequestWithMandatoryData()));
        Assertions.assertEquals(prepareAttestationRequest(), attestationRequest);
    }

    @Test
    @Disabled("get output JSON for test")
    void printCcmPtsRequestAsJson() throws Exception {
        final var mapper = new ObjectMapper();
        final var request = prepareRequestWithMandatoryData();
        Assertions.assertTrue(validator.validate(request).isEmpty());
        System.out.println(mapper.writeValueAsString(request));
    }

    private CcmPtsRequest prepareRequestWithMandatoryData() {
        return CcmPtsRequest.builder()
                .ts(sdf.format(new Date(1000000000_000L))) // для теста!
                .pk(CcmPtsRequest.Pk.builder().systemCode("11").id("0001020210329001515440422").build())
                .data(CcmPtsRequest.Record.builder()
                        .werks(10).werksName("w10")
                        .kceh(11).kcehName("k11")
                        .unitCode(12).unitName("u12")
                        .storageCode(13).storageName("s13")
                        .marking(CcmPtsRequest.Marking.builder()
                                .nplv(2106684).hnum(25217).tnum(22).roll(1).build())
                        .weightNet(BigDecimal.valueOf(140.0))
                        .geometry(CcmPtsRequest.Geometry.builder()
                                .thickness(BigDecimal.valueOf(30.0))
                                .width(BigDecimal.valueOf(300.0))
                                .length(BigDecimal.valueOf(3000.0)).build())
                        .bundles(List.of(
                                CcmPtsRequest.Bundle.builder().stripId(40L).stripNum(40)
                                        .stripWidth(BigDecimal.valueOf(400.0)).stripWeight(BigDecimal.valueOf(40.0)).build()
                        ))
                        .specifications(List.of(
                                CcmPtsRequest.Specification.builder()
                                        .specCode(SpecCode.STEEL_MARK.getValue())
                                        .specName(SpecCode.STEEL_MARK.getDesc())
                                        .specTypeCode(TypeCode.STRING.getValue())
                                        .specTypeName(TypeCode.STRING.getDesc())
                                        .specTypeValue(CcmPtsRequest.SpecTypeValue.SIMPLE) // !
                                        .specValue("Ст3сп")
                                        .listValues(List.of()).build()
                        ))
                        .chemical(List.of(
                                CcmPtsRequest.Chemical.builder()
                                        .id(1).listValues(List.of(
                                                CcmPtsRequest.OneChemicalValue.builder()
                                                        .code(SpecCode.MASS_FRACTION_B.getValue())
                                                        .name(SpecCode.MASS_FRACTION_B.getDesc())
                                                        .value(BigDecimal.valueOf(13.4))
                                                        .build()
                                        )).build()
                        ))
                        .properties(List.of(
                                CcmPtsRequest.OneProperty.builder()
                                        .typeCode(60).typeName("t61").testDate("2022-09-16T14:22:33+03:00").probeCode(70).probeName("p70")
                                        .analyzes(List.of(
                                                CcmPtsRequest.OnePropAnalyze.builder()
                                                        .samplingPlaceCode(62).samplingPlaceName("s63")
                                                        .analysisValue(AnalysisValue.BEST)
                                                        .listValues(List.of(
                                                                CcmPtsRequest.OneAnalyzeValue.builder()
                                                                        .attrCode(562)
                                                                        .attrType(TypeCode.NUMBER).build(),
                                                                CcmPtsRequest.OneAnalyzeValue.builder()
                                                                        .attrCode(99999)    // not allowed
                                                                        .attrType(TypeCode.NUMBER).build()
                                                        )).build()
                                        ))
                                        .attestationList(List.of(
                                                CcmPtsRequest.OnePropAtt.builder()
                                                        .typeCode(1082).typeName("1082")
                                                        .listValues(List.of(
                                                                CcmPtsRequest.OneAttValue.builder()
                                                                        .side(CcmPtsRequest.Side.BACK)
                                                                        .attrCode(73).attrValue(BigDecimal.valueOf(74.0))
                                                                        .build()
                                                        )).build()
                                        ))
                                        .listValues(List.of(
                                                CcmPtsRequest.OnePropValue.builder()
                                                        .attrCode(SpecCode.AGING_FACTOR.getValue())
                                                        .attrType(TypeCode.STRING).attrValue(List.of(
                                                                CcmPtsRequest.OnePropValueAttr.builder()
                                                                        .value("af12").build()
                                                        )).build(),
                                                CcmPtsRequest.OnePropValue.builder()
                                                        .attrCode(SpecCode.PLASTICITY_NUMBER_OF_BENDS.getValue())
                                                        .attrType(TypeCode.NUMBER).attrValue(List.of(
                                                                CcmPtsRequest.OnePropValueAttr.builder()
                                                                        .value("12").build()
                                                        )).build()
                                        )).build()
                        ))
                        .build())
                .build();
    }

    private AttestationRequest prepareAttestationRequest() {
        return AttestationRequest.builder()
                .value(Value.builder()
                        .ts(new Date(1000000000_000L)) // для теста!
                        .op("I")
                        .pk(Pk.builder().systemCode("11").id("0001020210329001515440422").build())
                        .data(DataPts.builder()
                                .primeId("0001020210329001515440422")
                                .nplv(2106684).hnum(25217).roll("1")
                                .length(BigDecimal.valueOf(3000.0)).thickness(BigDecimal.valueOf(30.0)).width(BigDecimal.valueOf(300.0))
                                .weightNet(BigDecimal.valueOf(140.0)).bundleWeight(BigDecimal.valueOf(180.0))
                                .kceh(11)
                                .specifications(List.of(
                                        Specs.builder()
                                                .specCode(SpecCode.STEEL_MARK.getValue())
                                                .specName(SpecCode.STEEL_MARK.getDesc())
                                                .specTypeCode(TypeCode.STRING.getValue())
                                                .specValue("Ст3сп")
                                                .build()
                                ))
                                .chemical(List.of(
                                        ChemicalSpec.builder()
                                                .chemCode(SpecCode.MASS_FRACTION_B.getValue())
                                                .chemName(SpecCode.MASS_FRACTION_B.getDesc())
                                                .chemValue("13.4")
                                                .build()
                                ))
                                .mechanicalPts(List.of(
                                        PtsMechanicalProperty.builder()
                                                .typeCode(60).typeName("t61").testDate("2022-09-16T14:22:33+03:00").probeCode(70).probeName("p70")
                                                .analyzes(List.of(
                                                        PtsPropertyAnalyzes.builder()
                                                                .samplingPlaceCode(62).samplingPlaceName("s63")
                                                                .analysisValue(AnalysisValue.BEST.getValue())
                                                                .listValues(List.of(
                                                                        PtsPropertyAnalyzesValue.builder().attrCode(562).attrType(TypeCode.NUMBER.getValue()).build(),
                                                                        PtsPropertyAnalyzesValue.builder().attrCode(99999).attrType(TypeCode.NUMBER.getValue()).build()
                                                                )).build()
                                                ))
                                                .listValues(List.of(
                                                        PtsPropertyValue.builder()
                                                                .attrCode(SpecCode.AGING_FACTOR.getValue())
                                                                .attrType(TypeCode.STRING.getValue())
                                                                .attrValue(List.of("af12"))
                                                                .build(),
                                                        PtsPropertyValue.builder()
                                                                .attrCode(SpecCode.PLASTICITY_NUMBER_OF_BENDS.getValue())
                                                                .attrType(TypeCode.NUMBER.getValue())
                                                                .attrValue(List.of("12"))
                                                                .build()
                                                ))
                                                .attestationList(List.of(
                                                        PtsPropertyAttribute.builder()
                                                                .typeCode(1082).typeName("1082")
                                                                .listValues(List.of(
                                                                        PtsPropertyAttributeValue.builder()
                                                                                .side(CcmPtsRequest.Side.BACK.getValue())
                                                                                .attrCode(73).attrValue("74.0").build()
                                                                )).build()
                                                )).build()))
                                .build())
                        .build())
                .build();
    }

}
