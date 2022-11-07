create table ccm_message_source
(
    id bigserial not null,
    request_id bigint not null,
    message_source varchar not null,
    CONSTRAINT ccm_message_source__id__pk PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS ccm_message_source_request_id__idx ON public.ccm_message_source (request_id);

COMMENT ON TABLE public.ccm_message_source IS E'Исходные сообщения запроса';
COMMENT ON COLUMN public.ccm_message_source.request_id IS E'request_id запроса';
COMMENT ON COLUMN public.ccm_message_source.message_source IS E'Строка исходного сообщения';
