package com.nlmk.kb.server.service.ccm;

import com.nlmk.attestation.product.api.pam.ChemicalSpec;
import com.nlmk.attestation.product.api.pam.Specs;
import com.nlmk.kb.server.api.ccm.pts.CcmPtsRequest;
import nlmk.l3.ccm.pts.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

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

    }
}
