package telegrambot.service.state_history;

import telegrambot.model.util.MsgFromStateHistory;

/**
 * Methods placed in CRUD order, then private methods
 * Base return types:
 * Create - <Entity>
 * Read - <Entity>
 * Update - <Entity>
 * Delete - void
 */
public interface MsgFromStateHistoryService {

    MsgFromStateHistory save(MsgFromStateHistory msgFromStateHistory);

    boolean isEmpty();

    MsgFromStateHistory getPreLast();

    void removeLast();


    void deleteAll();
}
