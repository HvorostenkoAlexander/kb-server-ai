create table public.sadim_pre_attestation_param
(
    id         bigserial not null,
    prime_id    varchar,
    t12_min     float8,
    t12_max     float8,
    tcm_min     float8,
    tcm_max     float8,
    pbi        float8,
    prof_fact   float8,
    wedge_fact  float8,
    sqc_crit_max float8,
    ph1_sgp     float8,
    ph12_sgp    varchar,
    ph23_sgp    float8,
    estimate   int4,
    CONSTRAINT sadim_pre_attestation_param__id__pk PRIMARY KEY (id)
);

COMMENT ON TABLE public.sadim_pre_attestation_param IS E'Сведения необходимые для пред. аттестации получаемые из kafka sadim';
COMMENT ON COLUMN public.sadim_pre_attestation_param.prime_id IS E'идентификатор';
COMMENT ON COLUMN public.sadim_pre_attestation_param.t12_min IS E'Температура конца прокатки (мin)';
COMMENT ON COLUMN public.sadim_pre_attestation_param.t12_max IS E'Температура конца прокатки (мах)';
COMMENT ON COLUMN public.sadim_pre_attestation_param.tcm_min IS E'Температура смотки (мin)';
COMMENT ON COLUMN public.sadim_pre_attestation_param.tcm_max IS E'Температура смотки (мах)';
COMMENT ON COLUMN public.sadim_pre_attestation_param.pbi IS E'Процент длины полосы, на которой ширина в допуске';
COMMENT ON COLUMN public.sadim_pre_attestation_param.prof_fact IS E'Профиль';
COMMENT ON COLUMN public.sadim_pre_attestation_param.wedge_fact IS E'Клин';
COMMENT ON COLUMN public.sadim_pre_attestation_param.sqc_crit_max IS E'Наибольшая критичность дефекта на полосе';
COMMENT ON COLUMN public.sadim_pre_attestation_param.ph1_sgp IS E'Процент длины полосы, на которой толщина входит в полный допуск';
COMMENT ON COLUMN public.sadim_pre_attestation_param.ph12_sgp IS E'Процент длины полосы, на которой толщина входит в (1/2) допуска';
COMMENT ON COLUMN public.sadim_pre_attestation_param.ph23_sgp IS E'Процент длины полосы, на которой толщина входит в (2/3) допуска';
COMMENT ON COLUMN public.sadim_pre_attestation_param.estimate IS E'Оценка годности полосы';

create table pre_attestation_param_lcl_thckng
(
    pre_attestation_param_id int8 not null,
    lcl_thckng float8
);

alter table pre_attestation_param_lcl_thckng
    add constraint FKng1axrfcoc0n8hmaiubtw2xfe foreign key (pre_attestation_param_id)
        references sadim_pre_attestation_param;