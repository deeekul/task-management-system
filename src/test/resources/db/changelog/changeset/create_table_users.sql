-- liquibase formatted sql

-- changeset deekul:create_table_users

create sequence users_sequence;

create table users
(
    id         bigint
        primary key default nextval('users_sequence'),
    first_name varchar(255),
    last_name  varchar(255),
    login      varchar(255),
    password   varchar(255),
    role       varchar(255)
        constraint users_role_check
            check ((role)::text = ANY
        ((ARRAY ['USER':: character varying, 'ADMIN':: character varying])::text[]))
);