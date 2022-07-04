package com.nlmk.kb.server.service;

import com.nlmk.kb.server.api.ccm.pts.CcmPtsRequest;
import com.nlmk.kb.server.api.ccm.pts.CcmPtsTypeCode;
import com.nlmk.kb.server.service.ccm.RestRequestAdapter;
import com.nlmk.kb.server.service.ccm.pts.CcmPtsRestRequestAdapterImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.validation.Validation;
import javax.validation.Validator;
import java.util.List;

class RestRequestAdapterTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    private final CommonConverter commonConverter = new CommonConverterImpl();
    private final RestRequestAdapter<CcmPtsRequest> ccmPtsAdapter = new CcmPtsRestRequestAdapterImpl(commonConverter);

    @Test
    void adaptCcmPts() {
        final var request = prepareRequestWithMandatoryData();
        Assertions.assertTrue(validator.validate(request).isEmpty());
        Assertions.assertDoesNotThrow(() -> ccmPtsAdapter.adapt(prepareRequestWithMandatoryData()));
    }

    private CcmPtsRequest prepareRequestWithMandatoryData() {
        return CcmPtsRequest.builder()
                .ts("2022-07-01T00:00:00.000Z")
                .pk(CcmPtsRequest.Pk.builder().systemCode("sc").id("id").build())
                .data(CcmPtsRequest.Record.builder()
                        .werks(10).werksName("w10")
                        .kceh(11).kcehName("k11")
                        .unitCode(12).unitName("u12")
                        .storageCode(13).storageName("s13")
                        .marking(CcmPtsRequest.Marking.builder().nplv(20).hnum(21).tnum(22).roll(23).build())
                        .weightNet(14.0)
                        .geometry(CcmPtsRequest.Geometry.builder().thickness(30.0).width(32.0).length(31.0).build())
                        .bundles(List.of(
                                CcmPtsRequest.Bundle.builder().stripId("s40").stripNum(41)
                                        .stripWidth(42.0).stripWeight(43.0).build()
                        ))
                        .specifications(List.of(
                                CcmPtsRequest.Specification.builder()
                                        .specCode(50).specName("s51")
                                        .specTypeCode(52).specTypeName("s53")
                                        .specTypeValue(CcmPtsRequest.SpecTypeValue.SIMPLE)
                                        .listValues(List.of(
                                                CcmPtsRequest.OneSpecValue.builder().value("v54").build()
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
                                                                        .attrType(CcmPtsTypeCode.NUMBER).build()
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
                        .chemical(List.of(
                                CcmPtsRequest.Chemical.builder()
                                        .id(80).listValues(List.of(
                                                CcmPtsRequest.OneChemicalValue.builder()
                                                        .code(81).name("n82").build()
                                        )).build()
                        ))
                        .build())
                .build();
    }

}
