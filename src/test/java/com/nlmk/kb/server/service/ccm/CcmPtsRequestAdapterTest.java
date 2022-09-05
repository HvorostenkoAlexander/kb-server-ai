package com.nlmk.kb.server.service.ccm;

import com.nlmk.kb.server.api.ccm.pts.CcmPtsRequest;
import nlmk.l3.ccm.pts.RecordBundles;
import nlmk.l3.ccm.pts.RecordData;
import nlmk.l3.ccm.pts.RecordGeometry;
import nlmk.l3.ccm.pts.RecordMarking;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class CcmPtsRequestAdapterTest {

    private static class Adapter extends CcmPtsRequestAdapter {
    }

    private final Adapter adapter = new Adapter();

    @Test
    void calcBundleWeight() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> adapter.calcBundleWeight("0.0"));
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

}
