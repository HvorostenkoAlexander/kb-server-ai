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

    /**
     * <a href="https://confluence.nlmk.com/pages/viewpage.action?pageId=120034208">Химический состав по стандартам</a>
     */
    @Override
    public ChemicalStdLimitDto toChemicalStdLimitDto(PdmDictionary dictionary) {
        Assert.notNull(dictionary, DICT_NOT_NULL);
        Assert.notNull(dictionary.getData(), DICT_DATA_NOT_NULL);

        final var specs = dictionary.getData().getSpecifications();

        log.debug("PDM DICTIONARY: {} ", dictionary);

        final var chemicalStdLimitDto = ChemicalStdLimitDto.builder()
                .remote_id(dictionary.getPk().getId())
                .ts(dictionary.getTs())
                .prProdMark(converter.getSpecValue(specs, STEEL_MARK))
                .prStandMark(converter.getSpecValue(specs, PRODUCT_STANDARD))
                .c(converter.stringToLimit(converter.getSpecValue(specs, MASS_FRACTION_C)))
                .si(converter.stringToLimit(converter.getSpecValue(specs, MASS_FRACTION_SI)))
                .mn(converter.stringToLimit(converter.getSpecValue(specs, MASS_FRACTION_MN)))
                .s(converter.stringToLimit(converter.getSpecValue(specs, MASS_FRACTION_S)))
                .p(converter.stringToLimit(converter.getSpecValue(specs, MASS_FRACTION_P)))
                .al(converter.stringToLimit(converter.getSpecValue(specs, MASS_FRACTION_AL)))
                .cr(converter.stringToLimit(converter.getSpecValue(specs, MASS_FRACTION_CR)))
                .ni(converter.stringToLimit(converter.getSpecValue(specs, MASS_FRACTION_NI)))
                .cu(converter.stringToLimit(converter.getSpecValue(specs, MASS_FRACTION_CU)))
                .n(converter.stringToLimit(converter.getSpecValue(specs, MASS_FRACTION_N)))
                .ti(converter.stringToLimit(converter.getSpecValue(specs, MASS_FRACTION_TI)))
                .nb(converter.stringToLimit(converter.getSpecValue(specs, MASS_FRACTION_NB)))
                .sn(converter.stringToLimit(converter.getSpecValue(specs, REQUIRED_CONTENT_SN_MAX)))
                .v(converter.stringToLimit(converter.getSpecValue(specs, MASS_FRACTION_V)))
                .b(converter.stringToLimit(converter.getSpecValue(specs, MASS_FRACTION_B)))
                .mo(converter.stringToLimit(converter.getSpecValue(specs, MASS_FRACTION_MO)))
                .ca(converter.stringToLimit(converter.getSpecValue(specs, MASS_FRACTION_CA)))
                .w(converter.stringToLimit(converter.getSpecValue(specs, MASS_FRACTION_W)))
                .as(converter.stringToLimit(converter.getSpecValue(specs, MASS_FRACTION_AS)))
                .cP(converter.stringToLimit(converter.getSpecValue(specs, CP)))
                .sP(converter.stringToLimit(converter.getSpecValue(specs, SP)))
                .crNiMoCu(converter.stringToLimit(converter.getSpecValue(specs, CR_NI_CU_MO)))
                .crMo(converter.stringToLimit(converter.getSpecValue(specs, CR_MO)))
                .alTi(converter.stringToLimit(converter.getSpecValue(specs, AL_TI)))
                .alTiVNb(converter.stringToLimit(converter.getSpecValue(specs, AL_TI_V_NB)))
                .bTiVNb(converter.stringToLimit(converter.getSpecValue(specs, B_TI_V_NB)))
                .vNbTi(converter.stringToLimit(converter.getSpecValue(specs, TI_V_NB)))
                .tiNb(converter.stringToLimit(converter.getSpecValue(specs, TI_NB)))
                .ti34n15s(converter.stringToLimit(converter.getSpecValue(specs, TI_34N_15S)))
                .prAnnotation(converter.getSpecValue(specs, NOTE))
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
                .prProdMark(converter.getSpecValue(specs, STEEL_MARK))
                .prThickUncoata(converter.stringToLimit(converter.getSpecValue(specs, THICKNESS_OF_ROLLED_PRODUCTS)))
                .category(converter.getSpecValue(specs, CATEGORY_GOST_4041))
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
                .routeShop(converter.getSpecValue(specs, ROUTE_SHOP))
                .standSort(converter.getSpecValue(specs, ASSORTMENT_STANDARD))
                .prProdMark(converter.getSpecValue(specs, STEEL_MARK))
                .prStrengthClass(converter.getSpecValue(specs, STRENGTH_CLASS))
                .thickValues(converter.getSpecValue(specs, THICKNESS_OF_ROLLED_PRODUCTS))
                .rollingThickAccuracy(converter.getSpecValue(specs, MANUFACTURING_PRECISION_BY_THICKNESS))
                .prYield(converter.stringToLimit(converter.getSpecValue(specs, YIELD_POINT)))
                .prThickGood(converter.stringToLimit(converter.getSpecValue(specs, THICKNESS_PRODUCTS)))
                .prWidthGood(converter.stringToLimit(converter.getSpecValue(specs, WHIDTH_PRODUCT)))
                .prThickTolMin(converter.getSpecValue(specs, THICKNESS_TOLERANCE_MIN))
                .prThickTolMax(converter.getSpecValue(specs, THICKNESS_TOLERANCE_MAX))
                .prThickTolMinPerc(converter.getSpecValue(specs, THICKNESS_TOLERANCE_PERCENT_MIN))
                .prThickTolMaxPerc(converter.getSpecValue(specs, THICKNESS_TOLERANCE_PERCENT_MAX))
                .prAnnotation(converter.getSpecValue(specs, NOTE))
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
                .standSort(converter.getSpecValue(specs, ASSORTMENT_STANDARD))
                .prProdMark(converter.getSpecValue(specs, STEEL_MARK))
                .prStandSteel(converter.getSpecValue(specs, MARK_STANDARD))
                .prFormSap(converter.getSpecValue(specs, FORM_SAP))
                .prWidthGood(converter.stringToLimit(converter.getSpecValue(specs, WHIDTH_PRODUCT)))
                .prThickGood(converter.stringToLimit(converter.getSpecValue(specs, THICKNESS_OF_ROLLED_PRODUCTS)))
                .prLengthGood(converter.stringToLimit(converter.getSpecValue(specs, LENGTH_PRODUCT)))
                .prCrop(converter.getSpecValue(specs, EDGE_CHARACTER))
                .rollingWidthAccuracy(converter.getSpecValue(specs, MANUFACTURING_PRECISION_BY_WIDTH))
                .prWidthTolMin(converter.parseToDouble(
                        converter.getSpecValue(specs, WIDTH_TOLERANCE_MIN)
                ))
                .prWidthTolMax(converter.parseToDouble(
                        converter.getSpecValue(specs, WIDTH_TOLERANCE_MAX)
                ))
                .prWidthTolPerc(converter.getSpecValue(specs, WHIDTH_TOLERANCE_PERCENT))
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
                .standSort(converter.getSpecValue(specs, ASSORTMENT_STANDARD))
                .prThickGood(converter.stringToLimit(converter.getSpecValue(specs, THICKNESS_OF_ROLLED_PRODUCTS)))
                .prLengthGood(converter.stringToLimit(converter.getSpecValue(specs, LENGTH_PRODUCT)))
                .prLengthTolMax(converter.parseToDouble(
                        converter.getSpecValue(specs, LENGTH_TOLERANCE_MAX)
                ))
                .rollingLengthAccuracy(converter.getSpecValue(specs, MANUFACTURING_PRECISION_BY_LENGTH))
                .prLengthTolMaxPerc(converter.getSpecValue(specs, LENGTH_TOLERANCE_PERCENT))
                .koefLengthTolMax(converter.parseToDouble(
                        converter.getSpecValue(specs, LENGTH_K)
                ))
                .prAnnotation(converter.getSpecValue(specs, NOTE))
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
                .pr_category(converter.getSpecValue(specs, CATEGORY_OF_MARK))
                .pr_prod_mark(converter.getSpecValue(specs, STEEL_MARK))
                .pr_stand_mark(converter.getSpecValue(specs, PRODUCT_STANDARD))
                .pr_thick_uncoat(converter.stringToLimit(converter.getSpecValue(specs, THICKNESS_OF_ROLLED_PRODUCTS)))
                .pr_drow(converter.getSpecValue(specs, DROW))
                .pr_scope_group(converter.getSpecValue(specs, SCOPE_GROUP))
                .pr_impact_energy(converter.stringToLimit(converter.getSpecValue(specs, IMPACT_ENERGY)))
                .pr_kv_60(converter.stringToLimit(converter.getSpecValue(specs, IMPACT_ENERGY_KV60)))
                .pr_kv_40(converter.stringToLimit(converter.getSpecValue(specs, IMPACT_ENERGY_KV40)))
                .pr_kv_20(converter.stringToLimit(converter.getSpecValue(specs, IMPACT_ENERGY_KV20)))
                .pr_kv_0(converter.stringToLimit(converter.getSpecValue(specs, IMPACT_ENERGY_KV0)))
                .pr_kv20(converter.stringToLimit(converter.getSpecValue(specs, IMPACT_ENERGY_KV_PLUS_20)))
                .pr_tensile_elongation4(converter.stringToLimit(converter.getSpecValue(specs, TENSILE_ELONGATION_4)))
                .pr_tensile_elongation5(converter.stringToLimit(converter.getSpecValue(specs, TENSILE_ELONGATION_5)))
                .pr_tensile_elongation10(converter.stringToLimit(converter.getSpecValue(specs, TENSILE_ELONGATION_10)))
                .pr_tensile_elongation50(converter.stringToLimit(converter.getSpecValue(specs, TENSILE_ELONGATION_50)))
                .pr_tensile_elongation80(converter.stringToLimit(converter.getSpecValue(specs, TENSILE_ELONGATION_80)))
                .pr_tensile_elongation200(converter.stringToLimit(converter.getSpecValue(specs, TENSILE_ELONGATION_200)))
                .pr_tensile_strength(converter.stringToLimit(converter.getSpecValue(specs, TENSILE_STRENGTH)))
                .pr_strength_class(converter.getSpecValue(specs, STRENGTH_CLASS))
                .pr_yield(converter.stringToLimit(converter.getSpecValue(specs, YIELD)))
                .pr_yield02(converter.stringToLimit(converter.getSpecValue(specs, YIELD_02)))
                .pr_relations2d(converter.getSpecValue(specs, RELATIONS_2D))
                .pr_180bend_diam(converter.getSpecValue(specs, BEND_DIAM_180))
                .pr_180bend_radius(converter.getSpecValue(specs, BEND_RADIUS_180))
                .pr_90bend_diam(converter.getSpecValue(specs, BEND_DIAM_90))
                .pr_r90_anisotropy(converter.getSpecValue(specs, ANISOTROPY_R90))
                .r0(converter.getSpecValue(specs, R0))
                .rm_rbar(converter.getSpecValue(specs, RM_BAR))
                .pr_n90_strength(converter.getSpecValue(specs, STRENGTH_N90))
                .n0(converter.getSpecValue(specs, N0))
                .pr_hardness_hr15t(converter.stringToLimit(converter.getSpecValue(specs, HARDNESS_HR15T)))
                .pr_hardness_hr30t(converter.stringToLimit(converter.getSpecValue(specs, HARDNESS_HR30T)))
                .pr_hardness_hrb(converter.stringToLimit(converter.getSpecValue(specs, HARDNESS_HRB)))
                .pr_hardness_hb(converter.stringToLimit(converter.getSpecValue(specs, HARDNESS_HB)))
                .pr_hardness_hrf(converter.stringToLimit(converter.getSpecValue(specs, HARDNESS_HRF)))
                .pr_hardness_hv5(converter.stringToLimit(converter.getSpecValue(specs, HARDNESS_HV5)))
                .pr_asperity(converter.getSpecValue(specs, ASPERITY))
                .pr_peak_number(converter.getSpecValue(specs, PEAK_NUMBER))
                .pr_bh2_effect(converter.getSpecValue(specs, BH2_EFFECT))
                .pr_kcu_0(converter.stringToLimit(converter.getSpecValue(specs, KCU0)))
                .pr_kcv_0(converter.stringToLimit(converter.getSpecValue(specs, KCV0)))
                .pr_kcv_10(converter.stringToLimit(converter.getSpecValue(specs, KCV10)))
                .pr_kcv10(converter.stringToLimit(converter.getSpecValue(specs, KCV_PLUS_10)))
                .pr_kcv_15(converter.stringToLimit(converter.getSpecValue(specs, KCV15)))
                .pr_kcu_20(converter.stringToLimit(converter.getSpecValue(specs, KCU20)))
                .pr_kcu20(converter.stringToLimit(converter.getSpecValue(specs, KCU_PLUS_20)))
                .pr_kcv_20(converter.stringToLimit(converter.getSpecValue(specs, KCV20)))
                .pr_kcv20(converter.stringToLimit(converter.getSpecValue(specs, KCV_PLUS_20)))
                .pr_kcu_30(converter.stringToLimit(converter.getSpecValue(specs, KCU30)))
                .pr_kcv_35(converter.stringToLimit(converter.getSpecValue(specs, KCV35)))
                .pr_kcu_40(converter.stringToLimit(converter.getSpecValue(specs, KCU40)))
                .pr_kcv_40(converter.stringToLimit(converter.getSpecValue(specs, KCV40)))
                .pr_kcu_50(converter.stringToLimit(converter.getSpecValue(specs, KCU50)))
                .pr_kcu_60(converter.stringToLimit(converter.getSpecValue(specs, KCU60)))
                .pr_kcu_70(converter.stringToLimit(converter.getSpecValue(specs, KCU70)))
                .pr_kcu_mech_old(converter.stringToLimit(converter.getSpecValue(specs, IMPACT_STRENGTH_AGING)))
                .pr_height(converter.getSpecValue(specs, DEPTH_HOLE))
                .pr_coil_tilt(converter.getSpecValue(specs, ROLL_CURVATURE))
                .pr_p1_50(converter.getSpecValue(specs, P150))
                .pr_p1_7_50(converter.getSpecValue(specs, P1750))
                .pr_p1_5_50(converter.getSpecValue(specs, P1550))
                .pr_p1_0_50(converter.getSpecValue(specs, P1050))
                .pr_p1_5_200(converter.getSpecValue(specs, P15200))
                .pr_p1_5_400(converter.getSpecValue(specs, P15400))
                .pr_p1_0_400(converter.getSpecValue(specs, P10400))
                .pr_p1_0_700(converter.getSpecValue(specs, P10700))
                .pr_p1_0_1000(converter.getSpecValue(specs, P101000))
                .pr_p004_500(converter.getSpecValue(specs, P004500))
                .pr_p01_500(converter.getSpecValue(specs, P01500))
                .pr_p004_1000(converter.getSpecValue(specs, P0041000))
                .pr_p01_1000(converter.getSpecValue(specs, P011000))
                .pr_p1_5_50_sst(converter.getSpecValue(specs, P1550SST))
                .pr_p1_7_50_sst(converter.getSpecValue(specs, P1750SST))
                .pr_p1_5_60(converter.getSpecValue(specs, P1560))
                .pr_p1_7_60(converter.getSpecValue(specs, P1760))
                .pr_p1_5_60_sst(converter.getSpecValue(specs, P1560SST))
                .pr_p1_7_60_sst(converter.getSpecValue(specs, P1760SST))
                .pr_p1_5_50_lb(converter.getSpecValue(specs, P1550LB))
                .pr_p1_7_50_lb(converter.getSpecValue(specs, P1750LB))
                .pr_p1_7_60_lb(converter.getSpecValue(specs, P1760LB))
                .pr_p1_5_50_sst_lb(converter.getSpecValue(specs, P1550SST_LB))
                .pr_p1_7_50_sst_lb(converter.getSpecValue(specs, P1750SST_LB))
                .pr_p1_7_60_sst_lb(converter.getSpecValue(specs, P1760SST_LB))
                .pr_p1_5_60_lb(converter.getSpecValue(specs, P1560LB))
                .pr_p1_5_60_sst_lb(converter.getSpecValue(specs, P1560SST_LB))
                .pr_h_004_500(converter.getSpecValue(specs, H004500))
                .pr_h_01_500(converter.getSpecValue(specs, H01500))
                .pr_h_004_1000(converter.getSpecValue(specs, H0041000))
                .pr_h_01_1000(converter.getSpecValue(specs, H011000))
                .pr_b_40(converter.getSpecValue(specs, B40))
                .pr_b_80(converter.getSpecValue(specs, B80))
                .pr_b_100(converter.getSpecValue(specs, B100))
                .pr_b_200(converter.getSpecValue(specs, B200))
                .pr_b_800(converter.getSpecValue(specs, B800))
                .pr_b_1000(converter.getSpecValue(specs, B1000))
                .pr_b_2500(converter.getSpecValue(specs, B2500))
                .pr_b_5000(converter.getSpecValue(specs, B5000))
                .pr_b_10000(converter.getSpecValue(specs, B10000))
                .pr_b_100_sst(converter.getSpecValue(specs, B100SST))
                .pr_b_800_sst(converter.getSpecValue(specs, B800SST))
                .pr_b_2500_sst(converter.getSpecValue(specs, B2500SST))
                .pr_coercive_field(converter.getSpecValue(specs, COERCIVE_FIELD))
                .factor_lamination(converter.getSpecValue(specs, FACTOR_LAMINATION))
                .macro_mann(converter.getSpecValue(specs, MACRO_MANN))
                .vnutr_tr(converter.getSpecValue(specs, INTERNAL_CRACKS))
                .vkl_obl(converter.getSpecValue(specs, CLOUD_INCLUSIONS))
                .osev_segr(converter.getSpecValue(specs, AXIAL_SEGREGATION))
                .vkl_toch(converter.getSpecValue(specs, POINT_INCLUSIONS))
                .uzkgr_tr(converter.getSpecValue(specs, EDGE_CRACKS))
                .uglov_tr(converter.getSpecValue(specs, ANGLE_CRACKS))
                .pr_bake_hardening(converter.getSpecValue(specs, BAKE_HARDENING))
                .pr_annotation(converter.getSpecValue(specs, NOTE))
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
                .standSort(converter.getSpecValue(specs, ASSORTMENT_STANDARD))
                .prWidthGood(converter.stringToLimit(converter.getSpecValue(specs, WHIDTH_PRODUCT)))
                .prThickGood(converter.stringToLimit(converter.getSpecValue(specs, THICKNESS_OF_ROLLED_PRODUCTS)))
                .prEvenness(converter.getSpecValue(specs, EVENNESS))
                .prYield(converter.stringToLimit(converter.getSpecValue(specs, YIELD_POINT)))
                .prEvennessTolMax(converter.parseToDouble(
                        converter.getSpecValue(specs, EVENNESS_TOLERANCE)
                ))
                .prEvennessTolPerc(converter.parseToDouble(
                        converter.getSpecValue(specs, EVENNESS_TOLERANCE_PERCENT)
                ))
                .prTensileStrength(converter.stringToLimit(converter.getSpecValue(specs, TENSILE_STRENGTH)))
                .prAnnotation(converter.getSpecValue(specs, NOTE))
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
                .tkNum(converter.getSpecValue(specs, TK_NUMBER_OR_VTK_VERSION_ROUTE))
                .tkPurp(converter.getSpecValue(specs, TARGET))
                .dateStart(this.getDocDate(specs, START_DATE))
                .dateFinish(this.getDocDate(specs, FINISH_DATE))
                .build();
    }

    private String getDocDate(List<Spec> specs, SpecCode specCode) {
        String docDate = converter.getSpecValue(specs, specCode);

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
                .ceqNum(converter.getSpecValue(specs, CARBON_EQUIVALENT_FORMULA_NUMBER))
                .ceqFormula(converter.getSpecValue(specs, CARBON_EQUIVALENT_FORMULA))
                .prAnnotation(converter.getSpecValue(specs, NOTE))
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
                .tk_num(converter.getSpecValue(specs, TK_NUMBER_OR_VTK_VERSION_ROUTE))
                ._prior(converter.parseToInteger(converter.getSpecValue(specs, PRIORITY)))
                .tk_route(converter.getSpecValue(specs, ROUTE_TK))
                .pr_category(converter.getSpecValue(specs, CATEGORY_OF_MARK))
                .pr_prod_mark(converter.getSpecValue(specs, STEEL_MARK))
                .pr_stand_mark(converter.getSpecValue(specs, PRODUCT_STANDARD))
                .pr_steel_mark(converter.getSpecValue(specs, MELTING_MARK))
                .pr_stand_steel(converter.getSpecValue(specs, MELTING_MARK_STANDART))
                .pr_thick_uncoat(converter.stringToLimit(converter.getSpecValue(specs, THICKNESS_OF_ROLLED_PRODUCTS)))
                .pr_drow(converter.getSpecValue(specs, DROW))
                .pr_scope_group(converter.getSpecValue(specs, SCOPE_GROUP))
                .pr_kv_60(converter.stringToLimit(converter.getSpecValue(specs, IMPACT_ENERGY_KV60)))
                .pr_kv_40(converter.stringToLimit(converter.getSpecValue(specs, IMPACT_ENERGY_KV40)))
                .pr_kv_20(converter.stringToLimit(converter.getSpecValue(specs, IMPACT_ENERGY_KV20)))
                .pr_kv_0(converter.stringToLimit(converter.getSpecValue(specs, IMPACT_ENERGY_KV0)))
                .pr_kv20(converter.stringToLimit(converter.getSpecValue(specs, IMPACT_ENERGY_KV_PLUS_20)))
                .pr_tensile_elongation4(converter.stringToLimit(converter.getSpecValue(specs, TENSILE_ELONGATION_4)))
                .pr_tensile_elongation5(converter.stringToLimit(converter.getSpecValue(specs, TENSILE_ELONGATION_5)))
                .pr_tensile_elongation10(converter.stringToLimit(converter.getSpecValue(specs, TENSILE_ELONGATION_10)))
                .pr_tensile_elongation50(converter.stringToLimit(converter.getSpecValue(specs, TENSILE_ELONGATION_50)))
                .pr_tensile_elongation80(converter.stringToLimit(converter.getSpecValue(specs, TENSILE_ELONGATION_80)))
                .pr_tensile_elongation200(converter.stringToLimit(converter.getSpecValue(specs, TENSILE_ELONGATION_200)))
                .pr_tensile_strength(converter.stringToLimit(converter.getSpecValue(specs, TENSILE_STRENGTH)))
                .pr_yield(converter.stringToLimit(converter.getSpecValue(specs, YIELD)))
                .pr_impact_energy(converter.stringToLimit(converter.getSpecValue(specs, IMPACT_ENERGY)))
                .pr_strength_class(converter.getSpecValue(specs, STRENGTH_CLASS))
                .pr_yield02(converter.getSpecValue(specs, YIELD_02))
                .pr_relations2d(converter.getSpecValue(specs, RELATIONS_2D))
                .pr_180bend_diam(converter.getSpecValue(specs, BEND_DIAM_180))
                .pr_180bend_radius(converter.getSpecValue(specs, BEND_RADIUS_180))
                .pr_90bend_diam(converter.getSpecValue(specs, BEND_DIAM_90))
                .pr_r90_anisotropy(converter.getSpecValue(specs, ANISOTROPY_R90))
                .r0(converter.getSpecValue(specs, R0))
                .rm_rbar(converter.getSpecValue(specs, RM_BAR))
                .pr_n90_strength(converter.getSpecValue(specs, STRENGTH_N90))
                .n0(converter.getSpecValue(specs, N0))
                .pr_hardness_hr15t(converter.getSpecValue(specs, HARDNESS_HR15T))
                .pr_hardness_hr30t(converter.getSpecValue(specs, HARDNESS_HR30T))

                .pr_hardness_hrb(converter.stringToLimit(converter.getSpecValue(specs, HARDNESS_HRB)))
                .pr_hardness_hb(converter.stringToLimit(converter.getSpecValue(specs, HARDNESS_HB)))

                .pr_hardness_hrf(converter.getSpecValue(specs, HARDNESS_HRF))
                .pr_hardness_hv5(converter.getSpecValue(specs, HARDNESS_HV5))
                .pr_asperity(converter.getSpecValue(specs, ASPERITY))
                .pr_peak_number(converter.getSpecValue(specs, PEAK_NUMBER))
                .pr_bh2_effect(converter.getSpecValue(specs, BH2_EFFECT))

                .pr_kcu_0(converter.stringToLimit(converter.getSpecValue(specs, KCU0)))
                .pr_kcv_0(converter.stringToLimit(converter.getSpecValue(specs, KCV0)))
                .pr_kcv_10(converter.stringToLimit(converter.getSpecValue(specs, KCV10)))
                .pr_kcv10(converter.stringToLimit(converter.getSpecValue(specs, KCV_PLUS_10)))
                .pr_kcv_15(converter.stringToLimit(converter.getSpecValue(specs, KCV15)))
                .pr_kcu_20(converter.stringToLimit(converter.getSpecValue(specs, KCU20)))
                .pr_kcu20(converter.stringToLimit(converter.getSpecValue(specs, KCU_PLUS_20)))
                .pr_kcv_20(converter.stringToLimit(converter.getSpecValue(specs, KCV20)))
                .pr_kcv20(converter.stringToLimit(converter.getSpecValue(specs, KCV_PLUS_20)))
                .pr_kcu_30(converter.stringToLimit(converter.getSpecValue(specs, KCU30)))
                .pr_kcv_35(converter.stringToLimit(converter.getSpecValue(specs, KCV35)))
                .pr_kcu_40(converter.stringToLimit(converter.getSpecValue(specs, KCU40)))
                .pr_kcv_40(converter.stringToLimit(converter.getSpecValue(specs, KCV40)))
                .pr_kcu_50(converter.stringToLimit(converter.getSpecValue(specs, KCU50)))
                .pr_kcu_60(converter.stringToLimit(converter.getSpecValue(specs, KCU60)))
                .pr_kcu_70(converter.stringToLimit(converter.getSpecValue(specs, KCU70)))
                .pr_kcu_mech_old(converter.stringToLimit(converter.getSpecValue(specs, IMPACT_STRENGTH_AGING)))

                .pr_height(converter.getSpecValue(specs, DEPTH_HOLE))
                .pr_coil_tilt(converter.getSpecValue(specs, ROLL_CURVATURE))
                .pr_p1_50(converter.getSpecValue(specs, P150))
                .pr_p1_7_50(converter.getSpecValue(specs, P1750))
                .pr_p1_5_50(converter.getSpecValue(specs, P1550))
                .pr_p1_0_50(converter.getSpecValue(specs, P1050))
                .pr_p1_5_200(converter.getSpecValue(specs, P15200))
                .pr_p1_5_400(converter.getSpecValue(specs, P15400))
                .pr_p1_0_400(converter.getSpecValue(specs, P10400))
                .pr_p1_0_700(converter.getSpecValue(specs, P10700))
                .pr_p1_0_1000(converter.getSpecValue(specs, P101000))
                .pr_p004_500(converter.getSpecValue(specs, P004500))
                .pr_p01_500(converter.getSpecValue(specs, P01500))
                .pr_p004_1000(converter.getSpecValue(specs, P0041000))
                .pr_p01_1000(converter.getSpecValue(specs, P011000))
                .pr_p1_5_50_sst(converter.getSpecValue(specs, P1550SST))
                .pr_p1_7_50_sst(converter.getSpecValue(specs, P1750SST))
                .pr_p1_5_60(converter.getSpecValue(specs, P1560))
                .pr_p1_7_60(converter.getSpecValue(specs, P1760))
                .pr_p1_5_60_sst(converter.getSpecValue(specs, P1560SST))
                .pr_p1_7_60_sst(converter.getSpecValue(specs, P1760SST))
                .pr_p1_5_50_lb(converter.getSpecValue(specs, P1550LB))
                .pr_p1_7_50_lb(converter.getSpecValue(specs, P1750LB))
                .pr_p1_7_60_lb(converter.getSpecValue(specs, P1760LB))
                .pr_p1_5_50_sst_lb(converter.getSpecValue(specs, P1550SST_LB))
                .pr_p1_7_50_sst_lb(converter.getSpecValue(specs, P1750SST_LB))
                .pr_p1_7_60_sst_lb(converter.getSpecValue(specs, P1760SST_LB))
                .pr_p1_5_60_lb(converter.getSpecValue(specs, P1560LB))
                .pr_p1_5_60_sst_lb(converter.getSpecValue(specs, P1560SST_LB))
                .pr_h_004_500(converter.getSpecValue(specs, H004500))
                .pr_h_01_500(converter.getSpecValue(specs, H01500))
                .pr_h_004_1000(converter.getSpecValue(specs, H0041000))
                .pr_h_01_1000(converter.getSpecValue(specs, H011000))
                .pr_b_40(converter.getSpecValue(specs, B40))
                .pr_b_80(converter.getSpecValue(specs, B80))
                .pr_b_100(converter.getSpecValue(specs, B100))
                .pr_b_200(converter.getSpecValue(specs, B200))
                .pr_b_800(converter.getSpecValue(specs, B800))
                .pr_b_1000(converter.getSpecValue(specs, B1000))
                .pr_b_2500(converter.getSpecValue(specs, B2500))
                .pr_b_5000(converter.getSpecValue(specs, B5000))
                .pr_b_10000(converter.getSpecValue(specs, B10000))
                .pr_b_100_sst(converter.getSpecValue(specs, B100SST))
                .pr_b_800_sst(converter.getSpecValue(specs, B800SST))
                .pr_b_2500_sst(converter.getSpecValue(specs, B2500SST))
                .pr_coercive_field(converter.getSpecValue(specs, COERCIVE_FIELD))
                .factor_lamination(converter.getSpecValue(specs, FACTOR_LAMINATION))
                .macro_mann(converter.getSpecValue(specs, MACRO_MANN))
                .vnutr_tr(converter.getSpecValue(specs, INTERNAL_CRACKS))
                .vkl_obl(converter.getSpecValue(specs, CLOUD_INCLUSIONS))
                .osev_segr(converter.getSpecValue(specs, AXIAL_SEGREGATION))
                .vkl_toch(converter.getSpecValue(specs, POINT_INCLUSIONS))
                .uzkgr_tr(converter.getSpecValue(specs, EDGE_CRACKS))
                .uglov_tr(converter.getSpecValue(specs, ANGLE_CRACKS))
                .pr_bake_hardening(converter.getSpecValue(specs, BAKE_HARDENING))
                .pr_annotation(converter.getSpecValue(specs, NOTE))
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
                .prior(converter.parseToInteger(converter.getSpecValue(specs, PRIORITY)))
                .tkNum(converter.getSpecValue(specs, TK_NUMBER_OR_VTK_VERSION_ROUTE))
                .tkRoute(converter.getSpecValue(specs, ROUTE_TK))
                .prSteelMark(converter.getSpecValue(specs, MELTING_MARK))
                .prStandSteel(converter.getSpecValue(specs, MELTING_MARK_STANDART))
                .prProdMark(converter.getSpecValue(specs, STEEL_MARK))
                .prStandMark(converter.getSpecValue(specs, PRODUCT_STANDARD))

                .prThickUncoat(converter.stringToLimit(converter.getSpecValue(specs, THICKNESS_OF_ROLLED_PRODUCTS)))

                .prDrow(converter.getSpecValue(specs, DROW))

                .c(converter.stringToLimit(converter.getSpecValue(specs, MASS_FRACTION_C)))
                .si(converter.stringToLimit(converter.getSpecValue(specs, MASS_FRACTION_SI)))
                .mn(converter.stringToLimit(converter.getSpecValue(specs, MASS_FRACTION_MN)))
                .s(converter.stringToLimit(converter.getSpecValue(specs, MASS_FRACTION_S)))
                .p(converter.stringToLimit(converter.getSpecValue(specs, MASS_FRACTION_P)))
                .al(converter.stringToLimit(converter.getSpecValue(specs, MASS_FRACTION_AL)))
                .cr(converter.stringToLimit(converter.getSpecValue(specs, MASS_FRACTION_CR)))
                .ni(converter.stringToLimit(converter.getSpecValue(specs, MASS_FRACTION_NI)))
                .cu(converter.stringToLimit(converter.getSpecValue(specs, MASS_FRACTION_CU)))
                .ti(converter.stringToLimit(converter.getSpecValue(specs, MASS_FRACTION_TI)))
                .n(converter.stringToLimit(converter.getSpecValue(specs, MASS_FRACTION_N)))
                .v(converter.stringToLimit(converter.getSpecValue(specs, MASS_FRACTION_V)))
                .nb(converter.stringToLimit(converter.getSpecValue(specs, MASS_FRACTION_NB)))
                .sn(converter.stringToLimit(converter.getSpecValue(specs, MASS_FRACTION_SN)))
                .mo(converter.stringToLimit(converter.getSpecValue(specs, MASS_FRACTION_MO)))
                .b(converter.stringToLimit(converter.getSpecValue(specs, MASS_FRACTION_B)))
                .as(converter.stringToLimit(converter.getSpecValue(specs, MASS_FRACTION_AS)))
                .ca(converter.stringToLimit(converter.getSpecValue(specs, MASS_FRACTION_CA)))
                .h(converter.stringToLimit(converter.getSpecValue(specs, MASS_FRACTION_H)))
                .sb(converter.stringToLimit(converter.getSpecValue(specs, MASS_FRACTION_SB)))
                .pb(converter.stringToLimit(converter.getSpecValue(specs, MASS_FRACTION_PB)))
                .siP(converter.stringToLimit(converter.getSpecValue(specs, SI_P)))
                .si25P(converter.stringToLimit(converter.getSpecValue(specs, SI_25P)))
                .crMo(converter.stringToLimit(converter.getSpecValue(specs, CR_MO)))
                .crNiCu(converter.stringToLimit(converter.getSpecValue(specs, CR_NI_CU)))
                .crNiMoCu(converter.stringToLimit(converter.getSpecValue(specs, CR_NI_CU_MO)))
                .cuNiCrMoV(converter.stringToLimit(converter.getSpecValue(specs, CU_NI_CR_MO_V)))
                .crNiCuMoSn(converter.stringToLimit(converter.getSpecValue(specs, CR_NI_CU_MO_SN)))
                .mnSi(converter.stringToLimit(converter.getSpecValue(specs, MN_SI)))
                .mnS(converter.stringToLimit(converter.getSpecValue(specs, MN_S)))
                .vNbTi(converter.stringToLimit(converter.getSpecValue(specs, TI_V_NB)))
                .niVTi(converter.stringToLimit(converter.getSpecValue(specs, NI_V_TI)))
                .p25SiAl(converter.stringToLimit(converter.getSpecValue(specs, P25_SI_AL)))
                .ti15S342N4C(converter.stringToLimit(converter.getSpecValue(specs, TI_15S_342N_4C)))
                .ti15S343N4C(converter.stringToLimit(converter.getSpecValue(specs, TI_15S_343N_4C)))
                .crNiCuSn(converter.stringToLimit(converter.getSpecValue(specs, CR_NI_CU_SN)))
                .prGroupNorm(converter.getSpecValue(specs, NORM_GROUP))
                .ceqNum(converter.getSpecValue(specs, CARBON_EQUIVALENT_FORMULA_NUMBER))
                .ceq(converter.stringToLimit(converter.getSpecValue(specs, CARBON_EQUIVALENT)))
                .tkPoint(converter.getSpecValue(specs, TK_POINT))
                .prAnnotation(converter.getSpecValue(specs, NOTE))
                .bi(converter.stringToLimit(converter.getSpecValue(specs, MASS_FRACTION_BI)))
                .co(converter.stringToLimit(converter.getSpecValue(specs, MASS_FRACTION_CO)))
                .fe(converter.stringToLimit(converter.getSpecValue(specs, MASS_FRACTION_FE)))
                .mg(converter.stringToLimit(converter.getSpecValue(specs, MASS_FRACTION_MG)))
                .o(converter.stringToLimit(converter.getSpecValue(specs, MASS_FRACTION_O)))
                .w(converter.stringToLimit(converter.getSpecValue(specs, MASS_FRACTION_W)))
                .zn(converter.stringToLimit(converter.getSpecValue(specs, MASS_FRACTION_ZN)))
                .zr(converter.stringToLimit(converter.getSpecValue(specs, MASS_FRACTION_ZR)))
                .crCuMo(converter.stringToLimit(converter.getSpecValue(specs, CR_CU_MO)))
                .cuCrNiMoTi(converter.stringToLimit(converter.getSpecValue(specs, CU_CR_NI_MO_TI)))
                .caS(converter.stringToLimit(converter.getSpecValue(specs, CA_S)))
                .alN(converter.stringToLimit(converter.getSpecValue(specs, AL_N)))
                .nbV(converter.stringToLimit(converter.getSpecValue(specs, NB_V)))
                .nAl(converter.stringToLimit(converter.getSpecValue(specs, N_AL)))
                .tiN(converter.stringToLimit(converter.getSpecValue(specs, TI_N)))
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
                .tkNum(converter.getSpecValue(specs, TK_NUMBER_OR_VTK_VERSION_ROUTE))
                .tkRoute(converter.getSpecValue(specs, ROUTE_TK))
                .prProdMark(converter.getSpecValue(specs, STEEL_MARK))
                .prStandMark(converter.getSpecValue(specs, PRODUCT_STANDARD))
                .prThickUncoat(converter.stringToLimit(converter.getSpecValue(specs, THICKNESS_OF_ROLLED_PRODUCTS)))
                .category(converter.getSpecValue(specs, CATEGORY_GOST_4041))
                .attestStand(converter.getSpecValue(specs, MICROCTRUCTURE_STANDART))
                .ferriteGrain(converter.stringToLimit(converter.getSpecValue(specs, FERRIT_GRAIN)))
                .unevenessFerriteGrain(converter.getSpecValue(specs, UNEVENNESS_OF_FERRIT_GRAIN))
                .structFreeCementite(converter.stringToLimit(converter.getSpecValue(specs, CEMENTITE)))
                .unmetallInclusionsOxides(converter.stringToLimit(converter.getSpecValue(specs, OXIDES)))
                .unmetallInclusionsSulfides(converter.stringToLimit(converter.getSpecValue(specs, SULPHIDES)))
                .unmetallInclusionsNitrides(converter.stringToLimit(converter.getSpecValue(specs, NITRIDES)))
                .unmetallInclusionsSilicates(converter.stringToLimit(converter.getSpecValue(specs, SILICATES)))
                .unmetallInclusionsOxidesB(converter.stringToLimit(converter.getSpecValue(specs, OXIDES_TYPE_B)))
                .unmetallInclusionsSulfidesA(converter.stringToLimit(converter.getSpecValue(specs, SULPHIDES_TYPE_A)))
                .unmetallInclusionsSilicatesC(converter.stringToLimit(converter.getSpecValue(specs, SILICATES_TYPE_C)))
                .unmetallInclusionsGlobOxidesD(converter.stringToLimit(converter.getSpecValue(specs, OXIDES_TYPE_D)))
                .unmetallInclusions(converter.stringToLimit(converter.getSpecValue(specs, NON_METALLIC_INCLUSIONS_ISO_4967_2013)))
                .polFerPerStruct(converter.stringToLimit(converter.getSpecValue(specs, BANDING_OF_FERRIE_PEARLITE_STRUCTURE)))
                .depthDecarbLayer(converter.stringToLimit(converter.getSpecValue(specs, DEPTH_WITHOUT_C_LAYER)))
                .perliteGrain(converter.stringToLimit(converter.getSpecValue(specs, PERLITE_GRAIN)))
                .prAnnotation(converter.getSpecValue(specs, NOTE))
                .build();
    }

    @Override
    public ChemicalEquivalentStdDto toChemicalEquivalentStdDto(PdmDictionary dictionary) {
        Assert.notNull(dictionary, DICT_NOT_NULL);
        Assert.notNull(dictionary.getData(), DICT_DATA_NOT_NULL);

        final var specs = dictionary.getData().getSpecifications();

        log.debug("--- toChemicalEquivalentStdDto PDM DICTIONARY: {} ", dictionary);

        return ChemicalEquivalentStdDto.builder()
                .remote_id(dictionary.getPk().getId())
                .ts(dictionary.getTs())
                .prProdMark(converter.getSpecValue(specs, STEEL_MARK))
                .prStandMark(converter.getSpecValue(specs, PRODUCT_STANDARD))
                .prThickUncoat(converter.stringToLimit(
                        converter.getSpecValue(specs, THICKNESS_OF_ROLLED_PRODUCTS))
                )
                .prStrengthClass(converter.getSpecValue(specs, STRENGTH_CLASS))
                .ceqNum(converter.getSpecValue(specs, CARBON_EQUIVALENT_FORMULA_NUMBER))
                .ceq(converter.stringToLimit(
                        converter.getSpecValue(specs, CARBON_EQUIVALENT))
                )
                .pcmNum(converter.getSpecValue(specs, CRACK_RESISTANCE_COEFFICIENT_FORMULA_NUMBER))
                .pcm(converter.stringToLimit(
                        converter.getSpecValue(specs, CRACK_RESISTANCE_COEFFICIENT))
                )
                .prAnnotation(converter.getSpecValue(specs, NOTE))
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
                .tkNum(converter.getSpecValue(specs, TK_NUMBER_OR_VTK_VERSION_ROUTE))
                .tkNumSap(converter.getSpecValue(specs, TK_SAP_NUMBER))
                .prAnnotation(converter.getSpecValue(specs, NOTE))
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
                .rpNumSap(converter.getSpecValue(specs, RP_SAP_NUMBER))
                .tkNum(converter.getSpecValue(specs, RP_NUMBER_VERSION_ROUTE))
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
                .pcmNum(converter.getSpecValue(specs, CRACK_RESISTANCE_COEFFICIENT_FORMULA_NUMBER))
                .pcmFormula(converter.getSpecValue(specs, CRACK_RESISTANCE_FORMULA))
                .prAnnotation(converter.getSpecValue(specs, NOTE))
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
                .prStandMark(converter.getSpecValue(specs, PRODUCT_STANDARD))
                .useStandMark(converter.getSpecValue(specs, PRODUCT_STANDARD_ADDITIONAL))
                .standTolThick(converter.getSpecValue(specs, THICKNESS_TOLERANCE_STANDART))
                .standTolWidth(converter.getSpecValue(specs, WIDTH_TOLERANCE_STANDART))
                .standTolLength(converter.getSpecValue(specs, LENGTH_TOLERANCE_STANDART))
                .standTolEvenness(converter.getSpecValue(specs, EVENNESS_TOLERANCE_STANDART))
                .prAnnotation(converter.getSpecValue(specs, NOTE))
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
                //fixme
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
                //fixme
                .build();
    }

}
