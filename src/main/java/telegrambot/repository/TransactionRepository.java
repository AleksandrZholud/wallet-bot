package telegrambot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import telegrambot.model.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}