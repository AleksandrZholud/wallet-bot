package telegrambot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import telegrambot.config.telegram.BotConfig;
import telegrambot.service.card.CardServiceImpl;

@Slf4j
@Component
public class WalletBot extends TelegramLongPollingBot {

    private final BotConfig botConfig;
    private final CardServiceImpl cardService;

    public WalletBot(BotConfig botConfig, CardServiceImpl cardService) {
        super(botConfig.getToken());
        this.botConfig = botConfig;
        this.cardService = cardService;
    }

    private String main(String text) {
        String output;
        //All logic is here ↓
        //////////////////////////////////////////////////////////////////////////

        output = cardService.getByName(text).getName();

        //////////////////////////////////////////////////////////////////////////
        //All logic is here ↑
        return output;
    }

    @Override
    //doNotModify this Method
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String text = update.getMessage().getText();
            String output;
            try {
                output = main(text);
                sendOutput(update, output, false);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                sendOutput(update, e.getMessage(), true);
            }
        }
    }

    public void sendOutput(Update update, String output, boolean isErrorMessage) {
        SendMessage sendMessage = prepareMessage(update, output, isErrorMessage);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    private SendMessage prepareMessage(Update update, String output, boolean isErrorMessage) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(validateOutputMessage(output));
        sendMessage.setChatId(update.getMessage().getChatId());
        sendMessage.setReplyToMessageId(isErrorMessage ? null : update.getMessage().getMessageId());
        return sendMessage;
    }

    private String validateOutputMessage(String output) {
        return output == null || output.isEmpty() ? "Something went wrong." : output;
    }

    @Override
    public String getBotUsername() {
        return botConfig.getName();
    }
}
