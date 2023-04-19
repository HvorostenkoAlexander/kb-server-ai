package com.nlmk.kb.server.service.pdm;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nlmk.attestation.product.api.nsi.LimitDto;
import com.nlmk.attestation.product.api.nsi.TkNumDto;
import com.nlmk.kb.server.entity.pdm.PdmDictionary;
import com.nlmk.kb.server.service.CommonConverter;
import com.nlmk.kb.server.service.CommonConverterImpl;

import java.util.Date;

import nlmk.l3.pdm.*;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SuppressWarnings("java:S5961")
class PdmConverterTest {

    private final CommonConverter commonConverter = new CommonConverterImpl();
    private final PdmDictionaryCreator pdmDictionaryCreator = new PdmDictionaryCreatorImpl(commonConverter);
    private final PdmDtoConverter pdmDtoConverter = new PdmDtoConverterImpl(commonConverter);

    private String getJsonFromPath(String path) throws IOException {
        return new String(Files.readAllBytes(Path.of(path)));
    }

    @Test
    void SpTkNumEmptyDateTest() throws Exception {
        SpTkNum tkNum = new ObjectMapper()
                .setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"))
                .readValue(getJsonFromPath("src/test/resources/json/SpTkNum.json"),
                        SpTkNum.class
                );

        PdmDictionary dictionary = pdmDictionaryCreator.createPdmDictionary(
                tkNum.getTs(), tkNum.getOp(), tkNum.getPk(), tkNum.getData()
        );

        TkNumDto tkNumDto = pdmDtoConverter.toTkNumDto(dictionary);

        assertNotNull(tkNumDto);
        assertNull(tkNumDto.getDateStart());
        assertNull(tkNumDto.getDateFinish());
    }

    @Test
    void SpTkNumOkDateTest() throws Exception {
        SpTkNum tkNum = new ObjectMapper()
                .setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"))
                .readValue(getJsonFromPath("src/test/resources/json/SpTkNumWithDate.json"),
                        SpTkNum.class
                );

        PdmDictionary dictionary = pdmDictionaryCreator.createPdmDictionary(
                tkNum.getTs(), tkNum.getOp(), tkNum.getPk(), tkNum.getData()
        );

        TkNumDto tkNumDto = pdmDtoConverter.toTkNumDto(dictionary);

        assertNotNull(tkNumDto);
        assertNotNull(tkNumDto.getDateStart());
        assertNotNull(tkNumDto.getDateFinish());
        assertEquals("2000-05-26T09:58:15.6117006+03:00", tkNumDto.getDateStart());
        assertEquals("2022-05-26T09:58:15.6117006+03:00", tkNumDto.getDateFinish());
    }

    @Test
    void fromMicrostructureTest() throws Exception {

        SpMicrostructure micro = new ObjectMapper()
                .setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"))
                .readValue(getJsonFromPath("src/test/resources/json/Microstructure.json"),
                        SpMicrostructure.class
                );

        PdmDictionary pdmDictionary = pdmDictionaryCreator.createPdmDictionary(
                micro.getTs(), micro.getOp(), micro.getPk(), micro.getData()
        );

        assertNotNull(pdmDictionary);
    }

    @Test
    void fromAsapChemicalPropertiesTest() throws Exception {
        SpAsapChemicalProperties chP = new ObjectMapper()
                .setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"))
                .readValue(getJsonFromPath("src/test/resources/json/AsapChemicalProperties.json"),
                        SpAsapChemicalProperties.class
                );

        PdmDictionary pdmDictionary = pdmDictionaryCreator.createPdmDictionary(
                chP.getTs(), chP.getOp(), chP.getPk(), chP.getData()
        );
        assertNotNull(pdmDictionary);
    }

