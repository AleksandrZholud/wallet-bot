package telegrambot.service.current_condition;

import telegrambot.model.enums.CommandEnum;
import telegrambot.model.enums.StateEnum;
import telegrambot.model.util.Command;
import telegrambot.model.util.CurrentCondition;
import telegrambot.model.util.State;

/**
 * Methods placed in CRUD order, then private methods
 * Base return types:
 * Create - <Entity>
 * Read - <Entity>
 * Update - <Entity>
 * Delete - void
 */
public interface CurrentConditionService {

    void reset();

    CurrentCondition getCurrentCondition();

    State getPreviousState();

    CurrentCondition updateCommandAndState(CommandEnum commandEnum, StateEnum stateEnum);

    CurrentCondition updateCommandAndState(Command command, State state);

    CurrentCondition updateState(State newState);
}
