-- liquibase formatted sql

-- changeset deekul:20250114_create_table_tokens

create sequence tokens_sequence;

create table tokens
(
    id         bigint
        primary key default nextval('tokens_sequence'),
    token      varchar(255)
        constraint unique_token
            unique,
    token_type varchar(255)
        constraint token_type_check
            check ((token_type)::text = 'BEARER'::text),
    expired    boolean not null,
    revoked    boolean not null,
    user_id   bigint
        constraint fk_user
            references users ON DELETE CASCADE
);