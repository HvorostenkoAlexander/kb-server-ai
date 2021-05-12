create table public.pdm_message (
        id  bigserial not null,
        msg_key varchar not null,
        msg_offset int8 not null,
        partition int4 not null,
        topic varchar not null,
        dictionary json,
        ts varchar not null,
        op varchar not null,
        CONSTRAINT  pdm_message_id_pk PRIMARY KEY (id)
);

COMMENT ON TABLE public.pdm_message IS E'Сообщения из kafka: справочники PDM';
COMMENT ON COLUMN public.pdm_message.msg_key IS E'Значение ключа в сообщении от kafka';
COMMENT ON COLUMN public.pdm_message.partition IS E'Значение partition сообщения от kafka';
COMMENT ON COLUMN public.pdm_message.msg_offset IS E'Значение offset сообщения от kafka';
COMMENT ON COLUMN public.pdm_message.ts IS E'Значение ts из тела сообщения kafka';
COMMENT ON COLUMN public.pdm_message.op IS E'Значение op из тела сообщения kafka';
COMMENT ON COLUMN public.pdm_message.topic IS E'Наименование топика';
COMMENT ON COLUMN public.pdm_message.dictionary IS E'JSON из соощения kafka';
