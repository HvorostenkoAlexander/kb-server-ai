create table public.messages_of_unrecoverable_parameters
(
    id        bigserial not null,
    message_key       varchar not null,
    message_partition int4 not null,
    message_offset    int4 not null,
    timestamp varchar not null,
    topic     varchar not null,
    param_id  int8 not null,
    CONSTRAINT messages_of_unrecoverable_parameters__id__pk PRIMARY KEY (id)
);

alter table public.messages_of_integral_parameters add message_partition int4 not null;

COMMENT ON TABLE public.messages_of_integral_parameters IS E'Сообщения из kafka: интегральные параметры единицы продукции';
COMMENT ON COLUMN public.messages_of_integral_parameters.message_key IS E'Значение ключа в сообщении от kafka';
COMMENT ON COLUMN public.messages_of_integral_parameters.message_partition IS E'Значение partition сообщения от kafka';
COMMENT ON COLUMN public.messages_of_integral_parameters.message_offset IS E'Значение offset сообщения от kafka';
COMMENT ON COLUMN public.messages_of_integral_parameters.timestamp IS E'Значение timestamp сообщения от kafka';
COMMENT ON COLUMN public.messages_of_integral_parameters.topic IS E'Наименование топика';

COMMENT ON TABLE public.messages_of_unrecoverable_parameters IS E'Сообщения из kafka: не хранимые параметры трендов';
COMMENT ON COLUMN public.messages_of_unrecoverable_parameters.message_key IS E'Значение ключа в сообщении от kafka';
COMMENT ON COLUMN public.messages_of_unrecoverable_parameters.message_partition IS E'Значение partition сообщения от kafka';
COMMENT ON COLUMN public.messages_of_unrecoverable_parameters.message_offset IS E'Значение offset сообщения от kafka';
COMMENT ON COLUMN public.messages_of_unrecoverable_parameters.timestamp IS E'Значение timestamp сообщения от kafka';
COMMENT ON COLUMN public.messages_of_unrecoverable_parameters.topic IS E'Наименование топика';


alter table messages_of_unrecoverable_parameters
    add constraint messages_of_unrecoverable_parameters__UK_param_id unique (param_id);

alter table messages_of_unrecoverable_parameters
    add constraint messages_of_unrecoverable_parameters__FK_param_id foreign key (param_id) references unrecoverable_parameters;