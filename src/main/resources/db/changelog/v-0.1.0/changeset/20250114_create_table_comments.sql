-- liquibase formatted sql

-- changeset deekul:20250114_create_table_comments

create sequence comments_sequence;

create table comments
(
    id           bigint
        primary key default nextval('comments_sequence'),
    text         text not null,
    created_date timestamp not null,
    user_id      bigint
        constraint comments_user_id_fk
            references users,
    task_id      bigint
        constraint comments_task_id_fk
            references tasks
);