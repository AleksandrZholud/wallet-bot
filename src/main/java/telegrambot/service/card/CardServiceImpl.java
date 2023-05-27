package telegrambot.service.card;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import telegrambot.model.Card;
import telegrambot.repository.CardRepository;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;

    @Override
    public Card getByName(String name) {
        return cardRepository.getByName(name).orElseThrow(() -> new IllegalStateException("Card is not exist."));
    }

    @Override
    public BigDecimal getBalance() {
        return null;
    }

    @Override
    public Card save(Card card) {
        return cardRepository.save(card);
    }

    @Override
    public void updateBalanceByName(BigDecimal amount, String name) {
        cardRepository.updateBalanceByName(amount, name);
    }

    @Override
    public List<Card> findAll() {
        return cardRepository.findAll();
    }

    @Override
    public boolean checkIfExistByName(String name) {
        var card = cardRepository.getByName(name);
        return card.isPresent();
    }
}