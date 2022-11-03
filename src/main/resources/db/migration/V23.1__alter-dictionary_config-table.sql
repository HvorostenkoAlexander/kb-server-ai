-- исправление ошибки в имени ограничения
ALTER TABLE IF EXISTS public.dictionary_config
RENAME CONSTRAINT result_config__id__pk TO dictionary_config__id__pk;
