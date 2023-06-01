package telegrambot.service.transaction_draft;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import telegrambot.model.Card;
import telegrambot.model.drafts.TransactionDraft;
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
        String status = "built...";

        when(transactionDraftRepository.updateStatus(status)).thenReturn(1);

        //when
        var actualRes = transactionDraftService.updateStatus(status);

        //then
        assertThat(actualRes)
                .isTrue();
        verify(transactionDraftRepository).updateStatus(status);
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
    void createFirstDraft() {

        //before
        when(transactionDraftRepository.createFirstDraft()).thenReturn(2);

        //when
        transactionDraftService.createFirstDraft();

        //then
        verify(transactionDraftRepository).createFirstDraft();
    }

    @Test
    void updateCardId() {

        //before
        Long id = 11L;

        when(transactionDraftRepository.updateCardId(id)).thenReturn(11);

        //when
        transactionDraftService.updateCardId(id);

        //then
        verify(transactionDraftRepository).updateCardId(id);
    }

    @Test
    void updateTransactionType() {

        //before
        String type = "Income";

        when(transactionDraftRepository.updateTransactionType(type)).thenReturn(2);

        //when
        transactionDraftService.updateTransactionType(type);

        //then
        verify(transactionDraftRepository).updateTransactionType(type);
    }

    @Test
    void updateAmountAndStatus() {

        //before
        BigDecimal amount = BigDecimal.ONE;
        String status = "built...";

        when(transactionDraftRepository.updateAmountAndStatus(amount, status)).thenReturn(2);

        //when
        transactionDraftService.updateAmountAndStatus(amount, status);

        //then
        verify(transactionDraftRepository).updateAmountAndStatus(amount, status);
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