package telegrambot.handlers;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import telegrambot.config.interceptor.AdditionalUserPropertiesContextHolder;
import telegrambot.repository.util.CommandStateDependencyRepository;
import telegrambot.repository.util.CurrentConditionRepository;

@AllArgsConstructor
@Component
public class BackCmdHandler extends AbstractCmdHandler {
    private static final String THIS_CMD = "/back";
    private final CurrentConditionRepository currentConditionRepository;
    private final CommandStateDependencyRepository commandStateDependencyRepository;

    @Override
    public SendMessage processMessage() {
        Update update = AdditionalUserPropertiesContextHolder.getContext().getUpdate();
        var currentCondition = currentConditionRepository.getFirst();
        var currentCommand = currentCondition.getCommand();
        var currentState = currentCondition.getState();
        var commandStateDependency = commandStateDependencyRepository
                .findByCurCommandAndCurSate(currentCommand, currentState);
        var previousState = commandStateDependency.getPreviousState();


        return null;
    }

    @Override
    public boolean canProcessMessage() {
        Update update = AdditionalUserPropertiesContextHolder.getContext().getUpdate();
        return update.getMessage().getText().equals(THIS_CMD);
    }
}
