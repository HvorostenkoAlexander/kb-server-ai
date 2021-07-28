CREATE TABLE public.dictionary_config
(
    id         bigserial NOT NULL,
    topic      varchar   NOT NULL,
    nsi_path    varchar   NOT NULL,
    is_enabled boolean   NOT NULL,
    CONSTRAINT result_config__id__pk PRIMARY KEY (id)
);
COMMENT ON TABLE public.dictionary_config IS E'Конфигурация результата аттестации';
COMMENT ON COLUMN public.dictionary_config.topic IS E'Топик';
COMMENT ON COLUMN public.dictionary_config.nsi_path IS E'Rest-point nsi-server';
COMMENT ON COLUMN public.dictionary_config.is_enabled IS E'Активность (вкл/выкл)';

create table dictionary_config_codes
(
    id  bigserial NOT NULL,
    dictionary_config_id bigint NOT NULL,
    codes                integer   NOT NULL,
    CONSTRAINT dictionary_config_codes__id__pk PRIMARY KEY (id)
);

alter table dictionary_config_codes
    add constraint FK_dictionary_config_codes__dictionary_config_id foreign key (dictionary_config_id)
        references dictionary_config;

