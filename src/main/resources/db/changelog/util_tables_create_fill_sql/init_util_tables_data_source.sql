BEGIN;
insert into commands (id, command)
values (1, '/start'),
       (2, '/showBalance'),
       (3, '/createCard'),
       (4, '/createOperation'),
       (5, '/showHistory'),
       (6, '/back'),
       (7, '/reset'),
       (8, '/confirm');

insert into states (id, state)
values (1, 'noState'),
       (2, 'setName'),
       (3, 'setBalance'),
       (4, 'setAmount'),
       (5, 'setType'),
       (6, 'chooseCard'),
       (7, 'setDateFrom'),
       (8, 'setDateTo'),
       (9, 'confirmation');

insert into command_state_dependency (id, commandId, baseId, currentStateId, nextStateId, previousStateId)
VALUES (1, 1, 1, 1, 1, 1),
       (2, 2, 1, 1, 1, 1),
       (3, 3, 1, 1, 2, 1),
       (4, 3, 1, 2, 3, 1),
       (5, 3, 1, 3, 1, 2),
       (6, 3, 1, 9, 1, 3),
       (7, 4, 1, 1, 6, 1),
       (8, 4, 1, 6, 4, 1),
       (9, 4, 1, 4, 5, 6),
       (10, 4, 1, 5, 1, 4),
       (11, 5, 1, 1, 6, 1),
       (12, 5, 1, 6, 7, 1),
       (13, 5, 1, 7, 8, 6),
       (14, 5, 1, 8, 1, 7),
       (15, 6, 1, 1, 1, 1),
       (16, 7, 1, 1, 1, 1),
       (17, 8, 1, 1, 1, 1);

insert into current_condition (id, commandid, stateid)
VALUES (1, 1, 1);
END;