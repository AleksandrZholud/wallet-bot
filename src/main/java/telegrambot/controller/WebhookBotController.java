package telegrambot.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import telegrambot.config.telegram.TelegramWalletBot;

import java.time.Duration;
import java.time.Instant;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/webhook")
public class WebhookBotController {

    private final TelegramWalletBot telegramWalletBot;

    private static final String ERROR_EMPTY_MESSAGE_FOUND = "Error: Cannot understand an empty command!";

    @PostMapping(value = "/general")
    public ResponseEntity<SendMessage> receiveUpdate(@RequestBody Update update) {

        if (!update.hasMessage()) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setText(ERROR_EMPTY_MESSAGE_FOUND);
            return ResponseEntity.ok(sendMessage);
        }

        Duration duration = getDurationBetweenRequestAndCurrentTime(update);

        if (duration.toHours() >= 12) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.ok(telegramWalletBot.onWebhookUpdateReceived(update));
    }

    private Duration getDurationBetweenRequestAndCurrentTime(Update update) {

        long unixTime = update.getMessage().getDate().longValue();
        Instant requestTime = Instant.ofEpochSecond(unixTime);
        Instant currentTime = Instant.now();

        return Duration.between(requestTime, currentTime);
    }
}