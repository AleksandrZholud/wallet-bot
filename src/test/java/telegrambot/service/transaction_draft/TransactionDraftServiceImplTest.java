package telegrambot.service.transaction_draft;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import telegrambot.model.drafts.TransactionDraft;
import telegrambot.model.entity.Card;
import telegrambot.model.enums.DraftStatus;
import telegrambot.model.enums.TransactionTypeEnum;
import telegrambot.repository.draft.TransactionDraftRepository;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@SpringBootTest(classes = TransactionDraftServiceImpl.class)
class TransactionDraftServiceImplTest {

    @Autowired
    TransactionDraftService transactionDraftService;

    @MockBean
    TransactionDraftRepository transactionDraftRepository;

    @Test
    void getFirstDraft() {

        //before
        TransactionTypeEnum transactionTypeEnum = TransactionTypeEnum.EXPENSE;
        Card card = new Card(2L, "New card", BigDecimal.ONE);
        DraftStatus draftStatus = DraftStatus.BUILT;
        TransactionDraft expectedRes =
                new TransactionDraft(3L, transactionTypeEnum, card, BigDecimal.ONE, draftStatus);
        Optional<TransactionDraft> optionalTransactionDraft = Optional.of(expectedRes);

        when(transactionDraftRepository.getFirstDraft()).thenReturn(optionalTransactionDraft);

        //when
        var actualRes = transactionDraftService.getFirstDraft();

        //then
        assertThat(actualRes)
                .isNotNull()
                .isEqualTo(expectedRes);
        verify(transactionDraftRepository).getFirstDraft();
    }

    @Test
    void getFirstDraft_whenDraftIsNotFound_throwEx() {

        //before
        when(transactionDraftRepository.getFirstDraft()).thenReturn(Optional.empty());

        //when
        assertThatThrownBy(() -> transactionDraftService.getFirstDraft()).isInstanceOf(IllegalStateException.class);

        //then
        verify(transactionDraftRepository).getFirstDraft();
    }

    @Test
    void updateStatus() {
        //before
        DraftStatus newStatus = DraftStatus.SAVED;
        TransactionDraft existedDraft = TransactionDraft.builder()
                .id(1L)
                .type(TransactionTypeEnum.INCOME)
                .card(new Card(1L, "name", BigDecimal.ZERO))
                .amount(BigDecimal.TEN)
                .status(DraftStatus.SAVING)
                .build();
        TransactionDraft changedDraft = TransactionDraft.builder()
                .id(1L)
                .type(TransactionTypeEnum.INCOME)
                .card(new Card(1L, "name", BigDecimal.ZERO))
                .amount(BigDecimal.TEN)
                .status(newStatus)
                .build();

        when(transactionDraftRepository.getFirstDraft()).thenReturn(Optional.of(existedDraft));
        when(transactionDraftRepository.save(any())).thenReturn(changedDraft);

        //when
        TransactionDraft actualRes = transactionDraftService.updateStatus(newStatus);

        //then
        assertThat(actualRes)
                .isNotNull()
                .isEqualTo(changedDraft);
        verify(transactionDraftRepository).getFirstDraft();
        verify(transactionDraftRepository).save(any());
    }

    @Test
    void updateStatus_Ex() {
        //before
        DraftStatus newStatus = DraftStatus.SAVED;
        when(transactionDraftRepository.getFirstDraft()).thenReturn(Optional.empty());

        //then
        assertThatThrownBy(() -> transactionDraftService.updateStatus(newStatus))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("No draft in DataBase");
        verify(transactionDraftRepository).getFirstDraft();
        verify(transactionDraftRepository, never()).save(any());
    }

    @Test
    void deleteAll() {

        //before
        doNothing().when(transactionDraftRepository).deleteAll();

        //when
        transactionDraftRepository.deleteAll();

        //then
        verify(transactionDraftRepository).deleteAll();
    }

    @Test
    void createFirstDraft_IfTableEmpty() {

        //before
        TransactionDraft draftToSave = TransactionDraft.builder()
                .id(1L)
                .status(DraftStatus.BUILDING)
                .build();
        when(transactionDraftRepository.getFirstDraft()).thenReturn(Optional.empty());
        when(transactionDraftRepository.save(draftToSave)).thenReturn(draftToSave);

        //when
        TransactionDraft actualRes = transactionDraftService.createSingleDraft();

        //then
        assertThat(actualRes)
                .isNotNull()
                .isEqualTo(draftToSave);
        verify(transactionDraftRepository).getFirstDraft();
        verify(transactionDraftRepository, never()).deleteAll();
        verify(transactionDraftRepository).save(any());
    }

    @Test
    void createFirstDraft_IfTableNotEmpty() {

        //before
        TransactionDraft draftToSave = TransactionDraft.builder()
                .id(1L)
                .status(DraftStatus.BUILDING)
                .build();
        when(transactionDraftRepository.getFirstDraft()).thenReturn(Optional.of(new TransactionDraft()));
        when(transactionDraftRepository.save(draftToSave)).thenReturn(draftToSave);

        //when
        TransactionDraft actualRes = transactionDraftService.createSingleDraft();

        //then
        assertThat(actualRes)
                .isNotNull()
                .isEqualTo(draftToSave);
        verify(transactionDraftRepository).getFirstDraft();
        verify(transactionDraftRepository).deleteAll();
        verify(transactionDraftRepository).save(any());
    }

