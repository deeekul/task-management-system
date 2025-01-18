-- liquibase formatted sql

-- changeset deekul:20250114_create_table_tasks

create sequence tasks_sequence;

create table tasks
(
    id          bigint
        primary key default nextval('tasks_sequence'),
    title       varchar(255),
    description text,
    status      varchar(255)
        constraint tasks_status_check
            check ((status)::text = ANY
        ((ARRAY ['PENDING':: character varying, 'IN_PROGRESS':: character varying, 'CANCELED':: character varying])::text[])),
    priority    varchar(255)
        constraint tasks_priority_check
            check ((priority)::text = ANY
                   ((ARRAY ['LOW':: character varying, 'MEDIUM':: character varying, 'HIGH':: character varying])::text[])),
    author_id   bigint
        constraint tasks_author_id_fk
            references users,
    assignee_id bigint
        constraint tasks_assignee_id_fk
            references users
);

ALTER SEQUENCE tasks_sequence RESTART WITH 11;