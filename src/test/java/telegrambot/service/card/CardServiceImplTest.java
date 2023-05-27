package telegrambot.service.card;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import telegrambot.model.Card;
import telegrambot.repository.CardRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@SpringBootTest(classes = CardServiceImpl.class)
class CardServiceImplTest {

    @Autowired
    CardService cardService;

    @MockBean
    CardRepository cardRepository;

    @Test
    void getByName_ok() {

        //before
        Long id = 1L;
        String name = "Exist name";
        BigDecimal balance = BigDecimal.ONE;

        Card expectedRes = new Card(id, name, balance);
        Optional<Card> optExistedCard = Optional.of(expectedRes);

        when(cardRepository.getByName(name)).thenReturn(optExistedCard);

        //when
        var actualRes = cardService.getByName(name);

        //then
        assertThat(actualRes)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(expectedRes);

        verify(cardRepository).getByName(name);
    }

    @Test
    void getByName_whenCardIsNotFound_throwEx() {

        //before
        String name = "Not exist name";
        when(cardRepository.getByName(name)).thenReturn(Optional.empty());

        //when
        assertThatThrownBy(() -> cardService.getByName(name)).isInstanceOf(IllegalStateException.class);

        //then
        verify(cardRepository).getByName(name);
    }

    @Test
    void getBalance() {

        //when
        var actualRes = cardService.getBalance();

        //then
        assertThat(actualRes)
                .isNull();
    }

    @Test
    void save() {

        //before
        Long id = 1L;
        String name = "Card name";
        BigDecimal balance = BigDecimal.ONE;

        Card expectedRes = new Card(id, name, balance);

        when(cardRepository.save(expectedRes)).thenReturn(expectedRes);

        //when
        var actualRes = cardService.save(expectedRes);

        //then
        assertThat(actualRes)
                .isEqualTo(expectedRes);

        verify(cardRepository).save(actualRes);
    }

    @Test
    void updateBalanceByName() {

        // before
        BigDecimal amount = BigDecimal.ONE;
        String name = "Card name";

        when(cardRepository.updateBalanceByName(amount, name)).thenReturn(1);

        // when
        cardService.updateBalanceByName(amount, name);

        // then
        verify(cardRepository).updateBalanceByName(amount, name);
    }

    @Test
    void findAll() {

        //before
        List<Card> expectedRes = new ArrayList<>();
        when(cardRepository.findAll()).thenReturn(expectedRes);

        //when
        var actualRes = cardService.findAll();

        //then
        assertThat(actualRes)
                .isEqualTo(expectedRes);
        verify(cardRepository).findAll();
    }

    @Test
    void checkIfExistByName() {

        //before
        Long id = 1L;
        String name = "Card name";
        BigDecimal balance = BigDecimal.ONE;
        Optional<Card> res = Optional.of(new Card(id, name, balance));

        when(cardRepository.getByName(name)).thenReturn(res);

        //when
        boolean actualRes = cardService.checkIfExistByName(name);

        //then
        assertThat(actualRes)
                .isTrue();

        verify(cardRepository).getByName(name);
    }
}