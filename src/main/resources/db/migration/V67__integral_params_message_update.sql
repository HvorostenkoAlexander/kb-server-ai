-- Добавление нового столбца и снятие ограничения --
ALTER TABLE IF EXISTS public.integral_params_message
    ADD COLUMN metal_unit_id uuid;
ALTER TABLE IF EXISTS public.integral_params_message
    ALTER COLUMN prime_id DROP NOT NULL;