    @Test
    void fromAsapMechPropertiesDtTest() throws Exception {
        final var obj = new ObjectMapper()
                .setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"))
                .readValue(getJsonFromPath("src/test/resources/json/AsapMechPropertiesDt.json"),
                        SpAsapMechPropertiesDt.class
                );

        final var dictionary = pdmDictionaryCreator.createPdmDictionary(
                obj.getTs(), obj.getOp(), obj.getPk(), obj.getData()
        );

        final var dto = pdmDtoConverter.toAsapMechPropertiesDtDto(dictionary);

        assertNotNull(dto);
        assertEquals("11ЮА", dto.getPrProdMark());
        assertEquals("ТУ 14-106-454-94", dto.getPrStandMark());
        assertEquals("4.00..8.00", dto.getPrThickUncoat().getSrcValue());
        assertEquals("Тест", dto.getPrAnnotation());
    }

    @Test
    void fromPhysMechPropAnisSteelStandTest() throws Exception {
        final var obj = new ObjectMapper()
                .setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"))
                .readValue(getJsonFromPath("src/test/resources/json/PhysMechPropAnisSteelStand.json"),
                        SpPhysMechPropAnisSteelStand.class
                );

        final var dictionary = pdmDictionaryCreator.createPdmDictionary(
                obj.getTs(), obj.getOp(), obj.getPk(), obj.getData()
        );

        final var dto = pdmDtoConverter.toPhysMechPropAnisSteelStandDto(dictionary);

        assertNotNull(dto);
        assertEquals("NV27S-160", dto.getPrProdMark());
        assertEquals("ТУ 24.10.53-0071-05757665-2021", dto.getPrStandMark());
        assertEquals("4.00..8.00", dto.getPrThickUncoat().getSrcValue());
        assertEquals("Тест", dto.getPrAnnotation());
    }

    @Test
    void fromTolEvennessTest() throws Exception {
        final var obj = new ObjectMapper()
                .setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"))
                .readValue(getJsonFromPath("src/test/resources/json/SpTolEvenness.json"),
                        SpTolEvenness.class
                );

        final var dictionary = pdmDictionaryCreator.createPdmDictionary(
                obj.getTs(), obj.getOp(), obj.getPk(), obj.getData()
        );

        final var dto = pdmDtoConverter.toEvennessTkLimitDto(dictionary);

        assertNotNull(dto);
        assertEquals("СТО 05757665-075-2019", dto.getStandSort());
        assertEquals("(150..*", dto.getPrWidthGood().getSrcValue());
        assertEquals("2", dto.getPrEvenness());
        assertEquals(4.0, dto.getPrEvennessTolMax());
        assertEquals(2.0, dto.getPrEvennessTolPerc());
        assertEquals("ЛНТ;РЛН;РСП", dto.getPrFormSap().getSrcValue());
        assertEquals("6", dto.getPrYield().getSrcValue());
        assertEquals("Тест", dto.getPrAnnotation());
    }

    @Test
    void fromTolEvennessDtTest() throws Exception {
        final var obj = new ObjectMapper()
                .setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"))
                .readValue(getJsonFromPath("src/test/resources/json/TolEvennessDt.json"),
                        SpTolEvennessDt.class
                );

        final var dictionary = pdmDictionaryCreator.createPdmDictionary(
                obj.getTs(), obj.getOp(), obj.getPk(), obj.getData()
        );

        final var dto = pdmDtoConverter.toTolEvennessDtDto(dictionary);

        assertNotNull(dto);
        assertEquals("ДТ 37.06", dto.getDt());
        assertEquals("IS 3024:2015", dto.getPrStandMark());
        assertEquals("(150..*", dto.getPrWidthGood().getSrcValue());
        assertEquals("", dto.getPrEvenness());
        assertEquals(3.0, dto.getPrEvennessTolMax());
        assertEquals(1.5, dto.getPrEvennessTolPerc());
        assertEquals("Тест", dto.getPrAnnotation());
    }

