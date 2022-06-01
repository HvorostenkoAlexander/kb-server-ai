create table public.sap_message (
        id  bigserial not null,
        msg_key varchar null,
        msg_offset int8 not null,
        partition int4 not null,
        topic varchar not null,
        ts_timestamp timestamp with time zone not null,
        bucket varchar not null,
        path varchar not null,
        processor_version varchar not null,
        server varchar not null,
        "order" varchar null,
        state varchar not null,
        CONSTRAINT  sap_message_id_pk PRIMARY KEY (id)
);

COMMENT ON TABLE public.sap_message IS E'Сообщения из kafka: сообщения SAP';
COMMENT ON COLUMN public.sap_message.msg_key IS E'Значение ключа в сообщении от kafka';
COMMENT ON COLUMN public.sap_message.partition IS E'Значение partition сообщения от kafka';
COMMENT ON COLUMN public.sap_message.msg_offset IS E'Значение offset сообщения от kafka';
COMMENT ON COLUMN public.sap_message.topic IS E'Наименование топика';
COMMENT ON COLUMN public.sap_message.ts_timestamp IS E'Значение ts из тела сообщения kafka';

COMMENT ON COLUMN public.sap_message.bucket IS E'Местоположение файла с данными по заказу (корзина S3 хранилища)';
COMMENT ON COLUMN public.sap_message.path IS E'Имя файла, содержащего xml с данными по заказу';
COMMENT ON COLUMN public.sap_message.processor_version IS E'версия процесса, осуществляющего выгрузку из системы источника';
COMMENT ON COLUMN public.sap_message.server IS E'адрес сервера S3 хранилища MinIO';
COMMENT ON COLUMN public.sap_message."order" IS E'xml-файл заказа';
COMMENT ON COLUMN public.sap_message.state IS E'состояние обработки сообщения';

alter table public.sap_message
    add constraint sap_message_topic_partition_msg_offset_uq unique (topic, partition, msg_offset);
