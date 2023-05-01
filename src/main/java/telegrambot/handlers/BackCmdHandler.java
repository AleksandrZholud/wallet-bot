package telegrambot.handlers;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import telegrambot.config.interceptor.AdditionalUserPropertiesContextHolder;
import telegrambot.repository.util.CommandStateDependencyRepository;
import telegrambot.repository.util.CurrentConditionRepository;
import telegrambot.repository.util.MsgFromStateHistoryRepository;
import telegrambot.util.SendMessageUtils;

@AllArgsConstructor
@Component
public class BackCmdHandler extends AbstractCmdHandler {
    private static final String THIS_CMD = "/back";
    private final CurrentConditionRepository currentConditionRepository;
    private final CommandStateDependencyRepository commandStateDependencyRepository;
    private final MsgFromStateHistoryRepository msgFromStateHistoryRepository;

    /**
     * Retrieve last message from DB
     * Remove last msg from DB and set current State to previous
     * Fails with default SendMessage if current condition wasn`t set, or if last msg wasn`t removed
     *
     * @return SendMessage with previous message
     */
    @Override
    public SendMessage processMessage() {
        var update = AdditionalUserPropertiesContextHolder.getContext().getUpdate();
        // TODO: 30.04.2023 possible NPE in removeById
        var isLastMsgRemoved = msgFromStateHistoryRepository.removeLast() == 1;
        var previousMessage = msgFromStateHistoryRepository.findLast();

        if (setPreviousState()) {
            if (previousMessage == null) {
                return SendMessageUtils.getSendMessageWithChatIdAndText(// TODO: 30.04.2023 add this to all SENDMESSAGE
                        "You are already in the root command."
                                + "\nPress /start to see all commands or type any text to continue.");
            }
            return SendMessage.builder()
                    .chatId(update.getMessage().getChatId())
                    .text(previousMessage.getMessage())
                    .build();
        }
        var a = SendMessageUtils.getSendMessageWithChatIdAndText("Something went wrong while executing 'back' command.");
        SendMessageUtils.addButtons(a, false);
        return a;
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
        if (dependencyRow.getCurrentState().getId().equals(dependencyRow.getPreviousState().getId())) { // TODO: 30.04.2023 NPE
            return currentConditionRepository.updateState(dependencyRow.getNextState().getId()) == 1;
        }
        return currentConditionRepository.updateState(dependencyRow.getPreviousState().getId()) == 1;
    }

    @Override
    public boolean canProcessMessage() {
        var update = AdditionalUserPropertiesContextHolder.getContext().getUpdate();
        return update.getMessage().getText().equals(THIS_CMD);
    }

    @Override
    public boolean cleanAllData() {
        return true;
    }
}
