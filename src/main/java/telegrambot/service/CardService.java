package telegrambot.service;

import telegrambot.model.Card;
import telegrambot.repository.CardRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class CardService {

    private final CardRepository cardRepository;

    public CardService(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    public Card getByName(String name) {
        return cardRepository.getByName(name);
    }

    public BigDecimal getBalance() {
        return cardRepository.getBalance();
    }
}
