package com.company.telegrambot.repository;

import com.company.telegrambot.model.Card;
import com.company.telegrambot.model.Operation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;

public interface CardRepository extends JpaRepository<Card, Long> {

    Card getByName(String name);

    @Query(value = "select c.balance from cards c where c.name = 'tmpCardForTest'", nativeQuery = true)
    BigDecimal getBalance();
}
