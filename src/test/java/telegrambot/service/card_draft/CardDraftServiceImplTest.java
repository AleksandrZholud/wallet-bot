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
        when(cardDraftRepository.getFirstDraft()).thenReturn(Optional.empty());
        //when
        cardDraftService.createSingleDraft();
        //then
        verify(cardDraftRepository).getFirstDraft();
    }

    @Test
    void updateName() {
        //before
        Long id = 1L;
        String name = "card draft";
        BigDecimal balance = BigDecimal.TEN;
        DraftStatus status = DraftStatus.BUILT;

        CardDraft existingCard = CardDraft.builder()
                .id(id)
                .balance(balance)
                .status(status)
                .name("Goga")
                .build();
        CardDraft changedCard = CardDraft.builder()
                .id(id)
                .balance(balance)
                .status(status)
                .name(name)
                .build();

        when(cardDraftRepository.getFirstDraft()).thenReturn(Optional.of(existingCard));
        when(cardDraftRepository.save(any())).thenReturn(changedCard);


        //when
        var updatedCard = cardDraftService.updateBalance(balance);

        //then
        assertThat(updatedCard)
                .isNotNull()
                .isEqualTo(changedCard);

        verify(cardDraftRepository).getFirstDraft();
        verify(cardDraftRepository).save(any());
    }

    @Test
    void updateBalanceAndGetEntity() {

        //before
        Long id = 1L;
        String name = "card draft";
        BigDecimal balance = BigDecimal.TEN;
        DraftStatus status = DraftStatus.BUILT;

        CardDraft existingCard = CardDraft.builder()
                .id(id)
                .balance(BigDecimal.ZERO)
                .status(status)
                .name(name)
                .build();
        CardDraft changedCard = CardDraft.builder()
                .id(id)
                .balance(balance)
                .status(status)
                .name(name)
                .build();

        when(cardDraftRepository.getFirstDraft()).thenReturn(Optional.of(existingCard));
        when(cardDraftRepository.save(any())).thenReturn(changedCard);


        //when
        var updatedCard = cardDraftService.updateBalance(balance);

        //then
        assertThat(updatedCard)
                .isNotNull()
                .isEqualTo(changedCard);

        verify(cardDraftRepository).getFirstDraft();
        verify(cardDraftRepository).save(any());

    }

    @Test
    void updateStatus() {
        //before
        Long id = 1L;
        String name = "card draft";
        BigDecimal balance = BigDecimal.TEN;
        DraftStatus status = DraftStatus.BUILT;

        CardDraft existingCard = CardDraft.builder()
                .id(id)
                .balance(balance)
                .status(DraftStatus.BUILDING)
                .name(name)
                .build();
        CardDraft changedCard = CardDraft.builder()
                .id(id)
                .balance(balance)
                .status(status)
                .name(name)
                .build();

        when(cardDraftRepository.getFirstDraft()).thenReturn(Optional.of(existingCard));
        when(cardDraftRepository.save(any())).thenReturn(changedCard);


        //when
        var updatedCard = cardDraftService.updateBalance(balance);

        //then
        assertThat(updatedCard)
                .isNotNull()
                .isEqualTo(changedCard);

        verify(cardDraftRepository).getFirstDraft();
        verify(cardDraftRepository).save(any());
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
}