package telegrambot.service.transaction;

import telegrambot.model.entity.Transaction;

public interface TransactionService {
    Transaction save(Transaction transactionToSave);
}
