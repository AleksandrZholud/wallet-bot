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

import static telegrambot.config.interceptor.AdditionalUserPropertiesContextHolder.validateOutputMessage;

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
            if (update.hasMessage() && update.getMessage().hasText()) {
                main(update.getMessage().getText());
                sendOutput();
            } else {
                sendEmptyMessageError();
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            SendMessage sendMessage = SendMessage.builder()
                    .chatId(update.getMessage().getChatId())
                    .text(validateOutputMessage(e.getMessage()))
                    .build();
            sendOutput(sendMessage);
        }
    }

    private void main(String text) throws IllegalAccessException {
        //All logic of TelegramBot is here ↓
        //////////////////////////////////////////////////////////////////////////
        var handlers = AbstractCmdHandler.getAllChildEntities();

        if (text.startsWith("/")) {
            for (AbstractCmdHandler handler : handlers) {
                if (handler.canProcessMessage()) {
                    handler.processMessage();
                }
            }
        } else {
            for (AbstractCmdHandler handler : AbstractCmdHandler.getAllChildEntities()) {
                if (handler.canProcessMessage()) {
                    handler.processMessage();
                }
            }
        }

        //////////////////////////////////////////////////////////////////////////
        //All logic of TelegramBot is here ↑
    }

    private void sendOutput() {
        sendOutput(AdditionalUserPropertiesContextHolder.performMessage());
    }

    private void sendOutput(SendMessage sendMessage) {
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    private void sendEmptyMessageError() {
        SendMessage sendMessage = AdditionalUserPropertiesContextHolder
                .getFacade()
                .addStartButton()
                .performSendMsg();
        sendMessage.setText(ERROR_EMPTY_MESSAGE_FOUND);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    private void setContext(Update update) throws IllegalAccessException {
        AdditionalUserPropertiesContextHolder.initContext(update);
    }
}