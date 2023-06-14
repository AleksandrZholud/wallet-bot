package telegrambot.service.current_condition;

import telegrambot.model.enums.CommandEnum;
import telegrambot.model.enums.StateEnum;
import telegrambot.model.util.Command;
import telegrambot.model.util.CurrentCondition;
import telegrambot.model.util.State;

public interface CurrentConditionService {

    CurrentCondition getCurrentCondition();

    CurrentCondition updateCommandAndState(CommandEnum commandEnum, StateEnum stateEnum);

    CurrentCondition updateCommandAndState(Command command, State state);

    State getPreviousState();

    CurrentCondition updateState(State newState);

    void reset();
}
