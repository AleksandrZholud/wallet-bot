package telegrambot.service.current_condition;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import telegrambot.model.enums.CommandEnum;
import telegrambot.model.enums.StateEnum;
import telegrambot.model.util.Command;
import telegrambot.model.util.CurrentCondition;
import telegrambot.model.util.State;
import telegrambot.repository.util.CommandRepository;
import telegrambot.repository.util.CurrentConditionRepository;
import telegrambot.repository.util.StateRepository;

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
    @MockBean
    StateRepository stateRepository;
    @MockBean
    CommandRepository commandRepository;

    @Test
    void getCurrentCondition() {
        //before
        Command command = new Command(1L, "Create a card");
        State state = new State(2L, "SetBalance");

        CurrentCondition expectedRes = new CurrentCondition(1L, command, state);
        Optional<CurrentCondition> optionalCurrentCondition = Optional.of(expectedRes);

        when(currentConditionRepository.getCurrentConditionOptional()).thenReturn(optionalCurrentCondition);
        //when

        var actualRes = currentConditionService.getCurrentCondition();

        //then

        assertThat(actualRes)
                .isNotNull()
                .isEqualTo(expectedRes);
        verify(currentConditionRepository).getCurrentConditionOptional();
    }

    @Test
    void getCurrentCondition_throwEx() {

        //before
        when(currentConditionRepository.getCurrentConditionOptional()).thenReturn(Optional.empty());

        //when
        assertThatThrownBy(() -> currentConditionService.getCurrentCondition())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("No Current Condition in DataBase.");

        //then
        verify(currentConditionRepository).getCurrentConditionOptional();
    }

    @Test
    void updateCommandAndState_1Overload() {
        //before
        Command command = new Command(1L, "Default command");
        State state = new State(1L, "Default state");
        CommandEnum commandEnum = CommandEnum.START_COMMAND;
        StateEnum stateEnum = StateEnum.NO_STATE;
        CurrentCondition existedCC = CurrentCondition.builder()
                .id(0L)
                .command(new Command(12L, "C"))
                .state(new State(12L, "S"))
                .build();
        CurrentCondition changedCC = CurrentCondition.builder()
                .id(0L)
                .command(command)
                .state(state)
                .build();

        when(commandRepository.findByNameOptional(any())).thenReturn(Optional.of(command));
        when(stateRepository.findByNameOptional(any())).thenReturn(Optional.of(state));
        when(currentConditionRepository.getCurrentConditionOptional()).thenReturn(Optional.of(existedCC));
        when(currentConditionRepository.save(any())).thenReturn(changedCC);

        //when
        CurrentCondition res = currentConditionService.updateCommandAndState(commandEnum, stateEnum);

        //then
        assertThat(res)
                .isNotNull()
                .isEqualTo(changedCC);
        verify(commandRepository).findByNameOptional(any());
        verify(stateRepository).findByNameOptional(any());
        verify(currentConditionRepository).getCurrentConditionOptional();
        verify(currentConditionRepository).save(any());
    }

    @Test
    void updateCommandAndState_2Overload() {
        //before
        Command command = new Command(1L, "Default command");
        State state = new State(1L, "Default state");
        CurrentCondition existedCC = CurrentCondition.builder()
                .id(0L)
                .command(new Command(12L, "C"))
                .state(new State(12L, "S"))
                .build();
        CurrentCondition changedCC = CurrentCondition.builder()
                .id(0L)
                .command(command)
                .state(state)
                .build();

        when(currentConditionRepository.getCurrentConditionOptional()).thenReturn(Optional.of(existedCC));
        when(currentConditionRepository.save(any())).thenReturn(changedCC);

        //when
        CurrentCondition res = currentConditionService.updateCommandAndState(command, state);

        //then
        assertThat(res)
                .isNotNull()
                .isEqualTo(changedCC);
        verify(currentConditionRepository).getCurrentConditionOptional();
        verify(currentConditionRepository).save(any());
    }

    @Test
    void getPreviousState() {
        //before
        State state = new State(1L, "State");
        when(currentConditionRepository.getPreviousStateOptional()).thenReturn(Optional.of(state));

        //when
        var actualRes = currentConditionService.getPreviousState();

        //then

        assertThat(actualRes)
                .isNotNull()
                .isEqualTo(state);
        verify(currentConditionRepository).getPreviousStateOptional();
    }

    @Test
    void getPreviousState_Ex() {
        //before
        State state = new State(1L, "State");
        when(currentConditionRepository.getPreviousStateOptional()).thenReturn(Optional.empty());

        //then
        assertThatThrownBy(() -> currentConditionService.getPreviousState())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("No Previous State in DataBase");
        verify(currentConditionRepository).getPreviousStateOptional();
    }

    @Test
    void updateState() {

        //before
        State state = new State(3L, "NO_STATE");
        CurrentCondition existedCC = CurrentCondition.builder()
                .id(1L)
                .command(new Command(1L, "Command"))
                .state(new State(1L, "State"))
                .build();
        CurrentCondition changedCC = CurrentCondition.builder()
                .id(1L)
                .command(new Command(1L, "Command"))
                .state(state)
                .build();

        when(currentConditionRepository.getCurrentConditionOptional()).thenReturn(Optional.of(existedCC));
        when(currentConditionRepository.save(any())).thenReturn(changedCC);

        //when
        CurrentCondition res = currentConditionService.updateState(state);

        //then
        assertThat(res)
                .isNotNull()
                .isEqualTo(changedCC);
        verify(currentConditionRepository).getCurrentConditionOptional();
        verify(currentConditionRepository).save(any());
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