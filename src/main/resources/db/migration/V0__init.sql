create table public.integral_parameters (
                                            id  bigserial not null,
                                            operation varchar not null,
                                            record_pk int4 not null,
                                            ts varchar not null,
                                            data_id int8,
                                            CONSTRAINT integral_parameters__id__pk PRIMARY KEY (id)
);
COMMENT ON TABLE public.integral_parameters IS E'Интегральные параметры еденицы продукции';
COMMENT ON COLUMN public.integral_parameters.operation IS E'Операция (I, U. D)';
COMMENT ON COLUMN public.integral_parameters.ts IS E'дата и время передачи';
COMMENT ON COLUMN public.integral_parameters.record_pk IS E'Id EM СУП';
COMMENT ON COLUMN public.integral_parameters.data_id IS E'id cписка передаваемых данных, указывается при статусе I,U';

create table public.unrecoverable_parameters (
                                                 id  bigserial not null,
                                                 operation varchar not null,
                                                 record_pk int4 not null,
                                                 ts varchar not null,
                                                 data_id int8,
                                                 CONSTRAINT unrecoverable_parameters__id__pk PRIMARY KEY (id)
);
COMMENT ON TABLE public.unrecoverable_parameters IS E'Передача не хранимых параметров трендов';
COMMENT ON COLUMN public.unrecoverable_parameters.operation IS E'Операция (I, U. D)';
COMMENT ON COLUMN public.unrecoverable_parameters.ts IS E'дата и время передачи';
COMMENT ON COLUMN public.unrecoverable_parameters.record_pk IS E'Id EM СУП';
COMMENT ON COLUMN public.unrecoverable_parameters.data_id IS E'id cписка передаваемых данных, указывается при статусе I,U';

create table public.record_data (
                                    id  bigserial not null,
                                    kceh int4 not null,
                                    kceh_name varchar not null,
                                    primeid varchar not null,
                                    unit_code int4 not null,
                                    unit_name varchar not null,
                                    werks_code int4 not null,
                                    werks_name varchar not null,
                                    CONSTRAINT record_data__id__pk PRIMARY KEY (id)
);
COMMENT ON TABLE public.record_data IS E'Список передаваемых данных, указывается при статусе I,U';
COMMENT ON COLUMN public.record_data.primeid IS E'Идентификатор (ИД) единицы ЕМ в первоисточнике CCM';
COMMENT ON COLUMN public.record_data.kceh IS E'Номер цеха';
COMMENT ON COLUMN public.record_data.kceh_name IS E'Название цеха';
COMMENT ON COLUMN public.record_data.unit_code IS E'Код агрегата';
COMMENT ON COLUMN public.record_data.unit_name IS E'Название агрегата';
COMMENT ON COLUMN public.record_data.werks_code IS E'Код завода';
COMMENT ON COLUMN public.record_data.werks_name IS E'Наименование завода';

create table public.record_specifications (
                                              id  bigserial not null,
                                              spec_code int4 not null,
                                              spec_format varchar,
                                              spec_measure varchar,
                                              spec_name varchar not null,
                                              spec_type_code int4 not null,
                                              spec_type_name varchar not null,
                                              spec_value float4,
                                              record_data_id int8 not null,
                                              CONSTRAINT record_specifications__id__pk PRIMARY KEY (id)
);
COMMENT ON TABLE public.record_specifications IS E'Список характеристик';
COMMENT ON COLUMN public.record_specifications.spec_code IS E'Код характеристики';
COMMENT ON COLUMN public.record_specifications.spec_name IS E'Наименование характеристики';
COMMENT ON COLUMN public.record_specifications.spec_type_code IS E'Тип данных (1-строка, 2-число, 3-дата)';
COMMENT ON COLUMN public.record_specifications.spec_type_name IS E'Наименование типа данных';
COMMENT ON COLUMN public.record_specifications.spec_value IS E'Значение характеристики';
COMMENT ON COLUMN public.record_specifications.spec_format IS E'Формат передачи характеристики';
COMMENT ON COLUMN public.record_specifications.spec_measure IS E'Единица измерения';

alter table public.integral_parameters
    add constraint FKkfa9j2fn6ujcsv56o7wrkdisv foreign key (data_id) references record_data;

alter table public.record_specifications
    add constraint FKofb8ca8c18ai4hvylwo3nwsgf foreign key (record_data_id) references record_data;

alter table public.unrecoverable_parameters
    add constraint UK_jr2as9xir0gep6os5ry448tmp unique (data_id);

alter table public.unrecoverable_parameters
    add constraint FK2588vsek6ts0i02j7rbqif4v6 foreign key (data_id) references record_data;