package com.nlmk.kb.server.service.impl;

import com.nlmk.attestation.product.api.nsi.ChemicalEquivalentStdDto;
import com.nlmk.attestation.product.api.nsi.ChemicalStdLimitDto;
import com.nlmk.attestation.product.api.nsi.LengthTkLimitDto;
import com.nlmk.attestation.product.api.nsi.MatchRpDto;
import com.nlmk.attestation.product.api.nsi.MatchTkDto;
import com.nlmk.attestation.product.api.nsi.MicrostructureDto;
import com.nlmk.attestation.product.api.nsi.PcmDto;
import com.nlmk.attestation.product.api.nsi.PhysMechPropertiesDto;
import com.nlmk.attestation.product.api.nsi.SteelCategoryG4041Dto;
import com.nlmk.attestation.product.api.nsi.ThicknessTkLimitDto;
import com.nlmk.attestation.product.api.nsi.ToleranceDto;
import com.nlmk.attestation.product.api.nsi.WidthTkLimitDto;
import com.nlmk.attestation.product.api.specification.SpecCode;
import com.nlmk.kb.server.entity.pdm.PdmDictionary;
import com.nlmk.kb.server.service.CommonConverter;
import com.nlmk.kb.server.service.PdmMessageConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;

@Slf4j
@Component
@RequiredArgsConstructor
public class PdmMessageConverterImpl implements PdmMessageConverter {

    private final CommonConverter converter;

    @Override
    public ChemicalStdLimitDto toChemicalStdLimitDto(PdmDictionary dictionary) {
        val specs = dictionary.getData().getSpecifications();

        log.debug("--- PDM DICTIONARY: {} ", dictionary);

        val chemicalStdLimitDto = ChemicalStdLimitDto.builder()
                .remote_id(dictionary.getPk().getId())
                // .ts(parseToDate(dictionary.getTs())) //todo заменить как решиться вопрос с датой в топиках на стророне НЛМК
                .prProdMark(converter.getSpecValue(specs, SpecCode.STEEL_MARK.getValue()))
                .prStandMark(converter.getSpecValue(specs, SpecCode.PRODUCT_STANDARD.getValue()))
                .c(converter.stringToLimit(converter.getSpecValue(specs, SpecCode.MASS_FRACTION_C.getValue())))
                .si(converter.stringToLimit(converter.getSpecValue(specs, SpecCode.MASS_FRACTION_SI.getValue())))
                .mn(converter.stringToLimit(converter.getSpecValue(specs, SpecCode.MASS_FRACTION_MN.getValue())))
                .s(converter.stringToLimit(converter.getSpecValue(specs, SpecCode.MASS_FRACTION_S.getValue())))
                .p(converter.stringToLimit(converter.getSpecValue(specs, SpecCode.MASS_FRACTION_P.getValue())))
                .al(converter.stringToLimit(converter.getSpecValue(specs, SpecCode.MASS_FRACTION_AL.getValue())))
                .cr(converter.stringToLimit(converter.getSpecValue(specs, SpecCode.MASS_FRACTION_CR.getValue())))
                .ni(converter.stringToLimit(converter.getSpecValue(specs, SpecCode.MASS_FRACTION_NI.getValue())))
                .cu(converter.stringToLimit(converter.getSpecValue(specs, SpecCode.MASS_FRACTION_CU.getValue())))
                .n(converter.stringToLimit(converter.getSpecValue(specs, SpecCode.MASS_FRACTION_N.getValue())))
                .ti(converter.stringToLimit(converter.getSpecValue(specs, SpecCode.MASS_FRACTION_TI.getValue())))
                .nb(converter.stringToLimit(converter.getSpecValue(specs, SpecCode.MASS_FRACTION_NB.getValue())))
                .v(converter.stringToLimit(converter.getSpecValue(specs, SpecCode.MASS_FRACTION_V.getValue())))
                .b(converter.stringToLimit(converter.getSpecValue(specs, SpecCode.MASS_FRACTION_B.getValue())))
                .mo(converter.stringToLimit(converter.getSpecValue(specs, SpecCode.MASS_FRACTION_MO.getValue())))
                .ca(converter.stringToLimit(converter.getSpecValue(specs, SpecCode.MASS_FRACTION_CA.getValue())))
                .w(converter.stringToLimit(converter.getSpecValue(specs, SpecCode.MASS_FRACTION_W.getValue())))
                .as(converter.stringToLimit(converter.getSpecValue(specs, SpecCode.MASS_FRACTION_AS.getValue())))
                .cP(converter.stringToLimit(converter.getSpecValue(specs, SpecCode.CP.getValue())))
                .sP(converter.stringToLimit(converter.getSpecValue(specs, SpecCode.SP.getValue())))
                .crNiMoCu(converter.stringToLimit(converter.getSpecValue(specs, SpecCode.CrNiCuMo.getValue())))
                .crMo(converter.stringToLimit(converter.getSpecValue(specs, SpecCode.CrMo.getValue())))
                .alTi(converter.stringToLimit(converter.getSpecValue(specs, SpecCode.AlTi.getValue())))
                .alTiVNb(converter.stringToLimit(converter.getSpecValue(specs, SpecCode.AlTiVNb.getValue())))
                .bTiVNb(converter.stringToLimit(converter.getSpecValue(specs, SpecCode.BTiVNb.getValue())))
                .vNbTi(converter.stringToLimit(converter.getSpecValue(specs, SpecCode.TiVNb.getValue())))
                .tiNb(converter.stringToLimit(converter.getSpecValue(specs, SpecCode.TiNb.getValue())))
                .ti34n15s(converter.stringToLimit(converter.getSpecValue(specs, SpecCode.Ti34N15S.getValue())))
                .prAnnotation(converter.getSpecValue(specs, SpecCode.NOTE.getValue()))
                .build();

        val format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SS");
        try {
            chemicalStdLimitDto.setTs(format.parse(dictionary.getTs()));
        } catch (ParseException e) {
            log.error("Ошибка парсинга ts: {}", dictionary.getTs());
            throw new RuntimeException("Ошибка парсинга ts: " + dictionary.getTs() + "; " + e);
        }

        log.debug("--- PDM chemicalStdLimitDto: {} ", chemicalStdLimitDto);

        return chemicalStdLimitDto;
    }

