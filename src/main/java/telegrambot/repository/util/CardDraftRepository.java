package telegrambot.repository.util;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import telegrambot.model.util.CardDraft;
import telegrambot.model.util.DRAFT_STATUS;

import java.math.BigDecimal;

@Repository
public interface CardDraftRepository extends JpaRepository<CardDraft, Long> {
    @Transactional
    @Modifying
    @Query(value = "UPDATE card_draft  SET name = :name", nativeQuery = true)
    int updateName(@Param("name") String name);

    @Transactional
    @Modifying
    @Query(value = "UPDATE card_draft SET balance = :balance", nativeQuery = true)
    int updateBalance(@Param("balance") BigDecimal balance);

    @Transactional
    @Modifying
    @Query(value = "UPDATE card_draft SET status = :status", nativeQuery = true)
    int updateStatus(@Param("status") String status);

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO card_draft(id, status) VALUES (2, 'BUILDING')", nativeQuery = true)
    int createFirstDraft();

    @Transactional
    @Query(value = "SELECT c.id, c.name, c.balance, c.status FROM card_draft AS c WHERE c.id=:id", nativeQuery = true)
    CardDraft getById(@Param("id") Long id);

    @Transactional
    @Query(value = "SELECT c.id, c.name, c.balance, c.status FROM card_draft AS c LIMIT 1", nativeQuery = true)
    CardDraft getFirstDraft();
}