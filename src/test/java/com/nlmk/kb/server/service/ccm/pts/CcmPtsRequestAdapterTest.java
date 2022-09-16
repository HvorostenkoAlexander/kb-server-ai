package com.nlmk.kb.server.service.ccm.pts;

import com.nlmk.attestation.product.api.pam.*;
import com.nlmk.attestation.product.api.specification.SpecCode;
import com.nlmk.kb.server.api.ccm.pts.CcmPtsRequest;
import nlmk.nlmk.l3.ccm.pts.db.attestation.request.ver1.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

class CcmPtsRequestAdapterTest {

    private static class Adapter extends CcmPtsRequestAdapter {
    }

    private final Adapter adapter = new Adapter();

    private RecordData prepareMinimalRecordData(Float weightNet) {
        return RecordData.newBuilder()
                .setWerks(1).setWerksName("1")
                .setKceh(11).setKcehName("11")
                .setUnitCode(2).setUnitName("2")
                .setStorageCode(3).setStorageName("3")
                .setMarking(RecordMarking.newBuilder()
                        .setNplv(4).setHnum(5).setTnum(6).setRoll(7)
                        .build())
                .setWeightNet(weightNet != null ? weightNet : Float.NaN)
                .setGeometry(RecordGeometry.newBuilder()
                        .setThickness(10).setWidth(11)
                        .build())
                .setSpecifications(List.of())
                .build();
    }

    private List<CcmPtsRequest.OnePropValue> prepareMechanicalPropertiesValues() {
        return Arrays.stream(SpecCode.values())
                .map(sc -> CcmPtsRequest.OnePropValue.builder()
                        .attrCode(sc.getValue())
                        .attrValue(sc.getValue().toString())
                        .attrType(sc.getTypeCode())
                        .build())
                .collect(Collectors.toList());
    }