    @Override
    public SteelCategoryG4041Dto toKatSteel4041Dto(PdmDictionary dictionary) {
        val specs = dictionary.getData().getSpecifications();

        val katSteel4041Dto = SteelCategoryG4041Dto.builder()
                .remote_id(dictionary.getPk().getId())
                .ts(converter.parseToDate(dictionary.getTs()))
                .prProdMark(converter.getSpecValue(specs, SpecCode.STEEL_MARK.getValue()))
                .prThickUncoata(converter.stringToLimit(converter.getSpecValue(specs, SpecCode.THICKNESS_OF_ROLLED_PRODUCTS.getValue())))
                .category(converter.getSpecValue(specs, SpecCode.CATEGORY_GOST_4041.getValue()))
                .build();
        return katSteel4041Dto;
    }

    @Override
    public ThicknessTkLimitDto toThicknessTkLimitDto(PdmDictionary dictionary) {
        val specs = dictionary.getData().getSpecifications();

        val thicknessTkLimitDto = ThicknessTkLimitDto.builder()
                .remote_id(dictionary.getPk().getId())
                .ts(converter.parseToDate(dictionary.getTs()))
                .routeShop(converter.getSpecValue(specs, SpecCode.ROUTE_SHOP.getValue()))
                .standSort(converter.getSpecValue(specs, SpecCode.ASSORTMENT_STANDARD.getValue()))
                .prProdMark(converter.getSpecValue(specs, SpecCode.STEEL_MARK.getValue()))
                .prStrengthClass(converter.getSpecValue(specs, SpecCode.STRENGTH_CLASS.getValue()))
                .thickValues(converter.getSpecValue(specs, SpecCode.THICKNESS_OF_ROLLED_PRODUCTS.getValue()))
                .rollingThickAccuracy(converter.getSpecValue(specs, SpecCode.MANUFACTURING_PRECISION_BY_THICKNESS.getValue()))
                .prYield(converter.stringToLimit(converter.getSpecValue(specs, SpecCode.YIELD_POINT.getValue())))
                .prThickGood(converter.stringToLimit(converter.getSpecValue(specs, 586)))
                .prWidthGood(converter.stringToLimit(converter.getSpecValue(specs, SpecCode.WHIDTH_PRODUCT.getValue())))
                .prThickTolMin(converter.getSpecValue(specs, SpecCode.THICKNESS_TOLERANCE_MIN.getValue()))
                .prThickTolMax(converter.getSpecValue(specs, SpecCode.THICKNESS_TOLERANCE_MAX.getValue()))
                .prThickTolMinPerc(converter.getSpecValue(specs, SpecCode.THICKNESS_TOLERANCE_PERCENT_MIN.getValue()))
                .prThickTolMaxPerc(converter.getSpecValue(specs, SpecCode.THICKNESS_TOLERANCE_PERCENT_MAX.getValue()))
                .prAnnotation(converter.getSpecValue(specs, SpecCode.NOTE.getValue()))
                .build();
        return thicknessTkLimitDto;
    }

