package telegrambot.handlers;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageId;
import org.telegram.telegrambots.meta.api.objects.Update;
import telegrambot.TelegramBotApplication;
import telegrambot.WalletBot;
import telegrambot.config.interceptor.AdditionalUserPropertiesContextHolder;
import telegrambot.model.util.MsgFromStateHistory;
import telegrambot.repository.util.CommandStateDependencyRepository;
import telegrambot.repository.util.CurrentConditionRepository;
import telegrambot.repository.util.MsgFromStateHistoryRepository;

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
        var lastMessage = msgFromStateHistoryRepository.findLast();
        var isLastMsgRemoved = msgFromStateHistoryRepository.removeById(lastMessage.getId()) == 1;

        if (isLastMsgRemoved && setPreviousState()) {
            return SendMessage.builder()
                    .chatId(update.getMessage().getChatId())
                    .text(lastMessage.getMessage())
                    .build();
        }
        return SendMessage.builder()
                .chatId(update.getMessage().getChatId())
                .text("Something went wrong while executing '/back' command."
                        + "\nMethod end with last message:\n"
                        + lastMessage.getMessage())
                .build();
    }


    /**
     * @return true, якщо стан вдалося змінити на попередній
     * false, якщо поточнийСтан=попередньому (тобто далі вже нікуди),
     * або запит повернув 0 rows affected
     */
    private boolean setPreviousState() {
        var currentCondition = currentConditionRepository.getFirst();
        var dependencyRow = commandStateDependencyRepository
                .findByCurCommandAndCurSate(currentCondition.getCommand(), currentCondition.getState());
        if (dependencyRow.getCurrentState().getId().equals(dependencyRow.getPreviousState().getId())) {
            return false;
        }
        return currentConditionRepository.updateState(dependencyRow.getPreviousState().getId()) == 1;
    }

    @Override
    public boolean canProcessMessage() {
        var update = AdditionalUserPropertiesContextHolder.getContext().getUpdate();
        return update.getMessage().getText().equals(THIS_CMD);
    }
}
