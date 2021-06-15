ALTER TABLE public.pdm_message ADD COLUMN IF NOT EXISTS kb_receipt_ts timestamp with time zone;
ALTER TABLE public.pdm_message ADD COLUMN IF NOT EXISTS note varchar;

UPDATE public.pdm_message SET kb_receipt_ts='1970-01-01 00:00:00.00';