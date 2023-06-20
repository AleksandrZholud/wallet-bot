package telegrambot.service.transaction;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import telegrambot.model.entity.Card;
import telegrambot.model.entity.Transaction;
import telegrambot.model.enums.TransactionTypeEnum;
import telegrambot.repository.TransactionRepository;

import java.math.BigDecimal;
import java.sql.Timestamp;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@SpringBootTest(classes = TransactionServiceImpl.class)
class TransactionServiceImplTest {

    @Autowired
    TransactionService transactionService;

    @MockBean
    TransactionRepository transactionRepository;

    @Test
    void save() {

        //before
        TransactionTypeEnum transactionTypeEnum = TransactionTypeEnum.EXPENSE;
        Card card = new Card(2L, "New card", BigDecimal.ONE);
        Timestamp timestamp = new Timestamp(323898954L);
        Transaction expectedRes = new Transaction(1L, transactionTypeEnum, card, BigDecimal.ONE, timestamp);

        when(transactionRepository.save(expectedRes)).thenReturn(expectedRes);

        //when
        var actualRes = transactionService.save(expectedRes);

        //then
        assertThat(actualRes)
                .isEqualTo(expectedRes);

        verify(transactionRepository).save(actualRes);
    }
}