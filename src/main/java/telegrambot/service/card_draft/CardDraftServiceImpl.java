package telegrambot.service.card_draft;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import telegrambot.model.drafts.CardDraft;
import telegrambot.model.enums.DraftStatus;
import telegrambot.repository.draft.CardDraftRepository;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CardDraftServiceImpl implements CardDraftService {

    private final CardDraftRepository cardDraftRepository;

    @Override
    public CardDraft createSingleDraft() {
        CardDraft draftToSave = CardDraft.builder()
                .id(1L)
                .status(DraftStatus.BUILDING)
                .build();

        Optional<CardDraft> draftOptional = cardDraftRepository.getFirstDraft();

        if (draftOptional.isPresent()) {
            cardDraftRepository.deleteAll();
        }

        return cardDraftRepository.save(draftToSave);
    }

    @Override
    public CardDraft getFirstDraft() {
        return cardDraftRepository.getFirstDraft()
                .orElseThrow(() -> new IllegalStateException("First CardDraft is not found."));
    }

    @Override
    public boolean isEmpty() {
        return cardDraftRepository.getFirstDraft().isEmpty();
    }

    @Override
    public CardDraft updateName(String draftName) {
        CardDraft draft = getDraftOrThrowEx();

        draft.setName(draftName);
        return cardDraftRepository.save(draft);
    }

    @Override
    public CardDraft updateBalance(BigDecimal draftBalance) {
        CardDraft draft = getDraftOrThrowEx();

        draft.setBalance(draftBalance);
        return cardDraftRepository.save(draft);
    }

    @Override
    public CardDraft updateStatus(DraftStatus draftStatus) {
        CardDraft draft = getDraftOrThrowEx();

        draft.setStatus(draftStatus);
        return cardDraftRepository.save(draft);
    }

    @Override
    public void deleteAll() {
        cardDraftRepository.deleteAll();
    }


    private CardDraft getDraftOrThrowEx() {
        Optional<CardDraft> draftOptional = cardDraftRepository.getFirstDraft();
        return draftOptional
                .orElseThrow(() -> new IllegalStateException("No draft in DataBase"));
    }
}
