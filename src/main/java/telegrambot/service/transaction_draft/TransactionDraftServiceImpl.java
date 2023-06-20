package telegrambot.service.transaction_draft;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import telegrambot.model.drafts.TransactionDraft;
import telegrambot.model.entity.Card;
import telegrambot.model.enums.DraftStatus;
import telegrambot.model.enums.TransactionTypeEnum;
import telegrambot.repository.draft.TransactionDraftRepository;

import java.math.BigDecimal;
import java.util.Optional;

@AllArgsConstructor
@Component
public class TransactionDraftServiceImpl implements TransactionDraftService {
    private final TransactionDraftRepository transactionDraftRepository;

    @Override
    public TransactionDraft createSingleDraft() {
        TransactionDraft draftToSave = TransactionDraft.builder()
                .id(1L)
                .status(DraftStatus.BUILDING)
                .build();

        Optional<TransactionDraft> draftOptional = transactionDraftRepository.getFirstDraft();

        if (draftOptional.isPresent()) {
            transactionDraftRepository.deleteAll();
        }

        return transactionDraftRepository.save(draftToSave);
    }

    @Override
    public TransactionDraft getFirstDraft() {
        return transactionDraftRepository.getFirstDraft()
                .orElseThrow(() -> new IllegalStateException("First TransactionDraft is not found"));
    }

    @Override
    public boolean isEmpty() {
        return transactionDraftRepository.getFirstDraft().isEmpty();
    }

    @Override
    public TransactionDraft updateStatus(DraftStatus status) {
        TransactionDraft draft = getDraftOrThrowEx();

        draft.setStatus(status);
        return transactionDraftRepository.save(draft);
    }

    @Override
    public TransactionDraft updateCard(Card card) {
        TransactionDraft draft = getDraftOrThrowEx();

        draft.setCard(card);
        return transactionDraftRepository.save(draft);
    }

    @Override
    public TransactionDraft updateTransactionType(TransactionTypeEnum type) {
        TransactionDraft draft = getDraftOrThrowEx();

        draft.setType(type);
        return transactionDraftRepository.save(draft);
    }

    @Override
    public TransactionDraft updateAmountAndStatus(BigDecimal amount, DraftStatus status) {
        TransactionDraft draft = getDraftOrThrowEx();

        draft.setAmount(amount);
        draft.setStatus(status);
        return transactionDraftRepository.save(draft);
    }

    @Override
    public void deleteAll() {
        transactionDraftRepository.deleteAll();
    }

    private TransactionDraft getDraftOrThrowEx() {
        Optional<TransactionDraft> draftOptional = transactionDraftRepository.getFirstDraft();
        return draftOptional
                .orElseThrow(() -> new IllegalStateException("No draft in DataBase"));
    }
}
