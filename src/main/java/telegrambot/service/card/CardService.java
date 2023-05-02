package telegrambot.service.card;

import telegrambot.model.Card;

import java.math.BigDecimal;

public interface CardService {
    Card getByName(String name);

    BigDecimal getBalance();
}
