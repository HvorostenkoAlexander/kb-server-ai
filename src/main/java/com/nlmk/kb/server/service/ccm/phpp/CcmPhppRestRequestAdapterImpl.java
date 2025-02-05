package com.nlmk.kb.server.service.ccm.phpp;

import com.nlmk.attestation.product.api.pam.AttestationRequest;
import com.nlmk.attestation.product.api.pam.DataPhpp;
import com.nlmk.attestation.product.api.pam.PhppChemical;
import com.nlmk.attestation.product.api.pam.PhppMechanicalAnalysisData;
import com.nlmk.attestation.product.api.pam.PhppMechanicalData;
import com.nlmk.attestation.product.api.pam.PhppMechanicalSpec;
import com.nlmk.attestation.product.api.pam.Pk;
import com.nlmk.attestation.product.api.pam.SpecValue;
import com.nlmk.attestation.product.api.pam.Specs;
import com.nlmk.attestation.product.api.pam.Value;
import com.nlmk.kb.server.api.ccm.phpp.CcmPhppRequest;
import com.nlmk.kb.server.service.CommonConverter;
import com.nlmk.kb.server.service.ccm.RestRequestAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CcmPhppRestRequestAdapterImpl implements RestRequestAdapter<CcmPhppRequest> {

    private final CommonConverter converter;

    @Override
    public AttestationRequest adapt(CcmPhppRequest requestMessage) {
        final var dateRequest = converter.parseToDate(requestMessage.getTs());

        return AttestationRequest.builder()
                .value(Value.builder()
                        .ts(dateRequest)
                        .op("I")
                        .pk(Pk.builder()
                                .systemCode(requestMessage.getPk().getSystemCode())
                                .id(requestMessage.getPk().getId())
                                .build())
                        .data(toPamDataField(requestMessage))
                        .build())
                .build();
    }

    private DataPhpp toPamDataField(CcmPhppRequest request) {
        final var data = request.getData();

        if (Objects.nonNull(data)) {
            return DataPhpp.builder()
                    .primeId(request.getPk().getId())
                    .heat(data.getHeat())
                    .hnum(data.getHnum())
                    .tnum(data.getTnum())
                    .roll(data.getRoll())
                    .length(data.getLength())
                    .thickness(data.getThickness())
                    .width(data.getWidth())
                    .weightNet(data.getWeightNet())
                    .kceh(data.getWorkshopNum())
                    .orderNum(data.getOrderNum())
                    .orderPos(data.getOrderPos())
                    .attestationPoint(data.getAttestationPoint())
                    .specifications(
                            data.getSpecifications().stream()
                                    .map(this::toPamSpecs)
                                    .collect(Collectors.toList())
                    )
                    .chemical(toPamChemicalList(data))
                    .mechanical(toPamMechanicalList(data))
                    .build();
        }

        return null;
    }

    private SpecValue toPamSpecValue(CcmPhppRequest.OneSpecValue specValue) {
        return SpecValue.builder()
                .value(specValue.getValue())
                .build();
    }

    private List<SpecValue> toPamSpecValueList(CcmPhppRequest.Specification specification) {
        if (specification.getListValues() == null || specification.getListValues().isEmpty()) {
            return List.of();
        }
        return specification.getListValues().stream()
                .map(this::toPamSpecValue)
                .collect(Collectors.toList());
    }

    private Specs toPamSpecs(CcmPhppRequest.Specification specification) {
        return Specs.builder()
                .specCode(specification.getSpecCode())
                .specName(specification.getSpecName())
                .specValue(specification.getSpecValue())
                .specTypeCode(specification.getSpecTypeCode())
                .specTypeName(specification.getSpecTypeName())
                .specTypeValue(specification.getSpecTypeValue().getValue())
                .listValues(toPamSpecValueList(specification))
                .build();
    }

    private PhppChemical toPamChemical(CcmPhppRequest.Chemical chemical) {
        return PhppChemical.builder()
                .chemCode(chemical.getChemCode())
                .chemName(chemical.getChemName())
                .chemValue(chemical.getChemValue())
                .build();
    }

    @SuppressWarnings("checkstyle:illegalidentifiername")
    private List<PhppChemical> toPamChemicalList(CcmPhppRequest.Record record) {
        if (record.getChemical() == null || record.getChemical().isEmpty()) {
            return List.of();
        }
        return record.getChemical().stream()
                .map(this::toPamChemical)
                .collect(Collectors.toList());

    }

    private PhppMechanicalAnalysisData toPamMechanicalAnalysisData(CcmPhppRequest.TestData testData) {
        return PhppMechanicalAnalysisData.builder()
                .mechCode(testData.getMechCode())
                .mechName(testData.getMechName())
                .mechTypeCode(testData.getMechTypeCode())
                .mechValue(testData.getMechValue())
                .build();
    }

    private PhppMechanicalData toPamMechanicalData(CcmPhppRequest.MechanicData mechanicData) {
        return PhppMechanicalData.builder()
                .analysisId(mechanicData.getAnalysisId())
                .testData(
                        mechanicData.getTestData().stream()
                                .map(this::toPamMechanicalAnalysisData)
                                .collect(Collectors.toList())
                )
                .build();
    }

    private PhppMechanicalSpec toPamMechanical(CcmPhppRequest.Mechanic mechanic) {
        return PhppMechanicalSpec.builder()
                .hnum(mechanic.getHnum())
                .tnum(mechanic.getTnum())
                .protNum(mechanic.getProtNum())
                .protDate(mechanic.getProtDate())
                .sampleNum(mechanic.getSampleNum())
                .signAnalysis(mechanic.getSignAnalysis())
                .mechData(
                        mechanic.getMechData().stream()
                                .map(this::toPamMechanicalData)
                                .collect(Collectors.toList())
                )
                .build();
    }

    @SuppressWarnings("checkstyle:illegalidentifiername")
    private List<PhppMechanicalSpec> toPamMechanicalList(CcmPhppRequest.Record record) {
        if (record.getMechanical() == null || record.getMechanical().isEmpty()) {
            return List.of();
        }
        return record.getMechanical().stream()
                .map(this::toPamMechanical)
                .collect(Collectors.toList());
    }

}
