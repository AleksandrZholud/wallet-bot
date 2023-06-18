package telegrambot.service.command;

import telegrambot.model.util.Command;

/**
 * Methods placed in CRUD order, then private methods
 * Base return types:
 * Create - <Entity>
 * Read - <Entity>
 * Update - <Entity>
 * Delete - void
 */
public interface CommandService {

    Command getByName(String name);
}
