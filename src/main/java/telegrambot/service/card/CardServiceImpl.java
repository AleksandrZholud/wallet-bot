package telegrambot.service.card;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import telegrambot.model.Card;
import telegrambot.repository.CardRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;

    @Override
    public Card getByName(String name) {
        return cardRepository.getByName(name)
                .orElseThrow(() -> new IllegalStateException("Card is not exist."));
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
        Optional<Card> optionalCard = cardRepository.getByName(name);
        return optionalCard.isPresent();
    }

    @Override
    public Card getCardById(Long id) {
        return cardRepository.getCardById(id)
                .orElseThrow(() -> new IllegalStateException("Card with id is not found."));
    }

    @Override
    public String updateNameById(Card card) {
        String newName = card.getName();
        card.setName(newName);

        cardRepository.save(card);
        return "You have successfully renamed the card to " + card.getName() + ".";
    }
}