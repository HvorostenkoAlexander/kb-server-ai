create table mdm_message
(
    id bigserial not null,
    msg_offset bigint not null,
    partition int4 not null,
    topic varchar not null,
    data json,
    note varchar,
    ts_timestamp timestamp with time zone not null,
    CONSTRAINT mdm_message__id__pk PRIMARY KEY (id)
);

COMMENT ON TABLE public.mdm_message IS E'Исходные сообщения из MDM';
COMMENT ON COLUMN public.mdm_message.id IS E'Идентификатор';
COMMENT ON COLUMN public.mdm_message.msg_offset IS E'Смещение в разделе топика';
COMMENT ON COLUMN public.mdm_message.partition IS E'Раздел топика';
COMMENT ON COLUMN public.mdm_message.topic IS E'Топик Kafka';
COMMENT ON COLUMN public.mdm_message.data IS E'JSON из сообщения Kafka';
COMMENT ON COLUMN public.mdm_message.note IS E'	Комментарий';
COMMENT ON COLUMN public.mdm_message.ts_timestamp IS E'	Дата и время в словаре';