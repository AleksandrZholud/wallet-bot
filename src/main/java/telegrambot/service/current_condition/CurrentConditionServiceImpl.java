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

import java.util.Optional;

@AllArgsConstructor
@Component

public class CurrentConditionServiceImpl implements CurrentConditionService {

    private final CurrentConditionRepository currentConditionRepository;
    private final CommandRepository commandRepository;
    private final StateRepository stateRepository;

    @Override
    public CurrentCondition getCurrentCondition() {
        return currentConditionRepository.getCurrentConditionOptional()
                .orElseThrow(() -> new IllegalStateException("No Current Condition in Data Base."));
    }

    @Override
    public CurrentCondition updateCommandAndState(CommandEnum commandEnum, StateEnum stateEnum) {
        Optional<CurrentCondition> currentConditionToChangeOptional = currentConditionRepository.getCurrentConditionOptional();
        CurrentCondition changedCurrentCondition = currentConditionToChangeOptional
                .orElseThrow(() -> new IllegalStateException("No Current Condition in Data Base."));

        Command command = commandRepository.findByName(commandEnum.getCommand())
                .orElseThrow(() -> new IllegalStateException("No Command with name '" + commandEnum.getCommand() + "' in DataBase"));
        State state = stateRepository.findByNameOptional(stateEnum.getState())
                .orElseThrow(() -> new IllegalStateException("No State with name '" + stateEnum.getState() + "' in DataBase"));

        changedCurrentCondition.setCommand(command);
        changedCurrentCondition.setState(state);
        return currentConditionRepository.save(changedCurrentCondition);
    }

    @Override
    public CurrentCondition updateCommandAndState(Command command, State state) {
        Optional<CurrentCondition> currentConditionToChangeOptional = currentConditionRepository.getCurrentConditionOptional();
        CurrentCondition changedCurrentCondition = currentConditionToChangeOptional
                .orElseThrow(() -> new IllegalStateException("No Current Condition in Data Base."));

        changedCurrentCondition.setCommand(command);
        changedCurrentCondition.setState(state);
        return currentConditionRepository.save(changedCurrentCondition);
    }

    @Override
    public CurrentCondition updateState(State newState) {
        Optional<CurrentCondition> currentConditionToChangeOptional = currentConditionRepository.getCurrentConditionOptional();
        CurrentCondition changedCurrentCondition = currentConditionToChangeOptional
                .orElseThrow(() -> new IllegalStateException("No Current Condition in Data Base."));

        changedCurrentCondition.setState(newState);
        return currentConditionRepository.save(changedCurrentCondition);
    }

    @Override
    public State getPreviousState() {
        Optional<State> stateOptional = currentConditionRepository.getPreviousStateOptional();
        return stateOptional
                .orElseThrow(() -> new IllegalStateException("No Previous State in DataBase"));
    }

    @Override
    public void reset() {
        currentConditionRepository.reset();
    }
}
