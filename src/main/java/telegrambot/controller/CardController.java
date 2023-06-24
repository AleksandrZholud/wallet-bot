package telegrambot.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import telegrambot.config.multitenancy.TenantManager;
import telegrambot.model.dto.CardReqDto;
import telegrambot.model.dto.CardResDto;
import telegrambot.service.card.CardService;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/card")
public class CardController {

    private final CardService cardService;
    private final TenantManager tenantManager;

    @PutMapping(value = "/{dbName}/{id}")
    public ResponseEntity<CardResDto> update(@PathVariable String dbName,
                                             @PathVariable Long id,
                                             @RequestBody CardReqDto cardReqDto) {
        tenantManager.switchDataSource(dbName, true);
        cardReqDto.setId(id);
        return ResponseEntity.ok(cardService.updateCard(cardReqDto));
    }

    @PostMapping(value = "/{dbName}")
    public ResponseEntity<CardResDto> create(@PathVariable String dbName,
                                             @RequestBody CardReqDto cardReqDTO) {
        tenantManager.switchDataSource(dbName, true);
        CardResDto card = cardService.createCard(dbName, cardReqDTO);

        return ResponseEntity.ok(card);
    }

    @GetMapping(value = "/{dbName}/{id}")
    public ResponseEntity<CardResDto> read(@PathVariable String dbName,
                                           @PathVariable Long id) {
        tenantManager.switchDataSource(dbName, true);
        CardResDto cardNyName = cardService.getCardById(id);
        return ResponseEntity.ok(cardNyName);
    }
}