    @Test
    void fromTolThickDtTest() throws Exception {
        final var obj = new ObjectMapper()
                .setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"))
                .readValue(getJsonFromPath("src/test/resources/json/TolThickDt.json"),
                        SpTolThickDt.class
                );

        final var dictionary = pdmDictionaryCreator.createPdmDictionary(
                obj.getTs(), obj.getOp(), obj.getPk(), obj.getData()
        );

        final var dto = pdmDtoConverter.toTolThickDtDto(dictionary);

        assertNotNull(dto);
        assertEquals("100", dto.getRemoteId());
        assertEquals("ДТ 157.00", dto.getDt());
        assertEquals("0.35", dto.getPrThickUncoat().getSrcValue());
        assertEquals("*..0.016", dto.getLongThickDif());
        assertEquals("*..0.015", dto.getPrUnevenGauge());
        assertEquals("Тест", dto.getPrAnnotation());
    }

    @Test
    void fromTolWidthDtTest() throws Exception {
        final var obj = new ObjectMapper()
                .setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"))
                .readValue(getJsonFromPath("src/test/resources/json/TolWidthDt.json"),
                        SpTolWidthDt.class
                );

        final var dictionary = pdmDictionaryCreator.createPdmDictionary(
                obj.getTs(), obj.getOp(), obj.getPk(), obj.getData()
        );

        final var dto = pdmDtoConverter.toTolWidthDtDto(dictionary);

        assertNotNull(dto);
        assertEquals("15", dto.getRemoteId());
        assertEquals("ДТ 37.06", dto.getDt());
        assertEquals("IS 3024:2015", dto.getPrStandMark());
        assertEquals("(1000..1020]", dto.getPrWidthGood().getSrcValue());
        assertEquals("ЛНТ;РЛН;РСП", dto.getPrFormSap().getSrcValue());
        assertEquals(1.5, dto.getPrWidthTolMax());
        assertEquals("*..0.9", dto.getSickleShape().getSrcValue());
        assertEquals("*..0.025", dto.getBurr().getSrcValue());
        assertEquals("Тест", dto.getPrAnnotation());
    }

    @Test
    void fromSpTolThickTest() throws Exception {
        final var obj = new ObjectMapper()
                .setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"))
                .readValue(getJsonFromPath("src/test/resources/json/SpTolThick.json"),
                        SpTolThick.class
                );

        final var dictionary = pdmDictionaryCreator.createPdmDictionary(
                obj.getTs(), obj.getOp(), obj.getPk(), obj.getData()
        );

        final var dto = pdmDtoConverter.toThicknessTkLimitDto(dictionary);

        assertNotNull(dto);
        assertEquals("7999", dto.getRemoteId());
        assertEquals("(2.50..3.00]", dto.getPrThickGood().getSrcValue());
        assertEquals("1.55", dto.getLongThickDif());
        assertEquals("", dto.getPrUnevenGauge());
        assertEquals("Тест", dto.getPrAnnotation());
    }

    @Test
    void fromSpTolWidthTest() throws Exception {
        final var obj = new ObjectMapper()
                .setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"))
                .readValue(getJsonFromPath("src/test/resources/json/SpTolWidth.json"),
                        SpTolEvenness.class
                );

        final var dictionary = pdmDictionaryCreator.createPdmDictionary(
                obj.getTs(), obj.getOp(), obj.getPk(), obj.getData()
        );

        final var dto = pdmDtoConverter.toWidthTkLimitDto(dictionary);

        assertNotNull(dto);
        assertEquals("ТУ 14-1-3441-82", dto.getStandSort());
        assertEquals("40.0;80.0", dto.getPrWidthGood().getSrcValue());
        assertEquals("НО", dto.getPrCrop());
        assertEquals(0.1, dto.getPrWidthTolMax());
        assertEquals(0.2, dto.getPrWidthTolMin());
        assertEquals("[200...*)", dto.getPrLengthGood().getSrcValue());
        assertEquals("*..3", dto.getCrescent().getSrcValue());
        assertEquals("*..0.015", dto.getBurr().getSrcValue());
    }

