package telegrambot.service.current_condition;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import telegrambot.model.util.Command;
import telegrambot.model.util.CurrentCondition;
import telegrambot.model.util.State;
import telegrambot.repository.util.CurrentConditionRepository;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@SpringBootTest(classes = CurrentConditionServiceImpl.class)
class CurrentConditionServiceImplTest {

    @Autowired
    CurrentConditionService currentConditionService;

    @MockBean
    CurrentConditionRepository currentConditionRepository;

    @Test
    void getCurrentCondition() {

        //before
        Command command = new Command(1L, "Create a card");
        State state = new State(2L, "SetBalance");

        CurrentCondition expectedRes = new CurrentCondition(1L, command, state);
        Optional<CurrentCondition> optionalCurrentCondition = Optional.of(expectedRes);

        when(currentConditionRepository.getOptionalCurrentCondition()).thenReturn(optionalCurrentCondition);
        //when

        var actualRes = currentConditionService.getCurrentCondition();

        //then

        assertThat(actualRes)
                .isNotNull()
                .isEqualTo(expectedRes);
        verify(currentConditionRepository).getOptionalCurrentCondition();
    }

    @Test
    void getCurrentCondition_whenConditionIsNotFound_throwEx() {

        //before
        when(currentConditionRepository.getOptionalCurrentCondition()).thenReturn(Optional.empty());

        //when
        assertThatThrownBy(() -> currentConditionService.getCurrentCondition())
                .isInstanceOf(IllegalStateException.class);

        //then
        verify(currentConditionRepository).getOptionalCurrentCondition();

    }

    @Test
    void updateCommandAndState() {

        //before
        Long command = 1L;
        Long state = 2L;

        when(currentConditionRepository.updateCommandAndState(command, state)).thenReturn(1);

        //when
        currentConditionService.updateCommandAndState(command, state);

        //then
        verify(currentConditionRepository).updateCommandAndState(command, state);
    }

    @Test
    void getPreviousStateId() {

        //before
        Long expectedRes = 1L;

        when(currentConditionRepository.getPreviousStateId()).thenReturn(expectedRes);

        //when
        var actualRes = currentConditionService.getPreviousStateId();

        //then

        assertThat(actualRes)
                .isEqualTo(expectedRes);
        verify(currentConditionRepository).getPreviousStateId();
    }

    @Test
    void updateState() {

        //before
        long stateId = 2L;

        when(currentConditionRepository.updateState(stateId)).thenReturn(2);

        //when
        currentConditionService.updateState(stateId);

        //then
        verify(currentConditionRepository).updateState(stateId);
    }

    @Test
    void reset() {

        //before
        doNothing().when(currentConditionRepository).reset();

        //when
        currentConditionService.reset();

        //then
        verify(currentConditionRepository).reset();
    }
}