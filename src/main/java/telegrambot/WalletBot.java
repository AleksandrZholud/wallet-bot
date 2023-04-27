package telegrambot;

import org.aspectj.weaver.tools.cache.AsynchronousFileCacheBacking;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import telegrambot.config.telegram.BotConfig;
import telegrambot.handlers.AbstractCmdHandler;
import telegrambot.repository.util.CurrentConditionRepository;
import telegrambot.service.card.CardServiceImpl;

@Component
public class WalletBot extends TelegramLongPollingBot {

    private final BotConfig botConfig;
    private final CardServiceImpl cardService;
    private final CurrentConditionRepository currentConditionRepository;

    public WalletBot(BotConfig botConfig, CardServiceImpl cardService,
                     CurrentConditionRepository currentConditionRepository) {
        super(botConfig.getToken());
        this.botConfig = botConfig;
        this.cardService = cardService;
        this.currentConditionRepository = currentConditionRepository;
    }

    @Override
    public void onUpdateReceived(Update update) {
        /*--------------------preparing--->>>>> */
        var sendMessage = SendMessage.builder()
                .chatId(update.getMessage().getChatId())
                .text("Uaschpie unrecognized command!")
                .build();
        if (!update.hasMessage() && !update.getMessage().hasText()) {
            sendMessage.setText("!-!->no message<-!-!");
        }
        /*--------------------logic--->>>>> */
        if (update.getMessage().getText().startsWith("/")) {
            for (AbstractCmdHandler handler : AbstractCmdHandler.getAllChildEntities()) {
                if (handler.canProcessMessage(update)) {
                    sendMessage = handler.processMessage(update);
                    break;
                }
            }
        } else {
            var c = currentConditionRepository.getFirst();
            var currentCommand = c.getCommand();
            var temp = update.getMessage().getText();
            update.getMessage().setText(currentCommand.getName());

            for (AbstractCmdHandler handler : AbstractCmdHandler.getAllChildEntities()) {
                if (handler.canProcessMessage(update)) {
                    update.getMessage().setText(temp);
                    sendMessage = handler.processMessage(update);
                    break;
                }
            }
        }
        /*--------------------execution--->>>>>> */
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return botConfig.getName();
    }
}
