drop table if exists message_info CASCADE;
create table message_info
(
    id   bigint generated by default as identity,
    user_id varchar(50),
    user_tag varchar(50)
    message_id varchar(50),


    primary key (id)
);