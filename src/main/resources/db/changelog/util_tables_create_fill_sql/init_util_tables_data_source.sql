BEGIN;
insert into Commands (id, name)
values (1, '/start'),
       (2, 'Show balance'),
       (3, 'Create a card'),
       (4, 'Add income/expense'),
       (5, 'Show operation history'),
       (6, 'Back'),
       (7, 'Reset'),
       (8, 'Confirm creating card'),
       (9, 'Confirm creating operation');

insert into States (id, name)
values (1, 'NoState'),
       (2, 'SetName'),
       (3, 'SetBalance'),
       (4, 'SetAmount'),
       (5, 'SetType'),
       (6, 'ChooseCard'),
       (7, 'SetDateFrom'),
       (8, 'SetDateTo'),
       (9, 'Confirmation');

insert into command_state_dependency (id, command_id, base_id, current_state_id, next_state_id, previous_state_id)
VALUES (1 , 1, 1, 1, 1, 1),
       (2 , 2, 1, 1, 1, 1),
       (3 , 3, 1, 1, 2, 1),
       (4 , 3, 1, 2, 3, 1),
       (5 , 3, 1, 3, 1, 2),
       (6 , 3, 1, 9, 1, 3),
       (7 , 4, 1, 1, 6, 1),
       (8 , 4, 1, 6, 4, 1),
       (9 , 4, 1, 4, 5, 6),
       (10, 4, 1, 5, 1, 4),
       (11, 5, 1, 1, 6, 1),
       (12, 5, 1, 6, 7, 1),
       (13, 5, 1, 7, 8, 6),
       (14, 5, 1, 8, 1, 7),
       (15, 6, 1, 1, 1, 1),
       (16, 7, 1, 1, 1, 1),
       (17, 8, 1, 1, 1, 1);

insert into current_condition (id, command_id, state_id)
VALUES (1, 1, 1);
END;