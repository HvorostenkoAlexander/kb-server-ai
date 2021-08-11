ALTER TABLE public.sadim_pre_attestation_param
    ADD COLUMN IF NOT EXISTS lot_no int4;

ALTER TABLE public.sadim_pre_attestation_param
    ADD COLUMN IF NOT EXISTS melt_no int4;

COMMENT ON COLUMN sadim_pre_attestation_param.lot_no IS E'Номер горячекатаной партии';
COMMENT ON COLUMN sadim_pre_attestation_param.melt_no IS E'Номер плавки';
