package com.nlmk.kb.server.service.result.sending.pts;

import com.nlmk.attestation.product.api.AttestationDto;
import com.nlmk.attestation.product.api.ProductDto;
import com.nlmk.attestation.product.api.Status;
import com.nlmk.attestation.product.api.specification.SpecCode;
import com.nlmk.kb.server.service.result.sending.ResultAdapter;
import nlmk.l3.apcs.*;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;

@Service
public class PtsResultAdapterImpl implements ResultAdapter<VerificationResultsPts> {

    private final ThreadLocal<SimpleDateFormat> dateFormat = ThreadLocal.withInitial(() -> {
        final var sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf;
    });

    @Override
    public VerificationResultsPts adapt(ProductDto product, boolean isNew) {
        Assert.notNull(product, "The product is null");
        Assert.notEmpty(product.getRequests(), "The product.getRequests() must contain elements.");
        Assert.notEmpty(product.getRequests().get(0).getAttestations(), "The product.getRequests().get(0).getAttestations() must contain elements.");

        var mismatch = Status.WAITING_FOR_DATA;
        String ts = null;

        final var attestations = product.getRequests().get(0).getAttestations();
        final var primeId = product.getRequests().get(0).getPrimeID();

        if (product.getRequests().get(0).getStatus() != null) {
            mismatch = product.getRequests().get(0).getStatus();
        }
        if (product.getRequests().get(0).getAttestationTs() != null) {
            ts = dateFormat.get().format(product.getRequests().get(0).getAttestationTs());
            dateFormat.remove();
        }

        return VerificationResultsPts.newBuilder()
                .setTs(ts)
                .setPk(RecordPk.newBuilder()
                        .setId(product.getId())
                        .setSystemCode(SpecCode.SYSTEM_CODE.getValue().toString())
                        .build())
                .setOp(isNew ? EnumOp.I : EnumOp.U)
                .setData(RecordPtsData.newBuilder()
                        .setPrimeSystemCode(product.getReferenceCode())
                        .setPrimeId(primeId)
                        .setMismatch(RecordPtsMismatch.newBuilder()
                                .setCode(mismatch.getValue())
                                .setName(mismatch.getDesc())
                                .build())
                        .setAttestationList(prepareRecordPtsAttLists(attestations))
                        .build())
                .build();
    }

    private List<RecordPtsAttList> prepareRecordPtsAttLists(List<AttestationDto> attestationList) {
        if (attestationList == null || attestationList.isEmpty()) {
            return List.of();
        }

        // todo
        return List.of();
    }

}
