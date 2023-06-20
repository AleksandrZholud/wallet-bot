package telegrambot.repository.draft;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import telegrambot.model.drafts.TransactionDraft;

import java.util.Optional;

public interface TransactionDraftRepository extends JpaRepository<TransactionDraft, Long> {

    @Query(value = "SELECT tr.id, tr.type, tr.card_id, tr.amount, tr.status FROM transaction_draft AS tr LIMIT 1", nativeQuery = true)
    Optional<TransactionDraft> getFirstDraft();
}