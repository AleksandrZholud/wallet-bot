package telegrambot.service.card;

import telegrambot.model.Card;

import java.math.BigDecimal;
import java.util.List;

public interface CardService {
    Card getByName(String name);

    BigDecimal getBalance();

    Card save(Card card);

    void updateBalanceByName(BigDecimal amount, String name);

    List<Card> findAll();


    boolean checkIfExistByName(String name);
}
