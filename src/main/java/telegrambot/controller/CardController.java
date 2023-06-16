package telegrambot.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import telegrambot.model.Card;
import telegrambot.service.card.CardService;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/card")
public class CardController {

    private final CardService cardService;

    @PostMapping(value = "/{id}")
    public ResponseEntity<String> changeCardName(@PathVariable Long id,
                                                 @RequestBody Card userCard) {

        Card existingCard = cardService.getCardById(id);
        existingCard.setName(userCard.getName());

        return ResponseEntity.ok(cardService.updateNameById(existingCard));
    }
}