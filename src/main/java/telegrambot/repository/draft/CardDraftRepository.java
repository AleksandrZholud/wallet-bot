package telegrambot.repository.draft;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import telegrambot.model.drafts.CardDraft;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface CardDraftRepository extends JpaRepository<CardDraft, Long> {

    @Query(value = "SELECT c.id, c.name, c.balance, c.status FROM card_draft AS c LIMIT 1", nativeQuery = true)
    Optional<CardDraft> getFirstDraft();

    @Transactional
    @Modifying
    @Query(value = "UPDATE card_draft  SET name = :name", nativeQuery = true)
    int updateName(@Param("name") String name);

    @Transactional
    @Modifying
    @Query(value = "UPDATE card_draft SET balance = :balance, status = :status", nativeQuery = true)
    int updateBalanceAndSetStatus(@Param("balance") BigDecimal balance,
                                  @Param("status") String status);

    @Transactional
    @Modifying
    @Query(value = "UPDATE card_draft SET status = :status", nativeQuery = true)
    int updateStatus(@Param("status") String status);
}