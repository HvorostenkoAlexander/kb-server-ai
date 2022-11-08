create table ccm_message_source
(
    request_id bigint not null,
    message_source varchar not null,
    created_at TIMESTAMP DEFAULT NOW(),
    CONSTRAINT ccm_message_source__request_id__pk PRIMARY KEY (request_id)
);

COMMENT ON TABLE public.ccm_message_source IS E'Исходные сообщения запроса';
COMMENT ON COLUMN public.ccm_message_source.request_id IS E'request_id запроса';
COMMENT ON COLUMN public.ccm_message_source.message_source IS E'Строка исходного сообщения';
COMMENT ON COLUMN public.ccm_message_source.created_at IS E'Время записи';
