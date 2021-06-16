alter table public.ccm_message
    add constraint UKeofqvxguwesr58gfhb012wej6 unique (topic, partition, msg_offset);

alter table public.pdm_message
    add constraint UKdf023h3ibhnqacludc6dersuc unique (topic, partition, msg_offset);

alter table public.sadim_message
    add constraint UK3t7jdaiyf21ipapplhd90cs7s unique (partition, msg_offset);