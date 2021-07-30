ALTER TABLE public.pdm_message
    ADD COLUMN IF NOT EXISTS ts_timestamp timestamp with time zone;

UPDATE public.pdm_message
SET ts_timestamp = CAST(ts AS timestamp with time zone);

ALTER TABLE public.pdm_message
    DROP COLUMN IF EXISTS ts;