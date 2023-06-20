package telegrambot.service.transaction;

import telegrambot.model.Transaction;

public interface TransactionService {
    Transaction save(Transaction transactionToSave);
}
