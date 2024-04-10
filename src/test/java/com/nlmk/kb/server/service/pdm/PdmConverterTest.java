package com.nlmk.kb.server.service.pdm;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nlmk.attestation.product.api.nsi.TkNumDto;
import com.nlmk.kb.server.entity.pdm.PdmDictionary;
import com.nlmk.kb.server.service.CommonConverter;
import com.nlmk.kb.server.service.CommonConverterImpl;
import nlmk.l3.pdm.SpAsapChemicalProperties;
import nlmk.l3.pdm.SpAsapMechPropertiesDt;
import nlmk.l3.pdm.SpChemicalPropertiesNotes;
import nlmk.l3.pdm.SpCodingSlab;
import nlmk.l3.pdm.SpMacrosructure;
import nlmk.l3.pdm.SpMicrostructure;
import nlmk.l3.pdm.SpPhysMechPropAnisSteelStand;
import nlmk.l3.pdm.SpRegisterParameters;
import nlmk.l3.pdm.SpSchemeStrippingSlab;
import nlmk.l3.pdm.SpTkNum;
import nlmk.l3.pdm.SpTolEvenness;
import nlmk.l3.pdm.SpTolEvennessDt;
import nlmk.l3.pdm.SpTolThick;
import nlmk.l3.pdm.SpTolThickDt;
import nlmk.l3.pdm.SpTolWidthDt;
import nlmk.l3.pdm.SpTypeSampleMacrostructure;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("java:S5961")
class PdmConverterTest {

    private final CommonConverter commonConverter = new CommonConverterImpl();
    private final PdmDictionaryCreator pdmDictionaryCreator = new PdmDictionaryCreatorImpl(commonConverter);
    private final PdmDtoConverter pdmDtoConverter = new PdmDtoConverterImpl(commonConverter);

    private final ObjectMapper objectMapper = new ObjectMapper()
            .setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"));

    private String getJsonFromPath(String path) throws IOException {
        return new String(Files.readAllBytes(Path.of(path)));
    }

    @Test
    void SpTkNumEmptyDateTest() throws Exception {
        SpTkNum tkNum = objectMapper.readValue(getJsonFromPath("src/test/resources/json/SpTkNum.json"), SpTkNum.class);

        PdmDictionary dictionary = pdmDictionaryCreator.createPdmDictionary(
                tkNum.getTs(), tkNum.getOp(), tkNum.getPk(), tkNum.getData()
        );

        TkNumDto tkNumDto = pdmDtoConverter.toTkNumDto(dictionary);

        assertThat(tkNumDto.getDateStart()).isNull();
        assertThat(tkNumDto.getDateFinish()).isNull();
    }

    @Test
    void SpTkNumOkDateTest() throws Exception {
        SpTkNum tkNum = objectMapper.readValue(getJsonFromPath("src/test/resources/json/SpTkNumWithDate.json"), SpTkNum.class);

        PdmDictionary dictionary = pdmDictionaryCreator.createPdmDictionary(
                tkNum.getTs(), tkNum.getOp(), tkNum.getPk(), tkNum.getData()
        );

        TkNumDto tkNumDto = pdmDtoConverter.toTkNumDto(dictionary);

        assertThat(tkNumDto.getDateStart()).isEqualTo("2000-05-26T09:58:15.6117006+03:00");
        assertThat(tkNumDto.getDateFinish()).isEqualTo("2022-05-26T09:58:15.6117006+03:00");
    }

    @Test
    void fromMicrostructureTest() throws Exception {
        SpMicrostructure micro = objectMapper.readValue(getJsonFromPath("src/test/resources/json/Microstructure.json"), SpMicrostructure.class);

        PdmDictionary pdmDictionary = pdmDictionaryCreator.createPdmDictionary(
                micro.getTs(), micro.getOp(), micro.getPk(), micro.getData()
        );

        assertThat(pdmDictionary).isNotNull();
    }

    @Test
    void fromAsapChemicalPropertiesTest() throws Exception {
        SpAsapChemicalProperties chP = objectMapper.readValue(getJsonFromPath("src/test/resources/json/AsapChemicalProperties.json"), SpAsapChemicalProperties.class);

        PdmDictionary pdmDictionary = pdmDictionaryCreator.createPdmDictionary(
                chP.getTs(), chP.getOp(), chP.getPk(), chP.getData()
        );
        assertThat(pdmDictionary).isNotNull();
    }

