package com.nlmk.kb.server.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nlmk.attestation.product.api.specification.TypeCode;
import com.nlmk.attestation.product.api.pam.*;
import com.nlmk.attestation.product.api.specification.SpecCode;
import com.nlmk.kb.server.api.ccm.pts.CcmPtsRequest;
import com.nlmk.kb.server.service.ccm.RestRequestAdapter;
import com.nlmk.kb.server.service.ccm.pts.CcmPtsRestRequestAdapterImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import javax.validation.Validation;
import javax.validation.Validator;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

class RestRequestAdapterTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    private final CommonConverter commonConverter = new CommonConverterImpl();
    private final RestRequestAdapter<CcmPtsRequest> ccmPtsAdapter = new CcmPtsRestRequestAdapterImpl(commonConverter);
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

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
                        .weightNet(140.0)
                        .geometry(CcmPtsRequest.Geometry.builder().thickness(30.0).width(300.0).length(3000.0).build())
                        .bundles(List.of(
                                CcmPtsRequest.Bundle.builder().stripId("s40").stripNum(40)
                                        .stripWidth(400.0).stripWeight(40.0).build()
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
                                                        .value(13.4)
                                                        .build()
                                        )).build()
                        ))
                        .properties(List.of(
                                CcmPtsRequest.OneProperty.builder().typeCode(60).typeName("t61")
                                        .analyzes(List.of(
                                                CcmPtsRequest.OnePropAnalyze.builder()
                                                        .samplingPlaceCode(62).samplingPlaceName("s63")
                                                        .analysisValue(CcmPtsRequest.AnalysisValue.BEST)
                                                        .listValues(List.of(
                                                                CcmPtsRequest.OnePropValue.builder()
                                                                        .attrCode(64)
                                                                        .attrType(TypeCode.NUMBER).build()
                                                        )).build()
                                        ))
                                        .attestationList(List.of(
                                                CcmPtsRequest.OnePropAtt.builder()
                                                        .typeCode(70).typeName("t71")
                                                        .listValues(List.of(
                                                                CcmPtsRequest.OneAttValue.builder()
                                                                        .side(CcmPtsRequest.Side.BACK)
                                                                        .attrCode(73).attrValue(74.0)
                                                                        .build()
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
                        .pk(new Pk("0001020210329001515440422", "11"))
                        .data(DataField.builder()
                                .primeId("0001020210329001515440422")
                                .nplv(2106684L).hnum(25217L).roll("1")
                                .length(3000.0).thickness(30.0).width(300.0)
                                .weightNet(140.0).bundleWeight(180.0)
                                .kceh(11L)
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
                                .orderReq(List.of())
                                .mechanical(List.of())
                                .metallographic(List.of())
                                .build())
                        .build())
                .build();
    }

}
