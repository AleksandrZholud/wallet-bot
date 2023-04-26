package telegrambot.service.card;

import lombok.RequiredArgsConstructor;
import telegrambot.model.Card;
import telegrambot.repository.CardRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;

    @Override
    public Card getByName(String name) {
        return cardRepository.getByName(name);
    }

    @Override
    public BigDecimal getBalance() {
        return cardRepository.getBalance();
    }
}
