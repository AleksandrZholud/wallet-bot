package com.company.telegrambot.service;

import com.company.telegrambot.model.Card;
import com.company.telegrambot.model.OPERATION_TYPE;
import com.company.telegrambot.model.Operation;
import com.company.telegrambot.repository.CardRepository;
import com.company.telegrambot.repository.OperationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class OperationServiceImpl implements OperationService {

    private final OperationRepository operationRepository;
    private final CardRepository cardRepository;

    public OperationServiceImpl(OperationRepository operationRepository, CardRepository cardRepository) {
        this.operationRepository = operationRepository;
        this.cardRepository = cardRepository;
    }

    @Transactional
    public void save(Operation operation) {
        Card card = operation.getCard();
        OPERATION_TYPE currentType = operation.getType();

        BigDecimal newBalance;
        if (currentType == OPERATION_TYPE.EXPENDITURE) {
            newBalance = card.getBalance().subtract(operation.getAmount());
        } else {
            newBalance = card.getBalance().add(operation.getAmount());
        }
        card.setBalance(newBalance);
        cardRepository.save(card);

        operationRepository.save(operation);
    }
}