    @Test
    void fromAsapMechPropertiesDtTest() throws Exception {
        final var obj = objectMapper.readValue(getJsonFromPath("src/test/resources/json/AsapMechPropertiesDt.json"), SpAsapMechPropertiesDt.class);

        final var dictionary = pdmDictionaryCreator.createPdmDictionary(
                obj.getTs(), obj.getOp(), obj.getPk(), obj.getData()
        );

        final var dto = pdmDtoConverter.toAsapMechPropertiesDtDto(dictionary);

        assertThat(dto.getPrProdMark()).isEqualTo("11ЮА");
        assertThat(dto.getPrStandMark()).isEqualTo("ТУ 14-106-454-94");
        assertThat(dto.getPrThickUncoat().getSrcValue()).isEqualTo("4.00..8.00");
        assertThat(dto.getPrAnnotation()).isEqualTo("Тест");
    }

    @Test
    void fromPhysMechPropAnisSteelStandTest() throws Exception {
        final var obj = objectMapper.readValue(getJsonFromPath("src/test/resources/json/PhysMechPropAnisSteelStand.json"), SpPhysMechPropAnisSteelStand.class);

        final var dictionary = pdmDictionaryCreator.createPdmDictionary(
                obj.getTs(), obj.getOp(), obj.getPk(), obj.getData()
        );

        final var dto = pdmDtoConverter.toPhysMechPropAnisSteelStandDto(dictionary);

        assertThat(dto.getPrProdMark()).isEqualTo("NV27S-160");
        assertThat(dto.getPrStandMark()).isEqualTo("ТУ 24.10.53-0071-05757665-2021");
        assertThat(dto.getPrThickUncoat().getSrcValue()).isEqualTo("4.00..8.00");
        assertThat(dto.getPrAnnotation()).isEqualTo("Тест");
    }

    @Test
    void fromTolEvennessTest() throws Exception {
        final var obj = objectMapper.readValue(getJsonFromPath("src/test/resources/json/SpTolEvenness.json"), SpTolEvenness.class);

        final var dictionary = pdmDictionaryCreator.createPdmDictionary(
                obj.getTs(), obj.getOp(), obj.getPk(), obj.getData()
        );

        final var dto = pdmDtoConverter.toEvennessTkLimitDto(dictionary);

        assertThat(dto.getStandSort()).isEqualTo("СТО 05757665-075-2019");
        assertThat(dto.getPrWidthGood().getSrcValue()).isEqualTo("(150..*");
        assertThat(dto.getPrEvenness()).isEqualTo("2");
        assertThat(dto.getPrEvennessTolMax()).isEqualTo(4.0);
        assertThat(dto.getPrEvennessTolPerc()).isEqualTo(2.0);
        assertThat(dto.getPrFormSap().getSrcValue()).isEqualTo("ЛНТ;РЛН;РСП");
        assertThat(dto.getPrYield().getSrcValue()).isEqualTo("6");
        assertThat(dto.getPrAnnotation()).isEqualTo("Тест");
    }

    @Test
    void fromTolEvennessDtTest() throws Exception {
        final var obj = objectMapper.readValue(getJsonFromPath("src/test/resources/json/TolEvennessDt.json"), SpTolEvennessDt.class);

        final var dictionary = pdmDictionaryCreator.createPdmDictionary(
                obj.getTs(), obj.getOp(), obj.getPk(), obj.getData()
        );

        final var dto = pdmDtoConverter.toTolEvennessDtDto(dictionary);

        assertThat(dto.getDt()).isEqualTo("ДТ 37.06");
        assertThat(dto.getPrStandMark()).isEqualTo("IS 3024:2015");
        assertThat(dto.getPrWidthGood().getSrcValue()).isEqualTo("(150..*");
        assertThat(dto.getPrEvenness()).isEmpty();
        assertThat(dto.getPrEvennessTolMax()).isEqualTo(3.0);
        assertThat(dto.getPrEvennessTolPerc()).isEqualTo(1.5);
        assertThat(dto.getPrAnnotation()).isEqualTo("Тест");
    }

