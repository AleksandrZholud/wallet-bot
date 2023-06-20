package telegrambot.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import telegrambot.config.interceptor.UserDataContextHolder;
import telegrambot.config.multitenancy.TenantManager;
import telegrambot.config.telegram.TelegramWalletBot;
import telegrambot.service.validation.ValidationService;

@RequestMapping(value = "/webhook")
@RestController
@RequiredArgsConstructor
public class WebhookBotController {

    private final ValidationService validationService;
    private final TenantManager tenantManager;
    private final TelegramWalletBot telegramWalletBot;

    @PostMapping(value = "/general")
    public SendMessage receiveUpdate(@RequestBody Update update) {
        UserDataContextHolder.initContext(update);

        validationService.validate(update);
        tenantManager.switchDataSource(UserDataContextHolder.getChatId(), UserDataContextHolder.getSenderName());

        return telegramWalletBot.onWebhookUpdateReceived(update);
    }
}