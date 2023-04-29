package telegrambot.repository;

import org.springframework.stereotype.Repository;
import telegrambot.model.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

    Card getByName(String name);

    @Query(value = "select c.balance from cards c where c.name = 'tmpCardForTest'", nativeQuery = true)
    BigDecimal getBalance();
}
