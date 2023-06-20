package telegrambot.service.transaction_draft;

import telegrambot.model.drafts.TransactionDraft;
import telegrambot.model.entity.Card;
import telegrambot.model.enums.DraftStatus;
import telegrambot.model.enums.TransactionTypeEnum;

import java.math.BigDecimal;

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
