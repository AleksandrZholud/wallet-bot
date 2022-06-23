package com.company.telegrambot;

import com.company.telegrambot.model.Card;
import com.company.telegrambot.model.Category;
import com.company.telegrambot.model.OPERATION_TYPE;
import com.company.telegrambot.model.Operation;
import com.company.telegrambot.repository.CategoryRepository;
import com.company.telegrambot.service.CardService;
import com.company.telegrambot.service.OperationServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Random;


@Component
public class WalletBot extends TelegramLongPollingBot {

    @Value("${telegram.bot.username}")
    private String username;
    @Value("${telegram.bot.token}")
    private String token;

    private final OperationServiceImpl operationServiceImpl;
    private final CardService cardService;
    private final CategoryRepository categoryRepository;

    public WalletBot(OperationServiceImpl operationServiceImpl, CardService cardService, CategoryRepository categoryRepository) {
        this.operationServiceImpl = operationServiceImpl;
        this.cardService = cardService;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public String getBotUsername() {
        return username;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            Message message = update.getMessage();
            if (message.hasText()) {
                String text = message.getText();
                //////////////////////////////////////////////////////////////


                String nameOfTmpCard = "tmpCardForTest";
                Card card = cardService.getByName(nameOfTmpCard);

                Category category = categoryRepository.getById(2L);
                Random r = new Random();
                OPERATION_TYPE randomTyype;
                switch (r.nextInt(2)){
                    case 0:
                        randomTyype = OPERATION_TYPE.EXPENDITURE;
                        break;
                    default:
                        randomTyype = OPERATION_TYPE.INCOME;
                        break;
                }
                Operation operation = new Operation(card, randomTyype, category, BigDecimal.valueOf(Long.parseLong(text)), LocalDateTime.now());
                operationServiceImpl.save(operation);

                String output = cardService.getBalance().toString();


                //////////////////////////////////////////////////////////////
                SendMessage sendMessage = new SendMessage();
                sendMessage.setText(output);
                sendMessage.setChatId(message.getChatId().toString());
                try {
                    execute(sendMessage);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
