create table public.sadim_pre_attestation_param
(
    id         bigserial not null,
    prime_id    varchar,
    t12_min     double precision,
    t12_max     double precision,
    tcm_min     double precision,
    tcm_max     double precision,
    pbi        double precision,
    prof_fact   double precision,
    wedge_fact  double precision,
    sqc_crit_max double precision,
    ph1_sgp     double precision,
    ph12_sgp    varchar,
    ph23_sgp    double precision,
    estimate   int4,
    lcl_thckng  double precision[],

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
COMMENT ON COLUMN public.sadim_pre_attestation_param.lcl_thckng IS E'Высота местных утолщений по ширине полосы';