    @Test
    void fromSpChemicalPropertiesNotesTest() throws Exception {
        final var obj = new ObjectMapper()
                .setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"))
                .readValue(getJsonFromPath("src/test/resources/json/SpChemicalPropertiesNotes.json"),
                        SpChemicalPropertiesNotes.class
                );

        Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX").parse("2022-12-05T06:14:41.647+03:00");
        final var dictionary = pdmDictionaryCreator.createPdmDictionary(
                obj.getTs(), obj.getOp(), obj.getPk(), obj.getData()
        );

        final var dto = pdmDtoConverter.toSpChemicalPropertiesNotesDto(dictionary);

        assertNotNull(dto);
        assertEquals("306", dto.getRemoteId());
        assertEquals(date, dto.getUpdateTs());
        assertEquals("", dto.getPrAnnotation());
        assertEquals("РП-336-1-2022.01", dto.getTkNum());
        assertEquals("1;2;3", dto.getTkRoute());
        assertEquals("0.06..0.09", dto.getC().getSrcValue());
        assertEquals("0.70..0.85", dto.getMn().getSrcValue());
        assertEquals("", dto.getAl().getSrcValue());
    }

    @Test
    void fromSpTolShapeSlabTest() throws Exception {
        final var obj = new ObjectMapper()
                .setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"))
                .readValue(getJsonFromPath("src/test/resources/json/SpTolShapeSlab.json"),
                        SpTolEvenness.class
                );

        final var dictionary = pdmDictionaryCreator.createPdmDictionary(
                obj.getTs(), obj.getOp(), obj.getPk(), obj.getData()
        );

        final var dto = pdmDtoConverter.toTolShapeSlabDto(dictionary);

        assertNotNull(dto);
        assertEquals("ДТ 0042.02", dto.getDt());
        assertEquals("ТУ 24.10.21-0036-05757665-2020", dto.getPrStandMark());
        assertEquals(10, dto.getPrior());
        assertEquals("Добавлены планшетность (мм) и серповидность (мм) по ДТ 0042.02", dto.getPrAnnotation());
        assertEquals("*..10", dto.getVypUzkGr().getSrcValue());
    }

    @Test
    void fromSpRegisterEquivalents() throws Exception {
        final var obj = new ObjectMapper()
                .setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"))
                .readValue(getJsonFromPath("src/test/resources/json/SpRegisterEquivalentsCEq.json"),
                        SpRegisterEquivalents.class
                );

        final var dictionary = pdmDictionaryCreator.createPdmDictionary(
                obj.getTs(), obj.getOp(), obj.getPk(), obj.getData()
        );

        final var dto = pdmDtoConverter.toRegisterEquivalentsDto(dictionary);
        assertNotNull(dto);
        assertNull(dto.getId());
        assertEquals("24", dto.getRemoteId());
        assertEquals("Угл. эквивалент", dto.getParameter());
        assertEquals("20", dto.getFormulaNumber());
        assertEquals("C+Mn/6+Si/24+Ni/40+Cr/5+Mo/4+V/14", dto.getFormula());
        assertEquals("*..0.20", dto.getCrNiCu().getSrcValue());
        assertEquals("0.0005..*", dto.getB().getSrcValue());
        assertEquals("*..0.12", dto.getC().getSrcValue());
        assertEquals("C > 0", dto.getPrAnnotation());
    }

