package telegrambot.handlers;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageId;
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
        var update = AdditionalUserPropertiesContextHolder.getContext().getUpdate();
        setPreviousCondition();

        var r = update.getMessage().isCommand();

        Message m = new Message();
        Chat chat = new Chat();

        getChatHistory()

        return null;
    }


    /**
     * Якщо ми знаходимось на першому стані поточної команди, то переходимо на базову (попередню) команду.
     * Поточний стан при цьому встановлюється на noState, але в таблиці CommandStateDependency такого нема,
     * тож треба або не перевіряти поточний стан у хендлерах (що зараз не робиться), або впровадити додаткові
     * поля в таблиці CommandStateDependency, де CurrentState зможе бути noState
     * <p>
     * По дефолту, залишаємося на поточній команді, але переходимо на попередній стан
     * <p>
     * Драфти при цьому не змінюються. Розуміється що поля драфтів перезапишуться при повторному прохобжені стану
     */
    private boolean setPreviousCondition() {
        var currentCondition = currentConditionRepository.getFirst();
        var currentCommand = currentCondition.getCommand();
        var currentState = currentCondition.getState();

        var csdRow = commandStateDependencyRepository.findByCurCommandAndCurSate(currentCommand, currentState);
        var previousState = csdRow.getPreviousState();

        var commandIdToSet = previousState.getName().equals("/noState") ?
                csdRow.getBaseCommandId().getId() : currentCommand.getId();

        return currentConditionRepository.updateCommandAndState(commandIdToSet, previousState.getId()) == 1;
    }

    @Override
    public boolean canProcessMessage() {
        Update update = AdditionalUserPropertiesContextHolder.getContext().getUpdate();
        return update.getMessage().getText().equals(THIS_CMD);
    }
}
