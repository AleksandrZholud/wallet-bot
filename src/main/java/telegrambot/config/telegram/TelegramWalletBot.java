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
        SendMessage systemMessage = runSystemExecutors();
        if (systemMessage != null) return systemMessage;

        SendMessage commandMessage = runCommandExecutors();
        if (commandMessage != null) return commandMessage;

        throw new IllegalStateException(ERROR_UNDEFINED_COMMAND);
    }

    private SendMessage runSystemExecutors() {
        for (AbstractCommandExecutor executor : AbstractCommandExecutor.getAllChildEntities()) {
            if (executor.isSystemExecutor() && executor.canExec()) {
                executor.exec();
                return UserDataContextHolder.performMessage();
            }
        }
        return null;
    }

    private SendMessage runCommandExecutors() {
        for (AbstractCommandExecutor handler : AbstractCommandExecutor.getAllChildEntities()) {
            if (!handler.isSystemExecutor() && handler.canExec()) {
                handler.exec();
                return UserDataContextHolder.performMessage();
            }
        }
        return null;
    }
}