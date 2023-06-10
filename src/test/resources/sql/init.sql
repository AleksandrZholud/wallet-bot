BEGIN;
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

create sequence hibernate_sequence;

insert into commands (id, name)
values (1, '/start'),
       (2, 'Show balance'),
       (3, 'Create a card'),
       (4, 'Add income/expense'),
       (5, 'Show operation history'),
       (6, 'Back'),
       (7, 'Reset'),
       (8, 'Confirm creating card'),
       (9, 'Confirm creating operation');

insert into states (id, name)
values (1, 'NoState'),
       (2, 'SetName'),
       (3, 'SetBalance'),
       (4, 'SetAmount'),
       (5, 'SetType'),
       (6, 'ChooseCard'),
       (7, 'SetDateFrom'),
       (8, 'SetDateTo'),
       (9, 'Confirmation');

insert into command_state_dependency (id, command_id, base_id, current_state_id, previous_state_id)
VALUES (1, 1, 1, 1, 1),
       (2, 2, 1, 1, 1),
       (3, 3, 1, 1, 1),
       (4, 3, 1, 2, 1),
       (5, 3, 1, 3, 2),
       (6, 3, 1, 9, 3),
       (7, 4, 1, 1, 1),
       (8, 4, 1, 6, 1),
       (9, 4, 1, 4, 6),
       (10, 4, 1, 5, 4),
       (11, 5, 1, 1, 1),
       (12, 5, 1, 6, 1),
       (13, 5, 1, 7, 6),
       (14, 5, 1, 8, 7),
       (15, 6, 1, 1, 1),
       (16, 7, 1, 1, 1),
       (17, 8, 1, 1, 1);

insert into current_condition (id, command_id, state_id)
VALUES (1, 1, 1);
END;