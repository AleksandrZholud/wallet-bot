package telegrambot.service.transaction;

import telegrambot.model.Transaction;

/**
 * Methods placed in CRUD order, then private methods
 * Base return types:
 * Create - <Entity>
 * Read - <Entity>
 * Update - <Entity>
 * Delete - void
 */
public interface TransactionService {
    Transaction save(Transaction transactionToSave);
}