    @Override
    public WidthTkLimitDto toWidthTkLimitDto(PdmDictionary dictionary) {
        val specs = dictionary.getData().getSpecifications();

        val widthTkLimitDto = WidthTkLimitDto.builder()
                .remote_id(dictionary.getPk().getId())
                .ts(converter.parseToDate(dictionary.getTs()))
                .standSort(converter.getSpecValue(specs, SpecCode.ASSORTMENT_STANDARD.getValue()))
                .prProdMark(converter.getSpecValue(specs, SpecCode.STEEL_MARK.getValue()))
                .prStandSteel(converter.getSpecValue(specs, SpecCode.MARK_STANDARD.getValue()))
                .prFormSap(converter.getSpecValue(specs, SpecCode.FORM_SAP.getValue()))
                .prWidthGood(converter.stringToLimit(converter.getSpecValue(specs, SpecCode.WHIDTH_PRODUCT.getValue())))
                .prThickGood(converter.stringToLimit(converter.getSpecValue(specs, SpecCode.THICKNESS_OF_ROLLED_PRODUCTS.getValue())))
                .prLengthGood(converter.stringToLimit(converter.getSpecValue(specs, SpecCode.LENGTH_PRODUCT.getValue())))
                .prCrop(converter.getSpecValue(specs, SpecCode.EDGE_CHARACTER.getValue()))
                .rollingWidthAccuracy(converter.getSpecValue(specs, SpecCode.MANUFACTURING_PRECISION_BY_WIDTH.getValue()))
                .prWidthTolMin(converter.parsToDouble(
                        converter.getSpecValue(specs, SpecCode.WIDTH_TOLERANCE_MIN.getValue())
                ))
                .prWidthTolMax(converter.parsToDouble(
                        converter.getSpecValue(specs, SpecCode.WIDTH_TOLERANCE_MAX.getValue())
                ))
                .prWidthTolPerc(converter.getSpecValue(specs, SpecCode.WHIDTH_TOLERANCE_PERCENT.getValue()))
                .build();

        val widthTolMinStr = converter.getSpecValue(specs, SpecCode.WIDTH_TOLERANCE_MIN.getValue());

        return widthTkLimitDto;
    }

    @Override
    public LengthTkLimitDto toLengthTkLimitDto(PdmDictionary dictionary) {
        val specs = dictionary.getData().getSpecifications();

        val lengthTkLimitDto = LengthTkLimitDto.builder()
                .remote_id(dictionary.getPk().getId())
                .ts(converter.parseToDate(dictionary.getTs()))
                .standSort(converter.getSpecValue(specs, SpecCode.ASSORTMENT_STANDARD.getValue()))
                .prThickGood(converter.stringToLimit(converter.getSpecValue(specs, SpecCode.THICKNESS_OF_ROLLED_PRODUCTS.getValue())))
                .prLengthGood(converter.stringToLimit(converter.getSpecValue(specs, SpecCode.LENGTH_PRODUCT.getValue())))
                .prLengthTolMax(converter.parsToDouble(
                        converter.getSpecValue(specs, SpecCode.LENGTH_TOLERANCE_MAX.getValue())
                ))
                .rollingLengthAccuracy(converter.getSpecValue(specs, SpecCode.MANUFACTURING_PRECISION_BY_LENGTH.getValue()))
                .prLengthTolMaxPerc(converter.getSpecValue(specs, SpecCode.LENGTH_TOLERANCE_PERCENT.getValue()))
                .koefLengthTolMax(converter.parsToDouble(
                        converter.getSpecValue(specs, SpecCode.LENGTH_K.getValue())
                ))
                .prAnnotation(converter.getSpecValue(specs, SpecCode.NOTE.getValue()))
                .build();

        return lengthTkLimitDto;
    }

