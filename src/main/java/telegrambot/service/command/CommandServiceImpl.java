package telegrambot.service.command;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import telegrambot.model.util.Command;
import telegrambot.repository.util.CommandRepository;

@AllArgsConstructor
@Component
public class CommandServiceImpl implements CommandService {

    private final CommandRepository commandRepository;

    @Override
    public Command getByName(String name) {
        return commandRepository.findByName(name).
                orElseThrow(() -> new IllegalStateException("Command with name '" + name + "' not found."));
    }
}
