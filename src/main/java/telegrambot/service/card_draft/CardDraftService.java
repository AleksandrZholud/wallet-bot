package telegrambot.service.card_draft;

import telegrambot.model.enums.DraftStatus;
import telegrambot.model.util.drafts.CardDraft;

import java.math.BigDecimal;

public interface CardDraftService {

    void deleteAll();

    void createFirstDraft();

    void updateName(String draftName);

    CardDraft updateBalanceAndGetEntity(BigDecimal draftBalance);

    void updateStatus(DraftStatus draftStatus);

    CardDraft getFirstDraft();

    boolean isEmpty();

    void claenupAndCreateFirst();
}