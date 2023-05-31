package telegrambot.config.telegram;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import telegrambot.config.interceptor.UserDataContextHolder;
import telegrambot.execurors.AbstractCommandExecutor;

@Slf4j
@Component
@RequiredArgsConstructor
public class TelegramWalletBot extends TelegramWebhookBot {

    private final BotConfig botConfig;
    private static final String ERROR_EMPTY_MESSAGE_FOUND = "Error: Cannot understand an empty command!";
    private static final String ERROR_UNDEFINED_COMMAND = "Error: Undefined command, try again.";

    @Override
    public String getBotPath() {
        return botConfig.getWebhookPath();
    }

    @Override
    public String getBotUsername() {
        return botConfig.getName();
    }

    @Override
    public SendMessage onWebhookUpdateReceived(Update update) {
        UserDataContextHolder.initContext(update);
        if (update.getMessage().hasText()) {
            //All logic of TelegramBot is here ↓
            //////////////////////////////////////////////////////////////////////////

            for (AbstractCommandExecutor executor : AbstractCommandExecutor.getAllChildEntities()) {
                if (executor.isSystemExecutor() && executor.canExec()) {
                    executor.exec();
                    return UserDataContextHolder.performMessage();
                }
            }

            for (AbstractCommandExecutor handler : AbstractCommandExecutor.getAllChildEntities()) {
                if (!handler.isSystemExecutor() && handler.canExec()) {
                    handler.exec();
                    return UserDataContextHolder.performMessage();
                }
            }

            throw new IllegalStateException(ERROR_UNDEFINED_COMMAND);

            //////////////////////////////////////////////////////////////////////////
            //All logic of TelegramBot is here ↑
        }
        return sendEmptyMessageError();
    }

    private SendMessage sendEmptyMessageError() {
        UserDataContextHolder
                .getFacade()
                .addStartButton()
                .setText(ERROR_EMPTY_MESSAGE_FOUND);
        return UserDataContextHolder.performMessage();
    }
}