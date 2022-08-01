CREATE TABLE public.result_config (
    id bigserial NOT NULL,
    topic varchar NOT NULL,
    avro varchar NOT NULL,
    condition varchar,
    is_enabled boolean NOT NULL,
    CONSTRAINT result_config__id__pk PRIMARY KEY (id)
);
COMMENT ON TABLE public.result_config IS E'Конфигурация отправки результата аттестации';
COMMENT ON COLUMN public.result_config.topic IS E'Топик';
COMMENT ON COLUMN public.result_config.avro IS E'Avro-схема';
COMMENT ON COLUMN public.result_config.condition IS E'Условия';
COMMENT ON COLUMN public.result_config.is_enabled IS E'Вкл/выкл';

INSERT INTO public.result_config (topic,avro,is_enabled)
VALUES ('000-1.l3-apcs.db.nlmk.verification-results.0','Передача результатов аттестации APCS. Version: [1]',true)
;
