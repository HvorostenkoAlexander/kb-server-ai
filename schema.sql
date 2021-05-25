create table integral_parameters (id  bigserial not null, operation varchar(255) not null, record_pk int4 not null, ts varchar(255) not null, data_id int8, primary key (id))
create table messages_of_integral_parameters (id  bigserial not null, message_key varchar(255) not null, message_offset int4 not null, message_partition int4 not null, timestamp varchar(255) not null, topic varchar(255) not null, param_id int8 not null, primary key (id))
create table messages_of_unrecoverable_parameters (id  bigserial not null, message_key varchar(255) not null, message_offset int4 not null, message_partition int4 not null, timestamp varchar(255) not null, topic varchar(255) not null, param_id int8 not null, primary key (id))
create table pdm_message (id  bigserial not null, dictionary json, is_posted boolean not null, msg_key varchar(255) not null, msg_offset int8 not null, op varchar(255) not null, partition int4 not null, topic varchar(255) not null, ts varchar(255) not null, primary key (id))

create table pre_attestation_param_lcl_thckng (pre_attestation_param_id int8 not null, lcl_thckng float8)

create table record_data (id  bigserial not null, kceh int4 not null, kceh_name varchar(255) not null, primeid varchar(255) not null, unit_code int4 not null, unit_name varchar(255) not null, werks_code int4 not null, werks_name varchar(255) not null, primary key (id))
create table record_specifications (id  bigserial not null, spec_code int4 not null, spec_format varchar(255), spec_measure varchar(255), spec_name varchar(255) not null, spec_type_code int4 not null, spec_type_name varchar(255) not null, spec_value float4, record_data_id int8 not null, primary key (id))

create table sadim_message (id  bigserial not null, msg_key varchar(255) not null, note varchar(255), msg_offset int8 not null, partition int4 not null, status varchar(255), ts timestamp not null, param_id int8 not null, primary key (id))

create table sadim_pre_attestation_param (id  bigserial not null, estimate int4, pbi float8, ph12_sgp float8, ph1_sgp float8, ph23_sgp float8, prime_id varchar(255), prof_fact float8, sqc_crit_max float8, t12_max float8, t12_min float8, tcm_max float8, tcm_min float8, wedge_fact float8, primary key (id))

create table unrecoverable_parameters (id  bigserial not null, operation varchar(255) not null, record_pk int4 not null, ts varchar(255) not null, data_id int8, primary key (id))

alter table messages_of_integral_parameters add constraint UK_4kykpfs4jmeqxphnu4cqd6hl0 unique (param_id)
alter table messages_of_unrecoverable_parameters add constraint UK_pb84dnqdg86a70suytyjjkqg5 unique (param_id)

alter table sadim_message add constraint UK_owy2et4gobls0pmpmvmjk16si unique (param_id)

alter table integral_parameters add constraint FKkfa9j2fn6ujcsv56o7wrkdisv foreign key (data_id) references record_data
alter table messages_of_integral_parameters add constraint FKgbvl1onrecuy1fipr23jakfec foreign key (param_id) references integral_parameters
alter table messages_of_unrecoverable_parameters add constraint FKr39a6kyex1dg0u6yu2g800h33 foreign key (param_id) references unrecoverable_parameters
alter table pre_attestation_param_lcl_thckng add constraint FKng1axrfcoc0n8hmaiubtw2xfe foreign key (pre_attestation_param_id) references sadim_pre_attestation_param
alter table record_specifications add constraint FKofb8ca8c18ai4hvylwo3nwsgf foreign key (record_data_id) references record_data

alter table sadim_message add constraint FKpocsxnabtb8nn52vircmkaqba foreign key (param_id) references sadim_pre_attestation_param

alter table unrecoverable_parameters add constraint FK2588vsek6ts0i02j7rbqif4v6 foreign key (data_id) references record_data
