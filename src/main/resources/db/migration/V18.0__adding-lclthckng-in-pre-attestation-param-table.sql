ALTER TABLE public.sadim_pre_attestation_param
    ADD COLUMN IF NOT EXISTS lclthckng varchar;

COMMENT ON COLUMN public.sadim_pre_attestation_param.lclthckng IS E'Высота местных утолщений по ширине полосы';
