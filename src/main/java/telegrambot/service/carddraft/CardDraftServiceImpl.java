package telegrambot.service.carddraft;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import telegrambot.model.enums.DraftStatus;
import telegrambot.model.util.drafts.CardDraft;
import telegrambot.repository.util.CardDraftRepository;

import java.math.BigDecimal;

import static telegrambot.model.enums.DraftStatus.BUILT;

@Service
@RequiredArgsConstructor
public class CardDraftServiceImpl implements CardDraftService{

    private final CardDraftRepository cardDraftRepository;

    @Override
    public void deleteAll() {
        cardDraftRepository.deleteAll();
    }

    @Override
    public void createFirstDraft() {
        cardDraftRepository.createFirstDraft();
    }

    @Override
    public void updateName(String draftName) {
        cardDraftRepository.updateName(draftName);
    }

    @Override
    public CardDraft updateBalanceAndGetEntity(BigDecimal draftBalance) {
        //TODO: VALERY изменить метод 'updateBalance' чтобы он сразу сетил и BUILD статус //BUILD.name()
        cardDraftRepository.updateBalance(draftBalance);
        updateStatus(BUILT);
        return getFirstDraft();
    }

    @Override
    public void updateStatus(DraftStatus draftStatus) {
        String statusName = draftStatus.name();
        cardDraftRepository.updateStatus(statusName);
    }

    @Override
    public CardDraft getFirstDraft() {
        return cardDraftRepository.getFirstDraft();
    }

    @Override
    public void claenupAndCreateFirst() {
        cardDraftRepository.deleteAll();
        cardDraftRepository.createFirstDraft();
    }
}