    @Test
    void fromTolThickDtTest() throws Exception {
        final var obj = objectMapper.readValue(getJsonFromPath("src/test/resources/json/TolThickDt.json"), SpTolThickDt.class);

        final var dictionary = pdmDictionaryCreator.createPdmDictionary(
                obj.getTs(), obj.getOp(), obj.getPk(), obj.getData()
        );

        final var dto = pdmDtoConverter.toTolThickDtDto(dictionary);

        assertThat(dto.getRemoteId()).isEqualTo("100");
        assertThat(dto.getDt()).isEqualTo("ДТ 157.00");
        assertThat(dto.getPrThickUncoat().getSrcValue()).isEqualTo("0.35");
        assertThat(dto.getLongThickDif()).isEqualTo("*..0.016");
        assertThat(dto.getPrUnevenGauge()).isEqualTo("*..0.015");
        assertThat(dto.getPrAnnotation()).isEqualTo("Тест");
    }

    @Test
    void fromTolWidthDtTest() throws Exception {
        final var obj = objectMapper.readValue(getJsonFromPath("src/test/resources/json/TolWidthDt.json"), SpTolWidthDt.class);

        final var dictionary = pdmDictionaryCreator.createPdmDictionary(
                obj.getTs(), obj.getOp(), obj.getPk(), obj.getData()
        );

        final var dto = pdmDtoConverter.toTolWidthDtDto(dictionary);

        assertThat(dto.getRemoteId()).isEqualTo("15");
        assertThat(dto.getDt()).isEqualTo("ДТ 37.06");
        assertThat(dto.getPrStandMark()).isEqualTo("IS 3024:2015");
        assertThat(dto.getPrWidthGood().getSrcValue()).isEqualTo("(1000..1020]");
        assertThat(dto.getPrFormSap().getSrcValue()).isEqualTo("ЛНТ;РЛН;РСП");
        assertThat(dto.getPrWidthTolMax()).isEqualTo(1.5);
        assertThat(dto.getSickleShape().getSrcValue()).isEqualTo("*..0.9");
        assertThat(dto.getBurr().getSrcValue()).isEqualTo("*..0.025");
        assertThat(dto.getPrAnnotation()).isEqualTo("Тест");
    }

    @Test
    void fromSpTolThickTest() throws Exception {
        final var obj = objectMapper.readValue(getJsonFromPath("src/test/resources/json/SpTolThick.json"), SpTolThick.class);

        final var dictionary = pdmDictionaryCreator.createPdmDictionary(
                obj.getTs(), obj.getOp(), obj.getPk(), obj.getData()
        );

        final var dto = pdmDtoConverter.toThicknessTkLimitDto(dictionary);

        assertThat(dto.getRemoteId()).isEqualTo("7999");
        assertThat(dto.getPrThickGood().getSrcValue()).isEqualTo("(2.50..3.00]");
        assertThat(dto.getLongThickDif()).isEqualTo("1.55");
        assertThat(dto.getPrUnevenGauge()).isEmpty();
        assertThat(dto.getPrAnnotation()).isEqualTo("Тест");
    }

    @Test
    void fromSpTolWidthTest() throws Exception {
        final var obj = objectMapper.readValue(getJsonFromPath("src/test/resources/json/SpTolWidth.json"), SpTolEvenness.class);

        final var dictionary = pdmDictionaryCreator.createPdmDictionary(
                obj.getTs(), obj.getOp(), obj.getPk(), obj.getData()
        );

        final var dto = pdmDtoConverter.toWidthTkLimitDto(dictionary);

        assertThat(dto.getStandSort()).isEqualTo("ТУ 14-1-3441-82");
        assertThat(dto.getPrWidthGood().getSrcValue()).isEqualTo("40.0;80.0");
        assertThat(dto.getPrCrop()).isEqualTo("НО");
        assertThat(dto.getPrWidthTolMax()).isEqualTo(0.1);
        assertThat(dto.getPrWidthTolMin()).isEqualTo(0.2);
        assertThat(dto.getPrLengthGood().getSrcValue()).isEqualTo("[200...*)");
        assertThat(dto.getCrescent().getSrcValue()).isEqualTo("*..3");
        assertThat(dto.getBurr().getSrcValue()).isEqualTo("*..0.015");
    }

