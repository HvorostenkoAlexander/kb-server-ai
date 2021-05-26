create table ccm_message
(
    id            bigserial    not null,
    kafka_ts      timestamp    not null,
    kb_receipt_ts timestamp    not null,
    msg_key       varchar not null,
    note          varchar,
    msg_offset    int4         not null,
    partition     int4         not null,
    status        varchar,
    topic         varchar not null,
    request       json,
    prime_id      varchar not null,
    primary key (id)
);