    @Override
    public PhysMechPropertiesDto toPhysMechPropertiesDto(PdmDictionary dictionary) {
        val specs = dictionary.getData().getSpecifications();

        val physMechPropertiesDto = PhysMechPropertiesDto.builder()
                .remote_id(dictionary.getPk().getId())
                .ts(converter.parseToDate(dictionary.getTs()))
                .pr_category(converter.getSpecValue(specs, SpecCode.CATEGORY_OF_MARK.getValue()))
                .pr_prod_mark(converter.getSpecValue(specs, SpecCode.STEEL_MARK.getValue()))
                .pr_stand_mark(converter.getSpecValue(specs, SpecCode.PRODUCT_STANDARD.getValue()))
                .pr_thick_uncoat(converter.stringToLimit(converter.getSpecValue(specs, SpecCode.THICKNESS_OF_ROLLED_PRODUCTS.getValue())))
                .pr_drow(converter.getSpecValue(specs, SpecCode.DROW.getValue()))
                .pr_scope_group(converter.getSpecValue(specs, SpecCode.SCOPE_GROUP.getValue()))
                .pr_impact_energy(converter.stringToLimit(converter.getSpecValue(specs, SpecCode.IMPACT_ENERGY.getValue())))
                .pr_kv_60(converter.stringToLimit(converter.getSpecValue(specs, SpecCode.IMPACT_ENERGY_KV60.getValue())))
                .pr_kv_40(converter.stringToLimit(converter.getSpecValue(specs, SpecCode.IMPACT_ENERGY_KV40.getValue())))
                .pr_kv_20(converter.stringToLimit(converter.getSpecValue(specs, SpecCode.IMPACT_ENERGY_KV20.getValue())))
                .pr_kv_0(converter.stringToLimit(converter.getSpecValue(specs, SpecCode.IMPACT_ENERGY_KV0.getValue())))
                .pr_kv20(converter.stringToLimit(converter.getSpecValue(specs, SpecCode.IMPACT_ENERGY_KV_PLUS_20.getValue())))
                .pr_tensile_elongation4(converter.stringToLimit(converter.getSpecValue(specs, SpecCode.TENSILE_ELONGATION_4.getValue())))
                .pr_tensile_elongation5(converter.stringToLimit(converter.getSpecValue(specs, SpecCode.TENSILE_ELONGATION_5.getValue())))
                .pr_tensile_elongation10(converter.stringToLimit(converter.getSpecValue(specs, SpecCode.TENSILE_ELONGATION_10.getValue())))
                .pr_tensile_elongation50(converter.stringToLimit(converter.getSpecValue(specs, SpecCode.TENSILE_ELONGATION_50.getValue())))
                .pr_tensile_elongation80(converter.stringToLimit(converter.getSpecValue(specs, SpecCode.TENSILE_ELONGATION_80.getValue())))
                .pr_tensile_elongation200(converter.stringToLimit(converter.getSpecValue(specs, SpecCode.TENSILE_ELONGATION_200.getValue())))
                .pr_tensile_strength(converter.stringToLimit(converter.getSpecValue(specs, SpecCode.TENSILE_STRENGTH.getValue())))
                .pr_strength_class(converter.getSpecValue(specs, SpecCode.STRENGTH_CLASS.getValue()))
                .pr_yield(converter.stringToLimit(converter.getSpecValue(specs, SpecCode.YIELD.getValue())))
                .pr_yield02(converter.stringToLimit(converter.getSpecValue(specs, SpecCode.YIELD_02.getValue())))
                .pr_relations2d(converter.getSpecValue(specs, SpecCode.RELATIONS_2D.getValue()))
                .pr_180bend_diam(converter.getSpecValue(specs, SpecCode.BEND_DIAM_180.getValue()))
                .pr_180bend_radius(converter.getSpecValue(specs, SpecCode.BEND_RADIUS_180.getValue()))
                .pr_90bend_diam(converter.getSpecValue(specs, SpecCode.BEND_DIAM_90.getValue()))
                .pr_r90_anisotropy(converter.getSpecValue(specs, SpecCode.ANISOTROPY_R90.getValue()))
                .r0(converter.getSpecValue(specs, SpecCode.R0.getValue()))
                .rm_rbar(converter.getSpecValue(specs, SpecCode.RM_BAR.getValue()))
                .pr_n90_strength(converter.getSpecValue(specs, SpecCode.STRENGTH_N90.getValue()))
                .n0(converter.getSpecValue(specs, SpecCode.N0.getValue()))
                .pr_hardness_hr15t(converter.stringToLimit(converter.getSpecValue(specs, SpecCode.HARDNESS_HR15T.getValue())))
                .pr_hardness_hr30t(converter.stringToLimit(converter.getSpecValue(specs, SpecCode.HARDNESS_HR30T.getValue())))
                .pr_hardness_hrb(converter.stringToLimit(converter.getSpecValue(specs, SpecCode.HARDNESS_HRB.getValue())))
                .pr_hardness_hb(converter.stringToLimit(converter.getSpecValue(specs, SpecCode.HARDNESS_HB.getValue())))
                .pr_hardness_hrf(converter.stringToLimit(converter.getSpecValue(specs, SpecCode.HARDNESS_HRF.getValue())))
                .pr_hardness_hv5(converter.stringToLimit(converter.getSpecValue(specs, SpecCode.HARDNESS_HV5.getValue())))
                .pr_asperity(converter.getSpecValue(specs, SpecCode.ASPERITY.getValue()))
                .pr_peak_number(converter.getSpecValue(specs, SpecCode.PEAK_NUMBER.getValue()))
                .pr_bh2_effect(converter.getSpecValue(specs, SpecCode.BH2_EFFECT.getValue()))
                .pr_kcu_0(converter.stringToLimit(converter.getSpecValue(specs, SpecCode.KCU0.getValue())))
                .pr_kcv_0(converter.stringToLimit(converter.getSpecValue(specs, SpecCode.KCV0.getValue())))
                .pr_kcv_10(converter.stringToLimit(converter.getSpecValue(specs, SpecCode.KCV10.getValue())))
                .pr_kcv10(converter.stringToLimit(converter.getSpecValue(specs, SpecCode.KCV_PLUS_10.getValue())))
                .pr_kcv_15(converter.stringToLimit(converter.getSpecValue(specs, SpecCode.KCV15.getValue())))
                .pr_kcu_20(converter.stringToLimit(converter.getSpecValue(specs, SpecCode.KCU20.getValue())))
                .pr_kcu20(converter.stringToLimit(converter.getSpecValue(specs, SpecCode.KCU_PLUS_20.getValue())))
                .pr_kcv_20(converter.stringToLimit(converter.getSpecValue(specs, SpecCode.KCV20.getValue())))
                .pr_kcv20(converter.stringToLimit(converter.getSpecValue(specs, SpecCode.KCV_PLUS_20.getValue())))
                .pr_kcu_30(converter.stringToLimit(converter.getSpecValue(specs, SpecCode.KCU30.getValue())))
                .pr_kcv_35(converter.stringToLimit(converter.getSpecValue(specs, SpecCode.KCV35.getValue())))
                .pr_kcu_40(converter.stringToLimit(converter.getSpecValue(specs, SpecCode.KCU40.getValue())))
                .pr_kcv_40(converter.stringToLimit(converter.getSpecValue(specs, SpecCode.KCV40.getValue())))
                .pr_kcu_50(converter.stringToLimit(converter.getSpecValue(specs, SpecCode.KCU50.getValue())))
                .pr_kcu_60(converter.stringToLimit(converter.getSpecValue(specs, SpecCode.KCU60.getValue())))
                .pr_kcu_70(converter.stringToLimit(converter.getSpecValue(specs, SpecCode.KCU70.getValue())))
                .pr_kcu_mech_old(converter.stringToLimit(converter.getSpecValue(specs, SpecCode.IMPACT_STRENGTH_AGING.getValue())))
                .pr_height(converter.getSpecValue(specs, SpecCode.DEPTH_HOLE.getValue()))
                .pr_coil_tilt(converter.getSpecValue(specs, SpecCode.ROLL_CURVATURE.getValue()))
                .pr_p1_50(converter.getSpecValue(specs, SpecCode.P150.getValue()))
                .pr_p1_7_50(converter.getSpecValue(specs, SpecCode.P1750.getValue()))
                .pr_p1_5_50(converter.getSpecValue(specs, SpecCode.P1550.getValue()))
                .pr_p1_0_50(converter.getSpecValue(specs, SpecCode.P1050.getValue()))
                .pr_p1_5_200(converter.getSpecValue(specs, SpecCode.P15200.getValue()))
                .pr_p1_5_400(converter.getSpecValue(specs, SpecCode.P15400.getValue()))
                .pr_p1_0_400(converter.getSpecValue(specs, SpecCode.P10400.getValue()))
                .pr_p1_0_700(converter.getSpecValue(specs, SpecCode.P10700.getValue()))
                .pr_p1_0_1000(converter.getSpecValue(specs, SpecCode.P101000.getValue()))
                .pr_p004_500(converter.getSpecValue(specs, SpecCode.P004500.getValue()))
                .pr_p01_500(converter.getSpecValue(specs, SpecCode.P01500.getValue()))
                .pr_p004_1000(converter.getSpecValue(specs, SpecCode.P0041000.getValue()))
                .pr_p01_1000(converter.getSpecValue(specs, SpecCode.P011000.getValue()))
                .pr_p1_5_50_sst(converter.getSpecValue(specs, SpecCode.P1550SST.getValue()))
                .pr_p1_7_50_sst(converter.getSpecValue(specs, SpecCode.P1750SST.getValue()))
                .pr_p1_5_60(converter.getSpecValue(specs, SpecCode.P1560.getValue()))
                .pr_p1_7_60(converter.getSpecValue(specs, SpecCode.P1760.getValue()))
                .pr_p1_5_60_sst(converter.getSpecValue(specs, SpecCode.P1560SST.getValue()))
                .pr_p1_7_60_sst(converter.getSpecValue(specs, SpecCode.P1760SST.getValue()))
                .pr_p1_5_50_lb(converter.getSpecValue(specs, SpecCode.P1550LB.getValue()))
                .pr_p1_7_50_lb(converter.getSpecValue(specs, SpecCode.P1750LB.getValue()))
                .pr_p1_7_60_lb(converter.getSpecValue(specs, SpecCode.P1760LB.getValue()))
                .pr_p1_5_50_sst_lb(converter.getSpecValue(specs, SpecCode.P1550SST_LB.getValue()))
                .pr_p1_7_50_sst_lb(converter.getSpecValue(specs, SpecCode.P1750SST_LB.getValue()))
                .pr_p1_7_60_sst_lb(converter.getSpecValue(specs, SpecCode.P1760SST_LB.getValue()))
                .pr_p1_5_60_lb(converter.getSpecValue(specs, SpecCode.P1560LB.getValue()))
                .pr_p1_5_60_sst_lb(converter.getSpecValue(specs, SpecCode.P1560SST_LB.getValue()))
                .pr_h_004_500(converter.getSpecValue(specs, SpecCode.H004500.getValue()))
                .pr_h_01_500(converter.getSpecValue(specs, SpecCode.H01500.getValue()))
                .pr_h_004_1000(converter.getSpecValue(specs, SpecCode.H0041000.getValue()))
                .pr_h_01_1000(converter.getSpecValue(specs, SpecCode.H011000.getValue()))
                .pr_b_40(converter.getSpecValue(specs, SpecCode.B40.getValue()))
                .pr_b_80(converter.getSpecValue(specs, SpecCode.B80.getValue()))
                .pr_b_100(converter.getSpecValue(specs, SpecCode.B100.getValue()))
                .pr_b_200(converter.getSpecValue(specs, SpecCode.B200.getValue()))
                .pr_b_800(converter.getSpecValue(specs, SpecCode.B800.getValue()))
                .pr_b_1000(converter.getSpecValue(specs, SpecCode.B1000.getValue()))
                .pr_b_2500(converter.getSpecValue(specs, SpecCode.B2500.getValue()))
                .pr_b_5000(converter.getSpecValue(specs, SpecCode.B5000.getValue()))
                .pr_b_10000(converter.getSpecValue(specs, SpecCode.B10000.getValue()))
                .pr_b_100_sst(converter.getSpecValue(specs, SpecCode.B100SST.getValue()))
                .pr_b_800_sst(converter.getSpecValue(specs, SpecCode.B800SST.getValue()))
                .pr_b_2500_sst(converter.getSpecValue(specs, SpecCode.B2500SST.getValue()))
                .pr_coercive_field(converter.getSpecValue(specs, SpecCode.COERCIVE_FIELD.getValue()))
                .factor_lamination(converter.getSpecValue(specs, SpecCode.FACTOR_LAMINATION.getValue()))
                .macro_mann(converter.getSpecValue(specs, SpecCode.MACRO_MANN.getValue()))
                .vnutr_tr(converter.getSpecValue(specs, SpecCode.INTERNAL_CRACKS.getValue()))
                .vkl_obl(converter.getSpecValue(specs, SpecCode.CLOUD_INCLUSIONS.getValue()))
                .osev_segr(converter.getSpecValue(specs, SpecCode.AXIAL_SEGREGATION.getValue()))
                .vkl_toch(converter.getSpecValue(specs, SpecCode.POINT_INCLUSIONS.getValue()))
                .uzkgr_tr(converter.getSpecValue(specs, SpecCode.EDGE_CRACKS.getValue()))
                .uglov_tr(converter.getSpecValue(specs, SpecCode.ANGLE_CRACKS.getValue()))
                .pr_bake_hardening(converter.getSpecValue(specs, SpecCode.BAKE_HARDENING.getValue()))
                .pr_annotation(converter.getSpecValue(specs, SpecCode.NOTE.getValue()))
                .build();

        return physMechPropertiesDto;
    }

