package telegrambot.service.transaction_draft;

import telegrambot.model.Card;
import telegrambot.model.drafts.TransactionDraft;
import telegrambot.model.enums.DraftStatus;
import telegrambot.model.enums.TransactionTypeEnum;

import java.math.BigDecimal;

/**
 * Methods placed in CRUD order, then private methods
 * Base return types:
 * Create - <Entity>
 * Read - <Entity>
 * Update - <Entity>
 * Delete - void
 */
public interface TransactionDraftService {
    TransactionDraft createSingleDraft();

    TransactionDraft getFirstDraft();

    boolean isEmpty();

    TransactionDraft updateStatus(DraftStatus status);

    TransactionDraft updateCard(Card card);

    TransactionDraft updateTransactionType(TransactionTypeEnum type);

    TransactionDraft updateAmountAndStatus(BigDecimal amount, DraftStatus status);

    void deleteAll();
}
