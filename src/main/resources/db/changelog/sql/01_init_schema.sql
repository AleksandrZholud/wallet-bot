-- liquibase formatted sql

-- changeset Aleks.JSD:1
-- preconditions onFail:MARK_RAN
-- precondition-sql-check expectedResult:0 SELECT CASE WHEN EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'cards') THEN 1 ELSE 0 END
-- comment: SIR-3001
create table if not exists cards
(
    id      bigint not null
        primary key,
    balance numeric(19, 2),
    name    varchar(255) unique
)
-- rollback DROP TABLE cards;

-- changeset Aleks.JSD:2
-- preconditions onFail:MARK_RAN
-- precondition-sql-check expectedResult:0 SELECT CASE WHEN EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'transactions') THEN 1 ELSE 0 END
-- comment: SIR-3001
create table if not exists transactions
(
    id      bigint         not null
        primary key,
    amount  numeric(19, 2) not null,
    type    varchar(255),
    card_id bigint
        constraint fkjxdscq0bxpy0pl465vvsqc89j
            references cards,
    timestamp timestamp default CURRENT_TIMESTAMP
)
-- rollback DROP TABLE transactions;

-- changeset Aleks.JSD:3
-- preconditions onFail:MARK_RAN
-- precondition-sql-check expectedResult:0 SELECT CASE WHEN EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'commands') THEN 1 ELSE 0 END
-- comment: SIR-3001
create table commands
(
    id   bigint       not null
        primary key,
    name varchar(255) not null
        constraint uk_sj9rerwuj7q00a7oy76efiehv
            unique
)
-- rollback DROP TABLE commands;

-- changeset Aleks.JSD:4
-- preconditions onFail:MARK_RAN
-- precondition-sql-check expectedResult:0 SELECT CASE WHEN EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'states') THEN 1 ELSE 0 END
-- comment: SIR-3001
create table states
(
    id   bigint       not null
        primary key,
    name varchar(255) not null
        constraint uk_rdb7pnbo5e3l4vc5bkpk5q6t1
            unique
)
-- rollback DROP TABLE states;

-- changeset Aleks.JSD:5
-- preconditions onFail:MARK_RAN
-- precondition-sql-check expectedResult:0 SELECT CASE WHEN EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'command_state_dependency') THEN 1 ELSE 0 END
-- comment: SIR-3001
create table command_state_dependency
(
    id                bigint not null
        primary key,
    base_id           bigint
        constraint fkerjv4ayfkhw840r0tkpi3ounm
            references commands,
    command_id        bigint
        constraint fk3swo3tv5uw84gt0h15o42y8s3
            references commands,
    current_state_id  bigint
        constraint fkd4jr0wuyapa5sgd4vr19g7v9f
            references states,
    next_state_id     bigint
        constraint fkf7j5gs40ihsgf9i9h4dtc4e9h
            references states,
    previous_state_id bigint
        constraint fkahgr74c14r91lcfw431eyk5ts
            references states
)
-- rollback DROP TABLE command_state_dependency;

-- changeset Aleks.JSD:6
-- preconditions onFail:MARK_RAN
-- precondition-sql-check expectedResult:0 SELECT CASE WHEN EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'current_condition') THEN 1 ELSE 0 END
-- comment: SIR-3001
create table current_condition
(
    id         bigint not null
        primary key,
    command_id bigint not null
        constraint uk_d6bvyng0m5wooggruq1kygv3c
            unique
        constraint fk94aseiloyag45c28srlpspwte
            references commands,
    state_id   bigint
        constraint fk7d9h2c3f6ml9lugnofv91qehu
            references states
)
-- rollback DROP TABLE current_condition;

-- changeset Aleks.JSD:7
-- preconditions onFail:MARK_RAN
-- precondition-sql-check expectedResult:0 SELECT CASE WHEN EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'card_draft') THEN 1 ELSE 0 END
-- comment: SIR-3001
create table card_draft
(
    id      bigint not null
        primary key,
    name    varchar(255),
    balance numeric(38, 2),
    status  varchar(30)
)
-- rollback DROP TABLE card_draft;

-- changeset Aleks.JSD:8
-- preconditions onFail:MARK_RAN
-- precondition-sql-check expectedResult:0 SELECT CASE WHEN EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'transaction_draft') THEN 1 ELSE 0 END
-- comment: SIR-3001
create table transaction_draft
(
    id      bigint         not null
        primary key,
    status  varchar(255),
    type    varchar(255),
    card_id bigint
        constraint fk3ng826qb34g3pri1j4hfqh8b8
            references cards,
    amount  numeric(19, 2)
)
-- rollback DROP TABLE transaction_draft;

-- changeset Aleks.JSD:9
-- preconditions onFail:MARK_RAN
-- precondition-sql-check expectedResult:0 SELECT CASE WHEN EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'command_state_message_history') THEN 1 ELSE 0 END
-- comment: SIR-3001
create table command_state_message_history
(
    id        bigint not null
        primary key,
    message   varchar(255),
    timestamp timestamp default current_timestamp
)
-- rollback DROP TABLE 'command_state_message_history';

-- changeset Aleks.JSD:10
-- preconditions onFail:MARK_RAN
-- precondition-sql-check expectedResult:0 SELECT CASE WHEN EXISTS (SELECT 1 FROM information_schema.sequences WHERE sequence_name = 'hibernate_sequence') THEN 1 ELSE 0 END
-- comment: SIR-3001
create sequence hibernate_sequence;
-- rollback DROP SEQUENCE hibernate_sequence;
