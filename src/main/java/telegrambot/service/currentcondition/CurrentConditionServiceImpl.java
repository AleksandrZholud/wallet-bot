package telegrambot.service.currentcondition;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import telegrambot.model.util.CurrentCondition;
import telegrambot.repository.util.CurrentConditionRepository;

@AllArgsConstructor
@Component

public class CurrentConditionServiceImpl implements CurrentConditionService {

    private final CurrentConditionRepository currentConditionRepository;

    @Override
    public CurrentCondition getCurrentCondition() {
        return currentConditionRepository.getCurrentCondition();
    }

    @Override
    public void updateCommandAndState(Long command, Long state) {
        currentConditionRepository.updateCommandAndState(command, state);
    }

    @Override
    public long getPreviousStateId() {
        return currentConditionRepository.getPreviousStateId();
    }

    @Override
    public void updateState(long previousStateId) {
        currentConditionRepository.updateState(previousStateId);
    }
}
