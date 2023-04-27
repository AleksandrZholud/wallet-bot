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
       (3, 3, 1, 2, 3, 1),
       (4, 3, 1, 3, 1, 2),
       (5, 4, 1, 6, 4, 1),
       (6, 4, 1, 4, 5, 6),
       (7, 4, 1, 5, 1, 4),
       (8, 5, 1, 6, 7, 1),
       (9, 5, 1, 7, 8, 6),
       (10, 5, 1, 8, 1, 7),
       (11, 6, 1, 1, 1, 1),
       (12, 7, 1, 1, 1, 1),
       (13, 8, 1, 1, 1, 1);

insert into current_condition (id, commandid, stateid)
VALUES (1, 1, 1);
END;