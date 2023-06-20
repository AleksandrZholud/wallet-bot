package telegrambot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import telegrambot.model.entity.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}