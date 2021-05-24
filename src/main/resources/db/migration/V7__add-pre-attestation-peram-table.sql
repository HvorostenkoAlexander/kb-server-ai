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