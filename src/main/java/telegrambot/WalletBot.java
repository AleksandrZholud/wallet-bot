package telegrambot;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import telegrambot.config.telegram.BotConfig;
import telegrambot.service.card.CardServiceImpl;

@Component
public class WalletBot extends TelegramLongPollingBot {

    private final BotConfig botConfig;
    private final CardServiceImpl cardService;

    public WalletBot(BotConfig botConfig, CardServiceImpl cardService) {
        super(botConfig.getToken());
        this.botConfig = botConfig;
        this.cardService = cardService;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String text = update.getMessage().getText();

            //////////////////////////////////////////////////////////////

            var output = cardService.getByName(text).getName();

            //////////////////////////////////////////////////////////////
            SendMessage sendMessage = new SendMessage();
            sendMessage.setText(output);
            sendMessage.setChatId(update.getMessage().getChatId());
            try {
                execute(sendMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public String getBotUsername() {
        return botConfig.getName();
    }
}
