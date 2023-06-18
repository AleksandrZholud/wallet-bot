package telegrambot.service.state;

import telegrambot.model.util.State;

/**
 * Methods placed in CRUD order, then private methods
 * Base return types:
 * Create - <Entity>
 * Read - <Entity>
 * Update - <Entity>
 * Delete - void
 */
public interface StateService {

    State findByName(String name);
}