    @Override
    public MicrostructureDto toMicrostructureDto(PdmDictionary dictionary) {
        val specs = dictionary.getData().getSpecifications();

        val microstructureDto = MicrostructureDto.builder()
                .remote_id(dictionary.getPk().getId())
                .ts(converter.parseToDate(dictionary.getTs()))
                .tkNum(converter.getSpecValue(specs, SpecCode.TK_NUMBER_OR_VTK_VERSION_ROUTE.getValue()))
                .tkRoute(converter.getSpecValue(specs, SpecCode.ROUTE_TK.getValue()))
                .prProdMark(converter.getSpecValue(specs, SpecCode.STEEL_MARK.getValue()))
                .prStandMark(converter.getSpecValue(specs, SpecCode.PRODUCT_STANDARD.getValue()))
                .prThickUncoat(converter.stringToLimit(converter.getSpecValue(specs, SpecCode.THICKNESS_OF_ROLLED_PRODUCTS.getValue())))
                .category(converter.getSpecValue(specs, SpecCode.CATEGORY_GOST_4041.getValue()))
                .attestStand(converter.getSpecValue(specs, SpecCode.MICROCTRUCTURE_STANDART.getValue()))
                .ferriteGrain(converter.stringToLimit(converter.getSpecValue(specs, SpecCode.FERRIT_GRAIN.getValue())))
                .unevenessFerriteGrain(converter.getSpecValue(specs, SpecCode.UNEVENNESS_OF_FERRIT_GRAIN.getValue()))
                .structFreeCementite(converter.stringToLimit(converter.getSpecValue(specs, SpecCode.CEMENTITE.getValue())))
                .unmetallInclusionsOxides(converter.stringToLimit(converter.getSpecValue(specs, SpecCode.OXIDES.getValue())))
                .unmetallInclusionsSulfides(converter.stringToLimit(converter.getSpecValue(specs, SpecCode.SULPHIDES.getValue())))
                .unmetallInclusionsNitrides(converter.stringToLimit(converter.getSpecValue(specs, SpecCode.NITRIDES.getValue())))
                .unmetallInclusionsSilicates(converter.stringToLimit(converter.getSpecValue(specs, SpecCode.SILICATES.getValue())))
                .unmetallInclusionsOxidesB(converter.stringToLimit(converter.getSpecValue(specs, SpecCode.OXIDES_TYPE_B.getValue())))
                .unmetallInclusionsSulfidesA(converter.stringToLimit(converter.getSpecValue(specs, SpecCode.SULPHIDES_TYPE_A.getValue())))
                .unmetallInclusionsSilicatesC(converter.stringToLimit(converter.getSpecValue(specs, SpecCode.SILICATES_TYPE_C.getValue())))
                .unmetallInclusionsGlobOxidesD(converter.stringToLimit(converter.getSpecValue(specs, SpecCode.OXIDES_TYPE_D.getValue())))
                .unmetallInclusions(converter.stringToLimit(converter.getSpecValue(specs, SpecCode.NON_METALLIC_INCLUSIONS_ISO_4967_2013.getValue())))
                .polFerPerStruct(converter.stringToLimit(converter.getSpecValue(specs, SpecCode.BANDING_OF_FERRIE_PEARLITE_STRUCTURE.getValue())))
                .depthDecarbLayer(converter.stringToLimit(converter.getSpecValue(specs, SpecCode.DEPTH_WITHOUT_C_LAYER.getValue())))
                .perliteGrain(converter.stringToLimit(converter.getSpecValue(specs, SpecCode.PERLITE_GRAIN.getValue())))
                .prAnnotation(converter.getSpecValue(specs, SpecCode.NOTE.getValue()))
                .build();
        return microstructureDto;
    }