    @Test
    void fromSpChemicalPropertiesNotesTest() throws Exception {
        final var obj = objectMapper.readValue(getJsonFromPath("src/test/resources/json/SpChemicalPropertiesNotes.json"), SpChemicalPropertiesNotes.class);

        Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX").parse("2022-12-05T06:14:41.647+03:00");
        final var dictionary = pdmDictionaryCreator.createPdmDictionary(
                obj.getTs(), obj.getOp(), obj.getPk(), obj.getData()
        );

        final var dto = pdmDtoConverter.toSpChemicalPropertiesNotesDto(dictionary);

        assertThat(dto.getRemoteId()).isEqualTo("306");
        assertThat(dto.getUpdateTs()).isEqualTo(date);
        assertThat(dto.getPrAnnotation()).isEmpty();
        assertThat(dto.getTkNum()).isEqualTo("РП-336-1-2022.01");
        assertThat(dto.getTkRoute()).isEqualTo("1;2;3");
        assertThat(dto.getC().getSrcValue()).isEqualTo("0.06..0.09");
        assertThat(dto.getMn().getSrcValue()).isEqualTo("0.70..0.85");
        assertThat(dto.getAl().getSrcValue()).isEmpty();
    }

    @Test
    void fromSpTolShapeSlabTest() throws Exception {
        final var obj = objectMapper.readValue(getJsonFromPath("src/test/resources/json/SpTolShapeSlab.json"), SpTolEvenness.class);

        final var dictionary = pdmDictionaryCreator.createPdmDictionary(
                obj.getTs(), obj.getOp(), obj.getPk(), obj.getData()
        );

        final var dto = pdmDtoConverter.toTolShapeSlabDto(dictionary);

        assertThat(dto.getDt()).isEqualTo("ДТ 0042.02");
        assertThat(dto.getPrStandMark()).isEqualTo("ТУ 24.10.21-0036-05757665-2020");
        assertThat(dto.getPrior()).isEqualTo(10);
        assertThat(dto.getPrAnnotation()).isEqualTo("Добавлены планшетность (мм) и серповидность (мм) по ДТ 0042.02");
        assertThat(dto.getVypUzkGr().getSrcValue()).isEqualTo("*..10");
    }

    @Test
    void fromSpRegisterParameters() throws Exception {
        final var obj = objectMapper.readValue(getJsonFromPath("src/test/resources/json/SpRegisterParametersCEq.json"), SpRegisterParameters.class);
        final var dictionary = pdmDictionaryCreator.createPdmDictionary(
                obj.getTs(), obj.getOp(), obj.getPk(), obj.getData()
        );

        final var dto = pdmDtoConverter.toRegisterEquivalentsDto(dictionary);

        assertThat(dto.getId()).isNull();
        assertThat(dto.getRemoteId()).isEqualTo("24");
        assertThat(dto.getParameter()).isEqualTo("Угл. эквивалент");
        assertThat(dto.getFormulaNumber()).isEqualTo("20");
        assertThat(dto.getFormula()).isEqualTo("C+Mn/6+Si/24+Ni/40+Cr/5+Mo/4+V/14");
        assertThat(dto.getCrNiCu().getSrcValue()).isEqualTo("*..0.20");
        assertThat(dto.getB().getSrcValue()).isEqualTo("0.0005..*");
        assertThat(dto.getC().getSrcValue()).isEqualTo("*..0.12");
        assertThat(dto.getPrAnnotation()).isEqualTo("C > 0");
    }

