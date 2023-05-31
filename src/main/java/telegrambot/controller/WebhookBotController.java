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

    @PostMapping(value = "/go")
    public ResponseEntity<SendMessage> receiveUpdate(@RequestBody Update update) {

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