    @Override
    public ChemicalEquivalentStdDto toChemicalEquivalentStdDto(PdmDictionary dictionary) {
        val specs = dictionary.getData().getSpecifications();

        log.debug("--- toChemicalEquivalentStdDto PDM DICTIONARY: {} ", dictionary);

        ChemicalEquivalentStdDto chemicalEquivalentStdDto = ChemicalEquivalentStdDto.builder()
                .remote_id(dictionary.getPk().getId())
                .ts(converter.parseToDate(dictionary.getTs()))
                .prProdMark(converter.getSpecValue(specs, SpecCode.STEEL_MARK.getValue()))
                .prStandMark(converter.getSpecValue(specs, SpecCode.PRODUCT_STANDARD.getValue()))
                .prThickUncoat(converter.stringToLimit(
                        converter.getSpecValue(specs, SpecCode.THICKNESS_OF_ROLLED_PRODUCTS.getValue()))
                )
                .prStrengthClass(converter.getSpecValue(specs, SpecCode.STRENGTH_CLASS.getValue()))
                .ceqNum(converter.getSpecValue(specs, SpecCode.CARBON_EQUIVALENT_FORMULA_NUMBER.getValue()))
                .ceq(converter.stringToLimit(
                        converter.getSpecValue(specs, SpecCode.CARBON_EQUIVALENT.getValue()))
                )
                .pcmNum(converter.getSpecValue(specs, SpecCode.CRACK_RESISTANCE_COEFFICIENT_FORMULA_NUMBER.getValue()))
                .pcm(converter.stringToLimit(
                        converter.getSpecValue(specs, SpecCode.CRACK_RESISTANCE_COEFFICIENT.getValue()))
                )
                .prAnnotation(converter.getSpecValue(specs, SpecCode.NOTE.getValue()))
                .build();
        log.debug("--- PDM chemicalEquivalentStdDto: {} ", chemicalEquivalentStdDto);

        return chemicalEquivalentStdDto;
    }

