package telegrambot.service.card;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import telegrambot.model.dto.CardReqDto;
import telegrambot.model.dto.CardResDto;
import telegrambot.model.entity.Card;
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
        return cardRepository.save(changedCard);
    }

    private Card getCardByNameOrElseThrowException(String name) {
        return cardRepository.getByName(name)
                .orElseThrow(() -> new IllegalStateException("No such Card in database"));
    }

    @Override
    public CardResDto updateCard(CardReqDto CardReqDto) {
        Card card = cardRepository.getById(CardReqDto.getId());
        if (card == null) {
            throw new IllegalStateException("Card with id " + CardReqDto.getId() + " is not found,");
        }
        card.setName(CardReqDto.getName());
        card.setBalance(CardReqDto.getBalance());
        cardRepository.save(card);
        return CardResDto.builder()
                .id(card.getId())
                .name(card.getName())
                .balance(card.getBalance())
                .build();
    }

    @Override
    public CardResDto createCard(String dbName, CardReqDto cardReqDto) {

        Optional<Card> optionalCard = cardRepository.getByName(cardReqDto.getName());
        if (optionalCard.isPresent()) {
            throw new IllegalStateException("Card with name '" + cardReqDto.getName() + "' already exist in database");
        }

        Card card = Card.builder()
                .name(cardReqDto.getName())
                .balance(cardReqDto.getBalance())
                .build();
        Card savedCard = cardRepository.save(card);
        return CardResDto.builder()
                .id(savedCard.getId())
                .name(savedCard.getName())
                .balance(savedCard.getBalance())
                .build();
    }

    @Override
    public CardResDto getCardById(Long id) {
        Card cardById = cardRepository.getById(id);

        if (cardById == null) {
            throw new IllegalStateException("Card is not found");
        }

        return CardResDto.builder()
                .id(cardById.getId())
                .name(cardById.getName())
                .balance(cardById.getBalance())
                .build();
    }
}