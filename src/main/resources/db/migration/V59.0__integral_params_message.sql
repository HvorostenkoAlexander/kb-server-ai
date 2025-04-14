create table integral_params_message
(
    id bigserial not null,
    prime_id varchar not null,
    response jsonb not null,
    created_at timestamp with time zone not null,
    constraint integral_params_message__id__pk primary key (id)
);

comment on table public.integral_params_message is E'Исходные сообщения запроса';
comment on column public.integral_params_message.id is E'Идентификатор, формируется автоматически';
comment on column public.integral_params_message.prime_id is E'Идентификатор ед. продукции';
comment on column public.integral_params_message.response is E'Тело ответа';
comment on column public.integral_params_message.created_at is E'Дата и время записи в таблицу';