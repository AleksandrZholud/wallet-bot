package telegrambot.service.card;

import telegrambot.model.Card;

import java.math.BigDecimal;
import java.util.List;

/**
 * Methods placed in CRUD order, then private methods
 * Base return types:
 * Create - <Entity>
 * Read - <Entity>
 * Update - <Entity>
 * Delete - void
 */
public interface CardService {
    Card getByName(String name);

    Card createCard(Card card);

    Card updateBalanceByName(BigDecimal amount, String name);

    List<Card> getAll();


    boolean checkIfExistByName(String name);
}
