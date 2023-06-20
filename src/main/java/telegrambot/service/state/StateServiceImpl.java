package telegrambot.service.state;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import telegrambot.model.util.State;
import telegrambot.repository.util.StateRepository;

@Service
@RequiredArgsConstructor
public class StateServiceImpl implements StateService {

    private final StateRepository stateRepository;

    @Override
    public State findByName(String name) {
        return stateRepository.findByNameOptional(name)
                .orElseThrow(() -> new IllegalStateException("State with name '" + name + "' not found."));
    }
}
