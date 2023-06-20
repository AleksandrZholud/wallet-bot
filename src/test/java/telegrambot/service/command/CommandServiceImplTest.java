package telegrambot.service.command;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import telegrambot.model.util.Command;
import telegrambot.repository.util.CommandRepository;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@SpringBootTest(classes = CommandServiceImpl.class)
class CommandServiceImplTest {

    @Autowired
    CommandService commandService;

    @MockBean
    CommandRepository commandRepository;

    @Test
    void findByName() {

        //before
        String name = "Create a card";

        Command expectedRes = new Command(1L, name);
        Optional<Command> commandOptional = Optional.of(expectedRes);

        when(commandRepository.findByNameOptional(name)).thenReturn(commandOptional);
        //when

        var actualRes = commandService.getByName(name);

        //then
        assertThat(actualRes)
                .isNotNull()
                .isEqualTo(expectedRes);

        verify(commandRepository).findByNameOptional(name);
    }

    @Test
    void findByName_whenCommandIsNotFound_throwEx() {

        //before
        String name = "Create the card";

        when(commandRepository.findByNameOptional(name)).thenReturn(Optional.empty());

        //when
        assertThatThrownBy(() -> commandService.getByName(name)).isInstanceOf(IllegalStateException.class);

        //then
        verify(commandRepository).findByNameOptional(name);
    }
}