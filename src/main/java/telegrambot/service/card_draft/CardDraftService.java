package telegrambot.service.card_draft;

import telegrambot.model.drafts.CardDraft;
import telegrambot.model.enums.DraftStatus;

import java.math.BigDecimal;

public interface CardDraftService {

    void deleteAll();

    CardDraft createSingleDraft();

    CardDraft updateName(String draftName);

    CardDraft updateBalance(BigDecimal draftBalance);

    CardDraft updateStatus(DraftStatus draftStatus);

    CardDraft getFirstDraft();

    boolean isEmpty();
}