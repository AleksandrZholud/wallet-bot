package telegrambot.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import telegrambot.model.Card;
import telegrambot.service.card.CardService;

@Slf4j
@RestController
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;

    @PostMapping("/change_card_name/{id}/{newName}")
    public String changeCardName(@PathVariable Long id,
                                 @PathVariable String newName) {

        Card card = cardService.getCardById(id);
        if (card != null) {
            card.setName(newName);
            cardService.save(card);
            return "You have successfully renamed the card to " + card.getName() + ".";
        }
        return "The card with your id was not found";
    }
}