package telegrambot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import telegrambot.config.interceptor.AdditionalUserPropertiesContextHolder;
import telegrambot.config.telegram.BotConfig;
import telegrambot.handlers.AbstractCmdHandler;
import telegrambot.repository.util.CurrentConditionRepository;

@Slf4j
@Component
public class WalletBot extends TelegramLongPollingBot {
    private final BotConfig botConfig;
    private final CurrentConditionRepository currentConditionRepository;

    public static final String ERROR_EMPTY_MESSAGE_FOUND = "Error: Cannot understand an empty command!";

    public WalletBot(BotConfig botConfig, CurrentConditionRepository currentConditionRepository) {
        super(botConfig.getToken());
        this.botConfig = botConfig;
        this.currentConditionRepository = currentConditionRepository;
    }

    private SendMessage main(SendMessage sendMessage) throws IllegalAccessException {
        //All logic of TelegramBot is here ↓
        //////////////////////////////////////////////////////////////////////////
        var update = AdditionalUserPropertiesContextHolder.getContext().getUpdate();

        if (update.getMessage().getText().startsWith("/")) {
            for (AbstractCmdHandler handler : AbstractCmdHandler.getAllChildEntities()) {
                if (handler.canProcessMessage()) {
                    sendMessage = handler.processMessage();
                    return sendMessage;
                }
            }
        } else {
            var currentCommand = currentConditionRepository.getFirst().getCommand();
            var temp = update.getMessage().getText();
            update.getMessage().setText(currentCommand.getName());

            for (AbstractCmdHandler handler : AbstractCmdHandler.getAllChildEntities()) {
                if (handler.canProcessMessage()) {
                    update.getMessage().setText(temp);
                    sendMessage = handler.processMessage();
                    return sendMessage;
                }
            }
        }
        return sendMessage;

        //////////////////////////////////////////////////////////////////////////
        //All logic of TelegramBot is here ↑
    }

    @Override
    //doNotModify this Method
    public void onUpdateReceived(Update update) {
        setContext(update);
        if (update.hasMessage() && update.getMessage().hasText()) {
            SendMessage sendMessage = SendMessage.builder()
                    .chatId(update.getMessage().getChatId())
                    .text("Uaschpie unrecognized command!")
                    .build();
            try {
                sendMessage = main(sendMessage);
                sendOutput(update, sendMessage, false);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                sendMessage.setText(e.getMessage());
                sendOutput(update, sendMessage, true);
            }
        } else {
            sendOutput(update, ERROR_EMPTY_MESSAGE_FOUND, true);
        }
    }

    private void sendOutput(Update update, String errorMsg, boolean isErrorMessage) {
        SendMessage sendMessage = SendMessage.builder()
                .chatId(update.getMessage().getChatId())
                .text(errorMsg)
                .build();
        sendOutput(update, sendMessage, isErrorMessage);
    }

    public void sendOutput(Update update, SendMessage sendMessage, boolean isErrorMessage) {
        SendMessage message = prepareMessage(update, sendMessage, isErrorMessage);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    private SendMessage prepareMessage(Update update, SendMessage message, boolean isErrorMessage) {
        message.setText(validateOutputMessage(message.getText()));
        message.setChatId(update.getMessage().getChatId());
        message.setReplyToMessageId(isErrorMessage ? null : update.getMessage().getMessageId());
        return message;
    }

    private String validateOutputMessage(String output) {
        return output == null || output.isEmpty() ? "Something went wrong." : output;
    }

    @Override
    public String getBotUsername() {
        return botConfig.getName();
    }

    private void setContext(Update update) {
        AdditionalUserPropertiesContextHolder.initContext();
        AdditionalUserPropertiesContextHolder.getContext().setUpdate(update);
    }
}