    @Test
    void fromSpMinNumberSampChemTest() throws Exception {
        final var obj = objectMapper.readValue(getJsonFromPath("src/test/resources/json/SpMinNumberSampChem.json"), SpTolEvenness.class);

        final var dictionary = pdmDictionaryCreator.createPdmDictionary(
                obj.getTs(), obj.getOp(), obj.getPk(), obj.getData()
        );

        final var dto = pdmDtoConverter.toMinNumberSampChemDto(dictionary);

        assertThat(dto.getRouteShop()).isEqualTo("КЦ-1");
        assertThat(dto.getNumberSamp()).isEqualTo(1);
        assertThat(dto.getPrior()).isEqualTo(1);
        assertThat(dto.getMnMax().getSrcValue()).isEqualTo("1.00..*");
        assertThat(dto.getSulfurMax().getSrcValue()).isEqualTo("*..0.010");
        assertThat(dto.getPhosphorusMax().getSrcValue()).isEqualTo("*..0.010");
        assertThat(dto.getPhosphorusMin().getSrcValue()).isEqualTo("(0..*");
        assertThat(dto.getCrMin().getSrcValue()).isEqualTo("(0..*");
        assertThat(dto.getNiMin().getSrcValue()).isEqualTo("(0..*");
        assertThat(dto.getCuMin().getSrcValue()).isEqualTo("(0..*");
        assertThat(dto.getTiMin().getSrcValue()).isEqualTo("(0..*");
        assertThat(dto.getVanadiumMin().getSrcValue()).isEqualTo("(0..*");
        assertThat(dto.getNbMin().getSrcValue()).isEqualTo("(0..*");
        assertThat(dto.getMoMin().getSrcValue()).isEqualTo("(0..*");
        assertThat(dto.getBorumMin().getSrcValue()).isEqualTo("(0..*");
        assertThat(dto.getSbMin().getSrcValue()).isEqualTo("(0..*");
    }

    @Test
    void fromSpSchemeStrippingSlab() throws Exception {
        final var obj = objectMapper.readValue(getJsonFromPath("src/test/resources/json/SpSchemeStrippingSlab.json"), SpSchemeStrippingSlab.class);

        final var dictionary = pdmDictionaryCreator.createPdmDictionary(
                obj.getTs(), obj.getOp(), obj.getPk(), obj.getData()
        );

        final var dto = pdmDtoConverter.toSchemeStrippingSlabDto(dictionary);

        assertThat(dto.getId()).isNull();
        assertThat(dto.getRemoteId()).isEqualTo("28");
        assertThat(dto.getPrStandMark()).isEqualTo("ГОСТ 1");
        assertThat(dto.getPrSteelMark()).isEqualTo("сп3");
        assertThat(dto.getPrior()).isEqualTo(1);
        assertThat(dto.getDt()).isEqualTo("ДТ 1");
        assertThat(dto.getRouteShop()).isEqualTo("КЦ-1");
        assertThat(dto.getWorkCenterCode()).isEmpty();
        assertThat(dto.getCustomerCodeName()).isEmpty();
        assertThat(dto.getPrCustomer()).isEqualTo("2000001389");
        assertThat(dto.getPrThickGood().getSrcValue()).isEqualTo("*..6)");
        assertThat(dto.getMacroStrAver().getSrcValue()).isEqualTo("!1..3");
        assertThat(dto.getUglr().getSrcValue()).isEqualTo("0.08..*");
        assertThat(dto.getMn().getSrcValue()).isEqualTo("(0.6..*");
        assertThat(dto.getNb().getSrcValue()).isEqualTo("*..0.005)");
        assertThat(dto.getB().getSrcValue()).isEqualTo("*..0.0006)");
        assertThat(dto.getCodeSlabEar().getSrcValue()).isEqualTo("U08");
        assertThat(dto.getMeltSlab()).isEqualTo("плавка");
        assertThat(dto.getNumberSlabSeria().getSrcValue()).isEqualTo("!100");
        assertThat(dto.getNumberSlabPlavka().getSrcValue()).isEqualTo("!100");
        assertThat(dto.getSnakeWide()).isEqualTo("1");
        assertThat(dto.getPerimeterWide()).isEqualTo("1");
        assertThat(dto.getEdgeWide()).isEqualTo("1");
        assertThat(dto.getSnakeNarrow()).isEqualTo("2");
        assertThat(dto.getPerimeterNarrow()).isEqualTo("2");
        assertThat(dto.getEdgeNarrow()).isEqualTo("2");
        assertThat(dto.getPrAnnotation()).isEqualTo("тест");
    }

