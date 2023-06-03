package telegrambot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import telegrambot.model.Card;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

    @Query(value = "SELECT c.id, c.name, c.balance  FROM cards c WHERE c.name = :name", nativeQuery = true)
    Optional<Card> getByName(@Param("name") String name);

    @Query(value = "SELECT c.id, c.name, c.balance  FROM cards c WHERE c.id = :id", nativeQuery = true)
    Optional<Card> getCardById(@Param("id") Long id);

    @Transactional
    @Modifying
    @Query(value = "UPDATE cards SET balance = :amount WHERE name = :name", nativeQuery = true)
    int updateBalanceByName(@Param("amount") BigDecimal amount,
                            @Param("name") String name);

}