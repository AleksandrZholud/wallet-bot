package telegrambot.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import telegrambot.model.dto.UpdateCardReqDto;
import telegrambot.model.dto.UpdateCardResDto;
import telegrambot.service.card.CardService;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/card")
public class CardController {

    private final CardService cardService;

    @PutMapping(value = "/{id}")
    public ResponseEntity<UpdateCardResDto> update(@PathVariable Long id,
                                                   @RequestBody UpdateCardReqDto updateCardReqDto) {
        updateCardReqDto.setId(id);
        return ResponseEntity.ok(cardService.updateCard(updateCardReqDto));
    }
}