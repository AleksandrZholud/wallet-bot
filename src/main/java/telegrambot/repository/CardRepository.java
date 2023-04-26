package telegrambot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import telegrambot.model.Card;

import java.math.BigDecimal;
import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, Long> {

    Optional<Card> getByName(String name);

    @Query(value = "select c.balance from cards c where c.name = 'tmpCardForTest'", nativeQuery = true)
    BigDecimal getBalance();
}
