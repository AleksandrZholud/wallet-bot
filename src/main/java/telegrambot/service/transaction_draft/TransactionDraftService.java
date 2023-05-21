package telegrambot.service.transaction_draft;

import telegrambot.model.util.drafts.TransactionDraft;

import java.math.BigDecimal;

public interface TransactionDraftService {
    TransactionDraft getFirstDraft();

    boolean updateStatus(String status);

    void deleteAll();

    void createFirstDraft();

    void updateCardId(Long id);

    void updateTransactionType(String type);

    void updateAmountAndStatus(BigDecimal amount, String status);
}
