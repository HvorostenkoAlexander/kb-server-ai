create table public.messages_of_integral_parameters
(
    id        bigserial not null,
    message_key       varchar not null,
    message_offset    int4 not null,
    timestamp varchar not null,
    topic     varchar not null,
    param_id  int8 not null,
    CONSTRAINT messages_of_integral_parameters__id__pk PRIMARY KEY (id)
);

COMMENT ON TABLE public.messages_of_integral_parameters IS E'Не хранимые параметры трендов';
COMMENT ON COLUMN public.integral_parameters.operation IS E'Операция (I, U. D)';
COMMENT ON COLUMN public.integral_parameters.ts IS E'дата и время передачи';
COMMENT ON COLUMN public.integral_parameters.record_pk IS E'Id EM СУП';
COMMENT ON COLUMN public.integral_parameters.data_id IS E'id cписка передаваемых данных, указывается при статусе I,U';

alter table messages_of_integral_parameters
    add constraint UK_958ro014jkubkibj2fb2sb0fd unique (message_key);

alter table messages_of_integral_parameters
    add constraint UK_4kykpfs4jmeqxphnu4cqd6hl0 unique (param_id);

alter table messages_of_integral_parameters
    add constraint FKgbvl1onrecuy1fipr23jakfec foreign key (param_id) references integral_parameters;