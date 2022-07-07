CREATE TABLE public.attestation_message (
    id  bigserial NOT NULL,
    sender VARCHAR NOT NULL,
    receipt_ts TIMESTAMP WITH TIME ZONE NOT NULL,
    prime_id VARCHAR NOT NULL,
    request VARCHAR NOT NULL,
    attestation_ts TIMESTAMP WITH TIME ZONE,
    CONSTRAINT attestation_message__id__pk PRIMARY KEY (id)
);

COMMENT ON TABLE public.attestation_message IS E'Сообщения с запросом на Аттестацию Единицы Продукции, REST';
COMMENT ON COLUMN public.attestation_message.id IS E'Идентификатор сообщения';
COMMENT ON COLUMN public.attestation_message.sender IS E'Отправитель';
COMMENT ON COLUMN public.attestation_message.receipt_ts IS E'Момент приема сообщения';
COMMENT ON COLUMN public.attestation_message.prime_id IS E'Идентификатор Единицы Металла';
COMMENT ON COLUMN public.attestation_message.request IS E'Запрос на Аттестацию в едином формате';
COMMENT ON COLUMN public.attestation_message.attestation_ts IS E'Момент завершения запроса на Аттестацию';

CREATE INDEX IF NOT EXISTS attestation_message__prime_id__idx ON public.attestation_message (prime_id);
