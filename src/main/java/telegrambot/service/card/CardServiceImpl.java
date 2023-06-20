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
    public Card createCard(Card card) {
        Optional<Card> cardToCheckOptional = cardRepository.getByName(card.getName());

        if (cardToCheckOptional.isPresent()) {
            throw new IllegalStateException("Card with name '" + card.getName() + "' already exist in database");
        }
        return cardRepository.save(card);
    }

    @Override
    public Card getByName(String name) {
        return getCardByNameOrElseThrowException(name);
    }

    @Override
    public List<Card> getAll() {
        return cardRepository.findAll();
    }

    @Override
    public boolean checkIfExistByName(String name) {
        Optional<Card> optionalCard = cardRepository.getByName(name);
        return optionalCard.isPresent();
    }

    @Override
    public Card updateBalanceByName(BigDecimal amount, String name) {
        Card changedCard = getCardByNameOrElseThrowException(name);

        changedCard.setBalance(amount);
        var r = cardRepository.save(changedCard);
        return r;
    }



    private Card getCardByNameOrElseThrowException(String name) {
        return cardRepository.getByName(name)
                .orElseThrow(() -> new IllegalStateException("No such Card in database"));
    }
}
