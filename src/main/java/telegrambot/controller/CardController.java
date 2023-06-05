package telegrambot.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import telegrambot.model.Card;
import telegrambot.service.card.CardService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/card")
public class CardController {

    private final CardService cardService;

    @PostMapping("/{id}/{newName}")
    public String changeCardName(@PathVariable Long id,
                                 @PathVariable String newName) {

        Card card = cardService.getCardById(id);
        card.setName(newName);
        return cardService.updateNameById(card);
    }
}