package com.nlmk.kb.server.service.pdm;

import com.nlmk.attestation.product.api.nsi.*;
import com.nlmk.attestation.product.api.specification.SpecCode;
import com.nlmk.kb.server.entity.pdm.PdmDictionary;
import com.nlmk.kb.server.entity.pdm.Spec;
import com.nlmk.kb.server.service.CommonConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.List;

import static com.nlmk.attestation.product.api.specification.SpecCode.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class PdmDtoConverterImpl implements PdmDtoConverter {

    private static final String DICT_NOT_NULL = "dictionary не должен быть null.";
    private static final String DICT_DATA_NOT_NULL = "dictionary.getData() не должен быть null.";

    private final CommonConverter converter;

    @Override
    public ChemicalStdLimitDto toChemicalStdLimitDto(PdmDictionary dictionary) {
        Assert.notNull(dictionary, DICT_NOT_NULL);
        Assert.notNull(dictionary.getData(), DICT_DATA_NOT_NULL);

        final var specs = dictionary.getData().getSpecifications();

        log.debug("toChemicalStdLimitDto, PDM DICTIONARY: {} ", dictionary);

        final var chemicalStdLimitDto = ChemicalStdLimitDto.builder()
                .remote_id(dictionary.getPk().getId())
                .ts(dictionary.getTs())
                .prProdMark(converter.getStringSpecValue(specs, STEEL_MARK))
                .prStandMark(converter.getStringSpecValue(specs, PRODUCT_STANDARD))
                .c(converter.getLimitSpecValue(specs, MASS_FRACTION_C))
                .si(converter.getLimitSpecValue(specs, MASS_FRACTION_SI))
                .mn(converter.getLimitSpecValue(specs, MASS_FRACTION_MN))
                .s(converter.getLimitSpecValue(specs, MASS_FRACTION_S))
                .p(converter.getLimitSpecValue(specs, MASS_FRACTION_P))
                .al(converter.getLimitSpecValue(specs, MASS_FRACTION_AL))
                .cr(converter.getLimitSpecValue(specs, MASS_FRACTION_CR))
                .ni(converter.getLimitSpecValue(specs, MASS_FRACTION_NI))
                .cu(converter.getLimitSpecValue(specs, MASS_FRACTION_CU))
                .n(converter.getLimitSpecValue(specs, MASS_FRACTION_N))
                .ti(converter.getLimitSpecValue(specs, MASS_FRACTION_TI))
                .nb(converter.getLimitSpecValue(specs, MASS_FRACTION_NB))
                .sn(converter.getLimitSpecValue(specs, REQUIRED_CONTENT_SN_MAX))
                .v(converter.getLimitSpecValue(specs, MASS_FRACTION_V))
                .b(converter.getLimitSpecValue(specs, MASS_FRACTION_B))
                .mo(converter.getLimitSpecValue(specs, MASS_FRACTION_MO))
                .ca(converter.getLimitSpecValue(specs, MASS_FRACTION_CA))
                .w(converter.getLimitSpecValue(specs, MASS_FRACTION_W))
                .as(converter.getLimitSpecValue(specs, MASS_FRACTION_AS))
                .cP(converter.getLimitSpecValue(specs, CP))
                .sP(converter.getLimitSpecValue(specs, SP))
                .crNiMoCu(converter.getLimitSpecValue(specs, CR_NI_CU_MO))
                .crMo(converter.getLimitSpecValue(specs, CR_MO))
                .alTi(converter.getLimitSpecValue(specs, AL_TI))
                .alTiVNb(converter.getLimitSpecValue(specs, AL_TI_V_NB))
                .bTiVNb(converter.getLimitSpecValue(specs, B_TI_V_NB))
                .vNbTi(converter.getLimitSpecValue(specs, TI_V_NB))
                .tiNb(converter.getLimitSpecValue(specs, TI_NB))
                .ti34n15s(converter.getLimitSpecValue(specs, TI_34N_15S))
                .prAnnotation(converter.getStringSpecValue(specs, NOTE))
                .build();

        log.debug("--- PDM chemicalStdLimitDto: {} ", chemicalStdLimitDto);

        return chemicalStdLimitDto;
    }

    @Override
    public SteelCategoryG4041Dto toKatSteel4041Dto(PdmDictionary dictionary) {
        Assert.notNull(dictionary, DICT_NOT_NULL);
        Assert.notNull(dictionary.getData(), DICT_DATA_NOT_NULL);

        final var specs = dictionary.getData().getSpecifications();

        return SteelCategoryG4041Dto.builder()
                .remote_id(dictionary.getPk().getId())
                .ts(dictionary.getTs())
                .prProdMark(converter.getStringSpecValue(specs, STEEL_MARK))
                .prThickUncoata(converter.getLimitSpecValue(specs, THICKNESS_OF_ROLLED_PRODUCTS))
                .category(converter.getStringSpecValue(specs, CATEGORY_GOST_4041))
                .build();
    }

    @Override
    public ThicknessTkLimitDto toThicknessTkLimitDto(PdmDictionary dictionary) {
        Assert.notNull(dictionary, DICT_NOT_NULL);
        Assert.notNull(dictionary.getData(), DICT_DATA_NOT_NULL);

        final var specs = dictionary.getData().getSpecifications();

        return ThicknessTkLimitDto.builder()
                .remote_id(dictionary.getPk().getId())
                .ts(dictionary.getTs())
                .routeShop(converter.getStringSpecValue(specs, ROUTE_SHOP))
                .standSort(converter.getStringSpecValue(specs, ASSORTMENT_STANDARD))
                .prProdMark(converter.getStringSpecValue(specs, STEEL_MARK))
                .prStrengthClass(converter.getStringSpecValue(specs, STRENGTH_CLASS))
                .thickValues(converter.getStringSpecValue(specs, THICKNESS_OF_ROLLED_PRODUCTS))
                .rollingThickAccuracy(converter.getStringSpecValue(specs, MANUFACTURING_PRECISION_BY_THICKNESS))
                .prYield(converter.getLimitSpecValue(specs, YIELD_POINT))
                .prThickGood(converter.getLimitSpecValue(specs, THICKNESS_PRODUCTS))
                .prWidthGood(converter.getLimitSpecValue(specs, WHIDTH_PRODUCT))
                .prThickTolMin(converter.getStringSpecValue(specs, THICKNESS_TOLERANCE_MIN))
                .prThickTolMax(converter.getStringSpecValue(specs, THICKNESS_TOLERANCE_MAX))
                .prThickTolMinPerc(converter.getStringSpecValue(specs, THICKNESS_TOLERANCE_PERCENT_MIN))
                .prThickTolMaxPerc(converter.getStringSpecValue(specs, THICKNESS_TOLERANCE_PERCENT_MAX))
                .prUnevenGauge(converter.getStringSpecValue(specs, UNEVEN_GAUGE))
                .longThickDif(converter.getStringSpecValue(specs, LONG_THICK_DIFF))
                .prAnnotation(converter.getStringSpecValue(specs, NOTE))
                .build();
    }

    @Override
    public WidthTkLimitDto toWidthTkLimitDto(PdmDictionary dictionary) {
        Assert.notNull(dictionary, DICT_NOT_NULL);
        Assert.notNull(dictionary.getData(), DICT_DATA_NOT_NULL);

        final var specs = dictionary.getData().getSpecifications();

        return WidthTkLimitDto.builder()
                .remote_id(dictionary.getPk().getId())
                .ts(dictionary.getTs())
                .standSort(converter.getStringSpecValue(specs, ASSORTMENT_STANDARD))
                .prProdMark(converter.getStringSpecValue(specs, STEEL_MARK))
                .prStandSteel(converter.getStringSpecValue(specs, MARK_STANDARD))
                .prFormSap(converter.getStringSpecValue(specs, FORM_SAP))
                .prWidthGood(converter.getLimitSpecValue(specs, WHIDTH_PRODUCT))
                .prThickGood(converter.getLimitSpecValue(specs, THICKNESS_OF_ROLLED_PRODUCTS))
                .prLengthGood(converter.getLimitSpecValue(specs, LENGTH_PRODUCT))
                .prCrop(converter.getStringSpecValue(specs, EDGE_CHARACTER))
                .rollingWidthAccuracy(converter.getStringSpecValue(specs, MANUFACTURING_PRECISION_BY_WIDTH))
                .prWidthTolMin(converter.parseToDouble(
                        converter.getStringSpecValue(specs, WIDTH_TOLERANCE_MIN)
                ))
                .prWidthTolMax(converter.parseToDouble(
                        converter.getStringSpecValue(specs, WIDTH_TOLERANCE_MAX)
                ))
                .prWidthTolPerc(converter.getStringSpecValue(specs, WHIDTH_TOLERANCE_PERCENT))
                .crescent(converter.getLimitSpecValue(specs, SICKLE_SHAPE))
                .burr(converter.getLimitSpecValue(specs, ZAUSENEC))
                .build();
    }

    @Override
    public LengthTkLimitDto toLengthTkLimitDto(PdmDictionary dictionary) {
        Assert.notNull(dictionary, DICT_NOT_NULL);
        Assert.notNull(dictionary.getData(), DICT_DATA_NOT_NULL);

        final var specs = dictionary.getData().getSpecifications();

        return LengthTkLimitDto.builder()
                .remote_id(dictionary.getPk().getId())
                .ts(dictionary.getTs())
                .standSort(converter.getStringSpecValue(specs, ASSORTMENT_STANDARD))
                .prThickGood(converter.getLimitSpecValue(specs, THICKNESS_OF_ROLLED_PRODUCTS))
                .prLengthGood(converter.getLimitSpecValue(specs, LENGTH_PRODUCT))
                .prLengthTolMax(converter.parseToDouble(
                        converter.getStringSpecValue(specs, LENGTH_TOLERANCE_MAX)
                ))
                .rollingLengthAccuracy(converter.getStringSpecValue(specs, MANUFACTURING_PRECISION_BY_LENGTH))
                .prLengthTolMaxPerc(converter.getStringSpecValue(specs, LENGTH_TOLERANCE_PERCENT))
                .koefLengthTolMax(converter.parseToDouble(
                        converter.getStringSpecValue(specs, LENGTH_K)
                ))
                .prAnnotation(converter.getStringSpecValue(specs, NOTE))
                .build();
    }

    @Override
    public PhysMechPropertiesDto toPhysMechPropertiesDto(PdmDictionary dictionary) {
        Assert.notNull(dictionary, DICT_NOT_NULL);
        Assert.notNull(dictionary.getData(), DICT_DATA_NOT_NULL);

        final var specs = dictionary.getData().getSpecifications();

        return PhysMechPropertiesDto.builder()
                .remote_id(dictionary.getPk().getId())
                .ts(dictionary.getTs())
                .pr_category(converter.getStringSpecValue(specs, CATEGORY_OF_MARK))
                .pr_prod_mark(converter.getStringSpecValue(specs, STEEL_MARK))
                .pr_stand_mark(converter.getStringSpecValue(specs, PRODUCT_STANDARD))
                .pr_thick_uncoat(converter.getLimitSpecValue(specs, THICKNESS_OF_ROLLED_PRODUCTS))
                .pr_drow(converter.getStringSpecValue(specs, DROW))
                .pr_scope_group(converter.getStringSpecValue(specs, SCOPE_GROUP))
                .pr_impact_energy(converter.getLimitSpecValue(specs, IMPACT_ENERGY))
                .pr_kv_60(converter.getLimitSpecValue(specs, IMPACT_ENERGY_KV60))
                .pr_kv_40(converter.getLimitSpecValue(specs, IMPACT_ENERGY_KV40))
                .pr_kv_20(converter.getLimitSpecValue(specs, IMPACT_ENERGY_KV20))
                .pr_kv_0(converter.getLimitSpecValue(specs, IMPACT_ENERGY_KV0))
                .pr_kv20(converter.getLimitSpecValue(specs, IMPACT_ENERGY_KV_PLUS_20))
                .pr_tensile_elongation4(converter.getLimitSpecValue(specs, TENSILE_ELONGATION_4))
                .pr_tensile_elongation5(converter.getLimitSpecValue(specs, TENSILE_ELONGATION_5))
                .pr_tensile_elongation10(converter.getLimitSpecValue(specs, TENSILE_ELONGATION_10))
                .pr_tensile_elongation50(converter.getLimitSpecValue(specs, TENSILE_ELONGATION_50))
                .pr_tensile_elongation80(converter.getLimitSpecValue(specs, TENSILE_ELONGATION_80))
                .pr_tensile_elongation200(converter.getLimitSpecValue(specs, TENSILE_ELONGATION_200))
                .pr_tensile_strength(converter.getLimitSpecValue(specs, TENSILE_STRENGTH))
                .pr_strength_class(converter.getStringSpecValue(specs, STRENGTH_CLASS))
                .pr_yield(converter.getLimitSpecValue(specs, YIELD))
                .pr_yield02(converter.getLimitSpecValue(specs, YIELD_02))
                .pr_relations2d(converter.getStringSpecValue(specs, RELATIONS_2D))
                .pr_180bend_diam(converter.getStringSpecValue(specs, BEND_DIAM_180))
                .pr_180bend_radius(converter.getStringSpecValue(specs, BEND_RADIUS_180))
                .pr_90bend_diam(converter.getStringSpecValue(specs, BEND_DIAM_90))
                .pr_r90_anisotropy(converter.getStringSpecValue(specs, ANISOTROPY_R90))
                .r0(converter.getStringSpecValue(specs, R0))
                .rm_rbar(converter.getStringSpecValue(specs, RM_BAR))
                .pr_n90_strength(converter.getStringSpecValue(specs, STRENGTH_N90))
                .n0(converter.getStringSpecValue(specs, N0))
                .pr_hardness_hr15t(converter.getLimitSpecValue(specs, HARDNESS_HR15T))
                .pr_hardness_hr30t(converter.getLimitSpecValue(specs, HARDNESS_HR30T))
                .pr_hardness_hrb(converter.getLimitSpecValue(specs, HARDNESS_HRB))
                .pr_hardness_hb(converter.getLimitSpecValue(specs, HARDNESS_HB))
                .pr_hardness_hrf(converter.getLimitSpecValue(specs, HARDNESS_HRF))
                .pr_hardness_hv5(converter.getLimitSpecValue(specs, HARDNESS_HV5))
                .pr_asperity(converter.getStringSpecValue(specs, ASPERITY))
                .pr_peak_number(converter.getStringSpecValue(specs, PEAK_NUMBER))
                .pr_bh2_effect(converter.getStringSpecValue(specs, BH2_EFFECT))
                .pr_kcu_0(converter.getLimitSpecValue(specs, KCU0))
                .pr_kcv_0(converter.getLimitSpecValue(specs, KCV0))
                .pr_kcv_10(converter.getLimitSpecValue(specs, KCV10))
                .pr_kcv10(converter.getLimitSpecValue(specs, KCV_PLUS_10))
                .pr_kcv_15(converter.getLimitSpecValue(specs, KCV15))
                .pr_kcu_20(converter.getLimitSpecValue(specs, KCU20))
                .pr_kcu20(converter.getLimitSpecValue(specs, KCU_PLUS_20))
                .pr_kcv_20(converter.getLimitSpecValue(specs, KCV20))
                .pr_kcv20(converter.getLimitSpecValue(specs, KCV_PLUS_20))
                .pr_kcu_30(converter.getLimitSpecValue(specs, KCU30))
                .pr_kcv_35(converter.getLimitSpecValue(specs, KCV35))
                .pr_kcu_40(converter.getLimitSpecValue(specs, KCU40))
                .pr_kcv_40(converter.getLimitSpecValue(specs, KCV40))
                .pr_kcu_50(converter.getLimitSpecValue(specs, KCU50))
                .pr_kcu_60(converter.getLimitSpecValue(specs, KCU60))
                .pr_kcu_70(converter.getLimitSpecValue(specs, KCU70))
                .pr_kcu_mech_old(converter.getLimitSpecValue(specs, IMPACT_STRENGTH_AGING))
                .pr_height(converter.getStringSpecValue(specs, DEPTH_HOLE))
                .pr_coil_tilt(converter.getStringSpecValue(specs, ROLL_CURVATURE))
                .pr_p1_50(converter.getStringSpecValue(specs, P150))
                .pr_p1_7_50(converter.getStringSpecValue(specs, P1750))
                .pr_p1_5_50(converter.getStringSpecValue(specs, P1550))
                .pr_p1_0_50(converter.getStringSpecValue(specs, P1050))
                .pr_p1_5_200(converter.getStringSpecValue(specs, P15200))
                .pr_p1_5_400(converter.getStringSpecValue(specs, P15400))
                .pr_p1_0_400(converter.getStringSpecValue(specs, P10400))
                .pr_p1_0_700(converter.getStringSpecValue(specs, P10700))
                .pr_p1_0_1000(converter.getStringSpecValue(specs, P101000))
                .pr_p004_500(converter.getStringSpecValue(specs, P004500))
                .pr_p01_500(converter.getStringSpecValue(specs, P01500))
                .pr_p004_1000(converter.getStringSpecValue(specs, P0041000))
                .pr_p01_1000(converter.getStringSpecValue(specs, P011000))
                .pr_p1_5_50_sst(converter.getStringSpecValue(specs, P1550SST))
                .pr_p1_7_50_sst(converter.getStringSpecValue(specs, P1750SST))
                .pr_p1_5_60(converter.getStringSpecValue(specs, P1560))
                .pr_p1_7_60(converter.getStringSpecValue(specs, P1760))
                .pr_p1_5_60_sst(converter.getStringSpecValue(specs, P1560SST))
                .pr_p1_7_60_sst(converter.getStringSpecValue(specs, P1760SST))
                .pr_p1_5_50_lb(converter.getStringSpecValue(specs, P1550LB))
                .pr_p1_7_50_lb(converter.getStringSpecValue(specs, P1750LB))
                .pr_p1_7_60_lb(converter.getStringSpecValue(specs, P1760LB))
                .pr_p1_5_50_sst_lb(converter.getStringSpecValue(specs, P1550SST_LB))
                .pr_p1_7_50_sst_lb(converter.getStringSpecValue(specs, P1750SST_LB))
                .pr_p1_7_60_sst_lb(converter.getStringSpecValue(specs, P1760SST_LB))
                .pr_p1_5_60_lb(converter.getStringSpecValue(specs, P1560LB))
                .pr_p1_5_60_sst_lb(converter.getStringSpecValue(specs, P1560SST_LB))
                .pr_h_004_500(converter.getStringSpecValue(specs, H004500))
                .pr_h_01_500(converter.getStringSpecValue(specs, H01500))
                .pr_h_004_1000(converter.getStringSpecValue(specs, H0041000))
                .pr_h_01_1000(converter.getStringSpecValue(specs, H011000))
                .pr_b_40(converter.getStringSpecValue(specs, B40))
                .pr_b_80(converter.getStringSpecValue(specs, B80))
                .pr_b_100(converter.getStringSpecValue(specs, B100))
                .pr_b_200(converter.getStringSpecValue(specs, B200))
                .pr_b_800(converter.getStringSpecValue(specs, B800))
                .pr_b_1000(converter.getStringSpecValue(specs, B1000))
                .pr_b_2500(converter.getStringSpecValue(specs, B2500))
                .pr_b_5000(converter.getStringSpecValue(specs, B5000))
                .pr_b_10000(converter.getStringSpecValue(specs, B10000))
                .pr_b_100_sst(converter.getStringSpecValue(specs, B100SST))
                .pr_b_800_sst(converter.getStringSpecValue(specs, B800SST))
                .pr_b_2500_sst(converter.getStringSpecValue(specs, B2500SST))
                .pr_coercive_field(converter.getStringSpecValue(specs, COERCIVE_FIELD))
                .factor_lamination(converter.getStringSpecValue(specs, FACTOR_LAMINATION))
                .macro_mann(converter.getStringSpecValue(specs, MACRO_MANN))
                .vnutr_tr(converter.getStringSpecValue(specs, INTERNAL_CRACKS))
                .vkl_obl(converter.getStringSpecValue(specs, CLOUD_INCLUSIONS))
                .osev_segr(converter.getStringSpecValue(specs, AXIAL_SEGREGATION))
                .vkl_toch(converter.getStringSpecValue(specs, POINT_INCLUSIONS))
                .uzkgr_tr(converter.getStringSpecValue(specs, EDGE_CRACKS))
                .uglov_tr(converter.getStringSpecValue(specs, ANGLE_CRACKS))
                .pr_bake_hardening(converter.getStringSpecValue(specs, BAKE_HARDENING))
                .pr_annotation(converter.getStringSpecValue(specs, NOTE))
                .build();
    }

    @Override
    public EvennessTkLimitDto toEvennessTkLimitDto(PdmDictionary dictionary) {
        Assert.notNull(dictionary, DICT_NOT_NULL);
        Assert.notNull(dictionary.getData(), DICT_DATA_NOT_NULL);

        final var specs = dictionary.getData().getSpecifications();

        return EvennessTkLimitDto.builder()
                .remote_id(dictionary.getPk().getId())
                .ts(dictionary.getTs())
                .standSort(converter.getStringSpecValue(specs, ASSORTMENT_STANDARD))
                .prWidthGood(converter.getLimitSpecValue(specs, WHIDTH_PRODUCT))
                .prThickGood(converter.getLimitSpecValue(specs, THICKNESS_OF_ROLLED_PRODUCTS))
                .prEvenness(converter.getStringSpecValue(specs, EVENNESS))
                .prYield(converter.getLimitSpecValue(specs, YIELD_POINT))
                .prFormSap(converter.getLimitSpecValue(specs, FORM_SAP))
                .prEvennessTolMax(converter.parseToDouble(
                        converter.getStringSpecValue(specs, EVENNESS_TOLERANCE)
                ))
                .prEvennessTolPerc(converter.parseToDouble(
                        converter.getStringSpecValue(specs, EVENNESS_TOLERANCE_PERCENT)
                ))
                .prTensileStrength(converter.getLimitSpecValue(specs, TENSILE_STRENGTH))
                .prAnnotation(converter.getStringSpecValue(specs, NOTE))
                .build();
    }

    @Override
    public TkNumDto toTkNumDto(PdmDictionary dictionary) {
        Assert.notNull(dictionary, DICT_NOT_NULL);
        Assert.notNull(dictionary.getData(), DICT_DATA_NOT_NULL);

        final var specs = dictionary.getData().getSpecifications();

        return TkNumDto.builder()
                .remote_id(dictionary.getPk().getId())
                .ts(dictionary.getTs())
                .tkNum(converter.getStringSpecValue(specs, TK_NUMBER_OR_VTK_VERSION_ROUTE))
                .tkPurp(converter.getStringSpecValue(specs, TARGET))
                .dateStart(this.getDocDate(specs, START_DATE))
                .dateFinish(this.getDocDate(specs, FINISH_DATE))
                .build();
    }

    private String getDocDate(List<Spec> specs, SpecCode specCode) {
        String docDate = converter.getStringSpecValue(specs, specCode);

        if (StringUtils.isEmpty(docDate)) {
            return null;
        }
        return docDate;
    }

    @Override
    public CEqDto toCEqDto(PdmDictionary dictionary) {
        Assert.notNull(dictionary, DICT_NOT_NULL);
        Assert.notNull(dictionary.getData(), DICT_DATA_NOT_NULL);

        final var specs = dictionary.getData().getSpecifications();

        return CEqDto.builder()
                .remote_id(dictionary.getPk().getId())
                .ts(dictionary.getTs())
                .ceqNum(converter.getStringSpecValue(specs, CARBON_EQUIVALENT_FORMULA_NUMBER))
                .ceqFormula(converter.getStringSpecValue(specs, CARBON_EQUIVALENT_FORMULA))
                .prAnnotation(converter.getStringSpecValue(specs, NOTE))
                .build();
    }

    @Override
    public MechanicalTkDto toMechanicalTkDto(PdmDictionary dictionary) {
        Assert.notNull(dictionary, DICT_NOT_NULL);
        Assert.notNull(dictionary.getData(), DICT_DATA_NOT_NULL);

        final var specs = dictionary.getData().getSpecifications();

        return MechanicalTkDto.builder()
                .remote_id(dictionary.getPk().getId())
                .ts(dictionary.getTs())
                .tk_num(converter.getStringSpecValue(specs, TK_NUMBER_OR_VTK_VERSION_ROUTE))
                ._prior(converter.parseToInteger(converter.getStringSpecValue(specs, PRIORITY)))
                .tk_route(converter.getStringSpecValue(specs, ROUTE_TK))
                .pr_category(converter.getStringSpecValue(specs, CATEGORY_OF_MARK))
                .pr_prod_mark(converter.getStringSpecValue(specs, STEEL_MARK))
                .pr_stand_mark(converter.getStringSpecValue(specs, PRODUCT_STANDARD))
                .pr_steel_mark(converter.getStringSpecValue(specs, MELTING_MARK))
                .pr_stand_steel(converter.getStringSpecValue(specs, MELTING_MARK_STANDART))
                .pr_thick_uncoat(converter.getLimitSpecValue(specs, THICKNESS_OF_ROLLED_PRODUCTS))
                .pr_drow(converter.getStringSpecValue(specs, DROW))
                .pr_scope_group(converter.getStringSpecValue(specs, SCOPE_GROUP))
                .pr_kv_60(converter.getLimitSpecValue(specs, IMPACT_ENERGY_KV60))
                .pr_kv_40(converter.getLimitSpecValue(specs, IMPACT_ENERGY_KV40))
                .pr_kv_20(converter.getLimitSpecValue(specs, IMPACT_ENERGY_KV20))
                .pr_kv_0(converter.getLimitSpecValue(specs, IMPACT_ENERGY_KV0))
                .pr_kv20(converter.getLimitSpecValue(specs, IMPACT_ENERGY_KV_PLUS_20))
                .pr_tensile_elongation4(converter.getLimitSpecValue(specs, TENSILE_ELONGATION_4))
                .pr_tensile_elongation5(converter.getLimitSpecValue(specs, TENSILE_ELONGATION_5))
                .pr_tensile_elongation10(converter.getLimitSpecValue(specs, TENSILE_ELONGATION_10))
                .pr_tensile_elongation50(converter.getLimitSpecValue(specs, TENSILE_ELONGATION_50))
                .pr_tensile_elongation80(converter.getLimitSpecValue(specs, TENSILE_ELONGATION_80))
                .pr_tensile_elongation200(converter.getLimitSpecValue(specs, TENSILE_ELONGATION_200))
                .pr_tensile_strength(converter.getLimitSpecValue(specs, TENSILE_STRENGTH))
                .pr_yield(converter.getLimitSpecValue(specs, YIELD))
                .pr_impact_energy(converter.getLimitSpecValue(specs, IMPACT_ENERGY))
                .pr_strength_class(converter.getStringSpecValue(specs, STRENGTH_CLASS))
                .pr_yield02(converter.getStringSpecValue(specs, YIELD_02))
                .pr_relations2d(converter.getStringSpecValue(specs, RELATIONS_2D))
                .pr_180bend_diam(converter.getStringSpecValue(specs, BEND_DIAM_180))
                .pr_180bend_radius(converter.getStringSpecValue(specs, BEND_RADIUS_180))
                .pr_90bend_diam(converter.getStringSpecValue(specs, BEND_DIAM_90))
                .pr_r90_anisotropy(converter.getStringSpecValue(specs, ANISOTROPY_R90))
                .r0(converter.getStringSpecValue(specs, R0))
                .rm_rbar(converter.getStringSpecValue(specs, RM_BAR))
                .pr_n90_strength(converter.getStringSpecValue(specs, STRENGTH_N90))
                .n0(converter.getStringSpecValue(specs, N0))
                .pr_hardness_hr15t(converter.getStringSpecValue(specs, HARDNESS_HR15T))
                .pr_hardness_hr30t(converter.getStringSpecValue(specs, HARDNESS_HR30T))

                .pr_hardness_hrb(converter.getLimitSpecValue(specs, HARDNESS_HRB))
                .pr_hardness_hb(converter.getLimitSpecValue(specs, HARDNESS_HB))

                .pr_hardness_hrf(converter.getStringSpecValue(specs, HARDNESS_HRF))
                .pr_hardness_hv5(converter.getStringSpecValue(specs, HARDNESS_HV5))
                .pr_asperity(converter.getStringSpecValue(specs, ASPERITY))
                .pr_peak_number(converter.getStringSpecValue(specs, PEAK_NUMBER))
                .pr_bh2_effect(converter.getStringSpecValue(specs, BH2_EFFECT))

                .pr_kcu_0(converter.getLimitSpecValue(specs, KCU0))
                .pr_kcv_0(converter.getLimitSpecValue(specs, KCV0))
                .pr_kcv_10(converter.getLimitSpecValue(specs, KCV10))
                .pr_kcv10(converter.getLimitSpecValue(specs, KCV_PLUS_10))
                .pr_kcv_15(converter.getLimitSpecValue(specs, KCV15))
                .pr_kcu_20(converter.getLimitSpecValue(specs, KCU20))
                .pr_kcu20(converter.getLimitSpecValue(specs, KCU_PLUS_20))
                .pr_kcv_20(converter.getLimitSpecValue(specs, KCV20))
                .pr_kcv20(converter.getLimitSpecValue(specs, KCV_PLUS_20))
                .pr_kcu_30(converter.getLimitSpecValue(specs, KCU30))
                .pr_kcv_35(converter.getLimitSpecValue(specs, KCV35))
                .pr_kcu_40(converter.getLimitSpecValue(specs, KCU40))
                .pr_kcv_40(converter.getLimitSpecValue(specs, KCV40))
                .pr_kcu_50(converter.getLimitSpecValue(specs, KCU50))
                .pr_kcu_60(converter.getLimitSpecValue(specs, KCU60))
                .pr_kcu_70(converter.getLimitSpecValue(specs, KCU70))
                .pr_kcu_mech_old(converter.getLimitSpecValue(specs, IMPACT_STRENGTH_AGING))

                .pr_height(converter.getStringSpecValue(specs, DEPTH_HOLE))
                .pr_coil_tilt(converter.getStringSpecValue(specs, ROLL_CURVATURE))
                .pr_p1_50(converter.getStringSpecValue(specs, P150))
                .pr_p1_7_50(converter.getStringSpecValue(specs, P1750))
                .pr_p1_5_50(converter.getStringSpecValue(specs, P1550))
                .pr_p1_0_50(converter.getStringSpecValue(specs, P1050))
                .pr_p1_5_200(converter.getStringSpecValue(specs, P15200))
                .pr_p1_5_400(converter.getStringSpecValue(specs, P15400))
                .pr_p1_0_400(converter.getStringSpecValue(specs, P10400))
                .pr_p1_0_700(converter.getStringSpecValue(specs, P10700))
                .pr_p1_0_1000(converter.getStringSpecValue(specs, P101000))
                .pr_p004_500(converter.getStringSpecValue(specs, P004500))
                .pr_p01_500(converter.getStringSpecValue(specs, P01500))
                .pr_p004_1000(converter.getStringSpecValue(specs, P0041000))
                .pr_p01_1000(converter.getStringSpecValue(specs, P011000))
                .pr_p1_5_50_sst(converter.getStringSpecValue(specs, P1550SST))
                .pr_p1_7_50_sst(converter.getStringSpecValue(specs, P1750SST))
                .pr_p1_5_60(converter.getStringSpecValue(specs, P1560))
                .pr_p1_7_60(converter.getStringSpecValue(specs, P1760))
                .pr_p1_5_60_sst(converter.getStringSpecValue(specs, P1560SST))
                .pr_p1_7_60_sst(converter.getStringSpecValue(specs, P1760SST))
                .pr_p1_5_50_lb(converter.getStringSpecValue(specs, P1550LB))
                .pr_p1_7_50_lb(converter.getStringSpecValue(specs, P1750LB))
                .pr_p1_7_60_lb(converter.getStringSpecValue(specs, P1760LB))
                .pr_p1_5_50_sst_lb(converter.getStringSpecValue(specs, P1550SST_LB))
                .pr_p1_7_50_sst_lb(converter.getStringSpecValue(specs, P1750SST_LB))
                .pr_p1_7_60_sst_lb(converter.getStringSpecValue(specs, P1760SST_LB))
                .pr_p1_5_60_lb(converter.getStringSpecValue(specs, P1560LB))
                .pr_p1_5_60_sst_lb(converter.getStringSpecValue(specs, P1560SST_LB))
                .pr_h_004_500(converter.getStringSpecValue(specs, H004500))
                .pr_h_01_500(converter.getStringSpecValue(specs, H01500))
                .pr_h_004_1000(converter.getStringSpecValue(specs, H0041000))
                .pr_h_01_1000(converter.getStringSpecValue(specs, H011000))
                .pr_b_40(converter.getStringSpecValue(specs, B40))
                .pr_b_80(converter.getStringSpecValue(specs, B80))
                .pr_b_100(converter.getStringSpecValue(specs, B100))
                .pr_b_200(converter.getStringSpecValue(specs, B200))
                .pr_b_800(converter.getStringSpecValue(specs, B800))
                .pr_b_1000(converter.getStringSpecValue(specs, B1000))
                .pr_b_2500(converter.getStringSpecValue(specs, B2500))
                .pr_b_5000(converter.getStringSpecValue(specs, B5000))
                .pr_b_10000(converter.getStringSpecValue(specs, B10000))
                .pr_b_100_sst(converter.getStringSpecValue(specs, B100SST))
                .pr_b_800_sst(converter.getStringSpecValue(specs, B800SST))
                .pr_b_2500_sst(converter.getStringSpecValue(specs, B2500SST))
                .pr_coercive_field(converter.getStringSpecValue(specs, COERCIVE_FIELD))
                .factor_lamination(converter.getStringSpecValue(specs, FACTOR_LAMINATION))
                .macro_mann(converter.getStringSpecValue(specs, MACRO_MANN))
                .vnutr_tr(converter.getStringSpecValue(specs, INTERNAL_CRACKS))
                .vkl_obl(converter.getStringSpecValue(specs, CLOUD_INCLUSIONS))
                .osev_segr(converter.getStringSpecValue(specs, AXIAL_SEGREGATION))
                .vkl_toch(converter.getStringSpecValue(specs, POINT_INCLUSIONS))
                .uzkgr_tr(converter.getStringSpecValue(specs, EDGE_CRACKS))
                .uglov_tr(converter.getStringSpecValue(specs, ANGLE_CRACKS))
                .pr_bake_hardening(converter.getStringSpecValue(specs, BAKE_HARDENING))
                .pr_annotation(converter.getStringSpecValue(specs, NOTE))
                .build();
    }

    @Override
    public ChemicalTkLimitDto toChemicalTkLimitDto(PdmDictionary dictionary) {
        Assert.notNull(dictionary, DICT_NOT_NULL);
        Assert.notNull(dictionary.getData(), DICT_DATA_NOT_NULL);

        final var specs = dictionary.getData().getSpecifications();

        return ChemicalTkLimitDto.builder()
                .remote_id(dictionary.getPk().getId())
                .ts(dictionary.getTs())
                .prior(converter.parseToInteger(converter.getStringSpecValue(specs, PRIORITY)))
                .tkNum(converter.getStringSpecValue(specs, TK_NUMBER_OR_VTK_VERSION_ROUTE))
                .tkRoute(converter.getStringSpecValue(specs, ROUTE_TK))
                .prSteelMark(converter.getStringSpecValue(specs, MELTING_MARK))
                .prStandSteel(converter.getStringSpecValue(specs, MELTING_MARK_STANDART))
                .prProdMark(converter.getStringSpecValue(specs, STEEL_MARK))
                .prStandMark(converter.getStringSpecValue(specs, PRODUCT_STANDARD))

                .prThickUncoat(converter.getLimitSpecValue(specs, THICKNESS_OF_ROLLED_PRODUCTS))

                .prDrow(converter.getStringSpecValue(specs, DROW))

                .c(converter.getLimitSpecValue(specs, MASS_FRACTION_C))
                .si(converter.getLimitSpecValue(specs, MASS_FRACTION_SI))
                .mn(converter.getLimitSpecValue(specs, MASS_FRACTION_MN))
                .s(converter.getLimitSpecValue(specs, MASS_FRACTION_S))
                .p(converter.getLimitSpecValue(specs, MASS_FRACTION_P))
                .al(converter.getLimitSpecValue(specs, MASS_FRACTION_AL))
                .cr(converter.getLimitSpecValue(specs, MASS_FRACTION_CR))
                .ni(converter.getLimitSpecValue(specs, MASS_FRACTION_NI))
                .cu(converter.getLimitSpecValue(specs, MASS_FRACTION_CU))
                .ti(converter.getLimitSpecValue(specs, MASS_FRACTION_TI))
                .n(converter.getLimitSpecValue(specs, MASS_FRACTION_N))
                .v(converter.getLimitSpecValue(specs, MASS_FRACTION_V))
                .nb(converter.getLimitSpecValue(specs, MASS_FRACTION_NB))
                .sn(converter.getLimitSpecValue(specs, MASS_FRACTION_SN))
                .mo(converter.getLimitSpecValue(specs, MASS_FRACTION_MO))
                .b(converter.getLimitSpecValue(specs, MASS_FRACTION_B))
                .as(converter.getLimitSpecValue(specs, MASS_FRACTION_AS))
                .ca(converter.getLimitSpecValue(specs, MASS_FRACTION_CA))
                .h(converter.getLimitSpecValue(specs, MASS_FRACTION_H))
                .sb(converter.getLimitSpecValue(specs, MASS_FRACTION_SB))
                .pb(converter.getLimitSpecValue(specs, MASS_FRACTION_PB))
                .siP(converter.getLimitSpecValue(specs, SI_P))
                .si25P(converter.getLimitSpecValue(specs, SI_25P))
                .crMo(converter.getLimitSpecValue(specs, CR_MO))
                .crNiCu(converter.getLimitSpecValue(specs, CR_NI_CU))
                .crNiMoCu(converter.getLimitSpecValue(specs, CR_NI_CU_MO))
                .cuNiCrMoV(converter.getLimitSpecValue(specs, CU_NI_CR_MO_V))
                .crNiCuMoSn(converter.getLimitSpecValue(specs, CR_NI_CU_MO_SN))
                .mnSi(converter.getLimitSpecValue(specs, MN_SI))
                .mnS(converter.getLimitSpecValue(specs, MN_S))
                .vNbTi(converter.getLimitSpecValue(specs, TI_V_NB))
                .niVTi(converter.getLimitSpecValue(specs, NI_V_TI))
                .p25SiAl(converter.getLimitSpecValue(specs, P25_SI_AL))
                .ti15S342N4C(converter.getLimitSpecValue(specs, TI_15S_342N_4C))
                .ti15S343N4C(converter.getLimitSpecValue(specs, TI_15S_343N_4C))
                .crNiCuSn(converter.getLimitSpecValue(specs, CR_NI_CU_SN))
                .prGroupNorm(converter.getStringSpecValue(specs, NORM_GROUP))
                .ceqNum(converter.getStringSpecValue(specs, CARBON_EQUIVALENT_FORMULA_NUMBER))
                .ceq(converter.getLimitSpecValue(specs, CARBON_EQUIVALENT))
                .tkPoint(converter.getStringSpecValue(specs, TK_POINT))
                .prAnnotation(converter.getStringSpecValue(specs, NOTE))
                .bi(converter.getLimitSpecValue(specs, MASS_FRACTION_BI))
                .co(converter.getLimitSpecValue(specs, MASS_FRACTION_CO))
                .fe(converter.getLimitSpecValue(specs, MASS_FRACTION_FE))
                .mg(converter.getLimitSpecValue(specs, MASS_FRACTION_MG))
                .o(converter.getLimitSpecValue(specs, MASS_FRACTION_O))
                .w(converter.getLimitSpecValue(specs, MASS_FRACTION_W))
                .zn(converter.getLimitSpecValue(specs, MASS_FRACTION_ZN))
                .zr(converter.getLimitSpecValue(specs, MASS_FRACTION_ZR))
                .crCuMo(converter.getLimitSpecValue(specs, CR_CU_MO))
                .cuCrNiMoTi(converter.getLimitSpecValue(specs, CU_CR_NI_MO_TI))
                .caS(converter.getLimitSpecValue(specs, CA_S))
                .alN(converter.getLimitSpecValue(specs, AL_N))
                .nbV(converter.getLimitSpecValue(specs, NB_V))
                .nAl(converter.getLimitSpecValue(specs, N_AL))
                .tiN(converter.getLimitSpecValue(specs, TI_N))
                .build();
    }

    @Override
    public MicrostructureDto toMicrostructureDto(PdmDictionary dictionary) {
        Assert.notNull(dictionary, DICT_NOT_NULL);
        Assert.notNull(dictionary.getData(), DICT_DATA_NOT_NULL);

        final var specs = dictionary.getData().getSpecifications();

        return MicrostructureDto.builder()
                .remote_id(dictionary.getPk().getId())
                .ts(dictionary.getTs())
                .tkNum(converter.getStringSpecValue(specs, TK_NUMBER_OR_VTK_VERSION_ROUTE))
                .tkRoute(converter.getStringSpecValue(specs, ROUTE_TK))
                .prProdMark(converter.getStringSpecValue(specs, STEEL_MARK))
                .prStandMark(converter.getStringSpecValue(specs, PRODUCT_STANDARD))
                .prThickUncoat(converter.getLimitSpecValue(specs, THICKNESS_OF_ROLLED_PRODUCTS))
                .category(converter.getStringSpecValue(specs, CATEGORY_GOST_4041))
                .attestStand(converter.getStringSpecValue(specs, MICROCTRUCTURE_STANDART))
                .ferriteGrain(converter.getLimitSpecValue(specs, FERRIT_GRAIN))
                .unevenessFerriteGrain(converter.getStringSpecValue(specs, UNEVENNESS_OF_FERRIT_GRAIN))
                .structFreeCementite(converter.getLimitSpecValue(specs, CEMENTITE))
                .unmetallInclusionsOxides(converter.getLimitSpecValue(specs, OXIDES))
                .unmetallInclusionsSulfides(converter.getLimitSpecValue(specs, SULPHIDES))
                .unmetallInclusionsNitrides(converter.getLimitSpecValue(specs, NITRIDES))
                .unmetallInclusionsSilicates(converter.getLimitSpecValue(specs, SILICATES))
                .unmetallInclusionsOxidesB(converter.getLimitSpecValue(specs, OXIDES_TYPE_B))
                .unmetallInclusionsSulfidesA(converter.getLimitSpecValue(specs, SULPHIDES_TYPE_A))
                .unmetallInclusionsSilicatesC(converter.getLimitSpecValue(specs, SILICATES_TYPE_C))
                .unmetallInclusionsGlobOxidesD(converter.getLimitSpecValue(specs, OXIDES_TYPE_D))
                .unmetallInclusions(converter.getLimitSpecValue(specs, NON_METALLIC_INCLUSIONS_ISO_4967_2013))
                .polFerPerStruct(converter.getLimitSpecValue(specs, BANDING_OF_FERRIE_PEARLITE_STRUCTURE))
                .depthDecarbLayer(converter.getLimitSpecValue(specs, DEPTH_WITHOUT_C_LAYER))
                .perliteGrain(converter.getLimitSpecValue(specs, PERLITE_GRAIN))
                .prAnnotation(converter.getStringSpecValue(specs, NOTE))
                .build();
    }

    @Override
    public ChemicalEquivalentStdDto toChemicalEquivalentStdDto(PdmDictionary dictionary) {
        Assert.notNull(dictionary, DICT_NOT_NULL);
        Assert.notNull(dictionary.getData(), DICT_DATA_NOT_NULL);

        final var specs = dictionary.getData().getSpecifications();

        log.debug("toChemicalEquivalentStdDto, PDM DICTIONARY: {} ", dictionary);

        return ChemicalEquivalentStdDto.builder()
                .remote_id(dictionary.getPk().getId())
                .ts(dictionary.getTs())
                .prProdMark(converter.getStringSpecValue(specs, STEEL_MARK))
                .prStandMark(converter.getStringSpecValue(specs, PRODUCT_STANDARD))
                .prThickUncoat(converter.getLimitSpecValue(specs, THICKNESS_OF_ROLLED_PRODUCTS))
                .prStrengthClass(converter.getStringSpecValue(specs, STRENGTH_CLASS))
                .ceqNum(converter.getStringSpecValue(specs, CARBON_EQUIVALENT_FORMULA_NUMBER))
                .ceq(converter.getLimitSpecValue(specs, CARBON_EQUIVALENT))
                .pcmNum(converter.getStringSpecValue(specs, CRACK_RESISTANCE_COEFFICIENT_FORMULA_NUMBER))
                .pcm(converter.getLimitSpecValue(specs, CRACK_RESISTANCE_COEFFICIENT))
                .prAnnotation(converter.getStringSpecValue(specs, NOTE))
                .build();
    }

    @Override
    public MatchTkDto toMatchTkDto(PdmDictionary dictionary) {
        Assert.notNull(dictionary, DICT_NOT_NULL);
        Assert.notNull(dictionary.getData(), DICT_DATA_NOT_NULL);

        final var specs = dictionary.getData().getSpecifications();

        return MatchTkDto.builder()
                .remote_id(dictionary.getPk().getId())
                .ts(dictionary.getTs())
                .tkNum(converter.getStringSpecValue(specs, TK_NUMBER_OR_VTK_VERSION_ROUTE))
                .tkNumSap(converter.getStringSpecValue(specs, TK_SAP_NUMBER))
                .prAnnotation(converter.getStringSpecValue(specs, NOTE))
                .build();
    }

    @Override
    public MatchRpDto toMatchRpDto(PdmDictionary dictionary) {
        Assert.notNull(dictionary, DICT_NOT_NULL);
        Assert.notNull(dictionary.getData(), DICT_DATA_NOT_NULL);

        final var specs = dictionary.getData().getSpecifications();

        return MatchRpDto.builder()
                .remote_id(dictionary.getPk().getId())
                .ts(dictionary.getTs())
                .rpNumSap(converter.getStringSpecValue(specs, RP_SAP_NUMBER))
                .tkNum(converter.getStringSpecValue(specs, RP_NUMBER_VERSION_ROUTE))
                .build();
    }

    @Override
    public PcmDto toPcmDto(PdmDictionary dictionary) {
        Assert.notNull(dictionary, DICT_NOT_NULL);
        Assert.notNull(dictionary.getData(), DICT_DATA_NOT_NULL);

        final var specs = dictionary.getData().getSpecifications();

        return PcmDto.builder()
                .remote_id(dictionary.getPk().getId())
                .ts(dictionary.getTs())
                .pcmNum(converter.getStringSpecValue(specs, CRACK_RESISTANCE_COEFFICIENT_FORMULA_NUMBER))
                .pcmFormula(converter.getStringSpecValue(specs, CRACK_RESISTANCE_FORMULA))
                .prAnnotation(converter.getStringSpecValue(specs, NOTE))
                .build();
    }

    @Override
    public ToleranceDto toToleranceDto(PdmDictionary dictionary) {
        Assert.notNull(dictionary, DICT_NOT_NULL);
        Assert.notNull(dictionary.getData(), DICT_DATA_NOT_NULL);

        final var specs = dictionary.getData().getSpecifications();

        return ToleranceDto.builder()
                .remote_id(dictionary.getPk().getId())
                .ts(dictionary.getTs())
                .prStandMark(converter.getStringSpecValue(specs, PRODUCT_STANDARD))
                .useStandMark(converter.getStringSpecValue(specs, PRODUCT_STANDARD_ADDITIONAL))
                .standTolThick(converter.getStringSpecValue(specs, THICKNESS_TOLERANCE_STANDART))
                .standTolWidth(converter.getStringSpecValue(specs, WIDTH_TOLERANCE_STANDART))
                .standTolLength(converter.getStringSpecValue(specs, LENGTH_TOLERANCE_STANDART))
                .standTolEvenness(converter.getStringSpecValue(specs, EVENNESS_TOLERANCE_STANDART))
                .prAnnotation(converter.getStringSpecValue(specs, NOTE))
                .build();
    }

    @Override
    public AsapMechPropertiesDtDto toAsapMechPropertiesDtDto(PdmDictionary dictionary) {
        Assert.notNull(dictionary, DICT_NOT_NULL);
        Assert.notNull(dictionary.getData(), DICT_DATA_NOT_NULL);

        final var specs = dictionary.getData().getSpecifications();

        return AsapMechPropertiesDtDto.builder()
                .remoteId(dictionary.getPk().getId())
                .updateTs(dictionary.getTs())
                .prProdMark(converter.getStringSpecValue(specs, STEEL_MARK)) // String
                .dt(converter.getStringSpecValue(specs, ADDITIONAL_REQUIREMENTS)) // String
                .prStandMark(converter.getStringSpecValue(specs, PRODUCT_STANDARD)) // String
                .prThickUncoat(converter.getLimitSpecValue(specs, THICKNESS_PRODUCTS)) // LimitDto
                .prB40(converter.getLimitSpecValue(specs, B40)) // LimitDto
                .prB80(converter.getLimitSpecValue(specs, B80)) // LimitDto
                .prB100(converter.getLimitSpecValue(specs, B100)) // LimitDto
                .prB200(converter.getLimitSpecValue(specs, B200)) // LimitDto
                .prB400(converter.getLimitSpecValue(specs, B400)) // LimitDto
                .prB800(converter.getLimitSpecValue(specs, B800)) // LimitDto
                .prB1000(converter.getLimitSpecValue(specs, B1000)) // LimitDto
                .prB2500(converter.getLimitSpecValue(specs, B2500)) // LimitDto
                .prB5000(converter.getLimitSpecValue(specs, B5000)) // LimitDto
                .prB10000(converter.getLimitSpecValue(specs, B10000)) // LimitDto
                .prB100Sst(converter.getLimitSpecValue(specs, B100SST)) // LimitDto
                .prB800Sst(converter.getLimitSpecValue(specs, B800SST)) // LimitDto
                .prB2500Sst(converter.getLimitSpecValue(specs, B2500SST)) // LimitDto
                .prH004500(converter.getLimitSpecValue(specs, H004500)) // LimitDto
                .prH01500(converter.getLimitSpecValue(specs, H01500)) // LimitDto
                .prH0041000(converter.getLimitSpecValue(specs, H0041000)) // LimitDto
                .prH011000(converter.getLimitSpecValue(specs, H011000)) // LimitDto
                .prCoerciveField(converter.getStringSpecValue(specs, COERCIVE_FIELD)) // String
                .factorLamination(converter.getLimitSpecValue(specs, FACTOR_LAMINATION)) // LimitDto
                .agingCoefficient(converter.getLimitSpecValue(specs, AGING_FACTOR)) // LimitDto
                .resistanceCoefficient(converter.getLimitSpecValue(specs, DRAG_FACTOR)) // LimitDto
                .resistanceCoefficientFront(converter.getLimitSpecValue(specs, DRAG_FACTOR_FRONT)) // LimitDto
                .resistanceCoefficientReverse(converter.getLimitSpecValue(specs, DRAG_FACTOR_REVERSE)) // LimitDto
                .adhesion(converter.getStringSpecValue(specs, ADHESION)) // String
                .adhesionFront(converter.getStringSpecValue(specs, ADHESION_FRONT)) // String
                .adhesionReverse(converter.getStringSpecValue(specs, ADHESION_REVERSE)) // String
                .plasticityNumberBends(converter.getLimitSpecValue(specs, PLASTICITY_NUMBER_OF_BENDS)) // LimitDto
                .tlotPokr(converter.getLimitSpecValue(specs, COATING_THICKNESS)) // LimitDto
                .tlotPokrFront(converter.getLimitSpecValue(specs, COATING_THICKNESS_FRONT)) // LimitDto
                .tlotPokrReverse(converter.getLimitSpecValue(specs, COATING_THICKNESS_REVERSE)) // LimitDto
                .prP1550(converter.getLimitSpecValue(specs, P1550)) // LimitDto
                .prP1560(converter.getLimitSpecValue(specs, P1560)) // LimitDto
                .prP1750(converter.getLimitSpecValue(specs, P1750)) // LimitDto
                .prP1760(converter.getLimitSpecValue(specs, P1760)) // LimitDto
                .prP1550Sst(converter.getLimitSpecValue(specs, P1550SST)) // LimitDto
                .prP1560Sst(converter.getLimitSpecValue(specs, P1560SST)) // LimitDto
                .prP1750Sst(converter.getLimitSpecValue(specs, P1750SST)) // LimitDto
                .prP1760Sst(converter.getLimitSpecValue(specs, P1760SST)) // LimitDto
                .prP15200(converter.getLimitSpecValue(specs, P15200)) // LimitDto
                .prP15400(converter.getLimitSpecValue(specs, P15400)) // LimitDto
                .prP1050(converter.getLimitSpecValue(specs, P1050)) // LimitDto
                .prP10400(converter.getLimitSpecValue(specs, P10400)) // LimitDto
                .prP10700(converter.getLimitSpecValue(specs, P10700)) // LimitDto
                .prP101000(converter.getLimitSpecValue(specs, P101000)) // LimitDto
                .prP004500(converter.getLimitSpecValue(specs, P004500)) // LimitDto
                .prP01500(converter.getLimitSpecValue(specs, P01500)) // LimitDto
                .prP0041000(converter.getLimitSpecValue(specs, P0041000)) // LimitDto
                .prP011000(converter.getLimitSpecValue(specs, P011000)) // LimitDto
                .prP1550Lb(converter.getLimitSpecValue(specs, P1550LB)) // LimitDto
                .prP1560Lb(converter.getLimitSpecValue(specs, P1560LB)) // LimitDto
                .prP1750Lb(converter.getLimitSpecValue(specs, P1750LB)) // LimitDto
                .prP1760Lb(converter.getLimitSpecValue(specs, P1760LB)) // LimitDto
                .prP1550SstLb(converter.getLimitSpecValue(specs, P1550SST_LB)) // LimitDto
                .prP1560SstLb(converter.getLimitSpecValue(specs, P1560SST_LB)) // LimitDto
                .prP1750SstLb(converter.getLimitSpecValue(specs, P1750SST_LB)) // LimitDto
                .prP1760SstLb(converter.getLimitSpecValue(specs, P1760SST_LB)) // LimitDto
                .prAnnotation(converter.getStringSpecValue(specs, NOTE)) // String
                .build();
    }

    @Override
    public PhysMechPropAnisSteelStandDto toPhysMechPropAnisSteelStandDto(PdmDictionary dictionary) {
        Assert.notNull(dictionary, DICT_NOT_NULL);
        Assert.notNull(dictionary.getData(), DICT_DATA_NOT_NULL);

        final var specs = dictionary.getData().getSpecifications();

        return PhysMechPropAnisSteelStandDto.builder()
                .remoteId(dictionary.getPk().getId())
                .updateTs(dictionary.getTs())
                .prProdMark(converter.getStringSpecValue(specs, STEEL_MARK)) // String
                .prStandMark(converter.getStringSpecValue(specs, PRODUCT_STANDARD)) // String
                .prThickUncoat(converter.getLimitSpecValue(specs, THICKNESS_PRODUCTS)) // LimitDto
                .prP1550(converter.getLimitSpecValue(specs, P1550)) // LimitDto
                .prP1560(converter.getLimitSpecValue(specs, P1560)) // LimitDto
                .prP1750(converter.getLimitSpecValue(specs, P1750)) // LimitDto
                .prP1760(converter.getLimitSpecValue(specs, P1760)) // LimitDto
                .prP1550Sst(converter.getLimitSpecValue(specs, P1550SST)) // LimitDto
                .prP1560Sst(converter.getLimitSpecValue(specs, P1560SST)) // LimitDto
                .prP1750Sst(converter.getLimitSpecValue(specs, P1750SST)) // LimitDto
                .prP1760Sst(converter.getLimitSpecValue(specs, P1760SST)) // LimitDto
                .prP15200(converter.getLimitSpecValue(specs, P15200)) // LimitDto
                .prP15400(converter.getLimitSpecValue(specs, P15400)) // LimitDto
                .prP1050(converter.getLimitSpecValue(specs, P1050)) // LimitDto
                .prP10400(converter.getLimitSpecValue(specs, P10400)) // LimitDto
                .prP10700(converter.getLimitSpecValue(specs, P10700)) // LimitDto
                .prP101000(converter.getLimitSpecValue(specs, P101000)) // LimitDto
                .prP004500(converter.getLimitSpecValue(specs, P004500)) // LimitDto
                .prP01500(converter.getLimitSpecValue(specs, P01500)) // LimitDto
                .prP0041000(converter.getLimitSpecValue(specs, P0041000)) // LimitDto
                .prP011000(converter.getLimitSpecValue(specs, P011000)) // LimitDto
                .prP1550Lb(converter.getLimitSpecValue(specs, P1550LB)) // LimitDto
                .prP1560Lb(converter.getLimitSpecValue(specs, P1560LB)) // LimitDto
                .prP1750Lb(converter.getLimitSpecValue(specs, P1750LB)) // LimitDto
                .prP1760Lb(converter.getLimitSpecValue(specs, P1760LB)) // LimitDto
                .prP1550SstLb(converter.getLimitSpecValue(specs, P1550SST_LB)) // LimitDto
                .prP1560SstLb(converter.getLimitSpecValue(specs, P1560SST_LB)) // LimitDto
                .prP1750SstLb(converter.getLimitSpecValue(specs, P1750SST_LB)) // LimitDto
                .prP1760SstLb(converter.getLimitSpecValue(specs, P1760SST_LB)) // LimitDto
                .prB40(converter.getLimitSpecValue(specs, B40)) // LimitDto
                .prB80(converter.getLimitSpecValue(specs, B80)) // LimitDto
                .prB100(converter.getLimitSpecValue(specs, B100)) // LimitDto
                .prB200(converter.getLimitSpecValue(specs, B200)) // LimitDto
                .prB400(converter.getLimitSpecValue(specs, B400)) // LimitDto
                .prB800(converter.getLimitSpecValue(specs, B800)) // LimitDto
                .prB1000(converter.getLimitSpecValue(specs, B1000)) // LimitDto
                .prB2500(converter.getLimitSpecValue(specs, B2500)) // LimitDto
                .prB5000(converter.getLimitSpecValue(specs, B5000)) // LimitDto
                .prB10000(converter.getLimitSpecValue(specs, B10000)) // LimitDto
                .prB100Sst(converter.getLimitSpecValue(specs, B100SST)) // LimitDto
                .prB800Sst(converter.getLimitSpecValue(specs, B800SST)) // LimitDto
                .prB2500Sst(converter.getLimitSpecValue(specs, B2500SST)) // LimitDto
                .prH004500(converter.getLimitSpecValue(specs, H004500)) // LimitDto
                .prH01500(converter.getLimitSpecValue(specs, H01500)) // LimitDto
                .prH0041000(converter.getLimitSpecValue(specs, H0041000)) // LimitDto
                .prH011000(converter.getLimitSpecValue(specs, H011000)) // LimitDto
                .factorLamination(converter.getLimitSpecValue(specs, FACTOR_LAMINATION)) // LimitDto
                .agingCoefficient(converter.getLimitSpecValue(specs, AGING_FACTOR)) // LimitDto
                .resistanceCoefficient(converter.getLimitSpecValue(specs, DRAG_FACTOR)) // LimitDto
                .resistanceCoefficientFront(converter.getLimitSpecValue(specs, DRAG_FACTOR_FRONT)) // LimitDto
                .resistanceCoefficientReverse(converter.getLimitSpecValue(specs, DRAG_FACTOR_REVERSE)) // LimitDto
                .adhesion(converter.getStringSpecValue(specs, ADHESION)) // String
                .adhesionFront(converter.getStringSpecValue(specs, ADHESION_FRONT)) // String
                .adhesionReverse(converter.getStringSpecValue(specs, ADHESION_REVERSE)) // String
                .plasticityNumberBends(converter.getLimitSpecValue(specs, PLASTICITY_NUMBER_OF_BENDS)) // LimitDto
                .tlotPokr(converter.getLimitSpecValue(specs, COATING_THICKNESS)) // LimitDto
                .tlotPokrFront(converter.getLimitSpecValue(specs, COATING_THICKNESS_FRONT)) // LimitDto
                .tlotPokrReverse(converter.getLimitSpecValue(specs, COATING_THICKNESS_REVERSE)) // LimitDto
                .prAnnotation(converter.getStringSpecValue(specs, NOTE)) // String
                .build();
    }

    @Override
    public TolEvennessDtDto toTolEvennessDtDto(PdmDictionary dictionary) {

        final var specs = dictionary.getData().getSpecifications();

        return TolEvennessDtDto.builder()
                .remoteId(dictionary.getPk().getId())
                .updateTs(dictionary.getTs())
                .dt(converter.getStringSpecValue(specs, ADDITIONAL_REQUIREMENTS))
                .prStandMark(converter.getStringSpecValue(specs, PRODUCT_STANDARD))
                .prThickGood(converter.getLimitSpecValue(specs, THICKNESS_OF_ROLLED_PRODUCTS))
                .prWidthGood(converter.getLimitSpecValue(specs, WHIDTH_PRODUCT))
                .prEvenness(converter.getStringSpecValue(specs, EVENNESS))
                .prEvennessTolMax(converter.parseToDouble(
                        converter.getStringSpecValue(specs, EVENNESS_TOLERANCE)
                ))
                .prEvennessTolPerc(converter.parseToDouble(
                        converter.getStringSpecValue(specs, EVENNESS_TOLERANCE_PERCENT)
                ))
                .prAnnotation(converter.getStringSpecValue(specs, NOTE))
                .build();
    }

    @Override
    public TolThickDtDto toTolThickDtDto(PdmDictionary dictionary) {

        final var specs = dictionary.getData().getSpecifications();

        return TolThickDtDto.builder()
                .remoteId(dictionary.getPk().getId())
                .updateTs(dictionary.getTs())
                .dt(converter.getStringSpecValue(specs, ADDITIONAL_REQUIREMENTS))
                .prThickUncoat(converter.getLimitSpecValue(specs, THICKNESS_PRODUCTS))
                .prWidthGood(converter.getLimitSpecValue(specs, WHIDTH_PRODUCT))

                .rollingThickAccuracy(converter.getStringSpecValue(specs, MANUFACTURING_PRECISION_BY_THICKNESS))
                .prThickTolMin(converter.getStringSpecValue(specs, THICKNESS_TOLERANCE_MIN))
                .prThickTolMax(converter.getStringSpecValue(specs, THICKNESS_TOLERANCE_MAX))
                .longThickDif(converter.getStringSpecValue(specs, LONG_THICK_DIFF))
                .prUnevenGauge(converter.getStringSpecValue(specs, UNEVEN_GAUGE))
                .prAnnotation(converter.getStringSpecValue(specs, NOTE))
                .build();
    }

    @Override
    public TolWidthDtDto toTolWidthDtDto(PdmDictionary dictionary) {

        final var specs = dictionary.getData().getSpecifications();

        return TolWidthDtDto.builder()
                .remoteId(dictionary.getPk().getId())
                .updateTs(dictionary.getTs())
                .dt(converter.getStringSpecValue(specs, ADDITIONAL_REQUIREMENTS))
                .prProdMark(converter.getStringSpecValue(specs, STEEL_MARK))
                .prStandMark(converter.getStringSpecValue(specs, PRODUCT_STANDARD))
                .prThickGood(converter.getLimitSpecValue(specs, THICKNESS_PRODUCTS))
                .prWidthGood(converter.getLimitSpecValue(specs, WHIDTH_PRODUCT))
                .prFormSap(converter.getLimitSpecValue(specs, FORM_SAP))
                .rollingWidthAccuracy(converter.getStringSpecValue(specs, MANUFACTURING_PRECISION_BY_WIDTH))

                .prLengthGood(converter.getLimitSpecValue(specs, LENGTH_PRODUCT))
                .prCrop(converter.getStringSpecValue(specs, EDGE_CHARACTER))
                .prWidthTolMin(converter.parseToDouble(
                        converter.getStringSpecValue(specs, WIDTH_TOLERANCE_MIN)
                ))
                .prWidthTolMax(converter.parseToDouble(
                        converter.getStringSpecValue(specs, WIDTH_TOLERANCE_MAX)
                ))
                .prWidthTolPerc(converter.getStringSpecValue(specs, WHIDTH_TOLERANCE_PERCENT))
                .sickleShape(converter.getLimitSpecValue(specs, SICKLE_SHAPE))
                .burr(converter.getLimitSpecValue(specs, BURR))
                .prAnnotation(converter.getStringSpecValue(specs, NOTE))
                .build();
    }

    @Override
    public SpChemicalPropertiesNotesDto toSpChemicalPropertiesNotesDto(PdmDictionary dictionary) {
        Assert.notNull(dictionary, DICT_NOT_NULL);
        Assert.notNull(dictionary.getData(), DICT_DATA_NOT_NULL);

        final var specs = dictionary.getData().getSpecifications();

        log.debug("toSpChemicalPropertiesNotesDto, PDM DICTIONARY: {} ", dictionary);

        return SpChemicalPropertiesNotesDto.builder()
                .remoteId(dictionary.getPk().getId())
                .updateTs(dictionary.getTs())
                .tkNum(converter.getStringSpecValue(specs, TK_NUMBER_OR_VTK_VERSION_ROUTE))
                .tkRoute(converter.getStringSpecValue(specs, ROUTE_TK))
                .prAnnotation(converter.getStringSpecValue(specs, NOTE))
                .c(converter.getLimitSpecValue(specs, REQUIRED_CONTENT_C_MAX))
                .si(converter.getLimitSpecValue(specs, REQUIRED_CONTENT_SI_MAX))
                .mn(converter.getLimitSpecValue(specs, REQUIRED_CONTENT_MN_MAX))
                .s(converter.getLimitSpecValue(specs, REQUIRED_CONTENT_S_MAX))
                .p(converter.getLimitSpecValue(specs, REQUIRED_CONTENT_P_MAX))
                .al(converter.getLimitSpecValue(specs, REQUIRED_CONTENT_AL_MAX))
                .cr(converter.getLimitSpecValue(specs, REQUIRED_CONTENT_CR_MAX))
                .ni(converter.getLimitSpecValue(specs, REQUIRED_CONTENT_NI_MAX))
                .cu(converter.getLimitSpecValue(specs, REQUIRED_CONTENT_CU_MAX))
                .n(converter.getLimitSpecValue(specs, REQUIRED_CONTENT_N))
                .ti(converter.getLimitSpecValue(specs, REQUIRED_CONTENT_TI_MAX))
                .nb(converter.getLimitSpecValue(specs, REQUIRED_CONTENT_NB_MAX))
                .sn(converter.getLimitSpecValue(specs, REQUIRED_CONTENT_SN_MAX))
                .v(converter.getLimitSpecValue(specs, REQUIRED_CONTENT_V_MAX))
                .b(converter.getLimitSpecValue(specs, REQUIRED_CONTENT_B_MAX))
                .mo(converter.getLimitSpecValue(specs, REQUIRED_CONTENT_MO_MAX))
                .ca(converter.getLimitSpecValue(specs, REQUIRED_CONTENT_CA_MAX))
                .usl1(converter.getStringSpecValue(specs, REQUIRED_CONTENT_USL1))
                .znachUsl1(converter.getStringSpecValue(specs, REQUIRED_CONTENT_ZNACH_USL1))
                .usl2(converter.getStringSpecValue(specs, REQUIRED_CONTENT_USL2))
                .znachUsl2(converter.getStringSpecValue(specs, REQUIRED_CONTENT_ZNACH_USL2))
                .usl3(converter.getStringSpecValue(specs, REQUIRED_CONTENT_USL3))
                .znachUsl3(converter.getStringSpecValue(specs, REQUIRED_CONTENT_ZNACH_USL3))
                .build();
    }

    @Override
    public TolShapeSlabDto toTolShapeSlabDto(PdmDictionary dictionary) {
        Assert.notNull(dictionary, DICT_NOT_NULL);
        Assert.notNull(dictionary.getData(), DICT_DATA_NOT_NULL);

        final var specs = dictionary.getData().getSpecifications();

        log.debug("toTolShapeSlabDto, PDM DICTIONARY: {} ", dictionary);

        return TolShapeSlabDto.builder()
                .remoteId(dictionary.getPk().getId())
                .updateTs(dictionary.getTs())
                .prStandMark(converter.getStringSpecValue(specs, PRODUCT_STANDARD))
                .prior(converter.parseToInteger(converter.getStringSpecValue(specs, PRIORITY)))
                .nomTlot(converter.getLimitSpecValue(specs, NOMINAL_THICKNESS))
                .nomWidth(converter.getLimitSpecValue(specs, NOMINAL_WIDTH))
                .nomLength(converter.getLimitSpecValue(specs, LENGTH_NOMINAL))
                .prCustomer(converter.getStringSpecValue(specs, CONSUMER_NAME))
                .prCustomerCode(converter.getStringSpecValue(specs, CONSUMER_CODE))
                .dt(converter.getStringSpecValue(specs, ADDITIONAL_REQUIREMENTS))
                .vognUzkGr(converter.getLimitSpecValue(specs, CONCAVITY_NARROW_EDGE))
                .neprNesoosn(converter.getLimitSpecValue(specs, NON_RECTANGULAR_MISALIGNMENT))
                .neprSrez(converter.getLimitSpecValue(specs, NON_RECTANGULAR_OBLIQUE_CUT))
                .neprTor(converter.getLimitSpecValue(specs, NON_RECTANGULAR_OBLIQUE_BUTT))
                .neprUzkGr(converter.getLimitSpecValue(specs, NON_RECTANGULAR_NARROW_EDGE))
                .vypIzgM(converter.getLimitSpecValue(specs, SICKLE_SHAPE_FACTOR))
                .vypIzgMm(converter.getLimitSpecValue(specs, SICKLE_SHAPE))
                .progWidthMm(converter.getLimitSpecValue(specs, WIDTH_DEFLECTION))
                .progWidthM(converter.getLimitSpecValue(specs, WIDTH_DEFLECTION_FACTOR))
                .progLengthM(converter.getLimitSpecValue(specs, LENGTH_DEFLECTION_FACTOR))
                .progLengthMm(converter.getLimitSpecValue(specs, LENGTH_DEFLECTION))
                .otklLength(converter.getLimitSpecValue(specs, LENGTH_DEVIATION))
                .prLengthTol(converter.getLimitSpecValue(specs, MANUFACTURING_PRECISION_BY_LENGTH))
                .otklWidth(converter.getLimitSpecValue(specs, WIDTH_DEVIATION))
                .prWidthTol(converter.getLimitSpecValue(specs, WIDTH_TOLERANCE_PERCENT))
                .otklTlotMm(converter.getLimitSpecValue(specs, MANUFACTURING_PRECISION_BY_THICKNESS))
                .otklTlotPr(converter.getLimitSpecValue(specs, THICKNESS_DEVIATION_PERCENT))
                .prThickTol(converter.getLimitSpecValue(specs, THICKNESS_TOLERANCE))
                .otklWeight(converter.getLimitSpecValue(specs, NOMINAL_THICKNESS))
                .slabWeight(converter.getLimitSpecValue(specs, SLAB_WEIGHT))
                .vypUzkGr(converter.getLimitSpecValue(specs, CONVEX_NARROW_EDGE))
                .prWidthTolMm(converter.getLimitSpecValue(specs, MANUFACTURING_PRECISION_BY_WIDTH))
                .prAnnotation(converter.getStringSpecValue(specs, NOTE))
                .build();
    }

    @Override
    public RegisterEquivalentsDto toRegisterEquivalentsDto(PdmDictionary dictionary) {
        Assert.notNull(dictionary, DICT_NOT_NULL);
        Assert.notNull(dictionary.getData(), DICT_DATA_NOT_NULL);

        final var specs = dictionary.getData().getSpecifications();

        log.debug("toRegisterEquivalentsDto, PDM DICTIONARY: {} ", dictionary);

        return RegisterEquivalentsDto.builder()
                .remoteId(dictionary.getPk().getId())
                .updateTs(dictionary.getTs())
                .parameter(converter.getStringSpecValue(specs, REGISTER_PARAMETER))
                .formulaNumber(converter.getStringSpecValue(specs, REGISTER_FORMULA_NUMBER))
                .formula(converter.getStringSpecValue(specs, REGISTER_FORMULA))
                .crNiCu(converter.getLimitSpecValue(specs, CR_NI_CU))
                .b(converter.getLimitSpecValue(specs, MASS_FRACTION_B))
                .c(converter.getLimitSpecValue(specs, MASS_FRACTION_C))
                .prAnnotation(converter.getStringSpecValue(specs, NOTE))
                .build();
    }

    @Override
    public MinNumberSampChemDto toMinNumberSampChemDto(PdmDictionary dictionary) {
        Assert.notNull(dictionary, DICT_NOT_NULL);
        Assert.notNull(dictionary.getData(), DICT_DATA_NOT_NULL);

        final var specs = dictionary.getData().getSpecifications();

        log.debug("toMinNumberSampChemDto, PDM DICTIONARY: {} ", dictionary);

        return MinNumberSampChemDto.builder()
                .remoteId(dictionary.getPk().getId())
                .updateTs(dictionary.getTs())
                .routeShop(converter.getStringSpecValue(specs, PRODUCTION_SHOP))
                .numberSamp(converter.parseToInteger(converter.getStringSpecValue(specs, SAMPLES_NUMBER)))
                .build();
    }

    @Override
    public SchemeStrippingSlabDto toSchemeStrippingSlabDto(PdmDictionary dictionary) {
        Assert.notNull(dictionary, DICT_NOT_NULL);
        Assert.notNull(dictionary.getData(), DICT_DATA_NOT_NULL);

        final var specs = dictionary.getData().getSpecifications();

        log.debug("toSchemeStrippingSlabDto, PDM DICTIONARY: {} ", dictionary);
        return SchemeStrippingSlabDto.builder()
                .remoteId(dictionary.getPk().getId())
                .updateTs(dictionary.getTs())
                .prStandMark(converter.getStringSpecValue(specs, PRODUCT_STANDARD))
                .prSteelMark(converter.getStringSpecValue(specs, STEEL_MARK))
                .prior(converter.parseToInteger(converter.getStringSpecValue(specs, PRIORITY)))
                .dt(converter.getStringSpecValue(specs, ADDITIONAL_REQUIREMENTS))
                .routeShop(converter.getStringSpecValue(specs, PRODUCTION_SHOP))
                .workCenterNum(converter.getStringSpecValue(specs, WORK_CENTER_NUM))
                .workCenterCode(converter.getStringSpecValue(specs, WORK_CENTER_CODE))
                .customerCodeName(converter.getStringSpecValue(specs, CONSUMER_NAME))
                .prCustomer(converter.getStringSpecValue(specs, CONSUMER_CODE))
                .prThickGood(converter.getLimitSpecValue(specs, THICKNESS_OF_ROLLED_PRODUCTS))
                .macroStrAver(converter.getLimitSpecValue(specs, MACRO_MANN))
                .uglr(converter.getLimitSpecValue(specs, REQUIRED_CONTENT_C_MAX))
                .mn(converter.getLimitSpecValue(specs, REQUIRED_CONTENT_MN_MAX))
                .nb(converter.getLimitSpecValue(specs, REQUIRED_CONTENT_NB_MAX))
                .b(converter.getLimitSpecValue(specs, REQUIRED_CONTENT_B_MAX))
                .codeSlabEar(converter.getLimitSpecValue(specs, CODE_SLAB_EAR))
                .meltSlab(converter.getStringSpecValue(specs, MELT_SLAB))
                .numberSlabSeria(converter.getLimitSpecValue(specs, NUMBER_SLAB_SERIA))
                .numberSlabPlavka(converter.getLimitSpecValue(specs, NUMBER_SLAB_PLAVKA))
                .snakeWide(converter.getStringSpecValue(specs, SNAKE_WIDE))
                .perimeterWide(converter.getStringSpecValue(specs, PERIMETER_WIDE))
                .edgeWide(converter.getStringSpecValue(specs, EDGE_WIDE))
                .snakeNarrow(converter.getStringSpecValue(specs, SNAKE_NARROW))
                .perimeterNarrow(converter.getStringSpecValue(specs, PERIMETER_NARROW))
                .edgeNarrow(converter.getStringSpecValue(specs, EDGE_NARROW))
                .prAnnotation(converter.getStringSpecValue(specs, NOTE))
                .build();
    }

}
