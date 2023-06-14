package telegrambot.service.card_draft;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import telegrambot.model.drafts.CardDraft;
import telegrambot.model.enums.DraftStatus;
import telegrambot.repository.draft.CardDraftRepository;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@SpringBootTest(classes = CardDraftServiceImpl.class)
class CardDraftServiceImplTest {

    @Autowired
    CardDraftService cardDraftService;

    @MockBean
    CardDraftRepository cardDraftRepository;

    @Test
    void deleteAll() {

        //before
        doNothing().when(cardDraftRepository).deleteAll();

        //when
        cardDraftService.deleteAll();

        //then
        verify(cardDraftRepository).deleteAll();
    }

    @Test
    void createFirstDraft() {

        //before
        when(cardDraftRepository.createFirstDraft()).thenReturn(1);
        //when
        cardDraftService.createSingleDraft();
        //then
        verify(cardDraftRepository).createFirstDraft();
    }

    @Test
    void updateName() {

        // before
        String name = "draft name";

        when(cardDraftRepository.updateName(name)).thenReturn(1);

        //when
        cardDraftService.updateName(name);

        //then
        verify(cardDraftRepository).updateName(name);
    }

    @Test
    void updateBalanceAndGetEntity() {

        //before
        Long id = 1L;
        String name = "card draft";
        BigDecimal draftBalance = BigDecimal.ONE;
        DraftStatus draftStatus = DraftStatus.BUILT;

        CardDraft expectedRes = new CardDraft(id, name, draftBalance, draftStatus);

        when(cardDraftRepository.updateBalanceAndSetStatus(draftBalance, String.valueOf(draftStatus)))
                .thenReturn(1);

        var spyCardDraftService = spy(cardDraftService);
        doReturn(expectedRes).when(spyCardDraftService).getFirstDraft();

        //when
        var actualRes = spyCardDraftService.updateBalance(draftBalance);

        //then
        assertThat(actualRes)
                .isNotNull()
                        .isEqualTo(expectedRes);

        verify(cardDraftRepository).updateBalanceAndSetStatus(draftBalance, draftStatus.name());
        verify(spyCardDraftService).getFirstDraft();

    }

    @Test
    void updateStatus() {

        //before
        DraftStatus draftStatus = DraftStatus.BUILT;
        String draftName = draftStatus.name();

        when(cardDraftRepository.updateStatus(draftName)).thenReturn(1);

        //when
        cardDraftService.updateStatus(draftStatus);

        //then
        verify(cardDraftRepository).updateStatus(draftName);
    }

    @Test
    void getFirstDraft() {

        //before
        Long id = 1L;
        String name = "card draft";
        BigDecimal balance = BigDecimal.ONE;
        DraftStatus draftStatus = DraftStatus.BUILT;
        CardDraft expectedRes = new CardDraft(id, name, balance, draftStatus);

        Optional<CardDraft> optionalCardDraft = Optional.of(expectedRes);
        when(cardDraftRepository.getFirstDraft()).thenReturn(optionalCardDraft);

        // when
        var actualRes = cardDraftService.getFirstDraft();

        // then
        assertThat(actualRes)
                .isNotNull()
                .isEqualTo(expectedRes);
        verify(cardDraftRepository).getFirstDraft();
    }

    @Test
    void getFirstDraft_whenDraftInNotFound_ThrowEx() {

        //before
        DraftStatus draftStatus = DraftStatus.BUILT;
        when(cardDraftRepository.getFirstDraft()).thenReturn(Optional.empty());

        //when
        assertThatThrownBy(() -> cardDraftService.getFirstDraft()).isInstanceOf(IllegalStateException.class);

        //then
        verify(cardDraftRepository).getFirstDraft();
    }

    @Test
    void isEmpty() {

        //before
        Long id = 1L;
        String name = "card draft";
        BigDecimal balance = BigDecimal.ONE;
        DraftStatus draftStatus = DraftStatus.BUILT;
        CardDraft expectedRes = new CardDraft(id, name, balance, draftStatus);

        Optional<CardDraft> optionalCardDraft = Optional.of(expectedRes);
        when(cardDraftRepository.getFirstDraft()).thenReturn(optionalCardDraft);

        //when
        Boolean actualRes = cardDraftService.isEmpty();

        //then
        assertThat(actualRes)
                .isNotNull();
        verify(cardDraftRepository).getFirstDraft();

    }

    @Test
    void claenupAndCreateFirst() {

        //before
        doNothing().when(cardDraftRepository).deleteAll();
        when(cardDraftRepository.createFirstDraft()).thenReturn(1);

        //when
        cardDraftService.cleanUpAndCreateFirst();

        //then
        verify(cardDraftRepository).deleteAll();
        verify(cardDraftRepository).createFirstDraft();
    }
}