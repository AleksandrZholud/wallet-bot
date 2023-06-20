-- liquibase formatted sql

-- changeset Aleks.JSD:1001
-- preconditions onFail:MARK_RAN

-- precondition-sql-check expectedResult:0 SELECT count(*) FROM information_schema.tables WHERE table_name IN ('cards', 'transactions', 'commands', 'states', 'command_state_dependency', 'current_condition', 'card_draft', 'transaction_draft', 'command_state_message_history')
-- comment: SIR-3001

create table cards
(
    id      bigint not null
        primary key,
    balance numeric(19, 2),
    name    varchar(255) unique
);
create table transactions
(
    id        bigint         not null
        primary key,
    amount    numeric(19, 2) not null,
    type      varchar(255),
    card_id   bigint
        constraint c_transaction_card
            references cards,
    timestamp timestamp default CURRENT_TIMESTAMP
);
create table commands
(
    id   bigint       not null
        primary key,
    name varchar(255) not null
        constraint c_command_name
            unique
);
create table states
(
    id   bigint       not null
        primary key,
    name varchar(255) not null
        constraint c_states_name
            unique
);
create table command_state_dependency
(
    id                bigint not null
        primary key,
    base_id           bigint
        constraint c_command_state_dependency_base_id
            references commands,
    command_id        bigint
        constraint c_command_state_dependency_command
            references commands,
    current_state_id  bigint
        constraint c_command_state_dependency_current_state
            references states,
    previous_state_id bigint
        constraint c_command_state_dependency_previous_state
            references states
);
create table current_condition
(
    id         bigint not null
        primary key,
    command_id bigint not null
        constraint c_current_condition_command
            unique
        constraint c_current_condition_cmd
            references commands,
    state_id   bigint
        constraint c_current_condition_state
            references states
);
create table card_draft
(
    id      bigint not null
        primary key,
    name    varchar(255),
    balance numeric(38, 2),
    status  varchar(30)
);
create table transaction_draft
(
    id      bigint not null
        primary key,
    status  varchar(255),
    type    varchar(255),
    card_id bigint
        constraint c_transaction_draft_card
            references cards,
    amount  numeric(19, 2)
);
create table command_state_message_history
(
    id        bigint not null
        primary key,
    message   varchar(255),
    timestamp timestamp default current_timestamp
);
-- rollback DROP TABLE IF EXISTS command_state_message_history, transaction_draft, card_draft, current_condition, command_state_dependency, states, commands, transactions, cards;

-- changeset Aleks.JSD:1002
-- preconditions onFail:MARK_RAN
-- precondition-sql-check expectedResult:0 SELECT CASE WHEN EXISTS (SELECT 1 FROM information_schema.sequences WHERE sequence_name = 'hibernate_sequence') THEN 1 ELSE 0 END
-- comment: SIR-3001
create sequence hibernate_sequence;
-- rollback DROP SEQUENCE hibernate_sequence;
