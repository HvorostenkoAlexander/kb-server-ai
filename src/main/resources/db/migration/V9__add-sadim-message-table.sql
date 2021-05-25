create table sadim_message
(
    id  bigserial not null,
    msg_key varchar not null,
    note varchar,
    msg_offset int8 not null,
    partition int4 not null,
    status varchar,
    ts timestamp not null,
    param_id int8 not null,
    primary key (id)
);

alter table sadim_message add constraint FKpocsxnabtb8nn52vircmkaqba
    foreign key (param_id) references sadim_pre_attestation_param;


