package telegrambot.handlers;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import telegrambot.config.interceptor.AdditionalUserPropertiesContextHolder;
import telegrambot.repository.util.CurrentConditionRepository;
import telegrambot.util.SendMessageUtils;

import static telegrambot.model.enums.CommandEnum.CREATE_CARD_COMMAND;

@AllArgsConstructor
@Component
public class StartCmdHandler extends AbstractCmdHandler {
    private static final String THIS_CMD = "/start";
    private final CurrentConditionRepository currentConditionRepository;

    @Override
    public SendMessage processMessage() {
        Update update = AdditionalUserPropertiesContextHolder.getContext().getUpdate();
        SendMessage sendMessage = SendMessageUtils.getSendMessageWithChatIdAndText(update,
                "Greetings, " + update.getMessage().getChat().getFirstName() + "!"
                        + "\nChoose your destiny...:");
        SendMessageUtils.addButtons(sendMessage, CREATE_CARD_COMMAND);

        currentConditionRepository.updateCommandAndState(1L,1L);// TODO: 30.04.2023  doResetCondition

        return sendMessage;
    }

    @Override
    public boolean canProcessMessage() {
        Update update = AdditionalUserPropertiesContextHolder.getContext().getUpdate();
        return update.getMessage().getText().equals(THIS_CMD);
    }
}