    @Test
    void fromSpMinNumberSampChemTest() throws Exception {
        final var obj = new ObjectMapper()
                .setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"))
                .readValue(getJsonFromPath("src/test/resources/json/SpMinNumberSampChem.json"),
                        SpTolEvenness.class
                );

        final var dictionary = pdmDictionaryCreator.createPdmDictionary(
                obj.getTs(), obj.getOp(), obj.getPk(), obj.getData()
        );

        final var dto = pdmDtoConverter.toMinNumberSampChemDto(dictionary);

        assertNotNull(dto);
        assertEquals("КЦ-1", dto.getRouteShop());
        assertEquals(1, dto.getNumberSamp());
        assertEquals(1, dto.getPrior());

        assertEquals("1.00..*", dto.getMnMax().getSrcValue());
        assertEquals("*..0.010", dto.getSMax().getSrcValue());
        assertEquals("*..0.010", dto.getPMax().getSrcValue());
        assertEquals("(0..*", dto.getPMin().getSrcValue());
        assertEquals("(0..*", dto.getCrMin().getSrcValue());
        assertEquals("(0..*", dto.getNiMin().getSrcValue());
        assertEquals("(0..*", dto.getCuMin().getSrcValue());
        assertEquals("(0..*", dto.getTiMin().getSrcValue());
        assertEquals("(0..*", dto.getVMin().getSrcValue());
        assertEquals("(0..*", dto.getNbMin().getSrcValue());
        assertEquals("(0..*", dto.getMoMin().getSrcValue());
        assertEquals("(0..*", dto.getBMin().getSrcValue());
        assertEquals("(0..*", dto.getSbMin().getSrcValue());


    }

    @Test
    void fromSpSchemeStrippingSlab() throws Exception {
        final var obj = new ObjectMapper()
                .setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"))
                .readValue(getJsonFromPath("src/test/resources/json/SpSchemeStrippingSlab.json"),
                        SpSchemeStrippingSlab.class
                );

        final var dictionary = pdmDictionaryCreator.createPdmDictionary(
                obj.getTs(), obj.getOp(), obj.getPk(), obj.getData()
        );

        final var dto = pdmDtoConverter.toSchemeStrippingSlabDto(dictionary);
        assertNotNull(dto);
        assertNull(dto.getId());
        assertEquals("28", dto.getRemoteId());
        // updateTs
        assertEquals("ГОСТ 1", dto.getPrStandMark());
        assertEquals("сп3", dto.getPrSteelMark());
        assertEquals(1, dto.getPrior());
        assertEquals("ДТ 1", dto.getDt());
        assertEquals("КЦ-1", dto.getRouteShop());
        assertEquals("", dto.getWorkCenterCode());
        assertEquals("", dto.getCustomerCodeName());
        assertEquals("2000001389", dto.getPrCustomer());
        assertEquals("*..6)", dto.getPrThickGood().getSrcValue());
        assertEquals("!1..3", dto.getMacroStrAver().getSrcValue());
        assertEquals("0.08..*", dto.getUglr().getSrcValue());
        assertEquals("(0.6..*", dto.getMn().getSrcValue());
        assertEquals("*..0.005)", dto.getNb().getSrcValue());
        assertEquals("*..0.0006)", dto.getB().getSrcValue());
        assertEquals("U08", dto.getCodeSlabEar().getSrcValue());
        assertEquals("плавка", dto.getMeltSlab());
        assertEquals("!100", dto.getNumberSlabSeria().getSrcValue());
        assertEquals("!100", dto.getNumberSlabPlavka().getSrcValue());
        assertEquals("1", dto.getSnakeWide());
        assertEquals("1", dto.getPerimeterWide());
        assertEquals("1", dto.getEdgeWide());
        assertEquals("2", dto.getSnakeNarrow());
        assertEquals("2", dto.getPerimeterNarrow());
        assertEquals("2", dto.getEdgeNarrow());
        assertEquals("тест", dto.getPrAnnotation());
    }