    @Test
    void fromSpMacrosructure() throws Exception {
        final var obj = objectMapper.readValue(getJsonFromPath("src/test/resources/json/SpMacrosructure.json"), SpMacrosructure.class);

        final var dictionary = pdmDictionaryCreator.createPdmDictionary(
                obj.getTs(), obj.getOp(), obj.getPk(), obj.getData()
        );

        final var dto = pdmDtoConverter.toMacrostructureDto(dictionary);

        assertThat(dto.getId()).isNull();
        assertThat(dto.getRemoteId()).isEqualTo("103");
        assertThat(dto.getPrStandMark()).isEqualTo("ГОСТ 1");
        assertThat(dto.getDt()).isEqualTo("ДТ 22.02");
        assertThat(dto.getPrior()).isEqualTo(10);
        assertThat(dto.getTkNum()).isEqualTo("ТК-185");
        assertThat(dto.getRoute()).isEqualTo("1");
        assertThat(dto.getCustomerCodeName()).isEqualTo("БОРУСАН МАННЕСМАНН БОРУ");
        assertThat(dto.getPrCustomer()).isEqualTo("2000002631");
        assertThat(dto.getPrThickGood().getSrcValue()).isEqualTo("(4..*");
        assertThat(dto.getGrSteelVmz()).isEqualTo("!1");
        assertThat(dto.getRasslOpeningWidth().getSrcValue()).isEqualTo("*..1");
        assertThat(dto.getRasslTotalLength().getSrcValue()).isEqualTo("*..50");
        assertThat(dto.getPoreDiametr().getSrcValue()).isEqualTo("*..2");
        assertThat(dto.getVnutrTr().getSrcValue()).isEqualTo("1..2");
        assertThat(dto.getVklObl().getSrcValue()).isEqualTo("2..3");
        assertThat(dto.getOsevSeqr().getSrcValue()).isEqualTo("3..4");
        assertThat(dto.getVklToch().getSrcValue()).isEqualTo("4..5");
        assertThat(dto.getUzkgrTr().getSrcValue()).isEqualTo("5..6");
        assertThat(dto.getUglovTr().getSrcValue()).isEqualTo("6..7");
        assertThat(dto.getPrAnnotation()).isEqualTo("тест");
    }

    @Test
    void fromSpTypeSampleMacrostructure() throws Exception {
        final var obj = objectMapper.readValue(getJsonFromPath("src/test/resources/json/SpTypeSampleMacrostructure.json"), SpTypeSampleMacrostructure.class);

        final var dictionary = pdmDictionaryCreator.createPdmDictionary(
                obj.getTs(), obj.getOp(), obj.getPk(), obj.getData()
        );

        final var dto = pdmDtoConverter.toTypeSampleMacrostructureDto(dictionary);

        assertThat(dto.getId()).isNull();
        assertThat(dto.getRemoteId()).isEqualTo("2");
        assertThat(dto.getPrStandMark()).isEqualTo("!ТУ 24.10.20");
        assertThat(dto.getPrior()).isEqualTo(2);
        assertThat(dto.getUglr().getSrcValue()).isEqualTo("0.01..*");
        assertThat(dto.getSera().getSrcValue()).isEqualTo("(0.003..*");
        assertThat(dto.getType()).isEqualTo("СО");
        assertThat(dto.getPrAnnotation()).isEqualTo("Серный отпечаток");
    }


    @Test
    void fromSpCodingSlab() throws Exception {
        final var obj = objectMapper.readValue(getJsonFromPath("src/test/resources/json/SpCodingSlab.json"), SpCodingSlab.class);
        final var dictionary = pdmDictionaryCreator.createPdmDictionary(
                obj.getTs(), obj.getOp(), obj.getPk(), obj.getData()
        );

        final var dto = pdmDtoConverter.toCodingSlabDto(dictionary);

        assertThat(dto.getId()).isNull();
        assertThat(dto.getRemoteId()).isEqualTo("40");
        assertThat(dto.getPrStandMark()).isEqualTo("ТУ 24.10.21-0036-05757665-2020");
        assertThat(dto.getPrMarkSteel()).isEqualTo("APM45R;APM60M;TER50D;APM55G;C091AL;APM50M;C75ARW;C331;66427B;NV60TX;APM420");
        assertThat(dto.getPrior()).isNull();
        assertThat(dto.getPrCustomer()).isEqualTo("ТЕРНИУМ МХ");
        assertThat(dto.getPrCustomerCode()).isEqualTo("2000001389");
        assertThat(dto.getCodeLimitDelivery()).isEmpty();
        assertThat(dto.getCodeBanDelivery()).isEqualTo("U11;U21");
        assertThat(dto.getAcceptVolCodLimit()).isEmpty();
        assertThat(dto.getPrAnnotation()).isEqualTo("ДТ 0042.02");
    }

}
