package telegrambot.service.card;

import telegrambot.model.Card;

import java.math.BigDecimal;
import java.util.List;

public interface CardService {
    Card getByName(String name);

    Card createCard(Card card);

    Card updateBalanceByName(BigDecimal amount, String name);

    List<Card> getAll();

    boolean checkIfExistByName(String name);
}
