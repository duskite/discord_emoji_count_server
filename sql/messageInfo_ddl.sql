drop table if exists message_info CASCADE;
create table message_info
(
    id   bigint generated by default as identity,
    message_id varchar(50),
    primary key (id)
);