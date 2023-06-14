package telegrambot.service.state;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import telegrambot.model.util.State;
import telegrambot.repository.util.StateRepository;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@SpringBootTest(classes = StateServiceImpl.class)
class StateServiceImplTest {

    @Autowired
    StateService stateService;

    @MockBean
    StateRepository stateRepository;

    @Test
    void findByName() {

        //before
        Long id = 1L;
        String name = "SetBalance";

        State expectedRes = new State(id, name);
        Optional<State> optionalState = Optional.of(expectedRes);

        when(stateRepository.findByNameOptional(name)).thenReturn(optionalState);

        //when
        var actualRes = stateService.findByName(name);

        //then
        assertThat(actualRes)
                .isNotNull()
                .isEqualTo(expectedRes);
        verify(stateRepository).findByNameOptional(name);
    }

    @Test
    void findByName_whenTheNameIsNotFound_throwEx() {

        //before
        String name = "GetUp";
        when(stateRepository.findByNameOptional(name)).thenReturn(Optional.empty());

        //when
        assertThatThrownBy(() -> stateService.findByName(name)).isInstanceOf(IllegalStateException.class);

        //then
        verify(stateRepository).findByNameOptional(name);
    }
}