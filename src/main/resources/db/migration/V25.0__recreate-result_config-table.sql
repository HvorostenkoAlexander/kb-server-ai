DROP TABLE IF EXISTS public.result_config;

CREATE TABLE public.result_config (
    id serial NOT NULL,
    topic varchar NOT NULL,
    avro_name varchar NOT NULL,
    condition varchar,
    enabled boolean NOT NULL,
    CONSTRAINT result_config__id__pk PRIMARY KEY (id)
);
COMMENT ON TABLE public.result_config IS E'Конфигурация отправки результатов Аттестации';
COMMENT ON COLUMN public.result_config.topic IS E'Топик';
COMMENT ON COLUMN public.result_config.avro_name IS E'Наименование головного объекта Avro-схемы';
COMMENT ON COLUMN public.result_config.condition IS E'Условия';
COMMENT ON COLUMN public.result_config.enabled IS E'Вкл/выкл';