    @Test
    void calcBundleWeight() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> adapter.calcBundleWeight("x.y"));
        Assertions.assertNull(adapter.calcBundleWeight(null));
        Assertions.assertNull(adapter.calcBundleWeight(CcmPtsRequest.builder().build()));
        Assertions.assertEquals(Double.NaN, adapter.calcBundleWeight(prepareMinimalRecordData(null)));

        Assertions.assertEquals(10.1, adapter.calcBundleWeight(CcmPtsRequest.builder()
                .data(CcmPtsRequest.Record.builder()
                        .weightNet(10.1)
                        .build())
                .build()));
        Assertions.assertEquals(20.5, adapter.calcBundleWeight(prepareMinimalRecordData(20.5f)));

        Assertions.assertEquals(20.0, adapter.calcBundleWeight(CcmPtsRequest.builder()
                .data(CcmPtsRequest.Record.builder()
                        .weightNet(10.1)
                        .bundles(List.of(
                                CcmPtsRequest.Bundle.builder().build(),
                                CcmPtsRequest.Bundle.builder().stripWeight(7.2).build(),
                                CcmPtsRequest.Bundle.builder().stripWeight(2.7).build()
                        ))
                        .build())
                .build()));

        final var record = prepareMinimalRecordData(20.5f);
        record.setBundles(List.of(
                RecordBundles.newBuilder().setStripId("s1").setStripNum(1).setStripWidth(1f).setStripWeight(2.3f).build(),
                RecordBundles.newBuilder().setStripId("s2").setStripNum(2).setStripWidth(2f).setStripWeight(2.5f).build(),
                RecordBundles.newBuilder().setStripId("s3").setStripNum(3).setStripWidth(3f).setStripWeight(5.2f).build()
        ));
        Assertions.assertEquals(30.5, adapter.calcBundleWeight(record));
    }

    @Test
    void prepareSpecs() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> adapter.prepareSpecs("x.y"));
        Assertions.assertEquals(List.of(), adapter.prepareSpecs(null));
        Assertions.assertEquals(List.of(), adapter.prepareSpecs(CcmPtsRequest.builder().build()));
        Assertions.assertEquals(List.of(), adapter.prepareSpecs(prepareMinimalRecordData(null)));

        Assertions.assertEquals(List.of(), adapter.prepareSpecs(CcmPtsRequest.builder()
                .data(CcmPtsRequest.Record.builder()
                        .specifications(List.of())
                        .build())
                .build()));

        final var record = prepareMinimalRecordData(null);
        record.setSpecifications(List.of());
        Assertions.assertEquals(List.of(), adapter.prepareSpecs(record));

        Assertions.assertEquals(List.of(
                Specs.builder().specCode(1).specValue("1").build(),
                Specs.builder().specCode(3).specValue("31").build(),
                Specs.builder().specCode(3).specValue("32").build()
        ), adapter.prepareSpecs(CcmPtsRequest.builder()
                .data(CcmPtsRequest.Record.builder()
                        .specifications(List.of(
                                CcmPtsRequest.Specification.builder()
                                        .specTypeValue(CcmPtsRequest.SpecTypeValue.SIMPLE).specCode(1).specValue("1")
                                        .build(),
                                CcmPtsRequest.Specification.builder()
                                        .specTypeValue(CcmPtsRequest.SpecTypeValue.ENUMERABLE).specCode(2)
                                        .build(),
                                CcmPtsRequest.Specification.builder()
                                        .specTypeValue(CcmPtsRequest.SpecTypeValue.ENUMERABLE).specCode(3)
                                        .listValues(List.of(
                                                CcmPtsRequest.OneSpecValue.builder().value("31").build(),
                                                CcmPtsRequest.OneSpecValue.builder().value("32").build()
                                        ))
                                        .build()
                        ))
                        .build())
                .build()));

        record.setSpecifications(List.of(
                RecordSpecifications.newBuilder().setSpecCode(1).setSpecName("1").setSpecValue("1")
                        .setSpecTypeCode(1).setSpecTypeName("1").setSpecTypeValue(1)
                        .build(),
                RecordSpecifications.newBuilder().setSpecCode(2).setSpecName("2").setSpecValue("2")
                        .setSpecTypeCode(2).setSpecTypeName("2").setSpecTypeValue(2)
                        .build(),
                RecordSpecifications.newBuilder().setSpecCode(3).setSpecName("3").setSpecValue("3")
                        .setSpecTypeCode(2).setSpecTypeName("2").setSpecTypeValue(2)
                        .setListValues(List.of(
                                RecordDataSpecificationsListValues.newBuilder().setValue("31").build(),
                                RecordDataSpecificationsListValues.newBuilder().setValue("32").build()
                        ))
                        .build()
        ));
        Assertions.assertEquals(List.of(
                Specs.builder().specCode(1).specName("1").specValue("1").specTypeCode(1).build(),
                Specs.builder().specCode(3).specName("3").specValue("31").specTypeCode(2).build(),
                Specs.builder().specCode(3).specName("3").specValue("32").specTypeCode(2).build()
        ), adapter.prepareSpecs(record));
    }

    @Test
    void prepareChemicalSpecs() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> adapter.prepareChemicalSpecs("x.y"));
        Assertions.assertEquals(List.of(), adapter.prepareChemicalSpecs(null));
        Assertions.assertEquals(List.of(), adapter.prepareChemicalSpecs(CcmPtsRequest.builder().build()));
        Assertions.assertEquals(List.of(), adapter.prepareChemicalSpecs(prepareMinimalRecordData(null)));

        Assertions.assertEquals(List.of(
                ChemicalSpec.builder().build(),
                ChemicalSpec.builder().chemCode(1).chemValue("1.2").build(),
                ChemicalSpec.builder().chemCode(2).chemValue("2.3").build()
        ), adapter.prepareChemicalSpecs(CcmPtsRequest.builder()
                .data(CcmPtsRequest.Record.builder()
                        .chemical(List.of(
                                CcmPtsRequest.Chemical.builder().id(1).listValues(List.of(
                                        CcmPtsRequest.OneChemicalValue.builder().build(),
                                        CcmPtsRequest.OneChemicalValue.builder().code(1).value(1.2).build()
                                )).build(),
                                CcmPtsRequest.Chemical.builder().id(2).listValues(List.of(
                                        CcmPtsRequest.OneChemicalValue.builder().code(2).value(2.3).build()
                                )).build()
                        ))
                        .build())
                .build()));

        final var record = prepareMinimalRecordData(null);
        record.setChemical(List.of());
        Assertions.assertEquals(List.of(), adapter.prepareChemicalSpecs(record));

        record.setChemical(List.of(
                RecordChemical.newBuilder().setId(1).setListValues(List.of(
                        RecordDataChemicalListValues.newBuilder().setCode(0).setName("0").build(),
                        RecordDataChemicalListValues.newBuilder().setCode(1).setName("1").setValue(1.2f).build()
                )).build(),
                RecordChemical.newBuilder().setId(2).setListValues(List.of(
                        RecordDataChemicalListValues.newBuilder().setCode(2).setName("2").setValue(2.3f).build()
                )).build()
        ));
        Assertions.assertEquals(List.of(
                ChemicalSpec.builder().chemCode(0).chemName("0").build(),
                ChemicalSpec.builder().chemCode(1).chemName("1").chemValue("1.2").build(),
                ChemicalSpec.builder().chemCode(2).chemName("2").chemValue("2.3").build()
        ), adapter.prepareChemicalSpecs(record));
    }

    @Test
    void prepareMechanicalProperties() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> adapter.prepareMechanicalProperties("x.y"));
        Assertions.assertEquals(List.of(), adapter.prepareMechanicalProperties(null));
        Assertions.assertEquals(List.of(), adapter.prepareMechanicalProperties(CcmPtsRequest.builder().build()));
        Assertions.assertEquals(List.of(), adapter.prepareMechanicalProperties(prepareMinimalRecordData(null)));

        var expected = List.of(
                PtsMechanicalProperty.builder()
                        .analyzes(List.of(
                                PtsPropertyAnalyzis.builder()
                                        .samplingPlaceCode(1)
                                        .analysisValue(CcmPtsRequest.AnalysisValue.BEST.getValue())
                                        .listValues(List.of())
                                        .build()
                        ))
                        .listValues(List.of())
                        .build(),
                PtsMechanicalProperty.builder()
                        .listValues(List.of(
                                PtsPropertyValue.builder().attrCode(1120).attrValue("1120").attrType(2).build()
                        ))
                        .analyzes(List.of(
                                PtsPropertyAnalyzis.builder()
                                        .samplingPlaceCode(2)
                                        .analysisValue(CcmPtsRequest.AnalysisValue.WORST.getValue())
                                        .listValues(
                                                Arrays.stream(
                                                                ("560;567;562;568;563;564;569;" +
                                                                        "529;540;546;549;542;544;552;553;528;541;547;550;543;545;548;551;533;532;536;537;538;539"
                                                                ).split(";"))
                                                        .sorted()
                                                        .map(sc -> PtsPropertyValue.builder()
                                                                .attrCode(Integer.parseInt(sc))
                                                                .attrValue(sc)
                                                                .attrType(SpecCode.fromValue(Integer.parseInt(sc)).getTypeCode().getValue())
                                                                .build())
                                                        .collect(Collectors.toUnmodifiableList())
                                        ).build()
                        )).build()
        );

        var request = CcmPtsRequest.builder()
                .data(CcmPtsRequest.Record.builder()
                        .properties(List.of(
                                CcmPtsRequest.OneProperty.builder()
                                        .analyzes(List.of())
                                        .listValues(List.of())
                                        .build(),
                                CcmPtsRequest.OneProperty.builder()
                                        .analyzes(List.of(
                                                CcmPtsRequest.OnePropAnalyze.builder()
                                                        .samplingPlaceCode(1)
                                                        .analysisValue(CcmPtsRequest.AnalysisValue.BEST)
                                                        .listValues(List.of(
                                                                CcmPtsRequest.OnePropValue.builder().build(),
                                                                CcmPtsRequest.OnePropValue.builder().attrCode(11).build(),
                                                                CcmPtsRequest.OnePropValue.builder().attrCode(12).build()
                                                        ))
                                                        .build()
                                        ))
                                        .listValues(List.of(
                                                CcmPtsRequest.OnePropValue.builder().build(),
                                                CcmPtsRequest.OnePropValue.builder().attrCode(1).build(),
                                                CcmPtsRequest.OnePropValue.builder().attrCode(2).build()
                                        ))
                                        .build(),
                                CcmPtsRequest.OneProperty.builder()
                                        // все возможные коды
                                        .analyzes(List.of(
                                                CcmPtsRequest.OnePropAnalyze.builder()
                                                        .samplingPlaceCode(2)
                                                        .analysisValue(CcmPtsRequest.AnalysisValue.WORST)
                                                        .listValues(prepareMechanicalPropertiesValues())
                                                        .build()
                                        ))
                                        .listValues(prepareMechanicalPropertiesValues())
                                        .build()
                        ))
                        .build())
                .build();

        var result = adapter.prepareMechanicalProperties(request);

        Assertions.assertEquals(expected, result);

        final var record = prepareMinimalRecordData(null);
        record.setProperties(List.of(
                RecordProperties.newBuilder()
                        .setProbeCode(1).setProbeName("1").setTestDate("1")
                        .setTypeCode(1).setTypeName("1")
                        .setAnalyzes(List.of())
                        .setAttestationList(List.of())
                        .setListValues(List.of())
                        .build(),
                RecordProperties.newBuilder()
                        .setProbeCode(2).setProbeName("2").setTestDate("2")
                        .setTypeCode(2).setTypeName("2")
                        .setAttestationList(List.of())
                        .setAnalyzes(List.of(
                               RecordAnalyzes.newBuilder()
                                       .setSamplingPlaceCode(111)
                                       .setSamplingPlaceName("1111")
                                       .setAnalysisValue(1)
                                       .setListValues(List.of(
                                               RecordDataPropertiesAnalyzesListValues.newBuilder()
                                                       .setAttrCode(540)
                                                       .setAttrType(2)
                                                       .setAttrValue("11111")
                                                       .build(),
                                               RecordDataPropertiesAnalyzesListValues.newBuilder()
                                                       .setAttrCode(99999)
                                                       .setAttrType(2)
                                                       .setAttrValue("99999")
                                                       .build()
                                       ))
                                       .build()
                        ))
                        .setListValues(List.of(
                                RecordDataPropertiesListValues.newBuilder()
                                        .setAttrCode(1).setAttrValue("1").setAttrType(1)
                                        .build(),
                                RecordDataPropertiesListValues.newBuilder()
                                        .setAttrCode(1120).setAttrValue("2").setAttrType(1)
                                        .build()
                        ))
                        .build(),
                RecordProperties.newBuilder()
                        .setProbeCode(3).setProbeName("3").setTestDate("3")
                        .setTypeCode(3).setTypeName("3")
                        .setAnalyzes(List.of())
                        .setAttestationList(List.of())
                        .setListValues(List.of(
                                RecordDataPropertiesListValues.newBuilder()
                                        .setAttrCode(3).setAttrValue("3").setAttrType(1)
                                        .build(),
                                RecordDataPropertiesListValues.newBuilder()
                                        .setAttrCode(1120).setAttrValue("4").setAttrType(1)
                                        .build()
                        ))
                        .build()
        ));

        var expected2 = List.of(
                PtsMechanicalProperty.builder()
                        .analyzes(List.of(PtsPropertyAnalyzis.builder()
                                .samplingPlaceCode(111)
                                .samplingPlaceName("1111")
                                .analysisValue(1)
                                .listValues(List.of(
                                        PtsPropertyValue.builder().attrCode(540).attrType(2).attrValue("11111").build()
                                ))
                                .build()))
                        .listValues(List.of(
                        PtsPropertyValue.builder().attrCode(1120).attrValue("2").attrType(1).build()
                )).build(),
                PtsMechanicalProperty.builder().listValues(List.of(
                        PtsPropertyValue.builder().attrCode(1120).attrValue("4").attrType(1).build()
                )).build()
        );

        Assertions.assertEquals(expected2, adapter.prepareMechanicalProperties(record));
    }

}
