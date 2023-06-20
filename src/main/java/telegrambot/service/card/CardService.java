package telegrambot.service.card;

import telegrambot.model.dto.UpdateCardReqDto;
import telegrambot.model.dto.UpdateCardResDto;
import telegrambot.model.entity.Card;

import java.math.BigDecimal;
import java.util.List;

public interface CardService {

    Card getByName(String name);

    Card createCard(Card card);

    Card updateBalanceByName(BigDecimal amount, String name);

    List<Card> getAll();

    boolean checkIfExistByName(String name);

    UpdateCardResDto updateCard(UpdateCardReqDto idCodeNameDTO);
}