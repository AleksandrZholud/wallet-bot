package telegrambot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import telegrambot.config.interceptor.UserDataContextHolder;
import telegrambot.config.telegram.BotConfig;
import telegrambot.execurors.AbstractCommandExecutor;

import static telegrambot.config.interceptor.UserDataContextHolder.validateOutputMessage;

@Slf4j
@Component
public class WalletBot extends TelegramLongPollingBot {
    private final BotConfig botConfig;
    public static final String ERROR_EMPTY_MESSAGE_FOUND = "Error: Cannot understand an empty command!";
    public static final String ERROR_SERVER_ERROR = "Server error: ";
    public static final String ERROR_UNDEFINED_COMMAND = "Error: Undefined command, try again.";

    public WalletBot(BotConfig botConfig) {
        super(botConfig.getToken());
        this.botConfig = botConfig;
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
                go();
            } else {
                sendEmptyMessageError();
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            UserDataContextHolder.getFacade()
                    .setText(validateOutputMessage(ERROR_SERVER_ERROR + e.getMessage()));
            SendMessage sendMessage = UserDataContextHolder.performMessage();
            sendOutput(sendMessage);
        }
    }

    private void go() throws IllegalAccessException {
        //All logic of TelegramBot is here ↓
        //////////////////////////////////////////////////////////////////////////

        for (AbstractCommandExecutor executor : AbstractCommandExecutor.getAllChildEntities()) {
            if (executor.isSystemExecutor() && executor.canExec()) {
                executor.exec();
                sendOutput();
                return;
            }
        }

        for (AbstractCommandExecutor handler : AbstractCommandExecutor.getAllChildEntities()) {
            if (!handler.isSystemExecutor() && handler.canExec()) {
                handler.exec();
                sendOutput();
                return;
            }
        }

        throw new IllegalStateException(ERROR_UNDEFINED_COMMAND);

        //////////////////////////////////////////////////////////////////////////
        //All logic of TelegramBot is here ↑
    }

    private void sendOutput() {
        sendOutput(UserDataContextHolder.performMessage());
    }

    private void sendOutput(SendMessage sendMessage) {
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    private void sendEmptyMessageError() {
        UserDataContextHolder
                .getFacade()
                .addStartButton()
                .setText(ERROR_EMPTY_MESSAGE_FOUND);
        try {
            execute(UserDataContextHolder.performMessage());
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    private void setContext(Update update) throws IllegalAccessException {
        UserDataContextHolder.initContext(update);
    }
}