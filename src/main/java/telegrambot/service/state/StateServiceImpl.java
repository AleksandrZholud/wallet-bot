package telegrambot.service.state;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import telegrambot.model.util.State;
import telegrambot.repository.util.StateRepository;

/**
 * Methods placed in CRUD order, then private methods
 * Base return types:
 * Create - <Entity>
 * Read - <Entity>
 * Update - <Entity>
 * Delete - void
 */
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
