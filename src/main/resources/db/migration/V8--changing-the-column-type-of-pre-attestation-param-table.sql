ALTER TABLE public.sadim_pre_attestation_param
    ALTER COLUMN ph12_sgp TYPE float8 USING ph12_sgp::double precision;