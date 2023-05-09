package telegrambot.repository.util;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import telegrambot.model.util.drafts.TransactionDraft;

import java.math.BigDecimal;

public interface TransactionDraftRepository extends JpaRepository<TransactionDraft, Long> {
    @Query(value = "SELECT tr.id, tr.type, tr.card_id, tr.status FROM transaction_draft AS tr LIMIT 1", nativeQuery = true)
    TransactionDraft getFirstDraft();

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO transaction_draft(id, status) VALUES (1, 'BUILDING')", nativeQuery = true)
    int createFirstDraft();


    @Transactional
    @Modifying
    @Query(value = "UPDATE transaction_draft SET card_id = :id", nativeQuery = true)
    int updateCardId(@Param("id") Long id);

    @Transactional
    @Modifying
    @Query(value = "UPDATE transaction_draft SET type = :type", nativeQuery = true)
    int updateTransactionType(@Param("type") String type);

    @Transactional
    @Modifying
    @Query(value = "UPDATE transaction_draft SET amount = :amount", nativeQuery = true)
    int updateAmount(@Param("amount") BigDecimal amount);

    @Transactional
    @Modifying
    @Query(value = "UPDATE transaction_draft SET status = :status", nativeQuery = true)
    int updateStatus(@Param("status") String status);

    @Transactional
    @Modifying
    @Query(value = "UPDATE transaction_draft SET amount = :amount AND status = :status", nativeQuery = true)
    int updateStatusAndStatus(@Param("amount") BigDecimal amount,
                              @Param("status") String status);
}