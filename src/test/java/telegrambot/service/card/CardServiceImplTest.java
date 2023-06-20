package telegrambot.service.card;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import telegrambot.model.entity.Card;
import telegrambot.repository.CardRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

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
        String cardName = "card";
        Card expectedResult = Card.builder()
                .id(1L)
                .name(cardName)
                .balance(BigDecimal.ONE)
                .build();

        when(cardRepository.getByName(cardName)).thenReturn(Optional.of(expectedResult));

        //when
        Card actualResult = cardService.getByName(cardName);

        //then
        assertThat(actualResult)
                .isNotNull()
                .isEqualTo(expectedResult);

        verify(cardRepository).getByName(cardName);
    }

    @Test
    void getByName_whenCardIsNotFound_throwEx() {

        //before
        String name = "Non_exist_name";
        when(cardRepository.getByName(name)).thenReturn(Optional.empty());

        //when
        assertThatThrownBy(() -> cardService.getByName(name))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("No such Card in database");

        //then
        verify(cardRepository).getByName(name);
    }

    @Test
    void createCard_ok() {

        //before
        Card expectedResult = Card.builder()
                .id(1L)
                .name("cardName")
                .balance(BigDecimal.ONE)
                .build();

        when(cardRepository.getByName(expectedResult.getName())).thenReturn(Optional.empty());
        when(cardRepository.save(expectedResult)).thenReturn(expectedResult);

        //when
        Card actualResult = cardService.createCard(expectedResult);

        //then
        assertThat(actualResult)
                .isEqualTo(expectedResult);

        verify(cardRepository).getByName(expectedResult.getName());
        verify(cardRepository).save(actualResult);
    }

    @Test
    void createCard_AlreadyExist() {

        //before
        Card expectedResult = Card.builder()
                .id(1L)
                .name("cardName")
                .balance(BigDecimal.ONE)
                .build();

        when(cardRepository.getByName(expectedResult.getName())).thenReturn(Optional.of(expectedResult));

        //when

        //then
        assertThatThrownBy(() -> cardService.createCard(expectedResult))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Card with name '" + expectedResult.getName() + "' already exist in database");

        verify(cardRepository).getByName(expectedResult.getName());
        verify(cardRepository, never()).save(new Card());
    }

    @Test
    void updateBalanceByName_ok() {

        // before
        BigDecimal amount = BigDecimal.TEN;
        String name = "Card_name";
        Card existingCard = Card.builder()
                .id(1L)
                .name(name)
                .balance(BigDecimal.ZERO)
                .build();
        Card changedCard = Card.builder()
                .id(1L)
                .name(name)
                .balance(amount)
                .build();

        when(cardRepository.getByName(name)).thenReturn(Optional.of(existingCard));
        when(cardRepository.save(any())).thenReturn(changedCard);

        // when
        Card actualResult = cardService.updateBalanceByName(amount, name);

        // then
        assertThat(actualResult)
                .isNotNull()
                .isEqualTo(changedCard);
        verify(cardRepository).getByName(name);
        verify(cardRepository).save(changedCard);
    }

    @Test
    void updateBalanceByName_NoCardWithName() {

        // before
        BigDecimal amount = BigDecimal.TEN;
        String name = "Card_name";
        Card existingCard = Card.builder()
                .id(1L)
                .name(name)
                .balance(BigDecimal.ZERO)
                .build();
        Card changedCard = Card.builder()
                .id(1L)
                .name(name)
                .balance(BigDecimal.ZERO.add(amount))
                .build();

        when(cardRepository.getByName(name)).thenReturn(Optional.empty());

        // when

        // then
        assertThatThrownBy(() -> cardService.updateBalanceByName(amount, name))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("No such Card in database");
        verify(cardRepository).getByName(name);
        verify(cardRepository, never()).save(new Card());
    }

    @Test
    void findAll() {

        //before
        List<Card> expectedRes = new ArrayList<>();
        when(cardRepository.findAll()).thenReturn(expectedRes);

        //when
        var actualRes = cardService.getAll();

        //then
        assertThat(actualRes)
                .isEqualTo(expectedRes);
        verify(cardRepository).findAll();
    }

    @Test
    void checkIfExistByName() {

        //before
        Long id = 1L;
        String name = "Card_name";
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