    @Test
    void updateCard() {
        //before
        Card newCard = Card.builder()
                .id(1L)
                .balance(BigDecimal.ZERO)
                .name("newCard")
                .build();
        TransactionDraft existedDraft = TransactionDraft.builder()
                .id(1L)
                .type(TransactionTypeEnum.INCOME)
                .card(new Card(2L, "name", BigDecimal.TEN))
                .amount(BigDecimal.TEN)
                .status(DraftStatus.SAVING)
                .build();
        TransactionDraft changedDraft = TransactionDraft.builder()
                .id(1L)
                .type(TransactionTypeEnum.INCOME)
                .card(newCard)
                .amount(BigDecimal.TEN)
                .status(DraftStatus.SAVING)
                .build();

        when(transactionDraftRepository.getFirstDraft()).thenReturn(Optional.of(existedDraft));
        when(transactionDraftRepository.save(any())).thenReturn(changedDraft);

        //when
        TransactionDraft actualRes = transactionDraftService.updateCard(newCard);

        //then
        assertThat(actualRes)
                .isNotNull()
                .isEqualTo(changedDraft);
        verify(transactionDraftRepository).getFirstDraft();
        verify(transactionDraftRepository).save(any());
    }

    @Test
    void updateCard_Ex() {
        //before
        Card newCard = Card.builder()
                .id(1L)
                .balance(BigDecimal.ZERO)
                .name("newCard")
                .build();

        when(transactionDraftRepository.getFirstDraft()).thenReturn(Optional.empty());

        //then
        assertThatThrownBy(() -> transactionDraftService.updateCard(newCard))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("No draft in DataBase");
        verify(transactionDraftRepository).getFirstDraft();
        verify(transactionDraftRepository, never()).save(any());
    }

    @Test
    void updateTransactionType() {
        //before
        TransactionTypeEnum newTransactionType = TransactionTypeEnum.INCOME;
        TransactionDraft existedDraft = TransactionDraft.builder()
                .id(1L)
                .type(TransactionTypeEnum.EXPENSE)
                .card(new Card(2L, "name", BigDecimal.TEN))
                .amount(BigDecimal.TEN)
                .status(DraftStatus.SAVING)
                .build();
        TransactionDraft changedDraft = TransactionDraft.builder()
                .id(1L)
                .type(newTransactionType)
                .card(new Card(2L, "name", BigDecimal.TEN))
                .amount(BigDecimal.TEN)
                .status(DraftStatus.SAVING)
                .build();

        when(transactionDraftRepository.getFirstDraft()).thenReturn(Optional.of(existedDraft));
        when(transactionDraftRepository.save(any())).thenReturn(changedDraft);

        //when
        TransactionDraft actualRes = transactionDraftService.updateTransactionType(newTransactionType);

        //then
        assertThat(actualRes)
                .isNotNull()
                .isEqualTo(changedDraft);
        verify(transactionDraftRepository).getFirstDraft();
        verify(transactionDraftRepository).save(any());
    }

    @Test
    void updateTransactionType_Ex() {
        //before
        TransactionTypeEnum newTransactionType = TransactionTypeEnum.INCOME;

        when(transactionDraftRepository.getFirstDraft()).thenReturn(Optional.empty());

        //then
        assertThatThrownBy(() -> transactionDraftService.updateTransactionType(newTransactionType))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("No draft in DataBase");
        verify(transactionDraftRepository).getFirstDraft();
        verify(transactionDraftRepository, never()).save(any());
    }

    @Test
    void updateAmountAndStatus() {
        //before
        BigDecimal newAmount = BigDecimal.TEN;
        DraftStatus newStatus = DraftStatus.SAVED;
        TransactionDraft existedDraft = TransactionDraft.builder()
                .id(1L)
                .type(TransactionTypeEnum.EXPENSE)
                .card(new Card(2L, "name", BigDecimal.TEN))
                .amount(BigDecimal.ZERO)
                .status(DraftStatus.SAVING)
                .build();
        TransactionDraft changedDraft = TransactionDraft.builder()
                .id(1L)
                .type(TransactionTypeEnum.EXPENSE)
                .card(new Card(2L, "name", BigDecimal.TEN))
                .amount(newAmount)
                .status(newStatus)
                .build();

        when(transactionDraftRepository.getFirstDraft()).thenReturn(Optional.of(existedDraft));
        when(transactionDraftRepository.save(any())).thenReturn(changedDraft);

        //when
        TransactionDraft actualRes = transactionDraftService.updateAmountAndStatus(newAmount, newStatus);

        //then
        assertThat(actualRes)
                .isNotNull()
                .isEqualTo(changedDraft);
        verify(transactionDraftRepository).getFirstDraft();
        verify(transactionDraftRepository).save(any());
    }

    @Test
    void updateAmountAndStatus_Ex() {
        //before
        BigDecimal newAmount = BigDecimal.TEN;
        DraftStatus newStatus = DraftStatus.SAVED;

        when(transactionDraftRepository.getFirstDraft()).thenReturn(Optional.empty());

        //then
        assertThatThrownBy(() -> transactionDraftService.updateAmountAndStatus(newAmount, newStatus))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("No draft in DataBase");
        verify(transactionDraftRepository).getFirstDraft();
        verify(transactionDraftRepository, never()).save(any());
    }

    @Test
    void isEmpty() {

        //before
        TransactionTypeEnum transactionTypeEnum = TransactionTypeEnum.EXPENSE;
        Card card = new Card(2L, "New card", BigDecimal.ONE);
        DraftStatus draftStatus = DraftStatus.BUILT;
        TransactionDraft expectedRes =
                new TransactionDraft(3L, transactionTypeEnum, card, BigDecimal.ONE, draftStatus);
        Optional<TransactionDraft> optionalTransactionDraft = Optional.of(expectedRes);

        when(transactionDraftRepository.getFirstDraft()).thenReturn(optionalTransactionDraft);

        //when
        Boolean actualRes = transactionDraftService.isEmpty();

        //then
        assertThat(actualRes)
                .isNotNull();
        verify(transactionDraftRepository).getFirstDraft();
    }
}