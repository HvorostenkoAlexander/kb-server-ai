drop table mes_message;

create table mes_message
(
    id bigserial not null,
    msg_offset int4 not null,
    msg_key varchar not null,
    partition int4 not null,
    topic varchar not null,
    metal_unit_id uuid not null,
    data jsonb,
    ts_timestamp timestamp with time zone not null,
    CONSTRAINT mes_message__id__pk PRIMARY KEY (id)
);

COMMENT ON TABLE public.mes_message IS E'Запросы на аттестацию из MES ЦГП';
COMMENT ON COLUMN public.mes_message.id IS E'Идентификатор';
COMMENT ON COLUMN public.mes_message.msg_offset IS E'Смещение в разделе топика';
COMMENT ON COLUMN public.mes_message.msg_key IS E'Ключ сообщения Kafka';
COMMENT ON COLUMN public.mes_message.partition IS E'Раздел топика';
COMMENT ON COLUMN public.mes_message.topic IS E'Топик Kafka';
COMMENT ON COLUMN public.mes_message.metal_unit_id IS E'Guid ед. продукции';
COMMENT ON COLUMN public.mes_message.data IS E'JSON из сообщения Kafka';
COMMENT ON COLUMN public.mes_message.ts_timestamp IS E'Дата и время в словаре';

CREATE INDEX idx_mes_message_metal_unit_id ON mes_message (metal_unit_id);

create table mes_message_source
(
    request_id bigint not null,
    prime_id varchar not null,
    message_source varchar not null,
    created_at TIMESTAMP DEFAULT NOW(),
    CONSTRAINT mes_message_source__request_id__pk PRIMARY KEY (request_id)
);

COMMENT ON TABLE public.ccm_message_source IS E'Исходные сообщения запроса';
COMMENT ON COLUMN public.ccm_message_source.request_id IS E'request_id запроса';
COMMENT ON COLUMN public.ccm_message_source.prime_id IS E'prime_id запроса';
COMMENT ON COLUMN public.ccm_message_source.message_source IS E'Строка исходного сообщения';
COMMENT ON COLUMN public.ccm_message_source.created_at IS E'Время записи';