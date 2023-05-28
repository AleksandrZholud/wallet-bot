package telegrambot.service.transaction_draft;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import telegrambot.model.util.drafts.TransactionDraft;
import telegrambot.repository.util.TransactionDraftRepository;

import java.math.BigDecimal;

@AllArgsConstructor
@Component
public class TransactionDraftServiceImpl implements TransactionDraftService {
    private final TransactionDraftRepository transactionDraftRepository;

    @Override
    public TransactionDraft getFirstDraft() {
        return transactionDraftRepository.getFirstDraft()
                .orElseThrow(() -> new IllegalStateException("TransactionDraft is not found"));
    }

    @Override
    public boolean updateStatus(String status) {
        int result = transactionDraftRepository.updateStatus(status);
        return result == 1;
    }

    @Override
    public void deleteAll() {
        transactionDraftRepository.deleteAll();
    }

    @Override
    public void createFirstDraft() {
        transactionDraftRepository.createFirstDraft();
    }

    @Override
    public void updateCardId(Long id) {
        transactionDraftRepository.updateCardId(id);
    }

    @Override
    public void updateTransactionType(String type) {
        transactionDraftRepository.updateTransactionType(type);
    }

    @Override
    public void updateAmountAndStatus(BigDecimal amount, String status) {
        transactionDraftRepository.updateAmountAndStatus(amount, status);
    }

    @Override
    public boolean isEmpty() {
        return transactionDraftRepository.getFirstDraft().isEmpty();
    }

}