    @Override
    public MatchTkDto toMatchTkDto(PdmDictionary dictionary) {
        val specs = dictionary.getData().getSpecifications();
        log.debug("--- toMatchTkDto PDM DICTIONARY: {} ", dictionary);

        MatchTkDto matchTkDto = MatchTkDto.builder()
                .remote_id(dictionary.getPk().getId())
                //.ts()// todo после решения вопроса по передачи даты и времени заменить
                .tkNum(converter.getSpecValue(specs, SpecCode.TK_NUMBER_OR_VTK_VERSION_ROUTE.getValue()))
                .tkNumSap(converter.getSpecValue(specs, SpecCode.TK_SAP_NUMBER.getValue()))
                .prAnnotation(converter.getSpecValue(specs, SpecCode.NOTE.getValue()))
                .build();

        val format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SS");
        try {
            matchTkDto.setTs(format.parse(dictionary.getTs()));
        } catch (ParseException e) {
            log.error("Ошибка парсинга ts: {}", dictionary.getTs());
            throw new RuntimeException("Ошибка парсинга ts: " + dictionary.getTs() + "; " + e);
        }

        log.debug("--- PDM MatchTkDto: {} ", matchTkDto);

        return matchTkDto;
    }

    @Override
    public MatchRpDto toMatchRpDto(PdmDictionary dictionary) {
        val specs = dictionary.getData().getSpecifications();
        log.debug("--- toMatchRpDto PDM DICTIONARY: {} ", dictionary);

        MatchRpDto matchTkDto = MatchRpDto.builder()
                .remote_id(dictionary.getPk().getId())
                // .ts() todo после решения вопроса по передачи даты и времени заменить
                .rpNumSap(converter.getSpecValue(specs, SpecCode.RP_SAP_NUMBER.getValue()))
                .tkNum(converter.getSpecValue(specs, SpecCode.RP_NUMBER_VERSION_ROUTE.getValue()))
                .build();

        val format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SS");
        try {
            matchTkDto.setTs(format.parse(dictionary.getTs()));
        } catch (ParseException e) {
            log.error("Ошибка парсинга ts: {}", dictionary.getTs());
            throw new RuntimeException("Ошибка парсинга ts: " + dictionary.getTs() + "; " + e);
        }

        log.debug("--- PDM MatchRpDto: {} ", matchTkDto);
        return matchTkDto;
    }

