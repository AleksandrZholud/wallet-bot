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
import telegrambot.util.SendMessageUtils;

@Slf4j
@Component
public class WalletBot extends TelegramLongPollingBot {
    private final BotConfig botConfig;
    private final CurrentConditionRepository currentConditionRepository;

    public static final String ERROR_EMPTY_MESSAGE_FOUND = "Error: Cannot understand an empty command!";
    public static final String SERVER_ERROR_MSG = "Server error.";

    public WalletBot(BotConfig botConfig, CurrentConditionRepository currentConditionRepository) {
        super(botConfig.getToken());
        this.botConfig = botConfig;
        this.currentConditionRepository = currentConditionRepository;
    }

    @Override
    public String getBotUsername() {
        return botConfig.getName();
    }

    @Override
    //doNotModify this Method
    public void onUpdateReceived(Update update) {
        try {
            setContext(update);
            SendMessage sendMessage = SendMessageUtils.getSendMessageWithChatIdAndText("SM created in onUpdate()");
            if (update.hasMessage() && update.getMessage().hasText()) {
                sendMessage = main(sendMessage);
                trySendMessage(update, sendMessage);
            } else {
                sendOutput(update, ERROR_EMPTY_MESSAGE_FOUND);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            SendMessage sendMessage = SendMessage.builder()
                    .chatId(getContextChatId())
                    .text(validateOutputMessage(e.getMessage()))
                    .build();
            sendOutput(update, sendMessage);
        }
    }

    private SendMessage main(SendMessage sendMessage) throws IllegalAccessException {
        //All logic of TelegramBot is here ↓
        //////////////////////////////////////////////////////////////////////////
        var update = AdditionalUserPropertiesContextHolder.getContext().getUpdate();
        var handlers = AbstractCmdHandler.getAllChildEntities();

        if (update.getMessage().getText().startsWith("/")) {
            for (AbstractCmdHandler handler : handlers) {
                if (handler.canProcessMessage()) {
                    sendMessage = handler.processMessage();
                    return sendMessage;
                }
            }
        } else {
            for (AbstractCmdHandler handler : AbstractCmdHandler.getAllChildEntities()) {
                if (handler.canProcessMessage()) {
                    sendMessage = handler.processMessage();
                    return sendMessage;
                }
            }
        }
        return sendMessage;

        //////////////////////////////////////////////////////////////////////////
        //All logic of TelegramBot is here ↑
    }

    private void trySendMessage(Update update, SendMessage sendMessage) {
        if (sendMessage != null) {
            sendOutput(update, sendMessage);
        } else {
            sendOutput(update, SERVER_ERROR_MSG);
        }
    }

    private void sendOutput(Update update, String errorMsg) {
        SendMessage sendMessage = SendMessage.builder()
                .chatId(update.getMessage().getChatId())
                .text(errorMsg)
                .build();
        sendOutput(update, sendMessage);
    }

    public void sendOutput(Update update, SendMessage sendMessage) {
        SendMessage message = prepareMessage(update, sendMessage);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    private SendMessage prepareMessage(Update update, SendMessage message) {
        message.setText(validateOutputMessage(message.getText()));
        message.setChatId(update.getMessage().getChatId());
        return message;
    }

    private String validateOutputMessage(String output) {
        return output == null || output.isEmpty() ? "Something went wrong." : output;
    }

    private void setContext(Update update) {
        AdditionalUserPropertiesContextHolder.initContext(update);
    }

    private Long getContextChatId() {
        return AdditionalUserPropertiesContextHolder.getContext().getUpdate().getMessage().getChatId();
    }
}