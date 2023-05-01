package telegrambot.handlers;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import telegrambot.config.interceptor.AdditionalUserPropertiesContextHolder;
import telegrambot.repository.util.CommandStateDependencyRepository;
import telegrambot.repository.util.CurrentConditionRepository;
import telegrambot.repository.util.MsgFromStateHistoryRepository;

import static telegrambot.model.enums.CommandEnum.GO_BACK_COMMAND;

@AllArgsConstructor
@Component
public class BackCmdHandler extends AbstractCmdHandler {
    private static final String THIS_CMD = GO_BACK_COMMAND.getCommand();
    private final CurrentConditionRepository currentConditionRepository;
    private final CommandStateDependencyRepository commandStateDependencyRepository;
    private final MsgFromStateHistoryRepository msgFromStateHistoryRepository;

    /**
     * Retrieve last message from DB
     * Remove last msg from DB and set current State to previous
     * Fails with default SendMessage if current condition wasn`t set, or if last msg wasn`t removed
     */
    @Override
    public void processMessage() {
        var previousMessage = msgFromStateHistoryRepository.findLast();

        if (setPreviousState()) {
            if (previousMessage == null) {
                AdditionalUserPropertiesContextHolder.getFacade()
                        .setText("You are already in the root command."
                                + "\nPress /start to see all commands or type any text to continue.")
                        .addStartButton();
                return;
            }
            AdditionalUserPropertiesContextHolder.getFacade()
                    .setText(previousMessage.getMessage())
                    .addButtons(true, true);
            return;
        }
        AdditionalUserPropertiesContextHolder.getFacade()
                .setText("Something went wrong while executing 'back' command.")
                .addStartButton();
    }

    /**
     * @return true, якщо стан вдалося змінити на попередній, також якщо
     * false, якщо поточнийСтан=попередньому (тобто далі вже нікуди),
     * або запит повернув 0 rows affected
     */
    private boolean setPreviousState() {
        var currentCondition = currentConditionRepository.getFirst();
        var dependencyRow = commandStateDependencyRepository
                .findByCurCommandAndCurSate(currentCondition.getCommand(), currentCondition.getState());
        if (dependencyRow.getCurrentState().getId().equals(dependencyRow.getPreviousState().getId())) {
            return currentConditionRepository.updateState(dependencyRow.getNextState().getId()) == 1;
        }
        return currentConditionRepository.updateState(dependencyRow.getPreviousState().getId()) == 1;
    }

    @Override
    public boolean canProcessMessage() {
        return AdditionalUserPropertiesContextHolder.getUpdate().getMessage().getText().equals(THIS_CMD);
    }

    @Override
    public boolean cleanAllData() {
        return true;
    }
}