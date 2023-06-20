package telegrambot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import telegrambot.model.entity.Card;

import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

    @Query(value = "SELECT c.id, c.name, c.balance  FROM cards c WHERE c.name = :name", nativeQuery = true)
    Optional<Card> getByName(@Param("name") String name);

    @Override
    @Query(value = "SELECT c.id, c.name, c.balance  FROM cards c WHERE c.id = :id", nativeQuery = true)
    Card getById(@Param("id") Long id);
}