CREATE TABLE public.reimport
(
    name             varchar NOT NULL,
    state            varchar NOT NULL,
    last_import_date timestamp with time zone,
    last_imported_id bigint,
    last_imported_ts timestamp with time zone,
    error_message    varchar,
    CONSTRAINT reimport__name__pk PRIMARY KEY (name)
);
COMMENT ON TABLE public.reimport IS E'Состояние реимпорта';
COMMENT ON COLUMN public.reimport.name IS E'Название (идентификатор)';
COMMENT ON COLUMN public.reimport.state IS E'Состояние реимпорта';
COMMENT ON COLUMN public.reimport.last_import_date IS E'Дата последнего импорта записи';
COMMENT ON COLUMN public.reimport.last_imported_id IS E'Идентификатор последней импортированной записи';
COMMENT ON COLUMN public.reimport.last_imported_ts IS E'Дата последней импортированной записи';
COMMENT ON COLUMN public.reimport.error_message IS E'Сообщение об ошибке импорта';