    @Override
    public PcmDto toPcmDto(PdmDictionary dictionary) {
        val specs = dictionary.getData().getSpecifications();
        log.debug("--- toPcmDto PDM DICTIONARY: {} ", dictionary);

        val pcmDto = PcmDto.builder()
                .remote_id(dictionary.getPk().getId())
                .ts(converter.parseToDate(dictionary.getTs()))
                .pcmNum(converter.getSpecValue(specs, SpecCode.CRACK_RESISTANCE_COEFFICIENT_FORMULA_NUMBER.getValue()))
                .pcmFormula(converter.getSpecValue(specs, SpecCode.CRACK_RESISTANCE_FORMULA.getValue()))
                .prAnnotation(converter.getSpecValue(specs, SpecCode.NOTE.getValue()))
                .build();

        log.debug("--- toPcmDto PcmDto: {} ", pcmDto);
        return pcmDto;
    }

    @Override
    public ToleranceDto toToleranceDto(PdmDictionary dictionary) {
        val specs = dictionary.getData().getSpecifications();
        log.debug("--- toleranceDto PDM DICTIONARY: {} ", dictionary);

        val toleranceDto = ToleranceDto.builder()
                .remote_id(dictionary.getPk().getId())
                .ts(converter.parseToDate(dictionary.getTs()))
                .prStandMark(converter.getSpecValue(specs, SpecCode.PRODUCT_STANDARD.getValue()))
                .useStandMark(converter.getSpecValue(specs, SpecCode.PRODUCT_STANDARD_ADDITIONAL.getValue()))
                .standTolThick(converter.getSpecValue(specs, SpecCode.THICKNESS_TOLERANCE_STANDART.getValue()))
                .standTolWidth(converter.getSpecValue(specs, SpecCode.WIDTH_TOLERANCE_STANDART.getValue()))
                .standTolLength(converter.getSpecValue(specs, SpecCode.LENGTH_TOLERANCE_STANDART.getValue()))
                .standTolEvenness(converter.getSpecValue(specs, SpecCode.EVENNESS_TOLERANCE_STANDART.getValue()))
                .prAnnotation(converter.getSpecValue(specs, SpecCode.NOTE.getValue()))
                .build();

        log.debug("--- toPcmDto PcmDto: {} ", toleranceDto);
        return toleranceDto;
    }
}
