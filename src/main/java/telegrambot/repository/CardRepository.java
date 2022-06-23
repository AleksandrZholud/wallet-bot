package telegrambot.repository;

import telegrambot.model.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;

public interface CardRepository extends JpaRepository<Card, Long> {

    Card getByName(String name);

    @Query(value = "select c.balance from cards c where c.name = 'tmpCardForTest'", nativeQuery = true)
    BigDecimal getBalance();
}
