package telegrambot.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import telegrambot.config.telegram.TelegramWalletBot;

@RestController
@RequiredArgsConstructor
public class WebhookBotController {

    private final TelegramWalletBot telegramWalletBot;

    @PostMapping
    public SendMessage receiveUpdate(@RequestBody Update update) {
        return telegramWalletBot.onWebhookUpdateReceived(update);
    }
}