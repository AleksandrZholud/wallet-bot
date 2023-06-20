package telegrambot.service.transaction;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import telegrambot.model.entity.Transaction;
import telegrambot.repository.TransactionRepository;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;

    @Override
    public Transaction save(Transaction transactionToSave) {
        return transactionRepository.save(transactionToSave);
    }
}
