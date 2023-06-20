package telegrambot.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import telegrambot.model.entity.Card;

import java.math.BigDecimal;

public class CardRepositoryTest extends PostgresAbstractContainer {
    @Autowired
    private CardRepository cardRepository;

    @Test
    public void getByName() {
        // Before
        var expected = cardRepository.save(Card.builder()
                .name("John Doe")
                .balance(BigDecimal.valueOf(1000))
                .build());

        // When
        Card actual = cardRepository.getByName("John Doe").orElse(new Card());

        // Then
        Assertions.assertEquals(expected, actual);
    }
}