    @Test
    void fromSpMacrosructure() throws Exception {
        final var obj = new ObjectMapper()
                .setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"))
                .readValue(getJsonFromPath("src/test/resources/json/SpMacrosructure.json"),
                        SpMacrosructure.class
                );

        final var dictionary = pdmDictionaryCreator.createPdmDictionary(
                obj.getTs(), obj.getOp(), obj.getPk(), obj.getData()
        );

        final var dto = pdmDtoConverter.toMacrostructureDto(dictionary);
        assertNotNull(dto);
        assertNull(dto.getId());
        assertEquals("103", dto.getRemoteId());
        assertEquals("ГОСТ 1", dto.getPrStandMark());
        assertEquals("ДТ 22.02", dto.getDt());
        assertEquals(10, dto.getPrior());
        assertEquals("ТК-185", dto.getTkNum());
        assertEquals("1", dto.getRoute());
        assertEquals("БОРУСАН МАННЕСМАНН БОРУ", dto.getCustomerCodeName());
        assertEquals("2000002631", dto.getPrCustomer());
        assertEquals("(4..*", dto.getPrThickGood().getSrcValue());
        assertEquals("!1", dto.getGrSteelVmz());
        assertEquals("*..1", dto.getRasslOpeningWidth().getSrcValue());
        assertEquals("*..50", dto.getRasslTotalLength().getSrcValue());
        assertEquals("*..2", dto.getPoreDiametr().getSrcValue());
        assertEquals("1..2", dto.getVnutrTr().getSrcValue());
        assertEquals("2..3", dto.getVklObl().getSrcValue());
        assertEquals("3..4", dto.getOsevSeqr().getSrcValue());
        assertEquals("4..5", dto.getVklToch().getSrcValue());
        assertEquals("5..6", dto.getUzkgrTr().getSrcValue());
        assertEquals("6..7", dto.getUglovTr().getSrcValue());
        assertEquals("тест", dto.getPrAnnotation());
    }

    @Test
    void fromSpTypeSampleMacrostructure() throws Exception {
        final var obj = new ObjectMapper()
                .setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"))
                .readValue(getJsonFromPath("src/test/resources/json/SpTypeSampleMacrostructure.json"),
                        SpTypeSampleMacrostructure.class
                );

        final var dictionary = pdmDictionaryCreator.createPdmDictionary(
                obj.getTs(), obj.getOp(), obj.getPk(), obj.getData()
        );

        final var dto = pdmDtoConverter.toTypeSampleMacrostructureDto(dictionary);
        assertNotNull(dto);
        assertNull(dto.getId());
        assertEquals("2", dto.getRemoteId());
        assertEquals("!ТУ 24.10.20", dto.getPrStandMark());
        assertEquals(2, dto.getPrior());
        assertEquals("0.01..*", dto.getUglr().getSrcValue());
        assertEquals("(0.003..*", dto.getSera().getSrcValue());
        assertEquals("СО", dto.getType());
        assertEquals("Серный отпечаток", dto.getPrAnnotation());
    }


    @Test
    void fromSpCodingSlab() throws Exception {
        final var obj = new ObjectMapper()
                .setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"))
                .readValue(getJsonFromPath("src/test/resources/json/SpCodingSlab.json"),
                        SpCodingSlab.class
                );

        final var dictionary = pdmDictionaryCreator.createPdmDictionary(
                obj.getTs(), obj.getOp(), obj.getPk(), obj.getData()
        );

        final var dto = pdmDtoConverter.toCodingSlabDto(dictionary);
        assertNotNull(dto);
        assertNull(dto.getId());
        assertEquals("40", dto.getRemoteId());
        assertEquals("ТУ 24.10.21-0036-05757665-2020", dto.getPrStandMark());
        assertEquals("APM45R;APM60M;TER50D;APM55G;C091AL;APM50M;C75ARW;C331;66427B;NV60TX;APM420", dto.getPrMarkSteel());
        assertEquals(null, dto.getPrior());
        assertEquals("ТЕРНИУМ МХ", dto.getPrCustomer());
        assertEquals("2000001389", dto.getPrCustomerCode());
        assertEquals(StringUtils.EMPTY, dto.getCodeLimitDelivery());
        assertEquals("U11;U21", dto.getCodeBanDelivery());
        assertEquals(StringUtils.EMPTY, dto.getAcceptVolCodLimit());
        assertEquals("ДТ 0042.02", dto.getPrAnnotation());
    }

}
