package telegrambot.service.current_condition;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import telegrambot.model.enums.CommandEnum;
import telegrambot.model.enums.StateEnum;
import telegrambot.model.util.Command;
import telegrambot.model.util.CurrentCondition;
import telegrambot.model.util.State;
import telegrambot.repository.util.CommandRepository;
import telegrambot.repository.util.CurrentConditionRepository;
import telegrambot.repository.util.StateRepository;

@AllArgsConstructor
@Component

public class CurrentConditionServiceImpl implements CurrentConditionService {

    private final CurrentConditionRepository currentConditionRepository;
    private final CommandRepository commandRepository;
    private final StateRepository stateRepository;

    @Override
    public void reset() {
        currentConditionRepository.reset();
    }

    @Override
    public CurrentCondition getCurrentCondition() {
        return getCurrentConditionOrElseThrowEx();
    }

    @Override
    public CurrentCondition updateCommandAndState(CommandEnum commandEnum, StateEnum stateEnum) {
        CurrentCondition changedCurrentCondition = getCurrentConditionOrElseThrowEx();

        Command command = commandRepository.findByNameOptional(commandEnum.getCommand())
                .orElseThrow(() -> new IllegalStateException("No Command with name '" + commandEnum.getCommand() + "' in DataBase"));
        State state = stateRepository.findByNameOptional(stateEnum.getState())
                .orElseThrow(() -> new IllegalStateException("No State with name '" + stateEnum.getState() + "' in DataBase"));

        changedCurrentCondition.setCommand(command);
        changedCurrentCondition.setState(state);
        return currentConditionRepository.save(changedCurrentCondition);
    }

    @Override
    public CurrentCondition updateCommandAndState(Command command, State state) {
        CurrentCondition changedCurrentCondition = getCurrentConditionOrElseThrowEx();

        changedCurrentCondition.setCommand(command);
        changedCurrentCondition.setState(state);
        return currentConditionRepository.save(changedCurrentCondition);
    }

    @Override
    public CurrentCondition updateState(State newState) {
        CurrentCondition changedCurrentCondition = getCurrentConditionOrElseThrowEx();

        changedCurrentCondition.setState(newState);
        return currentConditionRepository.save(changedCurrentCondition);
    }

    @Override
    public State getPreviousState() {
        return currentConditionRepository.getPreviousStateOptional()
                .orElseThrow(() -> new IllegalStateException("No Previous State in DataBase"));
    }

    private CurrentCondition getCurrentConditionOrElseThrowEx() {
        return currentConditionRepository.getCurrentConditionOptional()
                .orElseThrow(() -> new IllegalStateException("No Current Condition in DataBase."));
    }
}
