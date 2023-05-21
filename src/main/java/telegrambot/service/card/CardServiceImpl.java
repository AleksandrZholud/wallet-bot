package telegrambot.service.card;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import telegrambot.model.Card;
import telegrambot.repository.CardRepository;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;

    @Override
    public Card getByName(String name) {
        return cardRepository.getOptionalByName(name).orElseThrow(() -> new IllegalStateException("Card is not exist."));
    }

    @Override
    public BigDecimal getBalance() {
        return null;
    }

    @Override
    public Card save(Card card) {
        return  cardRepository.save(card);
    }
}