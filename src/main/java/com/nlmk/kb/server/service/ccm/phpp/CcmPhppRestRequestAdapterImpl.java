package com.nlmk.kb.server.service.ccm.phpp;

import com.nlmk.attestation.product.api.pam.AttestationRequest;
import com.nlmk.attestation.product.api.pam.DataPhpp;
import com.nlmk.attestation.product.api.pam.PhppChemical;
import com.nlmk.attestation.product.api.pam.PhppTestData;
import com.nlmk.attestation.product.api.pam.PhppTestSpecification;
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
                    .testData(toPamTestDataList(data))
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

    @SuppressWarnings("checkstyle:illegalidentifiername")
    private List<PhppTestData> toPamTestDataList(CcmPhppRequest.Record record) {
        if (record.getTestData() == null || record.getTestData().isEmpty()) {
            return List.of();
        }
        return record.getTestData().stream()
                .map(this::toPamTestData)
                .collect(Collectors.toList());
    }

    private PhppTestData toPamTestData(CcmPhppRequest.TestData testData) {
        return PhppTestData.builder()
                .accompanyingCardNum(testData.getAccompanyingCardNum())
                .distributionType(testData.getDistributionType())
                .testTypeRequest(testData.getTestTypeRequest())
                .specifications(toPamTestSpecificationsList(testData))
                .build();
    }

    private List<PhppTestSpecification> toPamTestSpecificationsList(CcmPhppRequest.TestData testData) {
        if (testData.getSpecifications() == null || testData.getSpecifications().isEmpty()) {
            return List.of();
        }
        return testData.getSpecifications().stream()
                .map(this::toPamTestSpecification)
                .collect(Collectors.toList());
    }

    private PhppTestSpecification toPamTestSpecification(CcmPhppRequest.TestSpecification testSpecification) {
        return PhppTestSpecification.builder()
                .specCode(testSpecification.getSpecCode())
                .specName(testSpecification.getSpecName())
                .specTypeCode(testSpecification.getSpecTypeCode())
                .specTypeName(testSpecification.getSpecTypeName())
                .specValue(testSpecification.getSpecValue())
                .build();
    }

}
